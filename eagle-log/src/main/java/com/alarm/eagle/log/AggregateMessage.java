package com.alarm.eagle.log;

import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Map;

/*
        基于Process做批量聚合操作，现还不完整，需引入process的时间窗口机制，否则结果每条直接输出无法聚合
     */
    public class AggregateMessage extends KeyedProcessFunction<String, Message, String> {

        @Override
        public void processElement(Message message, Context ctx, Collector<String> out) throws Exception {
            Map<String, Integer> map = new HashMap<String, Integer>();
            if (map.containsKey(message.getUrl())){
                String url = message.getUrl();
                Integer value = map.get(url);
                map.replace(url,++value);
            }else {
                String url = message.getUrl();
                map.put(url,1);
            }
            out.collect(map.toString());
        }
    }