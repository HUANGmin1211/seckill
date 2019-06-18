package com.hm.seckill.service;

import com.hm.seckill.dao.GoodsDao;
import com.hm.seckill.domain.Goods;
import com.hm.seckill.domain.MiaoshaGoods;
import com.hm.seckill.domain.MiaoshaOrder;
import com.hm.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    // 秒杀后减少库存
    public void reduceStock(GoodsVo goods) {
        MiaoshaGoods g = new MiaoshaGoods();
        g.setGoodsId(goods.getId());
        goodsDao.reduceStock(g);

    }
}
