package com.alarm.eagle.log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
    sinkLogToKafka方法由于替换了实体类类型，修改原有方法对应的实体类类型
 */
public class LogSchema2 implements DeserializationSchema<ResultMap>, SerializationSchema<ResultMap> {
    private static final Logger logger = LoggerFactory.getLogger(LogSchema.class);

    private String logIndex = null;

    public LogSchema2() {
    }

    public LogSchema2(String logIndex) {
        this.logIndex = logIndex;
    }

    @Override
    public ResultMap deserialize(byte[] bytes) {
        String msg = new String(bytes);
        try {
            ResultMap entry = new ResultMap((JsonObject) JsonParser.parseString(msg));
            if (logIndex != null) {
                entry.setIndex(logIndex);
            }
            return entry;
        } catch (Exception e) {
            logger.error("Cannot parse log:{}, exception:{}", msg, e);
        }
        return null;
    }

    @Override
    public byte[] serialize(ResultMap logEntry) {
        return logEntry.toJSON().toString().getBytes();
    }

    @Override
    public boolean isEndOfStream(ResultMap logEntry) {
        return false;
    }

    @Override
    public TypeInformation<ResultMap> getProducedType() {
        return TypeExtractor.getForClass(ResultMap.class);
    }


}
