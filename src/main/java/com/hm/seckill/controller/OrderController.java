package com.hm.seckill.controller;

import com.hm.seckill.domain.Goods;
import com.hm.seckill.domain.MiaoshaUser;
import com.hm.seckill.domain.OrderInfo;
import com.hm.seckill.redis.RedisService;
import com.hm.seckill.result.CodeMsg;
import com.hm.seckill.result.Result;
import com.hm.seckill.service.GoodsService;
import com.hm.seckill.service.MiaoshaUserService;
import com.hm.seckill.service.OrderService;
import com.hm.seckill.vo.GoodsVo;
import com.hm.seckill.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

//    @NeedLogin  // 每次判断用户是否为空都很麻烦，我们可以构造一个拦截器，是user不为空

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model, MiaoshaUser user,
                                      @RequestParam("orderId") long orderId) {
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo order = orderService.getOrderById(orderId);
        if(order == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setOrder(order);
        vo.setGoods(goods);
        return Result.success(vo);
    }

}

