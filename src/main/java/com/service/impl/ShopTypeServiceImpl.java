package com.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dto.Result;
import com.entity.ShopType;
import com.mapper.ShopTypeMapper;
import com.service.IShopTypeService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.utils.RedisConstants.CACHE_SHOP_TYPE;
import static com.utils.RedisConstants.CACHE_SHOP_TYPE_TTL;

@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryTypeList() {
        String shopTypeListJson = stringRedisTemplate.opsForValue().get(CACHE_SHOP_TYPE);
        if(StrUtil.isNotBlank(shopTypeListJson)) {
            List<ShopType> shopTypeList = JSONUtil.toList(shopTypeListJson, ShopType.class);
            return Result.ok(shopTypeList);
        }
        List<ShopType> shopTypeList = query().orderByAsc("sort").list();
        if (shopTypeList == null) {
            return Result.fail("商户类型表为空！");
        }
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_TYPE, JSONUtil.toJsonStr(shopTypeList));
        stringRedisTemplate.expire(CACHE_SHOP_TYPE, CACHE_SHOP_TYPE_TTL, TimeUnit.DAYS);
        return Result.ok(shopTypeList);
    }
}
