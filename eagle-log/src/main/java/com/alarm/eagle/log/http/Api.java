package com.alarm.eagle.log.http;

import java.util.Date;

public class Api {
    private Long id;
    private Long srvId;
    private String url;
    private Date time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSrvId() {
        return srvId;
    }

    public void setSrvId(Long srvId) {
        this.srvId = srvId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Api{" +
                "id=" + id +
                ", srvId=" + srvId +
                ", url='" + url + '\'' +
                ", time=" + time +
                '}';
    }
}
