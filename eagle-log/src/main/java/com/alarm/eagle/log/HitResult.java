package com.alarm.eagle.log;

import com.alarm.eagle.util.DateUtil;
import com.alarm.eagle.util.Md5Util;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;

import java.util.Date;

public class HitResult {
    private Long id;
    private Integer ruleTemplateId;
    private Long requestId;
    private Content content;
    private boolean hit;
    private String jsonStr = null;

    public HitResult() {
    }

    public HitResult(JSONObject json) {
        jsonStr = json.toString();
        id = json.getLong("id");
        ruleTemplateId = json.getInteger("rule_template_id");
        requestId = json.getLong("request_id");
        JSONObject contentJsonObject = json.getJSONObject("content");
        content = new Content(contentJsonObject);
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id",id);
        json.put("rule_template_id",ruleTemplateId);
        json.put("request_id",requestId);
        json.put("content",content);
        return json;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRuleTemplateId() {
        return ruleTemplateId;
    }

    public void setRuleTemplateId(Integer ruleTemplateId) {
        this.ruleTemplateId = ruleTemplateId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }



    @Override
    public String toString() {
        return "HitResult{" +
                "id=" + id +
                ", ruleTemplateId=" + ruleTemplateId +
                ", requestId=" + requestId +
                ", content=" + content +
                ", hit=" + hit +
                '}';
    }
}
