package com.hm.seckill.controller;

import com.hm.seckill.domain.Goods;
import com.hm.seckill.domain.MiaoshaOrder;
import com.hm.seckill.domain.MiaoshaUser;
import com.hm.seckill.domain.OrderInfo;
import com.hm.seckill.rabbitmq.MQSender;
import com.hm.seckill.rabbitmq.MiaoshaMessage;
import com.hm.seckill.redis.GoodsKey;
import com.hm.seckill.redis.RedisService;
import com.hm.seckill.result.CodeMsg;
import com.hm.seckill.result.Result;
import com.hm.seckill.service.GoodsService;
import com.hm.seckill.service.MiaoshaService;
import com.hm.seckill.service.OrderService;
import com.hm.seckill.vo.GoodsVo;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    MiaoshaService miaoshaService;
    @Autowired
    RedisService redisService;
    @Autowired
    MQSender mqSender;

    private HashMap<Long,Boolean> localOverMap = new HashMap<>();

    /**
     * 继承了InitializingBean接口，系统初始化就会调用这个方法
     * 将所有商品的库存都加载到缓存中去
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if (goodsList == null)
            return;

        for (GoodsVo goodsVo : goodsList){
            redisService.set(GoodsKey.getMiaoshaGoodsStock,""+goodsVo.getId(), goodsVo.getStockCount());
            localOverMap.put(goodsVo.getId(),false);
        }
    }

    /**
     * QPS:1306
     * 5000 * 10
     * 优化后QPS：2114
     * */
    @RequestMapping(value = "/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId){
        model.addAttribute("user", user);

        if (user == null)
            return Result.error(CodeMsg.SESSION_ERROR);

        // 利用内存标记减少redis访问
        // localOverMap的出现，避免了每次都要查询redis是否还有库存。
        // 这样没有库存就不需要查询redis了，减少开销
        boolean over = localOverMap.get(goodsId);
        if (over){
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        // 预减库存
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock,""+goodsId);
        if (stock < 0){
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }


        // 判断是否已经秒杀到了，不能重复下单
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null){
            return Result.error(CodeMsg.MIAOSHA_REPEAT);
        }

        // 入队
        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setGoodsId(goodsId);
        mm.setUser(user);
        mqSender.sendMiaoshaMessage(mm);

        return Result.success(0);   // 0代表在队列排队中





        /*
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

         */
    }

    /**
     * 返回orderId：成功
     * -1：秒杀失败
     * 0：排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId){
        model.addAttribute("user", user);

        if (user == null)
            return Result.error(CodeMsg.SESSION_ERROR);

        long result = miaoshaService.getMiaoshaResult(user.getId(),goodsId);
        return Result.success(result);
    }


}
