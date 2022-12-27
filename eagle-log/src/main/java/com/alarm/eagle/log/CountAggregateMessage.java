package com.alarm.eagle.log;

import org.apache.flink.api.common.functions.AggregateFunction;

/*
    根据MessageCount类型做聚合后输出，实际操作为每次自增count
 */
public class CountAggregateMessage implements AggregateFunction<Message, MessageCount, MessageCount> {

        @Override
        public MessageCount createAccumulator() {
            MessageCount messageCount = new MessageCount();
            messageCount.init();
            return messageCount;
        }

        @Override
        public MessageCount add(Message message, MessageCount messageCount) {
            int count = messageCount.getCount();
            count++;
            messageCount.setCount(count);
            messageCount.setUrl(message.getUrl());
            return messageCount;
        }

        @Override
        public MessageCount getResult(MessageCount messageCount) {
            return messageCount;
        }

        @Override
        public MessageCount merge(MessageCount a, MessageCount b) {
            a.setCount(a.getCount() + b.getCount());
            return a;
        }
    }