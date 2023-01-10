package com.alarm.eagle.log.sql;

public class Db {
    private Long id;
    private Long dbsrvId;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDbsrvId() {
        return dbsrvId;
    }

    public void setDbsrvId(Long dbsrvId) {
        this.dbsrvId = dbsrvId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Db{" +
                "id=" + id +
                ", dbsrvId=" + dbsrvId +
                ", name='" + name + '\'' +
                '}';
    }
}
