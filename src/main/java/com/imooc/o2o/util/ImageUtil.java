package com.imooc.o2o.util;

import com.imooc.o2o.web.superadmin.AreaController;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by pc on 2019/1/30.
 */
public class ImageUtil {
    //日志
    static Logger logger= LoggerFactory.getLogger(AreaController.class);

    private static String basePath=Thread.currentThread().getContextClassLoader().getResource("").getPath();
    private static final SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
    private static final Random r = new Random();
    //原码
//    public static String generateThumbnail(CommonsMultipartFile thumbnail,String targetAddr){
//        //三个函数
//        String realFileName=getRandomFileName();
//        String extension=getFileExtension(thumbnail);
//        makeDirPath(targetAddr);
//        String relativeAddr=targetAddr+realFileName+extension;
//        File dest=new File(PathUtil.getImgBasePath()+relativeAddr);
//        try{
//            Thumbnails.of(thumbnail.getInputStream()).size(1578, 1578)
//                    .watermark(Positions.BOTTOM_RIGHT,ImageIO.read(new File(basePath+"/watermark.jpg")),0.25f)
//                    .outputQuality(0.8f).toFile(dest);
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//        return relativeAddr;
//    }

    //处理缩略图片，并返回生成图片的相对值路径
    public static String generateThumbnail(InputStream thumbnailInputStream,String fileName, String targetAddr){
        //三个函数
        String realFileName=getRandomFileName();
        String extension=getFileExtension(fileName);
        makeDirPath(targetAddr);
        String relativeAddr=targetAddr+realFileName+extension;

        logger.debug("当前路径 current relativeAddr is:  "+relativeAddr);
        File dest=new File(PathUtil.getImgBasePath()+relativeAddr);
        logger.debug("当前完整路径 current completeAddr is:  "+PathUtil.getImgBasePath()+relativeAddr);

        try{
            Thumbnails.of(thumbnailInputStream).size(1578, 1578)
                    .watermark(Positions.BOTTOM_RIGHT,ImageIO.read(new File(basePath+"/watermark.jpg")),0.25f)
                    .outputQuality(0.8f).toFile(dest);
        }catch (IOException e){
            e.printStackTrace();
        }
        return relativeAddr;
    }
    /*
        创建目标路径所涉及到的目录，即/home/work/xiangze/xxx.jpg,
        那么home work xiangze这三个目录都会字都自动创建
     */
    private static void makeDirPath(String targetAddr){
        String realFileParentPath=PathUtil.getImgBasePath()+targetAddr;
        File dirPath=new File(realFileParentPath);
        if (!dirPath.exists()){
            dirPath.mkdirs();
        }
    }
    /*
        获取输入文件流的扩展名
     */
    private static String getFileExtension(String fileName){
        //String originalFileName=cFile.getName();
        return fileName.substring(fileName.lastIndexOf("."));
    }
    /*
        生成随机文件名，当前年月日小时分钟秒钟+5位随机数
     */
    public static String getRandomFileName(){
        //获取随机五位数
        int rannum=r.nextInt(89999)+10000;
        String nowTimeStr=sDateFormat.format(new Date());
        return nowTimeStr+rannum;
    }
    public static void main(String[] args) throws IOException {
        /**
         * watermark(位置，水印图，透明度)
         */

        //String path = System.getProperty("user.dir")+"/src/main/resources/";
        //System.out.println(path);
        Thumbnails.of(new File("C:\\Users\\pc\\Desktop\\o2oImg\\xiaohuangren.jpg")).size(1578, 1578)
                .watermark(Positions.BOTTOM_RIGHT,ImageIO.read(new File(basePath+ "/watermark.jpg")),0.25f)
                .outputQuality(0.8f).toFile("C:\\Users\\pc\\Desktop\\o2oImg\\xiaohuangrennew.jpg");


    }

    //删除图片,storePath是文件的路径还是目录的路径，
    //如果storePath是文件路径则删除该文件
    //如果storePath是目录路径就删除该目录下的所有文件
    public static void deleteFileOrPath(String storePath){
        File fileOrPath=new File(PathUtil.getImgBasePath()+storePath);
        if (fileOrPath.exists()){
            if (fileOrPath.isDirectory()){
                //如果是目录
                File files[]=fileOrPath.listFiles();
                for (int i=0;i<files.length;i++){
                    files[i].delete();
                }
            }
            //如果是文件
            fileOrPath.delete();
        }
    }
}
