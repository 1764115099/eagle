package com.alarm.eagle.log;

import java.util.Date;

/*
    根据RASP日志定义的实体类
 */
public class Message {
    private String id;
    private String requestpath;
    private String querystring;
    private String requestmethod;
    private String requestprotocol;
    private String remoteaddr;
    private String sqlserver;
    private String sql;
    private String requestserver;
    private String sqladdr;
    private String sqlport;
    private String sqluser;
    private String sqlpwd;
    private String sqldb;
    private String sqltable;
    private Date timestamp;
    private Date atTimestamp;

    public String getUrl(){
        return requestpath + (!querystring.isEmpty() ? "?"+querystring : "");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestpath() {
        return requestpath;
    }

    public void setRequestpath(String requestpath) {
        this.requestpath = requestpath;
    }

    public String getQuerystring() {
        return querystring;
    }

    public void setQuerystring(String querystring) {
        this.querystring = querystring;
    }

    public String getRequestmethod() {
        return requestmethod;
    }

    public void setRequestmethod(String requestmethod) {
        this.requestmethod = requestmethod;
    }

    public String getRequestprotocol() {
        return requestprotocol;
    }

    public void setRequestprotocol(String requestprotocol) {
        this.requestprotocol = requestprotocol;
    }

    public String getRemoteaddr() {
        return remoteaddr;
    }

    public void setRemoteaddr(String remoteaddr) {
        this.remoteaddr = remoteaddr;
    }

    public String getSqlserver() {
        return sqlserver;
    }

    public void setSqlserver(String sqlserver) {
        this.sqlserver = sqlserver;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getAtTimestamp() {
        return atTimestamp;
    }

    public void setAtTimestamp(Date atTimestamp) {
        this.atTimestamp = atTimestamp;
    }

    public String getRequestserver() {
        return requestserver;
    }

    public void setRequestserver(String requestserver) {
        this.requestserver = requestserver;
    }

    public String getSqladdr() {
        return sqladdr;
    }

    public void setSqladdr(String sqladdr) {
        this.sqladdr = sqladdr;
    }

    public String getSqlport() {
        return sqlport;
    }

    public void setSqlport(String sqlport) {
        this.sqlport = sqlport;
    }

    public String getSqluser() {
        return sqluser;
    }

    public void setSqluser(String sqluser) {
        this.sqluser = sqluser;
    }

    public String getSqlpwd() {
        return sqlpwd;
    }

    public void setSqlpwd(String sqlpwd) {
        this.sqlpwd = sqlpwd;
    }

    public String getSqldb() {
        return sqldb;
    }

    public void setSqldb(String sqldb) {
        this.sqldb = sqldb;
    }

    public String getSqltable() {
        return sqltable;
    }

    public void setSqltable(String sqltable) {
        this.sqltable = sqltable;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", requestpath='" + requestpath + '\'' +
                ", querystring='" + querystring + '\'' +
                ", requestmethod='" + requestmethod + '\'' +
                ", requestprotocol='" + requestprotocol + '\'' +
                ", remoteaddr='" + remoteaddr + '\'' +
                ", sqlserver='" + sqlserver + '\'' +
                ", sql='" + sql + '\'' +
                ", requestserver='" + requestserver + '\'' +
                ", sqladdr='" + sqladdr + '\'' +
                ", sqlport='" + sqlport + '\'' +
                ", sqluser='" + sqluser + '\'' +
                ", sqlpwd='" + sqlpwd + '\'' +
                ", sqldb='" + sqldb + '\'' +
                ", sqltable='" + sqltable + '\'' +
                ", timestamp=" + timestamp +
                ", atTimestamp=" + atTimestamp +
                '}';
    }
}