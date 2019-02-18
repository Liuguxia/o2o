package com.imooc.o2o.dao;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopCategory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by pc on 2019/1/29.
 */
public class ShopDaoTest extends BaseTest{
    @Autowired
    private ShopDao shopDao;

    @Test   //通过shop id查询店铺
    public void testQueryByShopId(){
        long shopId=36;
        Shop shop = shopDao.queryByShopId(shopId);
        System.out.println("shop : "+shop);
        System.out.println("areaId : "+shop.getArea().getAreaId());
        System.out.println("areaName : "+shop.getArea().getAreaName());
        //userId是要使用session的
        //System.out.println("userId : "+shop.getOwner().getUserId());
        System.out.println("name : "+shop.getShopCategory().getShopCategoryName());
    }
    @Test
    public void testInsertShop(){
        Shop shop=new Shop();
        Area area=new Area();
        PersonInfo owner = new PersonInfo();
        ShopCategory shopCategory = new ShopCategory();

        //设置关联ID
        owner.setUserId(1L);
        area.setAreaId(3);
        shopCategory.setShopCategoryId(2L);

        shop.setOwner(owner);
        shop.setArea(area);
        shop.setShopCategory(shopCategory);

        shop.setShopName("back");
        shop.setShopDesc("back");
        shop.setShopAddr("back");
        shop.setShopImg("back");
        shop.setPhone("back");
        shop.setCreateTime(new Date());
        shop.setEnableStatus(1);
        shop.setAdvice("审核中");

        int effectdNum=shopDao.insertShop(shop);
        assertEquals(1,effectdNum);
    }

    //更新店铺测试
    @Test
    public void testUpdateShop(){
        Shop shop=new Shop();
        shop.setShopId(1L);
//        PersonInfo owner = new PersonInfo();
//        owner.setUserId(10L);
//        shop.setOwner(owner);
//        shop.setShopName("孤");
        shop.setShopDesc("一支穿云箭");
        shop.setShopAddr("千军万马来相见");
        shop.setLastEditTime(new Date());
        int effectedNum = shopDao.updateShop(shop);
        assertEquals(1,effectedNum);
    }
}
