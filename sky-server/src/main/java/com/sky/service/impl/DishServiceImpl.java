package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 周超
 * @version 1.0
 */

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品和对应的口味:在新增菜品的时候，可能会有这个菜品的多种口味，这就涉及到了两张表的操作，
     * 涉及到多张数据表的操作，需要保证数据的一致性，所以需要事务来处理
     * @param dishDTO
     */
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO)
    {
        Dish dish = new Dish();
        //属性拷贝
        BeanUtils.copyProperties(dishDTO, dish);

        //向菜品表插入一条数据
        dishMapper.insert(dish);

        //获取insert语句生成的主键值,直接获取获取不到，要在新增菜品的insert语句中添加配置，当insert语句执行
        //完毕后，会自动返回id这个主键值
        Long dishId = dish.getId();

        //先得到口味的字段进行判断
        List<DishFlavor> flavors = dishDTO.getFlavors();

        //判断前端传过来的数据flavors集合中是否存在数据，因为口味并不是必须要添加的
        if (flavors != null && flavors.size() > 0) //说明有口味数据的提交
        {
            //先遍历，给dish_flavor表中特殊的字段dish_id赋值
            flavors.forEach(dishFlavor ->{
                dishFlavor.setDishId(dishId);
            });

            //向口味表插入n条数据
            //批量插入前端传过来的flavors数据，直接把集合对象传进去
            dishFlavorMapper.insertBatch(flavors);

        }

    }

}





