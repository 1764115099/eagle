package com.alarm.eagle.redis;

import com.alarm.eagle.log.ResultMap;
import org.apache.flink.api.common.functions.AggregateFunction;


/*
    sinkToRedis方法由于替换了实体类类型，修改原有方法对应的实体类类型
 */
public class LogStatAggregateFunction2 implements AggregateFunction<ResultMap, LogStatAccumulator, LogStatAccumulator> {
    private static final long serialVersionUID = 1L;

    @Override
    public LogStatAccumulator createAccumulator() {
        return new LogStatAccumulator();
    }

    @Override
    public LogStatAccumulator add(ResultMap resultMap, LogStatAccumulator logStatAccumulator) {
        if (logStatAccumulator.getKey().isEmpty()) {
            logStatAccumulator.setKey(resultMap.getIndex());
        }
        logStatAccumulator.addCount(1);
        String ip = resultMap.getIp();
        if (ip == null) {
            ip = "127.0.0.1";
        }
        logStatAccumulator.addIp(ip);
        return logStatAccumulator;
    }

    @Override
    public LogStatAccumulator getResult(LogStatAccumulator logStatAccumulator) {
        return logStatAccumulator;
    }

    @Override
    public LogStatAccumulator merge(LogStatAccumulator acc1, LogStatAccumulator acc2) {
        if (acc1.getKey().isEmpty()) {
            acc1.setKey(acc2.getKey());
        }
        acc1.addCount(acc2.getCount());
        acc1.addIpMap(acc2.getIpMap());
        return acc1;
    }
}
