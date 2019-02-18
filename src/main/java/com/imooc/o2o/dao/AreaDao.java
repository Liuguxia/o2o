package com.imooc.o2o.dao;

import com.imooc.o2o.entity.Area;

import java.util.List;

/**
 * Created by pc on 2019/1/28.
 */
public interface AreaDao {
    //验证dao层,列出区域列表
    List<Area> queryArea();
}
