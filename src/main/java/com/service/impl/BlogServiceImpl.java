package com.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dto.Result;
import com.dto.ScrollResult;
import com.dto.UserDTO;
import com.entity.Blog;
import com.entity.Follow;
import com.entity.User;
import com.mapper.BlogMapper;
import com.service.IBlogService;
import com.service.IFollowService;
import com.service.IUserService;
import com.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.utils.RedisConstants.BLOG_LIKED_KEY;
import static com.utils.RedisConstants.BOLG_FEED_KEY;
import static com.utils.SystemConstants.MAX_PAGE_SIZE;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {
    @Resource
    private IUserService userService;
    @Resource
    private IFollowService followService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryHotBlog(Integer current) {
        // 分页查询
        Page<Blog> page = query()
                .orderByDesc("liked")
                .page(new Page<>(current, MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        // 查询用户
        records.forEach(blog -> {
            this.queryUserByBlog(blog);
            this.isBlogLiked(blog);
        });
        return Result.ok(records);
    }

    @Override
    public Result saveBlog(Blog blog) {
        // 获取登录用户
        UserDTO userDTO = UserHolder.getUser();
        if (userDTO == null) {
            return Result.ok("当前用户尚未登录！");
        }
        Long userId = userDTO.getId();
        blog.setUserId(userId);
        // 保存探店博客
        boolean isSuccess = save(blog);
        if (!isSuccess) {
            return Result.fail("博客保存失败！");
        }
        // 查询博客作者的所有粉丝
        List<Follow> follows = followService.query().eq("follow_user_id", userId).list();
        if (follows == null || follows.isEmpty()) {
            return Result.ok();
        }
        for (Follow follow : follows) {
            Long id = follow.getUserId();
            // 推送博客id给所有粉丝
            stringRedisTemplate.opsForZSet()
                    .add(BOLG_FEED_KEY + id, blog.getId().toString(), System.currentTimeMillis());
        }
        // 返回id
        return Result.ok(blog.getId());
    }

    @Override
    public Result queryMyBlog(Integer current) {
        // 获取登陆用户id
        Long userId = UserHolder.getUser().getId();
        // 更具用户查询
        Page<Blog> page = query().eq("user_id", userId).page(new Page<>(current, MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        return Result.ok(records);
    }

    @Override
    public Result queryBlogById(Long id) {
        // 查询blog
        Blog blog = getById(id);
        if (blog == null) {
            return Result.fail("博客不存在！");
        }
        // 查询blog有关的用户
        queryUserByBlog(blog);
        // 查询blog是否被点赞
        isBlogLiked(blog);
        return Result.ok(blog);
    }

    @Override
    public Result likeBlog(Long id) {
        // 修改点赞数量 update tb_blog set liked = liked + 1 where id = ?
        // update().setSql("liked = liked + 1").eq("id", id).update();
        // 1.获取当前用户
        UserDTO userDTO = UserHolder.getUser();
        if (userDTO == null) {
            return Result.fail("用户尚未登录！");
        }
        Long userId = userDTO.getId();
        // 2.判断当前用户是否已经点赞
        String likeKey = BLOG_LIKED_KEY + id;
        Double score = stringRedisTemplate.opsForZSet().score(likeKey, userId.toString());
        if (score != null) {
            // 3.点赞过，取消点赞
            // 3.1数据库点赞数-1
            boolean isSuccess = update().setSql("liked = liked - 1").eq("id", id).update();
            if (isSuccess) {
                // 3.2把用户从Redis的SortedSet集合移除
                stringRedisTemplate.opsForZSet().remove(likeKey, userId.toString());
            }
            return Result.ok();
        }
        // 4.没有点赞过，可以点赞
        // 4.1数据库点赞数+1
        boolean isSuccess = update().setSql("liked = liked + 1").eq("id", id).update();
        if (isSuccess) {
            // 4.2把用户加进Redis的SortedSet集合 zadd key value score
            stringRedisTemplate.opsForZSet().add(likeKey, userId.toString(), System.currentTimeMillis());
        }
        return Result.ok();
    }

    @Override
    public Result queryBlogLikes(Long id) {
        String likeKey = BLOG_LIKED_KEY + id;
        // 1.查询top5的点赞用户zrange key 0 4
        Set<String> top5 = stringRedisTemplate.opsForZSet().range(likeKey, 0, 4);
        if (top5 == null || top5.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }
        // 2.解析出其中的用户id
        List<Long> ids = top5.stream().map(Long::valueOf).collect(Collectors.toList());
        String idStr = StrUtil.join(",", ids);
        // 3.根据用户id查询用户WHERE id IN (2 , 1) ORDER BY FIELD(id, 2, 1)
        List<UserDTO> userDTOS = userService.query()
                .in("id", ids).last("ORDER BY FIELD(id, " + idStr + ")").list()
                .stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                .collect(Collectors.toList());
        // 4.返回
        return Result.ok(userDTOS);
    }

    @Override
    public Result queryBlogsByUserId(Long id, Integer current) {
        // 根据用户查询
        Page<Blog> page = query().eq("user_id", id).page(new Page<>(current, MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        return Result.ok(records);
    }

    @Override
    public Result queryBlogOfFollow(Long max, Integer offset) {
        // 获取当前用户
        UserDTO userDTO = UserHolder.getUser();
        if (userDTO == null) {
            return Result.ok("当前用户尚未登录！");
        }
        // 查询收件箱ZREVRANGEBYSCORE key Max Min LIMIT offset count
        Long userId = userDTO.getId();
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(BOLG_FEED_KEY + userId, 0, max, offset, 2);
        // 非空判断
        if (typedTuples == null || typedTuples.isEmpty()) {
            return Result.ok();
        }
        // 解析数据：blogId、minTime(时间戳)、offset
        List<Long> ids = new ArrayList<>(typedTuples.size());
        long minTime = 0;
        int os = 1;
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            // 获取id
            ids.add(Long.valueOf(typedTuple.getValue()));
            // 获取分数（时间戳）
            long time = typedTuple.getScore().longValue();
            if (minTime == time) {
                os++;
            } else {
                minTime = time;
                os = 1;
            }
        }
        // 根据id查询blog
        String idStr = StrUtil.join(",", ids);
        List<Blog> blogs = query()
                .in("id", ids)
                .last("order by field(id," + idStr + ")")
                .list();
        for (Blog blog : blogs) {
            // 查询blog有关的用户
            queryUserByBlog(blog);
            // 查询blog是否被点赞
            isBlogLiked(blog);
        }
        ScrollResult scrollResult = new ScrollResult();
        scrollResult.setList(blogs);
        scrollResult.setOffset(os);
        scrollResult.setMinTime(minTime);
        return Result.ok(scrollResult);
    }

    private void queryUserByBlog(Blog blog) {
        Long userId = blog.getUserId();
        User user = userService.getById(userId);
        blog.setName(user.getNickName());
        blog.setIcon(user.getIcon());
    }

    private void isBlogLiked(Blog blog) {
        // 1.获取登录用户
        UserDTO userDTO = UserHolder.getUser();
        if (userDTO == null) {
            // 用户未登录，无需查询是否点赞
            return;
        }
        Long userId = userDTO.getId();
        // 2.判断当前登录用户是否已经点赞
        String likeKey = BLOG_LIKED_KEY + blog.getId();
        // 从Redis中获取set集合
        Double score = stringRedisTemplate.opsForZSet().score(likeKey, userId.toString());
        blog.setIsLike(score != null);
    }
}
