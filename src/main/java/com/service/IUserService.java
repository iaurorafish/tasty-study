package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dto.LoginFormDTO;
import com.dto.Result;
import com.entity.User;

import javax.servlet.http.HttpSession;

public interface IUserService extends IService<User> {
    Result sendCode(String phone, HttpSession session);

    Result login(LoginFormDTO loginForm, HttpSession session);

    Result info(Long userId);

    Result queryUserById(Long userId);

    Result sign();

    Result signCount();
}
