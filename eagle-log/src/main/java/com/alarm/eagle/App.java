package com.alarm.eagle;

import com.alarm.eagle.config.ConfigConstant;
import com.alarm.eagle.config.EagleProperties;
import com.alarm.eagle.es.ElasticsearchUtil;
import com.alarm.eagle.es.EsActionRequestFailureHandler;
//import com.alarm.eagle.es.EsSinkFunction;
import com.alarm.eagle.es.EsSinkFunction2;
import com.alarm.eagle.log.*;
import com.alarm.eagle.log.http.*;
import com.alarm.eagle.log.sql.*;
import com.alarm.eagle.mysql.MysqlSink;
import com.alarm.eagle.redis.*;
import com.alarm.eagle.rule.RuleBase;
import com.alarm.eagle.rule.RuleSourceFunction;
import com.alarm.eagle.util.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkBase;
import org.apache.flink.streaming.connectors.elasticsearch7.ElasticsearchSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.*;

/**
 * Created by luxiaoxun on 2020/01/27.
 */
public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        try {
            ParameterTool params = ParameterTool.fromArgs(args);
            ParameterTool parameter = EagleProperties.getInstance(params).getParameter();
            showConf(parameter);

            // Build stream DAG
            StreamExecutionEnvironment env = getStreamExecutionEnvironment(parameter);
            DataStream<LogEntry> dataSource = getKafkaDataSource(parameter, env);

            /*
                转换流数据类型，将原有eagle数据流程中的logEntry实体类中的message（即实际rasp日志部分）部分做反序列化，
                    然后根据message内的信息维系一个result类型对象，用于增量和全量数据入库
             */
            SingleOutputStreamOperator<Result> resultStream = dataSource.map(new MapFunction<LogEntry, Result>() {

                @Override
                public Result map(LogEntry logEntry) throws Exception {
                    /*
                        Message: 2022-12-21 16:19:05,606 INFO  [http-nio-8080-exec-6][com.baidu.openrasp.plugin.js.log] http://112.124.65.22:31875/vulns/012-jdbc-mysql.jsp [eagle-logger] ;;"timestamp":"2022-12-21T08:19:05.606Z","atTimestamp":"2022-12-21T08:19:05.606Z","id":"9y8em64ycv400","requestpath":"/vulns/012-jdbc-mysql.jsp","querystring":"id=1","requestmethod":"get","requestprotocol":"http/1.1","remoteaddr":"10.0.18.237","sqlserver":"mysql","sql":"SELECT * FROM vuln WHERE id = 1";;
                        根据事先在日志中预留的分隔符 “;;”，切割出有用的数据，便于反序列化
                     */
                    String[] values = logEntry.getMessage().split(";;");
                    String jsonString = "{" + values[1] + "}";
                    Message message = JsonUtil.jsonToObject(jsonString, Message.class);
                    Result result = messageToResult(message);
                    return result;
                }
                private static final long serialVersionUID = -6867736771747690202L;
            }).setParallelism(1);
//            resultStream.print();
            resultStream.addSink(new MysqlSink()).setParallelism(1);

            /*
                利用process批量处理逻辑，还需完善
             */
//            messageStream.keyBy(message -> message.getId())
//                    .process(new AggregateMessage())
//                    .print();


            /*
                根据url做基于时间窗口的聚合操作，一次仅根据keyBy做一种聚合
             */
//            SingleOutputStreamOperator<MessageCount> urlAggregate = messageStream.keyBy(message -> message.getUrl())
//                    .timeWindow(Time.seconds(30))
//                    .aggregate(new CountAggregateMessage());
//            urlAggregate.print();


           /*
                创建实体类聚合例子和sink例子，所有sink操作需要根据具体实体类，替换方法类型
            */
//           SingleOutputStreamOperator<ResultMap> resultmap = dataSource.keyBy(logEntry -> logEntry.getIp())
//                    .timeWindow(Time.milliseconds(10))
//                    .aggregate(new CountAggregate())
//                    .setParallelism(1).name("ipReuslt").uid("ipReuslt");
//            resultmap.print();
//            sinkToRedis(parameter, resultmap);
//            sinkToElasticsearch(parameter, resultmap);
//            DataStream<ResultMap> kafkaOutputStream = resultmap.getSideOutput(Descriptors.kafkaOutputTag2);
//            sinkLogToKafka(parameter, resultmap);


            /*
                旧有drools逻辑
             */
            BroadcastStream<RuleBase> ruleSource = getRuleDataSource(parameter, env);
            SingleOutputStreamOperator<LogEntry> processedStream = processLogStream(parameter, dataSource, ruleSource);
//            sinkToRedis(parameter, processedStream);
//            sinkToElasticsearch(parameter, processedStream);

//            DataStream<LogEntry> kafkaOutputStream2 = processedStream.getSideOutput(Descriptors.kafkaOutputTag);
//            sinkLogToKafka(parameter, kafkaOutputStream);

            env.getConfig().setGlobalJobParameters(parameter);
            env.execute("eagle-log");
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    /*
        根据message对象内的信息，生成一个Result类型的对象用于增量和全量数据入库
        //TODO 还没加锁，所以并发操作的时候会出现，同时判断sismember为false，然后hset内同一个key被修改多次id，并且同一条数据会有多个id入库的情况，目前并发调成了1，待优化
     */
    public static Result messageToResult(Message message){
		JedisCluster jedis = RedisUtil.getJedisCluster();
//        Jedis jedis = RedisUtil.getJedisClient();


        HttpResult httpResult = new HttpResult();

        /*
            Request Server 增量入库
            HashSet
                requestSrv => requestServer : srvId
         */
        String srvKey = message.getRequestserver();
        Srv srv = new Srv();
        if (!jedis.sismember("requestServer", srvKey)) {
            jedis.sadd("requestServer", srvKey);
            Long srvId = SnowIdUtils.uniqueLong();
            srv.setId(srvId);
            srv.setAddr(srvKey);
            srv.setTime(DateUtil.getCurrentDate());
            httpResult.setSrv(srv);

            jedis.hset("requestSrv", srvKey, String.valueOf(srvId));
        }


        /*
            Request Path 增量入库
            HashSet
                requestApi => requestPath : apiId
         */
        String apiKey = message.getRequestpath();
        Api api = new Api();
        if (!jedis.sismember("requestPath", apiKey)) {
            jedis.sadd("requestPath", apiKey);
            Long apiId = SnowIdUtils.uniqueLong();
            api.setId(apiId);
            String srvId = jedis.hget("requestSrv", srvKey);
            api.setSrvId(Long.valueOf(srvId));
            api.setUrl(apiKey);
            api.setTime(DateUtil.getCurrentDate());
            httpResult.setApi(api);

            jedis.hset("requestApi", apiKey, String.valueOf(apiId));
        }


        /*
            Request RemoteAddr 增量入库
            HashSet
                requestClient => remoteAddr : clientId
         */
        String clientKey = message.getRemoteaddr();
        Client client = new Client();
        if (!jedis.sismember("requestRemoteAddr", clientKey)) {
            jedis.sadd("requestRemoteAddr", clientKey);
            Long clientId = SnowIdUtils.uniqueLong();
            client.setId(clientId);
            client.setIp(clientKey);
            httpResult.setClient(client);

            jedis.hset("requestClient", clientKey, String.valueOf(clientId));
        }



        /*
            Http 全量入库
            只有增量数据才会产生新的id，所以借助Redis
                先通过set去重，只有set内不存在的key才会生成新的id，放入HashSet内
                    如果需要id就通过key去对应的HashSet内取
         */
        Long clientApiId = SnowIdUtils.uniqueLong();
        Long apiId = Long.valueOf(jedis.hget("requestApi", apiKey));
        Long clientId = Long.valueOf(jedis.hget("requestClient", clientKey));

        ClientApi clientApi = new ClientApi();
        clientApi.setId(clientApiId);
        clientApi.setApiId(apiId);
        clientApi.setClientId(clientId);
        clientApi.setTime(DateUtil.getCurrentDate());
        httpResult.setClientApi(clientApi);

        SqlResult sqlResult = new SqlResult();

        /*
            DbSrv 增量入库
            HashSet
                sqlDbSrv => sqlAddr + ":" + sqlPort : dbSrvId
         */
        String sqlAddr = message.getSqladdr();
        String sqlPort = message.getSqlport();
        String dbSrvKey = sqlAddr + ":" + sqlPort;
        Dbsrv dbsrv = new Dbsrv();
        if (!jedis.sismember("sqlAddr", dbSrvKey)) {
            jedis.sadd("sqlAddr", dbSrvKey);
            Long dbSrvId = SnowIdUtils.uniqueLong();
            dbsrv.setId(dbSrvId);
            dbsrv.setIp(sqlAddr);
            dbsrv.setPort(sqlPort);
            dbsrv.setType(message.getSqlserver());
            dbsrv.setTime(DateUtil.getCurrentDate());
            sqlResult.setDbsrv(dbsrv);

            jedis.hset("sqlDbSrv", dbSrvKey, String.valueOf(dbSrvId));
        }

        /*
            Db 增量入库
            HashSet
                sqlDb => sqlDb : dbId
         */
        String dbKey = message.getSqldb();
        Db db = new Db();
        if (!jedis.sismember("sqlDbSet", dbKey)) {
            jedis.sadd("sqlDbSet", dbKey);
            Long dbId = SnowIdUtils.uniqueLong();
            db.setId(dbId);
            String dbSrvId = jedis.hget("sqlDbSrv", dbSrvKey);
            db.setDbsrvId(Long.valueOf(dbSrvId));
            db.setName(dbKey);
            sqlResult.setDb(db);

            jedis.hset("sqlDb", dbKey, String.valueOf(dbId));
        }

        /*
            Account 增量入库
            HashSet
                sqlAccount => sqlUserName + ":" + sqlPwd : accountId
                    如果仅去重sqlUser，那相同数据库user，但password不同的账户信息，展示在页面是password会为相同user的第一条的password
                        不显示password则按照username去重即可
         */
        String sqlUserName = message.getSqluser();
        String sqlPwd = message.getSqlpwd();
        String accountKey = sqlUserName;
        Account account = new Account();
        if (!jedis.sismember("sqlAccountSet", accountKey)) {
            jedis.sadd("sqlAccountSet", accountKey);
            Long accountId = SnowIdUtils.uniqueLong();
            account.setId(accountId);
            String dbId = jedis.hget("sqlDb", dbKey);
            account.setDbId(Long.valueOf(dbId));
            account.setUsername(sqlUserName);
            account.setPwd(sqlPwd);
            sqlResult.setAccount(account);

            jedis.hset("sqlAccount", accountKey, String.valueOf(accountId));
        }

        /*
            Table 增量入库
            HashSet
                sqlTable => sqlTable : tableId
            具体还需根据到时候识别出来的Table格式修改table.setName输入数据
         */
        String tableKey = message.getSqltable();
        Table table = new Table();
        if (!jedis.sismember("sqlTableSet", tableKey)) {
            jedis.sadd("sqlTableSet", tableKey);
            Long tableId = SnowIdUtils.uniqueLong();
            table.setId(tableId);
            String dbId = jedis.hget("sqlDb", dbKey);
            table.setDbId(Long.valueOf(dbId));
            table.setName(tableKey);
            table.setTime(DateUtil.getCurrentDate());
            sqlResult.setTable(table);

            jedis.hset("sqlTable", tableKey, String.valueOf(tableId));
        }

        /*
            Sql全量入库
            只有增量数据才会产生新的id，所以借助Redis
                先通过set去重，只有set内不存在的key才会生成新的id，放入HashSet内
                    如果需要sql相关的id就通过key去对应的HashSet内取
                        http相关的id在上方已经取出，直接使用即可
         */
        Long apiTableId = SnowIdUtils.uniqueLong();
        Long accountId = Long.valueOf(jedis.hget("sqlAccount", accountKey));
        Long tableId = Long.valueOf(jedis.hget("sqlTable", tableKey));

        ApiTable apiTable = new ApiTable();
        apiTable.setId(apiTableId);
        apiTable.setAccountId(accountId);
        apiTable.setTableId(tableId);
        apiTable.setApiId(apiId);
        apiTable.setRequestId(clientApiId);
        apiTable.setTime(DateUtil.getCurrentDate());
        sqlResult.setApiTable(apiTable);

        /*
            将所有需要持久化的信息存入map
         */
        Result result = new Result();
        result.setHttpResult(httpResult);
        result.setSqlResult(sqlResult);

        return result;
    }

    private static StreamExecutionEnvironment getStreamExecutionEnvironment(ParameterTool parameter) {
        StreamExecutionEnvironment env = null;
        int globalParallelism = parameter.getInt(ConfigConstant.FLINK_PARALLELISM);
        if (parameter.get(ConfigConstant.FLINK_MODE).equals(ConfigConstant.MODE_DEV)) {
            env = StreamExecutionEnvironment.createLocalEnvironment();
            globalParallelism = 1;
        } else {
            env = StreamExecutionEnvironment.getExecutionEnvironment();
        }
        env.setParallelism(globalParallelism);

        //checkpoint
        boolean enableCheckpoint = parameter.getBoolean(ConfigConstant.FLINK_ENABLE_CHECKPOINT, false);
        if (enableCheckpoint) {
            env.enableCheckpointing(120000L);
            CheckpointConfig config = env.getCheckpointConfig();
            config.setMinPauseBetweenCheckpoints(30000L);
            config.setCheckpointTimeout(120000L);
            //RETAIN_ON_CANCELLATION则在job cancel的时候会保留externalized checkpoint state
            config.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.DELETE_ON_CANCELLATION);
        }

        return env;
    }

    private static BroadcastStream<RuleBase> getRuleDataSource(ParameterTool parameter, StreamExecutionEnvironment env) {
        String ruleUrl = parameter.get(ConfigConstant.STREAM_RULE_URL);
        String ruleName = "rules-source";
        return env.addSource(new RuleSourceFunction(ruleUrl)).name(ruleName).uid(ruleName).setParallelism(1)
                .broadcast(Descriptors.ruleStateDescriptor);
    }

    private static DataStream<LogEntry> getKafkaDataSource(ParameterTool parameter, StreamExecutionEnvironment env) {
        String kafkaBootstrapServers = parameter.get(ConfigConstant.KAFKA_BOOTSTRAP_SERVERS);
        String kafkaGroupId = parameter.get(ConfigConstant.KAFKA_GROUP_ID);
        String kafkaTopic = parameter.get(ConfigConstant.KAFKA_TOPIC);
        int kafkaParallelism = parameter.getInt(ConfigConstant.KAFKA_TOPIC_PARALLELISM);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", kafkaBootstrapServers);
        properties.setProperty("group.id", kafkaGroupId);
        FlinkKafkaConsumer<LogEntry> source = new FlinkKafkaConsumer<>(kafkaTopic, new LogSchema(), properties);
        source.setCommitOffsetsOnCheckpoints(true);
        return env.addSource(source).name(kafkaTopic).uid(kafkaTopic).setParallelism(kafkaParallelism);
    }

    private static SingleOutputStreamOperator<LogEntry> processLogStream(ParameterTool parameter, DataStream<LogEntry> dataSource,
                                                                         BroadcastStream<RuleBase> ruleSource) throws Exception {
        BroadcastConnectedStream<LogEntry, RuleBase> connectedStreams = dataSource.connect(ruleSource);
        int processParallelism = parameter.getInt(ConfigConstant.STREAM_PROCESS_PARALLELISM);
        String kafkaIndex = parameter.get(ConfigConstant.KAFKA_SINK_INDEX);
        RuleBase ruleBase = getInitRuleBase(parameter);
        if (ruleBase == null) {
            throw new Exception("Can not get initial rules");
        } else {
            String name = "process-log";
            logger.debug("Initial rules: " + ruleBase.toString());
            return connectedStreams.process(new LogProcessFunction(ruleBase, kafkaIndex))
                    .setParallelism(processParallelism).name(name).uid(name);
        }
    }

    private static RuleBase getInitRuleBase(ParameterTool parameter) {
        String ruleUrl = parameter.get(ConfigConstant.STREAM_RULE_URL);
        String content = HttpUtil.doGet(ruleUrl);
//        String content = HttpUtil.doGetMock(ruleUrl);
        if (content == null) {
            logger.error("Failed to get rules from url {}", ruleUrl);
            return null;
        }

//        JsonArray resJson = JsonParser.parseString(content).getAsJsonArray();
        JsonObject resJson = JsonParser.parseString(content).getAsJsonObject().getAsJsonObject("data");
        if (resJson == null) {
            logger.error("Failed to parse json:{}", content);
            return null;
        }

        RuleBase ruleBase = RuleBase.createRuleBase(resJson);
        return ruleBase;
    }

    private static void sinkLogToKafka(ParameterTool parameter, DataStream<ResultMap> stream) {
        String kafkaBootstrapServers = parameter.get(ConfigConstant.KAFKA_SINK_BOOTSTRAP_SERVERS);
        String kafkaTopic = parameter.get(ConfigConstant.KAFKA_SINK_TOPIC);
        int kafkaParallelism = parameter.getInt(ConfigConstant.KAFKA_SINK_TOPIC_PARALLELISM);
        String name = "kafka-sink";
        FlinkKafkaProducer<ResultMap> producer = new FlinkKafkaProducer<>(kafkaBootstrapServers, kafkaTopic,
                new LogSchema2());
        producer.setLogFailuresOnly(false);
        stream.addSink(producer).setParallelism(kafkaParallelism).name(name).uid(name);
    }

    private static void sinkToElasticsearch(ParameterTool parameter, DataStream<ResultMap> dataSource) {
        List<HttpHost> esHttpHosts = ElasticsearchUtil.getEsAddresses(parameter.get(ConfigConstant.ELASTICSEARCH_HOSTS));
        int bulkMaxActions = parameter.getInt(ConfigConstant.ELASTICSEARCH_BULK_FLUSH_MAX_ACTIONS, 5000);
        int bulkMaxSize = parameter.getInt(ConfigConstant.ELASTICSEARCH_BULK_FLUSH_MAX_SIZE_MB, 5);
        int intervalMillis = parameter.getInt(ConfigConstant.ELASTICSEARCH_BULK_FLUSH_INTERVAL_MS, 1000);
        int esSinkParallelism = parameter.getInt(ConfigConstant.ELASTICSEARCH_SINK_PARALLELISM);
        String indexPostfix = parameter.get(ConfigConstant.ELASTICSEARCH_INDEX_POSTFIX, "");

        String name = "ES-sink";
        ElasticsearchSink.Builder<ResultMap> esSinkBuilder = new ElasticsearchSink.Builder<>(esHttpHosts, new EsSinkFunction2(indexPostfix));
        esSinkBuilder.setBulkFlushMaxActions(bulkMaxActions);
        esSinkBuilder.setBulkFlushMaxSizeMb(bulkMaxSize);
        esSinkBuilder.setBulkFlushInterval(intervalMillis);
        esSinkBuilder.setBulkFlushBackoff(true);
        esSinkBuilder.setBulkFlushBackoffRetries(3);
        esSinkBuilder.setBulkFlushBackoffType(ElasticsearchSinkBase.FlushBackoffType.EXPONENTIAL);
        esSinkBuilder.setBulkFlushBackoffDelay(1000);
        esSinkBuilder.setFailureHandler(new EsActionRequestFailureHandler());
        dataSource.addSink(esSinkBuilder.build()).setParallelism(esSinkParallelism).name(name).uid(name);
    }

    private static void sinkToRedis(ParameterTool parameter, DataStream<ResultMap> dataSource) {
        // save statistic information in redis
        int windowTime = parameter.getInt(ConfigConstant.REDIS_WINDOW_TIME_SECONDS);
        int windowCount = parameter.getInt(ConfigConstant.REDIS_WINDOW_TRIGGER_COUNT);
        int redisSinkParallelism = parameter.getInt(ConfigConstant.REDIS_SINK_PARALLELISM);
        String name = "redis-agg-log";
        DataStream<LogStatWindowResult> keyedStream = dataSource.keyBy((KeySelector<ResultMap, String>) log -> log.getIndex())
                .timeWindow(Time.seconds(windowTime))
                .trigger(new CountTriggerWithTimeout<>(windowCount, TimeCharacteristic.ProcessingTime))
                .aggregate(new LogStatAggregateFunction2(), new LogStatWindowFunction())
                .setParallelism(redisSinkParallelism).name(name).uid(name);
        String sinkName = "redis-sink";
        keyedStream.addSink(new RedisAggSinkFunction()).setParallelism(redisSinkParallelism).name(sinkName).uid(sinkName);
    }

    private static void showConf(ParameterTool parameter) {
        logger.info("Show " + parameter.getNumberOfParameters() + " config parameters");
        Configuration configuration = parameter.getConfiguration();
        Map<String, String> map = configuration.toMap();
        for (String key : map.keySet()) {
            logger.info(key + ":" + map.get(key));
        }
    }
}
