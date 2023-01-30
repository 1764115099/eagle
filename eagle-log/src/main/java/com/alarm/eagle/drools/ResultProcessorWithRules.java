package com.alarm.eagle.drools;

import com.alarm.eagle.log.Content;
import com.alarm.eagle.log.HitResult;
import com.alarm.eagle.log.Message;
import com.alarm.eagle.log.Result;
import com.alarm.eagle.rule.RuleBase;

import com.alarm.eagle.util.DateUtil;
import com.alarm.eagle.util.SnowIdUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ldx on 2023/01/11.
 */
public class ResultProcessorWithRules implements ResultProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ResultProcessorWithRules.class);

    private String rulePackage = null;

    private RuleBase ruleBase = null;

    public ResultProcessorWithRules(String pkg) {
        rulePackage = pkg;
    }

    @Override
    public List<HitResult> execute(Result result) {
        List<HitResult> resultList = new LinkedList<>();
        try {
            //规则匹配
            //iswrong    是否识别为敏感
            Message message = result.getMessage();
            String sql = message.getSql();
            String sqlresult = message.getSqlresult();
            String requestBody = message.getRequestbody();

            // gson => fastjson
            String rule = ruleBase.getRule();
            System.out.println("================,rule: "+rule);
            JSONObject jsonObject = JSONObject.parseObject(rule);

            String hitSource;
            if (sqlresult != null){
                hitSource=sqlresult;
            }else {
                hitSource=requestBody;
            }

            //获取命中ruleTemplateId
            Integer ruleTemplateId = patternSource(jsonObject,hitSource);
            if (ruleTemplateId != null){
                //如果命中，封装对象
                Long requestId = result.getHttpResult().getClientApi().getId();

                HitResult hitResult = new HitResult();
                hitResult.setId(SnowIdUtils.uniqueLong());
                hitResult.setRuleTemplateId(ruleTemplateId);
                hitResult.setRequestId(requestId);
                hitResult.setHit(true);

                Content content = new Content();
                content.setRequestProtocol(message.getRequestprotocol());
                content.setRequestMethod(message.getRequestmethod());
                content.setRequestHeader(message.getRequestheader());
                content.setRequestBody(message.getRequestbody());
                content.setRequestServer(message.getRequestserver());
                content.setRequestPath(message.getRequestpath());
                content.setRequestRemoteAddr(message.getRemoteaddr());
                if(message.getSqlserver()!="" && message.getSqlserver() != null){
                    content.setSqlType(message.getSqlserver());
                    content.setSqlServer(message.getSqladdr());
                    content.setSqlPort(message.getSqlport());
                    content.setSqlUserName(message.getSqluser());
                    content.setSqlPwd(message.getSqlpwd());
                    content.setSqlDb(message.getSqldb());
                    content.setSqlTable(message.getSqltable());

                    content.setSqlResult(message.getSqlresult());

//                    String sqlResult = message.getSqlresult();
//                    String jsonString = JSON.toJSONString(sqlResult);
//                    JSONArray sqlArray = JSONObject.parseArray(jsonString);
//                    content.setSqlResult(sqlArray.toJSONString(jsonString));
//                    content.setSqlResult(jsonString);
                }
                hitResult.setContent(content);

                resultList.add(hitResult);
            }




        } catch (Exception ex) {
            ex.printStackTrace();
            logger.warn("Process log error: {}", ex.toString());
        }
        return resultList;
    }

    @Override
    public boolean loadRules(RuleBase rb) {
        if (rb.getRule().toString().isEmpty()) {
            logger.error("Failed to load rules, hash:{}", rb.getHash());
            return false;
        }
        ruleBase = rb;

        return true;
    }

    @Override
    public void destroy() {
        if (ruleBase != null) {
            ruleBase = null;
        }
    }

    public static Integer patternSource(JSONObject jsonObject, String source){
        JSONObject dataIdentification = jsonObject.getJSONObject("data_identification");
        JSONArray ruleArr = dataIdentification.getJSONArray("rule");
        JSONArray ruleTemplateArr = dataIdentification.getJSONArray("rule_template");

        /*
            获取ruleId和algorithm对应关系
                ruleId:[algorithm1,algorithm2]
         */
        HashMap<Integer, List<String>> ruleMap = new HashMap<>();
        for (int i = 0; i < ruleArr.size(); i++) {
            Integer ruleId = ruleArr.getJSONObject(i).getInteger("id");
            List<String> algorithms = new ArrayList<>();
            String contentStr = ruleArr.getJSONObject(i).getString("content");

            if(contentStr.contains("[")){
                JSONArray content = JSONArray.parseArray(contentStr);
                for (int j = 0; j < content.size(); j++) {
                    String algorithm = content.getJSONObject(j).getString("algorithm");
                    algorithms.add(algorithm);
                }
            }
            ruleMap.put(ruleId,algorithms);
        }
        System.out.println("ruleMap: "+ruleMap);

        /*
            获取ruleId和ruleTemplateId对应关系
                ruleId:ruleTemplateId
         */
        HashMap<Integer, Integer> ruleTemplateMap = new HashMap<>();
        for (int i = 0; i < ruleTemplateArr.size(); i++) {
            Integer ruleId = ruleTemplateArr.getJSONObject(i).getInteger("ruleId");
            Integer ruleTemplateId = ruleTemplateArr.getJSONObject(i).getInteger("id");
            ruleTemplateMap.put(ruleId,ruleTemplateId);
        }
        System.out.println("ruleTemplateMap: "+ruleTemplateMap);


        /*
            遍历ruleMap
                命中algorithm后获取当前ruleId
                    再根据ruleId获取ruleTemplateId
         */
        Set<Integer> keySet = ruleMap.keySet();
        Iterator<Integer> iterator = keySet.iterator();
        while (iterator.hasNext()){
            Integer ruleId = iterator.next();
            List<String> algorithms = ruleMap.get(ruleId);
            for (String algorithm:algorithms) {

                Matcher matcher = Pattern.compile(algorithm).matcher(source);

                if (matcher.find()){
                    //匹配成功
                    Integer ruleTemplateId = ruleTemplateMap.get(ruleId);
                    System.out.println("ruleId: " + ruleId + " ,ruleTemplateId: " + ruleTemplateId);
                    System.out.println("regexSource: "+ source + " ,algorithm: "+algorithm);
                    return ruleTemplateId;
                }
            }
        }
        return null;
    }
}
