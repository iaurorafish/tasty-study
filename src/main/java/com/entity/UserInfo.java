package com.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键，用户id
     */
    @TableId("user_id")
    private Long userId;
    /**
     * 城市名称
     */
    private String city;
    /**
     * 个人介绍，不要超过128个字符
     */
    private String introduce;
    /**
     * 粉丝数量
     */
    private Integer fans;
    /**
     * 关注的人的数量
     */
    private Integer followee;
    /**
     * 性别，0：男，1：女
     */
    private Integer gender;
    /**
     * 生日
     */
    private LocalDate birthday;
    /**
     * 积分
     */
    private Integer credits;
    /**
     * 会员级别，0~9级，0代表未开通会员
     */
    private Integer level;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
