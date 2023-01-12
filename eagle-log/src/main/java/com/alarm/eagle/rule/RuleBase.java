package com.alarm.eagle.rule;

import com.alarm.eagle.util.Md5Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luxiaoxun on 2020/01/27.
 */
public class RuleBase implements Serializable {
    private static final long serialVersionUID = -6249685986229931397L;
    private String rule = "";
    private String name = "Empty-Name";
    private String hash = "";

    public static RuleBase createRuleBase(JsonObject jsonObject) {
        RuleBase ruleBase = new RuleBase();
        String str = jsonObject.toString();
        ruleBase.setRule(str);


        ruleBase.hash = Md5Util.getMd5(str);
        ruleBase.name = "rules-" + ruleBase.hash;
        return ruleBase;
    }

    public static RuleBase createRuleBase(JsonObject jsonObject, String name) {
        RuleBase ruleBase = createRuleBase(jsonObject);
        ruleBase.name = name;
        return ruleBase;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "RuleBase{" +
                "rule=" + rule.toString() +
                ", name='" + name + '\'' +
                ", hash='" + hash + '\'' +
                '}';
    }
}
