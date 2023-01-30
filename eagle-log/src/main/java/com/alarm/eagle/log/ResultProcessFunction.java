package com.alarm.eagle.log;

import com.alarm.eagle.drools.LogProcessor;
import com.alarm.eagle.drools.LogProcessorWithRules;
import com.alarm.eagle.drools.ResultProcessor;
import com.alarm.eagle.drools.ResultProcessorWithRules;
import com.alarm.eagle.rule.RuleBase;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by luxiaoxun on 2020/01/27.
 */
public class ResultProcessFunction extends BroadcastProcessFunction<Result, RuleBase, HitResult> {
    private static final Logger logger = LoggerFactory.getLogger(ResultProcessFunction.class);

    private RuleBase latestRuleBase = null;
    private final String ruleKeyName = "logRule";
    private transient ResultProcessor logProcessor = null;
    private String kafkaIndex = null;

    public ResultProcessFunction(RuleBase ruleBase, String kafkaIndex) {
        this.latestRuleBase = ruleBase;
        this.kafkaIndex = kafkaIndex;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        logProcessor = new ResultProcessorWithRules(ResultProcessor.LOG_PKG);
        if (!logProcessor.loadRules(this.latestRuleBase)) {
            logger.error("Failed to load log rules");
            throw new Exception("Log processor load error");
        }
    }

    @Override
    public void processElement(Result logEntry, ReadOnlyContext readOnlyContext, Collector<HitResult> collector) throws Exception {
        try {
            List<HitResult> result = logProcessor.execute(logEntry);
            if (!result.isEmpty()){
                for (HitResult item : result) {
                    if (kafkaIndex != null) {
                        readOnlyContext.output(Descriptors.kafkaOutputHitResultTag, item);
                    }
                    collector.collect(item);
                }

            }
        } catch (Exception ex) {
            logger.error("Log process error: " + ex.toString());
        }
    }

    @Override
    public void processBroadcastElement(RuleBase ruleBase, Context context, Collector<HitResult> collector) throws Exception {
        BroadcastState<String, RuleBase> ruleState = context.getBroadcastState(Descriptors.ruleStateDescriptor);
        ruleState.put(ruleKeyName, ruleBase);
        if (latestRuleBase != null && StringUtils.equals(latestRuleBase.getHash(), ruleBase.getHash())) {
            logger.info("Receive same log rules, rules: {}", ruleBase.getName());
            return;
        }
//        logger.info("Get " + ruleBase.getRules().size() + " rules, rules: " + ruleBase.getName());
        if (logProcessor != null) {
            logProcessor.destroy();
            if (!logProcessor.loadRules(ruleBase)) {
                logger.error("Failed to load log rules");
            } else {
                logger.info("Log rules are updated, hash:{}", ruleBase.getHash());
                latestRuleBase = ruleBase;
            }
        } else {
            logProcessor = new ResultProcessorWithRules(ResultProcessor.LOG_PKG);
            if (!logProcessor.loadRules(ruleBase)) {
                logger.error("Failed to load log rules");
            } else {
                latestRuleBase = ruleBase;
            }
        }
    }
}
