package com.alarm.eagle.log.sql;

import java.util.Date;

public class ApiTable {
    private Long id;
    private Long apiId;
    private Long tableId;
    private Date time;
    private Long accountId;
    private Long requestId;

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

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return "ApiTable{" +
                "id=" + id +
                ", apiId=" + apiId +
                ", tableId=" + tableId +
                ", time=" + time +
                ", accountId=" + accountId +
                ", requestId=" + requestId +
                '}';
    }
}
