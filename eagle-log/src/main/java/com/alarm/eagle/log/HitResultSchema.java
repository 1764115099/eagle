package com.alarm.eagle.log;

import com.alibaba.fastjson.JSONObject;
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
public class HitResultSchema implements DeserializationSchema<HitResult>, SerializationSchema<HitResult> {
    private static final Logger logger = LoggerFactory.getLogger(LogSchema.class);

    private String logIndex = null;

    public HitResultSchema() {
    }

    public HitResultSchema(String logIndex) {
        this.logIndex = logIndex;
    }

    @Override
    public HitResult deserialize(byte[] bytes) {
        String msg = new String(bytes);
        try {
            HitResult entry = new HitResult(JSONObject.parseObject(msg));
            return entry;
        } catch (Exception e) {
            logger.error("Cannot parse log:{}, exception:{}", msg, e);
        }
        return null;
    }

    @Override
    public byte[] serialize(HitResult hitResult) {
        return hitResult.toJSON().toString().getBytes();
    }

    @Override
    public boolean isEndOfStream(HitResult hitResult) {
        return false;
    }

    @Override
    public TypeInformation<HitResult> getProducedType() {
        return TypeExtractor.getForClass(HitResult.class);
    }


}
