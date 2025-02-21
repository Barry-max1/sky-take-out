package com.sky.service.impl;

/**
 * @author 周超
 * @version 1.0
 */

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 用户下单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //1.在处理业务操作之前，先进行所有可能遇到的异常情况的处理（比如收货地址为空、超出配送范围、购物车为空）
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            //抛出业务异常
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }


        //查询当前用户的购物车数据
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //构造订单数据3 = {ShoppingCart@14598} "ShoppingCart(id=14, name=鮰鱼2斤, userId=4, dishId=67, setmealId=null, dishFlavor=重辣, number=1, amount=72.00, image=https://web-tlias123456789123.oss-cn-hangzhou.aliyuncs.com/ea6ace1a-5fc3-49ba-86af-bcbabe35e9b4.png, createTime=2025-02-19T20:39:05)"
        Orders order = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,order);
        order.setOrderTime(LocalDateTime.now());
        order.setPayStatus(Orders.UN_PAID);
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setNumber(String.valueOf(System.currentTimeMillis()));
        order.setPhone(addressBook.getPhone());
        order.setConsignee(addressBook.getConsignee());
        order.setUserId(userId);
        order.setAddress(addressBook.getDetail());

        //2.向订单表插入1条数据
        orderMapper.insert(order);
        //返回订单表的主键值的原因：因为会在订单明细表中使用到订单表的主键值，我们需要这个字段


        //订单明细数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart cart : shoppingCartList) {  //一个OrderDetail对象就是数据库表order_detail中的一条数据
            //一条购物车数据，对应的就要封装成一个OrderDetail对象，把ShoppingCart对象，封装成OrderDetail对象
            OrderDetail orderDetail = new OrderDetail();//订单明细对象
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(order.getId()); //设置当前订单明细相关联的订单id
            orderDetailList.add(orderDetail);
        }

        //3.向订单明细表中批量插入n条数据
        orderDetailMapper.insertBatch(orderDetailList);


        //4.下单成功后，清理购物车中的数据
        shoppingCartMapper.deleteByUserId(userId);

        //5.封装VO对象返回结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(order.getId())
                .orderNumber(order.getNumber())
                .orderAmount(order.getAmount())
                .orderTime(order.getOrderTime())
                .build();

        return orderSubmitVO;
    }
}





















