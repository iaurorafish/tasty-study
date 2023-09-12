package com.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import com.dto.UserDTO;
import com.entity.User;
import com.service.impl.UserServiceImpl;
import lombok.Cleanup;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.utils.RedisConstants.LOGIN_TOKEN_KEY;
import static com.utils.RedisConstants.LOGIN_TOKEN_TTL;

@SpringBootTest
public class UserServiceTest {
    @Resource
    private UserServiceImpl userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 在MySQL中创建1000个用户信息
     */
    @Test
    void testAddUsers() {
        for (long i = 13688668000L; i < 13688669000L; i++) {
            // 创建用户
            String phone = String.valueOf(i);
            User user = userService.createUserWithPhone(phone);
        }
    }

    @Test
    void testLogin() {
        User user = userService.query().eq("phone", "13456789001").one();
        // 随机生成token，作为登录令牌
        String token = UUID.randomUUID().toString(true);
        // 将User对象转为HashMap存储
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        // 存储
        String tokenKey = LOGIN_TOKEN_KEY + token;
        stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
        // 设置token有效期
        stringRedisTemplate.expire(tokenKey, LOGIN_TOKEN_TTL, TimeUnit.MINUTES);
    }

    /**
     * 在Redis中保存1000个用户信息并将其token写入文件中，方便测试多人秒杀业务
     */
    @Test
    void testMultiLogin() throws IOException {
        List<User> userList = userService.query().last("limit 1000").list();
        for (User user : userList) {
            // 随机生成token，作为登录令牌
            String token = UUID.randomUUID().toString(true);
            // 将User对象转为HashMap存储
            UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
            Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                    CopyOptions.create()
                            .setIgnoreNullValue(true)
                            .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
            // 存储
            String tokenKey = LOGIN_TOKEN_KEY + token;
            stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
            // 设置token有效期
            stringRedisTemplate.expire(tokenKey, LOGIN_TOKEN_TTL, TimeUnit.MINUTES);
        }
        // 读取所有键
        Set<String> keys = stringRedisTemplate.keys(LOGIN_TOKEN_KEY + "*");
        @Cleanup FileWriter fileWriter = new FileWriter(System.getProperty("user.dir") + "\\tokens.txt", false);
        @Cleanup BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        assert keys != null;
        for (String key : keys) {
            String token = key.substring(LOGIN_TOKEN_KEY.length());
            String text = token + "\n";
            bufferedWriter.write(text);
        }
    }
}
