package com.imooc.o2o.service.impl;

import com.imooc.o2o.dao.ShopDao;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.enums.ShopStateEnum;
import com.imooc.o2o.exceptions.ShopOperationException;
import com.imooc.o2o.service.ShopService;
import com.imooc.o2o.util.ImageUtil;
import com.imooc.o2o.util.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.InputStream;
import java.util.Date;

/**
 * Created by pc on 2019/2/1.
 */
@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopDao shopDao;

    //根据id查询店铺
    @Override
    public Shop getByShopId(long shopId) {
        return shopDao.queryByShopId(shopId);
    }

    //修改店铺信息
    @Override
    public ShopExecution modifyShop(Shop shop, InputStream shopImgInputStream, String fileName) throws ShopOperationException {
        System.out.println("111");
        if (shop==null||shop.getShopId()==null){
            System.out.println("shop 为空");
            return new ShopExecution(ShopStateEnum.NULL_SHOP);
        }else {
            try{
            //1.判断是否需要处理图片
            if (shopImgInputStream!=null&&fileName!=null&&!"".equals(fileName)){
                Shop tempShop=shopDao.queryByShopId(shop.getShopId());
                if (tempShop.getShopImg()!=null){
                    ImageUtil.deleteFileOrPath(tempShop.getShopImg());
                }
                addShopImg(shop,shopImgInputStream,fileName);
            }
            //2.更新店铺信息
            System.out.println("开始了吗");
            shop.setLastEditTime(new Date());
            int effectedNum=shopDao.updateShop(shop);
            if (effectedNum<=0){
                return new ShopExecution(ShopStateEnum.INNER_ERROR);
            }else {
                System.out.println("查询");
                shop=shopDao.queryByShopId(shop.getShopId());
                System.out.println("成功");
                return new ShopExecution(ShopStateEnum.SUCCESS,shop);
            }}catch (Exception e){
                throw new ShopOperationException("modifyShop error:"+e.getMessage());
            }
        }
    }

    //店铺注册，也就是添加
    @Override
    @Transactional
    public ShopExecution addShop(Shop shop, InputStream shopImgInputStream, String fileName) throws ShopOperationException{
        //1.空值判断
        if (shop==null){
            return new ShopExecution(ShopStateEnum.NULL_SHOP);
        }
        try {
            //2.给店铺信息初始化一些数据
            shop.setEnableStatus(0);
            shop.setCreateTime(new Date());
            shop.setLastEditTime(new Date());
            //3.添加店铺信息
            System.out.println("开始   添加");
            int effectedNum=shopDao.insertShop(shop);
            System.out.println("添加数量effectedNum："+effectedNum);
            if (effectedNum<=0){
                //RuntimeException，失败时，事务才会回滚
                throw new ShopOperationException("店铺创建失败");
            }else {
                if (shopImgInputStream !=null){
                    //存储图片
                    try{
                        addShopImg(shop, shopImgInputStream,fileName);
                    }catch (Exception e){
                        throw new ShopOperationException("addShopImg error "+ e.getMessage());
                    }
                    //更新店铺的图片地址
                    effectedNum=shopDao.updateShop(shop);
                    if (effectedNum<=0){
                        throw new ShopOperationException("更新店铺的图片地址  失败");
                    }
                }
            }
        }catch (Exception e){
            throw new ShopOperationException("addShop error  啊"+e.getMessage());
        }
        return new ShopExecution(ShopStateEnum.CHECK,shop);
    }

    private void addShopImg(Shop shop,InputStream shopImgInputStream,String fileName ) throws Exception {
        //获取shop图片目录的相对值路径
        String dest= PathUtil.getShopImagePath(shop.getShopId());
        String shopImgAddr= ImageUtil.generateThumbnail(shopImgInputStream,fileName,dest);
        shop.setShopImg(shopImgAddr);
    }
}
