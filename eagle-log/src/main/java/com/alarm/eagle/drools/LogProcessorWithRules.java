package com.alarm.eagle.drools;

import com.alarm.eagle.log.LogEntry;
import com.alarm.eagle.rule.RuleBase;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by luxiaoxun on 2020/01/29.
 */
public class LogProcessorWithRules implements LogProcessor {
    private static final Logger logger = LoggerFactory.getLogger(LogProcessorWithRules.class);

//    private KieBase kieBase = null;
//    private KieSession kieSession = null;
    private String rulePackage = null;

    private RuleBase ruleBase = null;

    public LogProcessorWithRules(String pkg) {
        rulePackage = pkg;
    }

//    public LogProcessorWithRules(String pkg, RuleBase ruleBase) {
//        rulePackage = pkg;
//        KieHelper kieHelper = new KieHelper();
//        if (ruleBase != null && ruleBase.getRules().size() > 0) {
//            for (Rule rule : ruleBase.getRules()) {
//                kieHelper.addResource(ResourceFactory.newByteArrayResource(rule.getScript().getBytes(StandardCharsets.UTF_8)), ResourceType.DRL);
//            }
//        }
//        KieBaseConfiguration config = KieServices.Factory.get().newKieBaseConfiguration();
//        config.setOption(EventProcessingOption.STREAM);
//        kieBase = kieHelper.build();
//        kieSession = kieBase.newKieSession();
//    }

    @Override
    public List<LogEntry> execute(String msg) {
        LogEntry entry = new LogEntry((JsonObject) JsonParser.parseString(msg));
        return execute(entry);
    }

    @Override
    public List<LogEntry> execute(LogEntry entry) {
        List<LogEntry> result = new LinkedList<>();
        try {
            //规则匹配
            //iswrong    是否识别为敏感

//            if (kieSession != null) {
////                kieSession.setGlobal("LOG", logger);
//                kieSession.insert(entry);
//                kieSession.fireAllRules();
//                for (Object obj : kieSession.getObjects()) {
//                    result.add((LogEntry) obj);
//                }
//            }
        } catch (Exception ex) {
            logger.warn("Process log error: {}", ex.toString());
        }
        return result;
    }

    @Override
    public boolean loadRules(RuleBase rb) {
//        KieSessionHelper ksHelper = new KieSessionHelper(rulePackage);
        if (rb.getRule().isEmpty()) {
            logger.error("Failed to load rules, hash:{}", rb.getHash());
            return false;
        }
        ruleBase = rb;
//        kieBase = ksHelper.createKieBase();
//        kieSession = kieBase.newKieSession();
        return true;
    }

//    @Override
//    public boolean addRule(Rule rule) {
//        if (kieBase != null) {
//            org.kie.api.definition.rule.Rule ruleCache = kieBase.getRule(rulePackage, rule.getName());
//            if (ruleCache != null && ruleCache.getName().equals(rule.getName())) {
//                logger.info("Rule {} already exist", rule.getName());
//            } else {
//                KnowledgeBuilder kb = KnowledgeBuilderFactory.newKnowledgeBuilder();
//                kb.add(ResourceFactory.newByteArrayResource(rule.getScript().getBytes(StandardCharsets.UTF_8)), ResourceType.DRL);
//                KnowledgeBaseImpl kieBaseImpl = (KnowledgeBaseImpl) kieBase;
//                kieBaseImpl.addKnowledgePackages(kb.getKnowledgePackages());
//                kieSession = kieBase.newKieSession();
//            }
//            logger.info("Add rule {} successfully", rule.getName());
//            return true;
//        } else {
//            return false;
//        }
//    }

//    @Override
//    public boolean removeRule(Rule rule) {
//        if (kieBase != null) {
//            kieBase.removeRule(rulePackage, rule.getName());
//            kieSession = kieBase.newKieSession();
//            logger.info("Remove rule {} successfully", rule.getName());
//            return true;
//        } else {
//            return false;
//        }
//    }

    @Override
    public void destroy() {
        if (ruleBase != null) {
            ruleBase = null;
        }
    }
}
