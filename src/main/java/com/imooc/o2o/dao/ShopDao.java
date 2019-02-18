package com.imooc.o2o.dao;

import com.imooc.o2o.entity.Shop;

/**
 * Created by pc on 2019/1/29.
 */
public interface ShopDao {
    //通过shop id查询店铺
    Shop queryByShopId(long shopId);

    //新增店铺
    int insertShop(Shop shop);

    //更新店铺
    int updateShop(Shop shop);
}
