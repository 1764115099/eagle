package com.alarm.eagle.drools;

import com.alarm.eagle.log.HitResult;
import com.alarm.eagle.log.Result;
import com.alarm.eagle.rule.RuleBase;

import java.util.List;

/**
 * Created by luxiaoxun on 2020/01/29.
 */
public interface ResultProcessor {
    String LOG_PKG = "logrules";

//    List<Result> execute(String msg);

    List<HitResult> execute(Result entry);

    boolean loadRules(RuleBase ruleBase);

//    boolean addRule(Rule rule);

//    boolean removeRule(Rule rule);

    void destroy();
}
