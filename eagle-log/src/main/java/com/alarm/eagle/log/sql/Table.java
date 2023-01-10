package com.alarm.eagle.log.sql;

import java.util.Date;

public class Table {
    private Long id;
    private Long dbId;
    private String name;
    private Date time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDbId() {
        return dbId;
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Table{" +
                "id=" + id +
                ", dbId=" + dbId +
                ", name='" + name + '\'' +
                ", time=" + time +
                '}';
    }
}
