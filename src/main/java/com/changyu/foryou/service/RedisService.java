package com.changyu.foryou.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSON;
import com.changyu.foryou.model.DSHOrder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.jpush.api.utils.StringUtils;


@Repository
public class RedisService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public void add(String key, DSHOrder dshOrder, Long time) {
        Gson gson = new Gson();
        stringRedisTemplate.opsForValue().set(key, gson.toJson(dshOrder), time, TimeUnit.MINUTES);
    }

    public void add(String key, List<DSHOrder> dshOrders, Long time) {
        Gson gson = new Gson();
        String src = gson.toJson(dshOrders);
        stringRedisTemplate.opsForValue().set(key, src, time, TimeUnit.MINUTES);
    }

    public DSHOrder get(String key) {
        String source = stringRedisTemplate.opsForValue().get(key);
        if (!StringUtils.isEmpty(source)) {
            return new Gson().fromJson(source, DSHOrder.class);
        }
        return null;
    }

    public List<DSHOrder> getDSHOrderList(String key) {
        String source = stringRedisTemplate.opsForValue().get(key);
        if (!StringUtils.isEmpty(source)) {
            return new Gson().fromJson(source, new TypeToken<List<DSHOrder>>() {
            }.getType());
        }
        return null;
    }

    public void delete(String key) {
        stringRedisTemplate.opsForValue().getOperations().delete(key);
    }
    
    /**
     * scan命令迭代获取结果
     * @param pattern 通配符表达式
     * @param count 每次遍历的结果数量，当数据量较大时，可能需要设置的比较大，比如1万，否则时间可能会比较长
     * @return
     */
    public List<String> scan(final String pattern,final int count) {
        if (StringUtils.isEmpty(pattern)){
            return new ArrayList<>();
        }
        return stringRedisTemplate.execute(new RedisCallback<List<String>>() {
            public List<String> doInRedis(RedisConnection connection) throws DataAccessException {
                ScanOptions options = ScanOptions.scanOptions().match(pattern).count(count).build();
                Cursor<byte[]> cursor = connection.scan(options);
                List<String> result = new ArrayList<String>();
                if(cursor != null){
                    while (cursor.hasNext()){
                        result.add(new String(cursor.next()));
                    }
                }
                return result;
            }
        });
    }
}