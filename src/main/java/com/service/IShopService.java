package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dto.Result;
import com.entity.Shop;

public interface IShopService extends IService<Shop> {
    Result queryById(Long id);

    Result updateShop(Shop shop);

    Result queryShopByNameByPage(String name, Integer current);

    Result queryShopByType(Integer typeId, Integer current, Double x, Double y);
}
