package com.alarm.eagle.log.sql;

public class SqlResult {
    private Dbsrv dbsrv;
    private Db db;
    private Account account;
    private Table table;
    private ApiTable apiTable;

    public Dbsrv getDbsrv() {
        return dbsrv;
    }

    public void setDbsrv(Dbsrv dbsrv) {
        this.dbsrv = dbsrv;
    }

    public Db getDb() {
        return db;
    }

    public void setDb(Db db) {
        this.db = db;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public ApiTable getApiTable() {
        return apiTable;
    }

    public void setApiTable(ApiTable apiTable) {
        this.apiTable = apiTable;
    }

    @Override
    public String toString() {
        return "SqlResult{" +
                "dbsrv=" + dbsrv +
                ", db=" + db +
                ", account=" + account +
                ", table=" + table +
                ", apiTable=" + apiTable +
                '}';
    }
}
