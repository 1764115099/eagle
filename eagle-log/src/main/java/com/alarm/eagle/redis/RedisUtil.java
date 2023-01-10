package com.alarm.eagle.redis;

import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.Set;

public class RedisUtil {

    public static JedisCluster getJedisCluster(){
        JedisCluster jedis = null;
        String redisCluster = "112.124.65.22:6371";
        if (!StringUtils.isEmpty(redisCluster)) {
            Set<HostAndPort> nodes = new HashSet<>();
            for (String server : redisCluster.split(",")) {
                String[] hostPort = server.split(":");
                nodes.add(new HostAndPort(hostPort[0], hostPort.length >= 2 ? Integer.parseInt(hostPort[1]) : 6379));
            }
            JedisPoolConfig jpc = new JedisPoolConfig();
            jpc.setMaxTotal(8);
            jpc.setMaxIdle(8);
            jpc.setMaxWaitMillis(1000L);
            int timeout = 3000;
            int maxAttempts = 10;
            jedis = new JedisCluster(nodes, timeout, maxAttempts, jpc);
        }
        return jedis;
    }


    public static Jedis getJedisClient() {
        JedisPool jedisPool = null;
        if(jedisPool==null){
            //      println("开辟一个连接池")
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxTotal(100);  //最大连接数
            jedisPoolConfig.setMaxIdle(20);   //最大空闲
            jedisPoolConfig.setMinIdle(20);     //最小空闲
            jedisPoolConfig.setBlockWhenExhausted(true);  //忙碌时是否等待
            jedisPoolConfig.setMaxWaitMillis(500);//忙碌时等待时长 毫秒
            jedisPoolConfig.setTestOnBorrow(true); //每次获得连接的进行测试

            jedisPool=new JedisPool(jedisPoolConfig,"112.124.65.22",31020);
        }
        return jedisPool.getResource();
    }
}
