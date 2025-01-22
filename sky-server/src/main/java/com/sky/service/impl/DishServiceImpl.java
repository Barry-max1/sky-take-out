package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Employee;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
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

    @Autowired
    private SetmealDishMapper setmealDishMapper;

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

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO)
    {
        //开始分页查询
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        //调用Mapper层，执行sql语句
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        //在page对象里，得到总记录数和当前页数据集合
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids)
    {
        //判断当前菜品是否能够删除——>当前菜品的状态是否为启售？
        //遍历ids，查询ids中是否有id的状态为启售，只要有一个id的状态为启售，那么就整个抛一个异常：不允许删除
        for (Long id : ids) {
            Dish dish = dishMapper.getByid(id);
            if (dish.getStatus() == StatusConstant.ENABLE)
            {
                //当前菜品处于启售状态，不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
                //传入一个message，就是提示信息，最终提示信息会给到前端由其展示出来
            }
        }

        //判断当前菜品是否能够删除——>当前菜品是否被已有的套餐所关联？
        //在setmeal_dish表中，根据菜品dish_id来查询套餐setmeal_id,如果能查出来，说明此
        //菜品已经被套餐关联，不允许删除
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishids(ids);
        //接下来判断，只要有一个菜品被套餐关联，就都不能删除
        if (setmealIds != null && setmealIds.size() > 0)
        {
            //有菜品被套餐关联了，不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //到达这一步，表示可以删除当前菜品
        for (Long id : ids) {
            dishMapper.deleteByid(id);
            //然后在删除当前菜品关联的口味数据
            dishFlavorMapper.deleteByDishId(id);
        }
    }


}





