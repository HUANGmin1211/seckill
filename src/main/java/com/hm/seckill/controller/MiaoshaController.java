package com.hm.seckill.controller;

import com.hm.seckill.domain.Goods;
import com.hm.seckill.domain.MiaoshaOrder;
import com.hm.seckill.domain.MiaoshaUser;
import com.hm.seckill.domain.OrderInfo;
import com.hm.seckill.result.CodeMsg;
import com.hm.seckill.result.Result;
import com.hm.seckill.service.GoodsService;
import com.hm.seckill.service.MiaoshaService;
import com.hm.seckill.service.OrderService;
import com.hm.seckill.vo.GoodsVo;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {

    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    MiaoshaService miaoshaService;

    /**
     * QPS:1306
     * 5000 * 10
     * */
    @RequestMapping(value = "/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<OrderInfo> miaosha(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId){
        model.addAttribute("user", user);

        if (user == null)
            return Result.error(CodeMsg.SESSION_ERROR);

        // 判断商品库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock <= 0){
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }

        // 判断是否已经秒杀到了，不能重复下单
        // 但是如果同一个用户同时发出了两个请求线程，都还没有秒杀开始，这里会判断并没有秒杀，然后两个线程都进入了秒杀开始，导致重复秒杀
        // 通过在miaoshaOrder数据表建立userId和goods_id的唯一索引来保证用户不会重复秒杀，见数据库表
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goods.getId());
        if (order != null){
            return Result.error(CodeMsg.MIAOSHA_REPEAT);
        }

        // 秒杀开始：减库存、写入秒杀订单【在事务中】
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
        return Result.success(orderInfo);
    }
}
