package com.alarm.eagle.es;

import com.alarm.eagle.log.ResultMap;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
    sinkToElasticsearch方法由于替换了实体类类型，修改原有方法对应的实体类类型
 */
public class EsSinkFunction2 implements ElasticsearchSinkFunction<ResultMap> {
    private static final Logger logger = LoggerFactory.getLogger(EsSinkFunction.class);
    private String indexPostfix = "";

    public EsSinkFunction2(String indexPostfix) {
        this.indexPostfix = indexPostfix;
    }

    @Override
    public void process(ResultMap log, RuntimeContext runtimeContext, RequestIndexer requestIndexer) {
        log.setEsIndexPrefix(indexPostfix);
        // Use log id as ES doc id
        requestIndexer.add(new IndexRequest(log.generateIndexName())
                .id(log.getId())
                .source(log.toJSON().toString(), XContentType.JSON));
    }
}
