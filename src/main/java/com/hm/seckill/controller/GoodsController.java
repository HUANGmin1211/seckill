package com.hm.seckill.controller;

import com.alibaba.druid.util.StringUtils;
import com.hm.seckill.domain.Goods;
import com.hm.seckill.domain.MiaoshaUser;
import com.hm.seckill.domain.User;
import com.hm.seckill.redis.GoodsKey;
import com.hm.seckill.redis.RedisService;
import com.hm.seckill.service.GoodsService;
import com.hm.seckill.service.MiaoshaUserService;
import com.hm.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    MiaoshaUserService userService;
    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;    // 框架自带，用于手动渲染模板
    @Autowired
    ApplicationContext applicationContext;

//    @RequestMapping("/to_list")
    // 页面缓存
    @RequestMapping(path = "/to_list", produces = "text/html")
    @ResponseBody
//    public String toList(Model model, MiaoshaUser user){
    public String toList(HttpServletResponse response, HttpServletRequest request, Model model, MiaoshaUser user){

        model.addAttribute("user", user);

        // 取缓存
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if (!StringUtils.isEmpty(html))
            return html;

        // 查询商品列表
        List<GoodsVo> goodsList=  goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
//        return "goods_list";

        // 如果没有缓存，手动渲染
        WebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);

        if (!StringUtils.isEmpty(html))
            redisService.set(GoodsKey.getGoodsList,"", html);

        return html;
    }

    //    @RequestMapping("/to_list")
//    public String toList(HttpServletResponse response, Model model,
//                         @CookieValue(value = MiaoshaUserService.COOKIE_NAME_TOKEN, required = false) String cookieToken,
//                         @RequestParam(value = MiaoshaUserService.COOKIE_NAME_TOKEN, required = false) String paramToken){
//
//        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken))
//            return "login";
//
//        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;  // paramToken有的话就先取它，即它的优先级较高
//        MiaoshaUser user =  userService.getByToken(response, token);
//        model.addAttribute("user", user);
//        return "goods_list";
//    }

//    @RequestMapping("/to_detail/{goodsId}")

    // URL缓存
    @RequestMapping(path = "/to_detail/{goodsId}", produces = "text/html")
    @ResponseBody
    public String toDetail(Model model, MiaoshaUser user, @PathVariable("goodsId") long goodsId,
                           HttpServletResponse response, HttpServletRequest request){
        model.addAttribute("user", user);

        // 取缓存
        String html = redisService.get(GoodsKey.getGoodsDetail, "" + goodsId, String.class);  // 加参数"" + goodsId， URL缓存
        if (!StringUtils.isEmpty(html))
            return html;

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);

        //
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus = 0;
        int remainSeconds = 0;

        if (now < startAt){ // 秒杀还没开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int)(startAt - now )/ 1000;
        }else if (now > endAt){ // 秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else{ // 秒杀进行中
            miaoshaStatus = 1;
        }

        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);

//        return "goods_detail";

        // 如果没有缓存，手动渲染
        WebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);

        if (!StringUtils.isEmpty(html))
            redisService.set(GoodsKey.getGoodsDetail,"" + goodsId, html);

        return html;
    }

}
