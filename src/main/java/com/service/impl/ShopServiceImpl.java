package com.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dto.Result;
import com.entity.Shop;
import com.mapper.ShopMapper;
import com.service.IShopService;
import com.utils.CacheClient;
import com.utils.RedisData;
import com.utils.SystemConstants;
import jodd.util.StringUtil;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.data.redis.domain.geo.Metrics;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.utils.RedisConstants.*;
import static com.utils.SystemConstants.DEFAULT_PAGE_SIZE;
import static com.utils.SystemConstants.MAX_PAGE_SIZE;

@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CacheClient cacheClient;

    @Override
    public Result queryById(Long id) {
        // 缓存穿透
        // Shop shop = queryWithPassThrough(id);
        // Shop shop = cacheClient.queryWithPassThrough(CACHE_SHOP_KEY, id, Shop.class, this::getById, CACHE_SHOP_TTL, TimeUnit.MINUTES);

        // 互斥锁解决缓存击穿
        // Shop shop = queryWithMutex(id);

        // 逻辑过期解决缓存击穿问题
        // Shop shop = queryWithLogicalExpire(id);
        Shop shop = cacheClient.queryWithLogicalExpire(CACHE_SHOP_KEY, id, LOCK_SHOP_KEY, Shop.class, this::getById, LOCK_SHOP_TTL, TimeUnit.SECONDS, 20L, TimeUnit.SECONDS);
        if (shop == null) {
            return Result.fail("该商户不存在");
        }
        // 7.返回
        return Result.ok(shop);
    }

    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    public Shop queryWithLogicalExpire(Long id) {
        // 1.从Redis查询商铺缓存
        String shopKey = CACHE_SHOP_KEY + id;
        String redisDataJson = stringRedisTemplate.opsForValue().get(shopKey);
        // 2.判断是否存在
        if (StrUtil.isBlank(redisDataJson)) {
            // 3.不存在，直接返回null
            return null;
        }
        // 4.命中，需要先把Json反序列化为对象
        RedisData redisData = JSONUtil.toBean(redisDataJson, RedisData.class);
        Shop shop = JSONUtil.toBean((JSONObject) redisData.getData(), Shop.class);
        LocalDateTime expireTime = redisData.getExpireTime();
        // 判断是否过期
        if (expireTime.isAfter(LocalDateTime.now())) {
            // 5.1未过期，直接返回店铺信息
            return shop;
        }
        // 5.2已过期，需要缓存重建
        // 6.缓存重建
        String lockKey = LOCK_SHOP_KEY + id;
        // 6.1获取互斥锁，判断是否获取锁成功
        if (tryLock(lockKey)) {
            // 未获取锁，直接返回
            return shop;
        }
        // 6.2获取锁成功，DoubleCheck缓存是否有该对象
        redisDataJson = stringRedisTemplate.opsForValue().get(shopKey);
        // 判断是否存在
        if (StrUtil.isBlank(redisDataJson)) {
            // 不存在，直接返回null
            return null;
        }
        CACHE_REBUILD_EXECUTOR.submit(() -> {
            try {
                // 6.3缓存重建
                this.saveShop2Redis(id, CACHE_SHOP_TTL);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                // 释放锁
                unlock(lockKey);
            }
        });
        // 6.4返回过期的商户信息
        return shop;
    }

    public Shop queryWithMutex(Long id) {
        // 1.从Redis查询商铺缓存
        String shopKey = CACHE_SHOP_KEY + id;
        String shopJson = stringRedisTemplate.opsForValue().get(shopKey);
        // 2.判断是否存在
        if (StrUtil.isNotBlank(shopJson)) {
            // 3.存在，直接返回
            return JSONUtil.toBean(shopJson, Shop.class);
        }
        // 判断命中的是否是空值
        if (shopJson != null) {
            // 返回一个错误信息
            return null;
        }
        // 4.实现缓存重建
        // 4.1获取互斥锁
        String lockKey = LOCK_SHOP_KEY + id;
        Shop shop = null;
        try {
            // 4.2判断是否获取成功
            if (!tryLock(lockKey)) {
                // 4.3失败，则休眠并重试
                Thread.sleep(50);
                return queryWithMutex(id);
            }
            // 4.4成功，DoubleCheck
            shopJson = stringRedisTemplate.opsForValue().get(shopKey);
            // 2.判断是否存在
            if (StrUtil.isNotBlank(shopJson)) {
                // 3.存在，直接返回
                return JSONUtil.toBean(shopJson, Shop.class);
            }
            // 判断命中的是否是空值
            if (shopJson != null) {
                // 返回一个错误信息
                return null;
            }
            // 根据id查询数据库
            shop = getById(id);
            // 模拟重建的延时
            Thread.sleep(200);
            // 5.不存在，返回错误
            if (shop == null) {
                // 将空值写入Redis
                stringRedisTemplate.opsForValue().set(shopKey, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
                return null;
            }
            // 6.存在，写入Redis
            stringRedisTemplate.opsForValue().set(shopKey, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 7.释放互斥锁
            unlock(lockKey);
        }
        // 8.返回
        return shop;
    }

    public Shop queryWithPassThrough(Long id) {
        // 1.从Redis查询商铺缓存
        String shopKey = CACHE_SHOP_KEY + id;
        String shopJson = stringRedisTemplate.opsForValue().get(shopKey);
        // 2.判断是否存在
        if (StrUtil.isNotBlank(shopJson)) {
            // 3.存在，直接返回
            return JSONUtil.toBean(shopJson, Shop.class);
        }
        // 判断命中的是否是空值
        if (shopJson != null) {
            // 返回一个错误信息
            return null;
        }
        // 4.不存在，根据id查询数据库
        Shop shop = getById(id);
        // 5.不存在，返回错误
        if (shop == null) {
            // 将空值写入Redis
            stringRedisTemplate.opsForValue().set(shopKey, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        // 6.存在，写入Redis
        stringRedisTemplate.opsForValue().set(shopKey, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.MINUTES);
        // 7.返回
        return shop;
    }

    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", LOCK_SHOP_TTL, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }

    public void saveShop2Redis(Long id, Long expireSeconds) throws InterruptedException {
        // 1.查询商户数据
        Shop shop = getById(id);
        Thread.sleep(200);
        // 2.封装逻辑过期时间
        RedisData redisData = new RedisData();
        redisData.setData(shop);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(expireSeconds));
        // 3.写入Redis
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, JSONUtil.toJsonStr(redisData));
        stringRedisTemplate.expire(CACHE_SHOP_KEY + id, CACHE_SHOP_TTL, TimeUnit.HOURS);
    }

    @Override
    @Transactional
    public Result updateShop(Shop shop) {
        Long id = shop.getId();
        if (id == null) {
            return Result.fail("未指定商户id！");
        }
        // 1.更新数据库
        updateById(shop);
        // 2.删除缓存
        stringRedisTemplate.delete(CACHE_SHOP_KEY + id);
        return Result.ok();
    }

    @Override
    public Result queryShopByNameByPage(String name, Integer current) {
        // 根据商户名分页查询
        Page<Shop> page = query()
                .eq(StringUtil.isNotBlank(name), "name", name)
                .page(new Page<>(current, MAX_PAGE_SIZE));
        // 返回数据
        return Result.ok(page.getRecords());
    }

    @Override
    public Result queryShopByType(Integer typeId, Integer current, Double x, Double y) {
        // 不需要坐标查询，按数据库查询
        if (x == null || y == null) {
            // 普通查询
            Page<Shop> page = query()
                    .eq("type_id", typeId)
                    .page(new Page<>(current, DEFAULT_PAGE_SIZE));
            return Result.ok(page.getRecords());
        }

        // 计算分页参数
        int from = (current - 1) * DEFAULT_PAGE_SIZE;
        int end = current * DEFAULT_PAGE_SIZE;
        // 查询Redis，按照距离排序、分页，结果：shopId、distance
        // GEOSEARCH key [FROMLONLAT longitude latitude] [BYRADIUS radius m|km|ft|mi] [ASC|DESC] [COUNT count [ANY]] [WITHDIST]
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = stringRedisTemplate.opsForGeo().search(
                SHOP_GEO_KEY + typeId,
                GeoReference.fromCoordinate(x, y),
                new Distance(100000),
                RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().limit(end)
        );
        // 解析出id
        if (results == null) {
            return Result.ok();
        }
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> list = results.getContent();
        if (list.size() == 0) {
            return Result.ok();
        }
        // 截取from ~ end
        if (list.size() <= from) {
            return Result.ok();
        }
        Map<String, Distance> distanceMap = new HashMap<>(DEFAULT_PAGE_SIZE);
        List<Long> ids = new ArrayList<>(DEFAULT_PAGE_SIZE);
        list.stream().skip(from).forEach(result -> {
            // 获取店铺id
            String idStr = result.getContent().getName();
            ids.add(Long.valueOf(idStr));
            // 获取距离
            Distance distance = result.getDistance();
            distanceMap.put(idStr, distance);
        });
        // 根据id查询shop
        String idStr = StrUtil.join(",", ids);
        List<Shop> shops = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();
        for (Shop shop : shops) {
            shop.setDistance(distanceMap.get(shop.getId().toString()).getValue());
        }
        // 返回数据
        return Result.ok(shops);
    }
}
