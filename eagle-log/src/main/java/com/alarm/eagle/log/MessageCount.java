package com.alarm.eagle.log;

/*
    根据Url聚合和输出的实体类
 */
public class MessageCount {
        private Integer count;
        private String url;

        public void init(){
            count=new Integer(0);
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return "MessageCount{" +
                    "count=" + count +
                    ", url='" + url + '\'' +
                    '}';
        }
    }