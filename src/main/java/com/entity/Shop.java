package com.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Shop implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 商铺类型的id
     */
    private Long typeId;
    /**
     * 商铺图片，多个图片以','隔开
     */
    private String images;
    /**
     * 商圈，例如陆家嘴
     */
    private String area;
    /**
     * 地址
     */
    private String address;
    /**
     * 经度
     */
    private Double x;
    /**
     * 纬度
     */
    private Double y;
    /**
     * 均价，取整数
     */
    private Long avgPrice;
    /**
     * 销量
     */
    private Integer sold;
    /**
     * 评论数量
     */
    private Integer comments;
    /**
     * 评分，1~5分，乘10保存，避免小数
     */
    private Integer score;
    /**
     * 营业时间，例如10:00-22:00
     */
    private String openHours;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 与参考坐标的距离
     */
    @TableField(exist = false)
    private Double distance;
}
