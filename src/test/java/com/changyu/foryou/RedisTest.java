package com.changyu.foryou;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.changyu.foryou.model.DSHOrder;
import com.changyu.foryou.model.User;
import com.changyu.foryou.service.RedisService;
import com.changyu.foryou.tools.Constants;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    RedisService redisService;

    @Before
    public void setUp() {

    }

    @Test
    public void get() {
    	DSHOrder dshOrder = new DSHOrder();
    	dshOrder.setOrderId(123445);
    	dshOrder.setStatus((short)5);
    	dshOrder.setStartTime(222222222);

        redisService.add(Constants.REDISPREFIX + dshOrder.getOrderId(), dshOrder, 10L);
        List<DSHOrder> list = new ArrayList<>();
        list.add(dshOrder);
        redisService.add(Constants.REDISPREFIX, list, 10L);
        DSHOrder dshOrder1 = redisService.get(Constants.REDISPREFIX + dshOrder.getOrderId());
        Assert.notNull(dshOrder1, "dshOrder1 is null");
        List<DSHOrder> list2 = redisService.getDSHOrderList(Constants.REDISPREFIX);
        Assert.notNull(list2, "list is null");
    }
}