package com.sky.service;

/**
 * @author 周超
 * @version 1.0
 */
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ShoppingCartService {

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);


    /**
     * 查看购物车
     * @return
     */
    List<ShoppingCart> showShoppingCart();


    /**
     * 清空购物车商品
     */
    void cleanShoppingCart();
}
