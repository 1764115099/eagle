package com.alarm.eagle.log.http;

import java.util.Date;

public class ClientApi {
    private Long id;
    private Long apiId;
    private Long clientId;
    private Date time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ClientApi{" +
                "id=" + id +
                ", apiId=" + apiId +
                ", clientId=" + clientId +
                ", time=" + time +
                '}';
    }
}
