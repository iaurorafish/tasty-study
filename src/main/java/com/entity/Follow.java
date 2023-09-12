package com.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Follow implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 关联的用户id
     */
    private Long followUserId;
    /**
     * 关联时间
     */
    private LocalDateTime createTime;
}
