package com.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Voucher implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 店铺id
     */
    private Long shopId;
    /**
     * 代金券标题
     */
    private String title;
    /**
     * 副标题
     */
    private String subTitle;
    /**
     * 使用规则
     */
    private String rules;
    /**
     * 支付金额，单位是分。例如200代表2元
     */
    private Long payValue;
    /**
     * 抵扣金额，单位是分。例如200代表2元
     */
    private Long actualValue;
    /**
     * 优惠券类型，0：普通券，1：秒杀券
     */
    private Integer type;
    /**
     * 优惠券状态，1：上架，2：下架，3：过期
     */
    private Integer status;
    /**
     * 库存（秒杀券使用）
     */
    @TableField(exist = false)
    private Integer stock;
    /**
     * 生效时间（秒杀券使用）
     */
    @TableField(exist = false)
    private LocalDateTime beginTime;
    /**
     * 失效时间
     */
    @TableField(exist = false)
    private LocalDateTime endTime;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
