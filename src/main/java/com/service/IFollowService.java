package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dto.Result;
import com.entity.Follow;

public interface IFollowService extends IService<Follow> {
    Result follow(Long followUserId, Boolean isFollow);

    Result isFollow(Long followUserId);

    Result queryCommonFollow(Long followUserId);
}
