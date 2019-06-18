package com.hm.seckill.service;

import com.hm.seckill.dao.GoodsDao;
import com.hm.seckill.domain.Goods;
import com.hm.seckill.domain.MiaoshaOrder;
import com.hm.seckill.domain.MiaoshaUser;
import com.hm.seckill.domain.OrderInfo;
import com.hm.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods){
        // 减库存
        goodsService.reduceStock(goods);

        // 写订单,order_info  miaosha_order
        return orderService.creatOrder(user, goods);
    }
}
