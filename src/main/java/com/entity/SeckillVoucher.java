package com.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SeckillVoucher implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 关联的优惠券id
     */
    @TableId(value = "voucher_id", type = IdType.INPUT)
    private Long voucherId;
    /**
     * 库存
     */
    private Integer stock;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 生效时间
     */
    private LocalDateTime beginTime;
    /**
     * 失效时间
     */
    private LocalDateTime endTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
