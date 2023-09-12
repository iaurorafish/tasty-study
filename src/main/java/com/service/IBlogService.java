package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dto.Result;
import com.entity.Blog;

public interface IBlogService extends IService<Blog> {
    Result queryHotBlog(Integer current);

    Result saveBlog(Blog blog);

    Result queryMyBlog(Integer current);

    Result queryBlogById(Long id);

    Result likeBlog(Long id);

    Result queryBlogLikes(Long id);

    Result queryBlogsByUserId(Long id, Integer current);

    Result queryBlogOfFollow(Long max, Integer offset);
}
