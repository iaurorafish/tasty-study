package com.controller;

import com.dto.LoginFormDTO;
import com.dto.Result;
import com.dto.UserDTO;
import com.entity.User;
import com.service.IUserService;
import com.utils.UserHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private IUserService userService;

    /**
     * 发送手机验证码
     * @param phone
     * @param session
     * @return
     */
    @PostMapping("/code")
    public Result sendCode(@RequestParam("phone") String phone, HttpSession session) {
        return userService.sendCode(phone, session);
    }

    /**
     * 登录功能
     * @param loginForm 登录参数，包含手机号、验证码；或者手机号、密码
     * @param session
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginForm, HttpSession session) {
        return userService.login(loginForm, session);
    }

    /**
     * 登出功能
     * @return 无
     */
    @PostMapping("/logout")
    public Result logout() {
        UserHolder.removeUser();
        return Result.ok();
    }

    /**
     * 获取当前登陆的用户
     * @return
     */
    @GetMapping("/me")
    public Result me() {
        UserDTO user = UserHolder.getUser();
        return Result.ok(user);
    }

    /**
     * 根据id查询用户
     * @param userId
     * @return
     */
    @GetMapping("/{id}")
    public Result queryUserById(@PathVariable("id") Long userId) {
        return userService.queryUserById(userId);
    }

    /**
     * 查询详情
     * @param userId
     * @return
     */
    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long userId) {
        return userService.info(userId);
    }

    @PostMapping("/sign")
    public Result sign() {
        return userService.sign();
    }

    @GetMapping("/sign/count")
    public Result signCount() {
        return userService.signCount();
    }
}
