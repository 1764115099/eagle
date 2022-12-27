package com.alarm.eagle.log;

import org.apache.flink.api.common.functions.AggregateFunction;

/*
    基于ResultMap类型做聚合后输出，具体操作为每次count自增，并将其余所需参数一并set进去
 */
public class CountAggregate implements AggregateFunction<LogEntry, ResultMap, ResultMap> {

    @Override
    public ResultMap createAccumulator() {
        ResultMap resultMap = new ResultMap();
        resultMap.init();
        return resultMap;
    }

    @Override
    public ResultMap add(LogEntry logEntry, ResultMap resultMap) {
        resultMap.setId(logEntry.getId());
        resultMap.setIp(logEntry.getIp());
        int count = resultMap.getCount();
        count++;
        resultMap.setCount(count);
        resultMap.setIndex(logEntry.getIndex());
        resultMap.setTimestamp(logEntry.getTimestamp());
        return resultMap;
    }

    @Override
    public ResultMap getResult(ResultMap resultMap) {
        return resultMap;
    }

    @Override
    public ResultMap merge(ResultMap a, ResultMap b) {
        a.setCount(a.getCount() + b.getCount());
        return a;
    }
}