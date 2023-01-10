package com.alarm.eagle.log.http;

public class Client {
    private Long id;
    private String ip;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                '}';
    }
}
