package com.imooc.o2o.web.shopadmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopCategory;
import com.imooc.o2o.enums.ShopStateEnum;
import com.imooc.o2o.exceptions.ShopOperationException;
import com.imooc.o2o.service.AreaService;
import com.imooc.o2o.service.ShopCategoryService;
import com.imooc.o2o.service.ShopService;
import com.imooc.o2o.util.CodeUtil;
import com.imooc.o2o.util.HttpServletRequestUtil;
import com.imooc.o2o.util.ImageUtil;
import com.imooc.o2o.util.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pc on 2019/2/2.
 */
@Controller
@RequestMapping("/shopadmin")
public class ShopManagementController {
    @Autowired
    private ShopService shopService;
    @Autowired
    private ShopCategoryService shopCategoryService;
    @Autowired
    private AreaService areaService;

    //根据id查询店铺
    @RequestMapping(value = "/getshopbyid",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> getShopById(HttpServletRequest request){
        Map<String,Object> modelMap=new HashMap<String,Object>();
        Long shopId=HttpServletRequestUtil.getLong(request,"shopId");
        if (shopId>-1){
            try {
                Shop shop=shopService.getByShopId(shopId);
                List<Area> areaList=areaService.getAreaList();
                modelMap.put("shop",shop);
                modelMap.put("areaList",areaList);
                modelMap.put("success",true);
            } catch (Exception e) {
                modelMap.put("success",false);
                modelMap.put("errMsg",e.toString());
            }
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","-----empty shopId-------");
        }
        return modelMap;
    }
    //获取店铺类别，区域信息并返回给前台
    @RequestMapping(value = "/getshopinitinfo",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> getShopinitInfo(){
        Map<String,Object> modelMap=new HashMap<String,Object>();
        List<ShopCategory> shopCategoryList=new ArrayList<ShopCategory>();
        List<Area> areaList=new ArrayList<Area>();
        try {
            shopCategoryList=shopCategoryService.getShopCategoryList(new ShopCategory());
            areaList=areaService.getAreaList();
            modelMap.put("shopCategoryList",shopCategoryList);
            modelMap.put("areaList",areaList);
            modelMap.put("success",true);
        } catch (Exception e) {
            modelMap.put("success",false);
            modelMap.put("errMsg",e.getMessage());
        }
        return modelMap;
    }

    //店铺注册
    @ResponseBody
    @RequestMapping(value = "/registershop",method = RequestMethod.POST)
    private Map<String,Object> registerShop(HttpServletRequest request){
        Map<String,Object> modelMap=new HashMap<String,Object>();
        //比较验证码
        if(!CodeUtil.checkVerifyCode(request)){
            modelMap.put("success",false);
            modelMap.put("errMsg","输入验证码错误");
            return modelMap;
        }
        //1.接收并转化相应的参数，包括店铺信息以及图片信息
        String shopStr= HttpServletRequestUtil.getString(request,"shopStr");
        ObjectMapper mapper=new ObjectMapper();
        Shop shop=null;
        try{
            shop = mapper.readValue(shopStr,Shop.class);
        } catch (Exception e) {
            modelMap.put("success",false);
            modelMap.put("registerShop errMsg错误 ",e.getMessage());
            return  modelMap;
        }
        CommonsMultipartFile shopImg =null;
        CommonsMultipartResolver commonsMultipartResolver=new CommonsMultipartResolver(request.getSession().getServletContext());
        if (commonsMultipartResolver.isMultipart(request)){
            MultipartHttpServletRequest multipartHttpServletRequest=
                    (MultipartHttpServletRequest) request;
            shopImg= (CommonsMultipartFile) multipartHttpServletRequest.getFile("shopImg");
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","上传图片不能为空");
            return modelMap;
        }
        //2.注册店铺
        if (shop!=null&&shopImg!=null){
            //问题出自这里，userId没有
            PersonInfo owner = (PersonInfo) request.getSession().getAttribute("user");
            shop.setOwner(owner);
            ShopExecution se;
            try {
                se = shopService.addShop(shop, shopImg.getInputStream(),shopImg.getOriginalFilename());
                if (se.getState()== ShopStateEnum.CHECK.getState()){
                    modelMap.put("success",true);
                    //该用户可以操作的店铺列表
                    List<Shop> shopList= (List<Shop>) request.getSession().getAttribute("shopList");
                    if (shopList==null||shopList.size()==0){
                        //第一次创建店铺
                        shopList=new ArrayList<Shop>();
                    }
                    shopList.add(se.getShop());
                    request.getSession().setAttribute("shopList",shopList);
                }else {
                    modelMap.put("success",false);
                    modelMap.put("errMsg","se.getStateInfo()"+se.getStateInfo());
                }
            }catch (ShopOperationException e) {
                modelMap.put("success",false);
                modelMap.put("errMsg",e.getMessage());
            } catch (IOException e) {
                modelMap.put("success",false);
                modelMap.put("errMsg",e.getMessage());
            }
            //3.返回结果
            return modelMap;
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","请输入店铺信息");
            return modelMap;
        }
    }

    //修改店铺信息
    @ResponseBody
    @RequestMapping(value = "/modifyshop",method = RequestMethod.POST)
    private Map<String,Object> modifyShop(HttpServletRequest request){
        Map<String,Object> modelMap=new HashMap<String,Object>();
        System.out.println("比较验证码");
        //比较验证码
        if(!CodeUtil.checkVerifyCode(request)){
            modelMap.put("success",false);
            modelMap.put("errMsg","输入验证码错误");
            return modelMap;
        }
        //1.接收并转化相应的参数，包括店铺信息以及图片信息
        String shopStr= HttpServletRequestUtil.getString(request,"shopStr");
        ObjectMapper mapper=new ObjectMapper();
        Shop shop=null;
        try {
            shop=mapper.readValue(shopStr,Shop.class);
        } catch (Exception e) {
            modelMap.put("success",false);
            modelMap.put("modifyShop 的 errMsg",e.getMessage());
            return  modelMap;
        }
        CommonsMultipartFile shopImg =null;
        CommonsMultipartResolver commonsMultipartResolver=new CommonsMultipartResolver(request.getSession().getServletContext());
        if (commonsMultipartResolver.isMultipart(request)){
            MultipartHttpServletRequest multipartHttpServletRequest=
                    (MultipartHttpServletRequest) request;
            shopImg= (CommonsMultipartFile) multipartHttpServletRequest.getFile("shopImg");
        }
        //2.修改店铺信息
        if (shop!=null&&shop.getShopId()!=null){
            ShopExecution se;
            try {
                if (shopImg==null){
                    System.out.println("开始修改1");
                    se = shopService.modifyShop(shop, null,null);
                }else {
                    System.out.println("开始修改2");
                    se = shopService.modifyShop(shop, shopImg.getInputStream(),shopImg.getOriginalFilename());
                }
                if (se.getState()== ShopStateEnum.SUCCESS.getState()){
                    modelMap.put("success",true);
                }else {
                    modelMap.put("success",false);
                    modelMap.put("errMsg","se.getStateInfo()"+se.getStateInfo());
                }
            }catch (ShopOperationException e) {
                modelMap.put("success",false);
                modelMap.put("errMsg",e.getMessage());
            } catch (IOException e) {
                modelMap.put("success",false);
                modelMap.put("errMsg",e.getMessage());
            }
            //3.返回结果
            return modelMap;
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","请输入店铺Id");
            return modelMap;
        }
    }

    //转
//    private static void inputStreamToFile(InputStream ins, File file){
//        FileOutputStream os=null;
//        try {
//            os=new FileOutputStream(file);
//            int bytesRead=0;
//            byte[] buffer=new byte[1024];
//            while ((bytesRead=ins.read(buffer))!=-1){
//                os.write(buffer,0,bytesRead);
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("调用inputStreamToFile 方法产生异常"+e.getMessage());
//        }finally {
//            try{
//                if (os!=null){
//                    os.close();
//                }
//                if (ins!=null){
//                    ins.close();
//                }
//                }catch (IOException e){
//                throw new RuntimeException("inputStreamToFile 方法  关闭IO 产生异常"+e.getMessage());
//            }
//        }
//    }
}
