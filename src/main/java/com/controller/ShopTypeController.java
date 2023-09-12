package com.controller;

import com.dto.Result;
import com.entity.ShopType;
import com.service.IShopTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/shop-type")
public class ShopTypeController {
    @Resource
    private IShopTypeService shopTypeService;

    /**
     * 查询商户类型列表
     * @return
     */
    @GetMapping("/list")
    public Result queryTypeList() {
        return shopTypeService.queryTypeList();
    }

}
