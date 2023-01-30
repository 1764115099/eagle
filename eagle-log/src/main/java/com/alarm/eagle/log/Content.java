package com.alarm.eagle.log;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

public class Content {
    private String requestProtocol;
    private String requestMethod;
    private String requestHeader;
    private String requestBody;
    private String requestServer;
    private String requestPath;
    private String requestRemoteAddr;
    private String sqlType;
    private String sqlServer;
    private String sqlPort;
    private String sqlUserName;
    private String sqlPwd;
    private String sqlDb;
    private String sqlTable;
    private String sqlResult;

    public Content() {
    }

    public Content(JSONObject json) {
//        jsonStr = json.toString();
        requestProtocol = json.getString("Request protocol");
        requestMethod = json.getString("Request method");
        requestHeader = json.getString("Request header");
        requestBody = json.getString("Request body");
        requestServer = json.getString("Request server");
        requestPath = json.getString("Request path");
        requestRemoteAddr = json.getString("Request remoteAddr");
        sqlType = json.getString("SQL type");
        sqlServer = json.getString("SQL server");
        sqlPort = json.getString("SQL port");
        sqlUserName = json.getString("SQL username");
        sqlPwd = json.getString("SQL pwd");
        sqlDb = json.getString("SQL db");
        sqlTable = json.getString("SQL table");
        sqlResult = json.getString("SQL result");
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("Request protocol",requestProtocol);
        json.put("Request method",requestMethod);
        json.put("Request header", requestHeader);
        json.put("Request body", requestBody);
        json.put("Request server",requestServer);
        json.put("Request path",requestPath);
        json.put("Request remoteAddr",requestRemoteAddr);
        json.put("SQL type",sqlType);
        json.put("SQL server",sqlServer);
        json.put("SQL port",sqlPort);
        json.put("SQL username",sqlUserName);
        json.put("SQL pwd",sqlPwd);
        json.put("SQL db",sqlDb);
        json.put("SQL table",sqlTable);
        json.put("SQL result",sqlResult);

        return json;
    }

    public String getRequestProtocol() {
        return requestProtocol;
    }

    public void setRequestProtocol(String requestProtocol) {
        this.requestProtocol = requestProtocol;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(String requestHeader) {
        this.requestHeader = requestHeader;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getRequestServer() {
        return requestServer;
    }

    public void setRequestServer(String requestServer) {
        this.requestServer = requestServer;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public String getRequestRemoteAddr() {
        return requestRemoteAddr;
    }

    public void setRequestRemoteAddr(String requestRemoteAddr) {
        this.requestRemoteAddr = requestRemoteAddr;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public String getSqlServer() {
        return sqlServer;
    }

    public void setSqlServer(String sqlServer) {
        this.sqlServer = sqlServer;
    }

    public String getSqlPort() {
        return sqlPort;
    }

    public void setSqlPort(String sqlPort) {
        this.sqlPort = sqlPort;
    }

    public String getSqlUserName() {
        return sqlUserName;
    }

    public void setSqlUserName(String sqlUserName) {
        this.sqlUserName = sqlUserName;
    }

    public String getSqlPwd() {
        return sqlPwd;
    }

    public void setSqlPwd(String sqlPwd) {
        this.sqlPwd = sqlPwd;
    }

    public String getSqlDb() {
        return sqlDb;
    }

    public void setSqlDb(String sqlDb) {
        this.sqlDb = sqlDb;
    }

    public String getSqlTable() {
        return sqlTable;
    }

    public void setSqlTable(String sqlTable) {
        this.sqlTable = sqlTable;
    }

    public String getSqlResult() {
        return sqlResult;
    }

    public void setSqlResult(String sqlResult) {
        this.sqlResult = sqlResult;
    }

    @Override
    public String toString() {
        return "Content{" +
                "requestProtocol='" + requestProtocol + '\'' +
                ", requestMethod='" + requestMethod + '\'' +
                ", requestHeader='" + requestHeader + '\'' +
                ", requestBody='" + requestBody + '\'' +
                ", requestServer='" + requestServer + '\'' +
                ", requestPath='" + requestPath + '\'' +
                ", requestRemoteAddr='" + requestRemoteAddr + '\'' +
                ", sqlType='" + sqlType + '\'' +
                ", sqlServer='" + sqlServer + '\'' +
                ", sqlPort='" + sqlPort + '\'' +
                ", sqlUserName='" + sqlUserName + '\'' +
                ", sqlPwd='" + sqlPwd + '\'' +
                ", sqlDb='" + sqlDb + '\'' +
                ", sqlTable='" + sqlTable + '\'' +
                ", sqlResult='" + sqlResult + '\'' +
                '}';
    }
}
