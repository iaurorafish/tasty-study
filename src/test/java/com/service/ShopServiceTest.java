package com.service;

import com.entity.Shop;
import com.service.impl.ShopServiceImpl;
import com.utils.CacheClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.utils.RedisConstants.CACHE_SHOP_KEY;
import static com.utils.RedisConstants.SHOP_GEO_KEY;

@SpringBootTest
public class ShopServiceTest {
    @Resource
    private ShopServiceImpl shopService;

    @Resource
    private CacheClient cacheClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void testSaveShop2Redis() {
        List<Shop> shops = shopService.list();
        for (Shop shop : shops) {
            cacheClient.setWithLogicalExpire(CACHE_SHOP_KEY + shop.getId(), shop, 5L, TimeUnit.SECONDS);
        }
    }

    @Test
    void testSaveShop2RedisGeo() {
        // 查询商户信息
        List<Shop> shops = shopService.list();
        // 把店铺分组，按照typeId分组，typeId一致的放到一个集合
        Map<Long, List<Shop>> map = shops.stream().collect(Collectors.groupingBy(Shop::getTypeId));
        // 分批完成写入Redis
        for (Map.Entry<Long, List<Shop>> entry : map.entrySet()) {
            // 获取类型id
            Long typeId = entry.getKey();
            // 获取同类型的店铺的集合
            List<Shop> shopByType = entry.getValue();
            List<RedisGeoCommands.GeoLocation<String>> locations = new ArrayList<>(shopByType.size());
            // 写入Redis GEOADD key 经度 维度 member
            for (Shop shop : shopByType) {
                locations.add(new RedisGeoCommands.GeoLocation(
                        shop.getId().toString(),
                        new Point(shop.getX(), shop.getY())));
            }
            stringRedisTemplate.opsForGeo().add(SHOP_GEO_KEY + typeId, locations);
        }
    }
}
