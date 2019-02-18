package com.imooc.o2o.service;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopCategory;
import com.imooc.o2o.enums.ShopStateEnum;
import com.imooc.o2o.exceptions.ShopOperationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by pc on 2019/2/1.
 */
public class ShopServiceTest extends BaseTest {
    @Autowired
    private ShopService shopService;

    @Test
    public void testModifyShop() throws ShopOperationException,FileNotFoundException{
        Shop shop =new Shop();
        shop.setShopId(37L);
        shop.setShopName("修改名称后的店铺 名称");
        File shopImg=new File("C:\\Users\\pc\\Desktop\\o2oImg\\a.jpg");
        InputStream is=new FileInputStream(shopImg);
        ShopExecution shopExecution = shopService.modifyShop(shop,is,"a.jpg");
        System.out.println("新的图片地址为：" + shopExecution.getShop().getShopImg());
    }

    @Test
    public void testAddShop() throws ShopOperationException,FileNotFoundException {
        Shop shop=new Shop();
        PersonInfo owner = new PersonInfo();
        Area area=new Area();
        ShopCategory shopCategory = new ShopCategory();

        //设置关联ID
        owner.setUserId(1L);
        area.setAreaId(1);
        shopCategory.setShopCategoryId(2L);

        shop.setOwner(owner);
        shop.setArea(area);
        shop.setShopCategory(shopCategory);

        shop.setShopName("service");
        shop.setShopDesc("service");
        shop.setShopAddr("service");
        shop.setPhone("service");
        shop.setCreateTime(new Date());
        shop.setEnableStatus(ShopStateEnum.CHECK.getState());
        shop.setAdvice("service");

        File shopImg=new File("C:\\Users\\pc\\Desktop\\o2oImg\\test.jpg");
        FileInputStream is = new FileInputStream(shopImg);
        ShopExecution se = shopService.addShop(shop, is,shopImg.getName());
        assertEquals(ShopStateEnum.CHECK.getState(),se.getState());
    }
}
