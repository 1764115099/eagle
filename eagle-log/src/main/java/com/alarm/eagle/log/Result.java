package com.alarm.eagle.log;


import com.alarm.eagle.log.http.HttpResult;
import com.alarm.eagle.log.sql.SqlResult;

public class Result {
    private HttpResult httpResult;
    private SqlResult sqlResult;
    private Message message;

    public HttpResult getHttpResult() {
        return httpResult;
    }

    public void setHttpResult(HttpResult httpResult) {
        this.httpResult = httpResult;
    }

    public SqlResult getSqlResult() {
        return sqlResult;
    }

    public void setSqlResult(SqlResult sqlResult) {
        this.sqlResult = sqlResult;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Result{" +
                "httpResult=" + httpResult +
                ", sqlResult=" + sqlResult +
                ", message=" + message +
                '}';
    }
}
