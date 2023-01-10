package com.alarm.eagle.log;


import com.alarm.eagle.log.http.HttpResult;
import com.alarm.eagle.log.sql.SqlResult;

public class Result {
    private HttpResult httpResult;
    private SqlResult sqlResult;

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

    @Override
    public String toString() {
        return "Result{" +
                "httpResult=" + httpResult +
                ", sqlResult=" + sqlResult +
                '}';
    }
}
