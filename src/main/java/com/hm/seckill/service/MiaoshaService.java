package com.hm.seckill.service;

import com.hm.seckill.dao.GoodsDao;
import com.hm.seckill.domain.Goods;
import com.hm.seckill.domain.MiaoshaOrder;
import com.hm.seckill.domain.MiaoshaUser;
import com.hm.seckill.domain.OrderInfo;
import com.hm.seckill.redis.MiaoshaKey;
import com.hm.seckill.redis.RedisService;
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
    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods){
        // 减库存,下订单，写入秒杀订单
        boolean success = goodsService.reduceStock(goods);

        if (success){
            // 写订单,order_info  miaosha_order
            return orderService.creatOrder(user, goods);
        }else{
            setGoodsOver(goods.getId());
            return null;
        }
    }

    public long getMiaoshaResult(Long userId, long goodsId) {
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId,goodsId);
        if (order != null)  // 秒杀成功
            return order.getOrderId();
        else{
            boolean isOver = getGoodsOver(goodsId);
            if (isOver){
                return -1;  // 秒杀失败
            }else{
                return 0;   // 排队中
            }
        }

    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver,""+goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MiaoshaKey.isGoodsOver,""+goodsId);
    }
}
