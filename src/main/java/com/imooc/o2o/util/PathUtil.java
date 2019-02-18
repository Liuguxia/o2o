package com.imooc.o2o.util;

/**
 * Created by pc on 2019/1/31.
 */
public class PathUtil {
    private static String seperator=System.getProperty("file.separator");
    public static String getImgBasePath(){
        String os=System.getProperty("os.name");
        String basePath="";
        if(os.toLowerCase().startsWith("win")){
            basePath="C:/Users/pc/Desktop/o2oImg/";
        }else {
            basePath="/home/xiangze/image/";
        }
        basePath=basePath.replace("/",seperator);
        return basePath;
    }

    public static String getShopImagePath(long shopId){
        String imagePath="upload/item/shop"+shopId+"/";
        return imagePath.replace("/",seperator);
    }
}
