package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 周超
 * @version 1.0
 */

@Mapper
public interface SetmealDishMapper
{
    //根据菜品id来查询套餐id，菜品表和套餐表是多对多关系，可能会查出来多个套餐,所以用List封装
    List<Long> getSetmealIdsByDishids(List<Long> dishIds);

}
