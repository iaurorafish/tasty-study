package com.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dto.LoginFormDTO;
import com.dto.Result;
import com.dto.UserDTO;
import com.entity.User;
import com.entity.UserInfo;
import com.mapper.UserMapper;
import com.service.IUserInfoService;
import com.service.IUserService;
import com.utils.RegexUtils;
import com.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.utils.RedisConstants.*;
import static com.utils.SystemConstants.USER_NICK_NAME_PREFIX;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private IUserInfoService userInfoService;

    /**
     * 发送短信验证码
     * @param phone  电话
     * @param session 当前会话
     * @return
     */
    @Override
    public Result sendCode(String phone, HttpSession session) {
        // 1.校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 2.如果不符合，返回错误信息
            return Result.fail("手机号格式错误！");
        }
        // 3.符合，生成校验码
        String code = RandomUtil.randomNumbers(6);
        // 4.保存验证码到redis，set key value ex 120
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + phone, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);
        // 5.发送验证码
        log.debug("发送短信验证码成功，验证码：{}", code);
        // 返回OK
        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        // 1.校验手机号
        String phone = loginForm.getPhone();
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 如果不符合，返回错误信息
            return Result.fail("手机号格式错误");
        }
        // 2.校验验证码
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);
        String code = loginForm.getCode();
        if (cacheCode == null || !cacheCode.equals(code)) {
            // 3.不一致，报错
            return Result.fail("验证码错误！");
        }
        // 4.一致，根据手机号查询用户
        User user = query().eq("phone", phone).one();
        // 5.判断用户是否存在
        if (user == null) {
            // 6.不存在，创建新用户并保存
            user = createUserWithPhone(phone);
        }
        // 7.保存用户信息到redis中
        // 7.1随机生成token，作为登录令牌
        String token = UUID.randomUUID().toString(true);
        // 7.2将User对象转为HashMap存储
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        // 7.3存储
        String tokenKey = LOGIN_TOKEN_KEY + token;
        stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
        // 7.4设置token有效期
        stringRedisTemplate.expire(tokenKey, LOGIN_TOKEN_TTL, TimeUnit.MINUTES);
        // 8.返回token
        return Result.ok(token);
    }

    @Override
    public Result info(Long userId) {
        // 查询详情
        UserInfo info = userInfoService.getById(userId);
        if (info == null) {
            // 没有详情，应该是第一次查看详情
            return Result.ok();
        }
        return Result.ok(info);
    }

    @Override
    public Result queryUserById(Long userId) {
        // 查询详情
        User user = getById(userId);
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        // 返回
        return Result.ok(userDTO);
    }

    @Override
    public Result sign() {
        // 获取当前登录用户
        UserDTO userDTO = UserHolder.getUser();
        if (userDTO == null) {
            return Result.fail("当前用户尚未登录！");
        }
        // 获取日期
        LocalDateTime now = LocalDateTime.now();
        // 拼接key
        String key = USER_SIGN_KEY + userDTO.getId() + now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        // 写入Redis SETBIT key offset 1
        stringRedisTemplate.opsForValue().setBit(key, now.getDayOfMonth() - 1, true);
        return Result.ok();
    }

    @Override
    public Result signCount() {
        // 获取当前登录用户
        UserDTO userDTO = UserHolder.getUser();
        if (userDTO == null) {
            return Result.fail("当前用户尚未登录！");
        }
        // 获取日期
        LocalDateTime now = LocalDateTime.now();
        // 拼接key
        String key = USER_SIGN_KEY + userDTO.getId() + now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        // 获取今天是本月的第几天
        int dayOfMonth = now.getDayOfMonth();
        // 获取本月截止今天为止的所有签到记录，返回的是一个十进制的数据BITFIELD sign:1:202309 GET u5 0
        BitFieldSubCommands subCommands = BitFieldSubCommands.create()
                .get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth))
                .valueAt(0);
        List<Long> result = stringRedisTemplate.opsForValue().bitField(key, subCommands);
        if(result == null || result.isEmpty()) {
            // 没有任何签到结果
            return Result.ok(0);
        }
        Long num = result.get(0);
        if (num == null || num == 0) {
            // 没有任何签到结果
            return Result.ok(0);
        }
        // 循环遍历
        int count = 0;
        while (true) {
            // 让这个数字与1做与运算，得到数字的最后一个bit位
            // 判断这个bit位是否为0
            if((num & 1)==0) {
                // 如果为0，说明未签到，结束
                break;
            } else {
                // 如果不为0，说明已签到，计数器+1
                count++;
            }
            // 把数字右移一位，抛弃最后一个bit位，继续下一个bit位
            num >>>= 1;
        }
        return Result.ok(count);
    }

    public User createUserWithPhone(String phone) {
        // 1.创建用户
        User user = new User();
        user.setPhone(phone);
        user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomString(10));
        // 2.保存用户
        save(user);
        return user;
    }
}
