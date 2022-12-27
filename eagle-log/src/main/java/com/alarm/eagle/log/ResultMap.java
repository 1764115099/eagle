package com.alarm.eagle.log;

import com.alarm.eagle.util.DateUtil;
import com.alarm.eagle.util.Md5Util;
import com.alarm.eagle.util.StringUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


import java.util.Date;

/*
    初次测试聚合以及sink操作时定义的实体类，后续如不需要，可删除
 */
public class ResultMap {
        private String ip;
        private String id;
        private String index;
        private Integer count;
        private String esIndexPrefix = "";
        private String esIndexPostfix = "yyyy-MM-dd";
        private Date timestamp;
        private Date atTimestamp;
        private String jsonStr = null;

        public Date getAtTimestamp() {
            return atTimestamp;
        }

        public void setAtTimestamp(Date atTimestamp) {
            this.atTimestamp = atTimestamp;
        }

        private String indexTarget = "";
        private long offset = -1;

        public String getJsonStr() {
            return jsonStr;
        }

        public String getIndexTarget() {
            return indexTarget;
        }

        public void setIndexTarget(String indexTarget) {
            this.indexTarget = indexTarget;
        }

        public long getOffset() {
            return offset;
        }

        public void setOffset(long offset) {
            this.offset = offset;
        }

        public void setJsonStr(String jsonStr) {
            this.jsonStr = jsonStr;
        }
        public ResultMap(){
            atTimestamp = new Date();
        }
        public ResultMap(JsonObject json) {
            jsonStr = json.toString();

            if (json.has("ip")) {
                ip = json.get("ip").getAsString();
            }
            if (json.has("index")) {
                index = json.get("index").getAsString();
            }
            if (json.has("timestamp")) {
                timestamp = DateUtil.toAtTimestampWithZone(json.get("timestamp").getAsString());
            }

            if (json.has("offset")) {
                offset = json.get("offset").getAsLong();
            }

            id = Md5Util.getMd5(json.toString());

            if (json.has("indexTarget")) {
                indexTarget = json.get("indexTarget").getAsString();
            }

        }

        public Date getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
        }

        public void init(){
            count=new Integer(0);
        }

        public String generateIndexName() {
            String index = "sinkestest-12-11";
            return index;
        }

        private void setJson(JsonObject json, String key, Object value) {
            if (!StringUtil.isEmpty(key) && value != null) {
                json.add(key, new Gson().toJsonTree(value));
            }
        }

        public JsonObject toJSON() {
            JsonObject json = new JsonObject();
            json.addProperty("id",id);
            json.addProperty("ip", ip);
            json.addProperty("index", index);
            json.addProperty("count",count);
            if (timestamp != null) {
                setJson(json, "timestamp", DateUtil.getEsString(timestamp.getTime()));
            }

            return json;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEsIndexPrefix() {
            return esIndexPrefix;
        }

        public void setEsIndexPrefix(String esIndexPrefix) {
            this.esIndexPrefix = esIndexPrefix;
        }

        public String getEsIndexPostfix() {
            return esIndexPostfix;
        }

        public void setEsIndexPostfix(String esIndexPostfix) {
            this.esIndexPostfix = esIndexPostfix;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        @Override
        public String toString() {
            return "ResultMap{" +
                    "ip='" + ip + '\'' +
                    ", id='" + id + '\'' +
                    ", index='" + index + '\'' +
                    ", count=" + count +
                    ", esIndexPrefix='" + esIndexPrefix + '\'' +
                    ", esIndexPostfix='" + esIndexPostfix + '\'' +
                    ", timestamp=" + timestamp +
                    ", jsonStr='" + jsonStr + '\'' +
                    ", indexTarget='" + indexTarget + '\'' +
                    ", offset=" + offset +
                    '}';
        }
    }

