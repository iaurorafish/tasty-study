package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dto.Result;
import com.entity.ShopType;

public interface IShopTypeService extends IService<ShopType> {
    Result queryTypeList();
}
