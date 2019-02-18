package com.imooc.o2o.service;

import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.exceptions.ShopOperationException;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.InputStream;

/**
 * Created by pc on 2019/2/1.
 */
public interface ShopService {
    //通过店铺ID获取店铺信息
    Shop getByShopId(long shopId);

    //更新店铺信息，包括对图片的处理
    ShopExecution modifyShop(Shop shop,InputStream shopImgInputStream,String fileName)throws ShopOperationException;

    //注册店铺，包括图片处理
    ShopExecution addShop(Shop shop,InputStream shopImgInputStream,String fileName) throws ShopOperationException;
}
