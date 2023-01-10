package com.alarm.eagle.log.http;

import java.util.Date;

public class Srv {
    private Long id;
    private String addr;
    private Date time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Srv{" +
                "id=" + id +
                ", addr='" + addr + '\'' +
                ", time=" + time +
                '}';
    }
}
