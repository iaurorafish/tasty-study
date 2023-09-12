package com.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dto.Result;
import com.dto.UserDTO;
import com.entity.Follow;
import com.mapper.FollowMapper;
import com.service.IFollowService;
import com.service.IUserService;
import com.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.utils.RedisConstants.FOLLOWS_KEY;

@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private IUserService userService;

    @Override
    public Result follow(Long followUserId, Boolean isFollow) {
        UserDTO userDTO = UserHolder.getUser();
        if (userDTO == null) {
            return Result.fail("用户尚未登录！");
        }
        Long userId = userDTO.getId();
        String key = FOLLOWS_KEY + userId;
        // 1.判断到底是关注还是取关
        if (isFollow) {
            // 2.关注，新增数据
            Follow follow = new Follow();
            follow.setUserId(userId);
            follow.setFollowUserId(followUserId);
            boolean isSuccess = save(follow);
            if (isSuccess) {
                // 把关注用户的id，放入redis的set集合 sadd userId followerUserId
                stringRedisTemplate.opsForSet().add(key, followUserId.toString());
                return Result.ok();
            } else {
                return Result.fail("关注失败！");
            }

        } else {
            // 3.取关，删除 delete from tb_follow where user_id = ? and follow_user_id = ?
            boolean isSuccess = remove(new QueryWrapper<Follow>()
                    .eq("user_id", userId).eq("follow_user_id", followUserId));
            if (isSuccess) {
                // 把关注用户的id从Redis集合中移除
                stringRedisTemplate.opsForSet().remove(key, followUserId.toString());
                return Result.ok();
            }
            return Result.fail("取关失败！");
        }
    }

    @Override
    public Result isFollow(Long followUserId) {
        UserDTO userDTO = UserHolder.getUser();
        if (userDTO == null) {
            return Result.fail("用户尚未登录！");
        }
        Long userId = userDTO.getId();
        // 查询是否关注select count(*) from tb_follow where user_id = ? and follow_user_id = ?
        Integer count = query().eq("user_id", userId).eq("follow_user_id", followUserId).count();
        return Result.ok(count > 0);
    }

    @Override
    public Result queryCommonFollow(Long followUserId) {
        // 获取当前用户
        UserDTO userDTO = UserHolder.getUser();
        if(userDTO == null) {
            return Result.fail("该用户尚未登录！");
        }
        Long userId = userDTO.getId();
        // 求交集
        Set<String> common = stringRedisTemplate.opsForSet()
                .intersect(FOLLOWS_KEY + userId, FOLLOWS_KEY + followUserId);
        // 解析id集合
        List<Long> ids = common.stream().map(Long::valueOf).collect(Collectors.toList());
        if(ids == null || ids.isEmpty()) {
            // 无交集
            // return Result.ok(Collections.emptyList());
            return Result.fail("你们没有共同关注！");
        }
        // 查询用户
        List<UserDTO> userDTOS = userService.listByIds(ids)
                .stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                .collect(Collectors.toList());
        return Result.ok(userDTOS);
    }
}
