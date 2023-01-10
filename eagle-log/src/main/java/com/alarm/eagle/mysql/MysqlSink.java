package com.alarm.eagle.mysql;

import com.alarm.eagle.log.Result;
import com.alarm.eagle.log.http.Api;
import com.alarm.eagle.log.http.Client;
import com.alarm.eagle.log.http.ClientApi;
import com.alarm.eagle.log.http.Srv;
import com.alarm.eagle.log.sql.*;
import com.alarm.eagle.util.DateUtil;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/*
    增量和全量数据入库Mysql
 */
public class MysqlSink extends RichSinkFunction<Result> {
    private Connection connection;
//    private PreparedStatement preparedStatement;
    private PreparedStatement srvPreparedStatement;
    private PreparedStatement apiPreparedStatement;
    private PreparedStatement clientPreparedStatement;
    private PreparedStatement clientApiPreparedStatement;
    private PreparedStatement dbSrvPreparedStatement;
    private PreparedStatement dbPreparedStatement;
    private PreparedStatement accountPreparedStatement;
    private PreparedStatement tablePreparedStatement;
    private PreparedStatement apiTablePreparedStatement;

    private String srvSql = "INSERT INTO srv(id,addr,time) VALUES(?,?,?)";
    private String apiSql = "INSERT INTO api(id,srv_id,url,time) VALUES(?,?,?,?)";
    private String clientSql = "INSERT INTO client(id,ip) VALUES(?,?)";
    private String clientApiSql = "INSERT INTO client_api(id,api_id,client_id,time) VALUES(?,?,?,?)";
    private String dbSrvSql = "INSERT INTO dbsrv(id,ip,port,type,time) VALUES(?,?,?,?,?)";
    private String dbSql = "INSERT INTO db(id,dbsrv_id,name) VALUES(?,?,?)";
    private String accountSql = "INSERT INTO account(id,db_id,username,pwd) VALUES(?,?,?,?)";
    private String tableSql = "INSERT INTO `table`(id,db_id,name,time) VALUES(?,?,?,?)";
    private String apiTableSql = "INSERT INTO api_table(id,api_id,table_id,account_id,request_id,time) VALUES(?,?,?,?,?,?)";


    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        //加载数据库驱动
        Class.forName("com.mysql.jdbc.Driver");
        //获取连接
        connection = DriverManager.getConnection("jdbc:mysql://112.124.65.22:31242/datasec", "root", "111111");
    }

    @Override
    public void close() throws Exception {
        super.close();
        if (srvPreparedStatement != null) {
            srvPreparedStatement.close();
        }
        if (apiPreparedStatement != null) {
            apiPreparedStatement.close();
        }
        if (clientPreparedStatement != null) {
            clientPreparedStatement.close();
        }
        if (clientApiPreparedStatement != null) {
            clientApiPreparedStatement.close();
        }
        if (dbSrvPreparedStatement != null) {
            dbSrvPreparedStatement.close();
        }
        if (dbPreparedStatement != null) {
            dbPreparedStatement.close();
        }
        if (accountPreparedStatement != null) {
            accountPreparedStatement.close();
        }
        if (tablePreparedStatement != null) {
            tablePreparedStatement.close();
        }
        if (apiTablePreparedStatement != null) {
            apiTablePreparedStatement.close();
        }
        if (connection != null) {
            connection.close();
        }
        super.close();
    }

    @Override
    public void invoke(Result result, Context context) throws Exception {
        try {
            /*
                Srv增量入库
             */
            Srv srv = result.getHttpResult().getSrv();
            if (srv != null) {
                srvPreparedStatement = connection.prepareStatement(srvSql);
                srvPreparedStatement.setLong(1,srv.getId());
                srvPreparedStatement.setString(2,srv.getAddr());
                srvPreparedStatement.setTimestamp(3, DateUtil.getSqlDate(srv.getTime()));
                srvPreparedStatement.execute();
            }

            /*
                Api增量入库
             */
            Api api = result.getHttpResult().getApi();
            if (api != null) {
                apiPreparedStatement = connection.prepareStatement(apiSql);
                apiPreparedStatement.setLong(1,api.getId());
                apiPreparedStatement.setLong(2,api.getSrvId());
                apiPreparedStatement.setString(3,api.getUrl());
                apiPreparedStatement.setTimestamp(4,DateUtil.getSqlDate(api.getTime()));
                apiPreparedStatement.execute();
            }

            /*
                Client增量入库
             */
            Client client = result.getHttpResult().getClient();
            if (client != null) {
                clientPreparedStatement = connection.prepareStatement(clientSql);
                clientPreparedStatement.setLong(1,client.getId());
                clientPreparedStatement.setString(2,client.getIp());
                clientPreparedStatement.execute();
            }

            /*
                ClientApi全量入库
             */
            ClientApi clientApi = result.getHttpResult().getClientApi();
            if (clientApi != null) {
                clientApiPreparedStatement = connection.prepareStatement(clientApiSql);
                clientApiPreparedStatement.setLong(1, clientApi.getId());
                clientApiPreparedStatement.setLong(2, clientApi.getApiId());
                clientApiPreparedStatement.setLong(3, clientApi.getClientId());
                clientApiPreparedStatement.setTimestamp(4, DateUtil.getSqlDate(clientApi.getTime()));
                clientApiPreparedStatement.execute();
            }


            /*
                Dbsrv增量入库
             */
            Dbsrv dbsrv = result.getSqlResult().getDbsrv();
            if (dbsrv != null) {
                dbSrvPreparedStatement = connection.prepareStatement(dbSrvSql);
                dbSrvPreparedStatement.setLong(1,dbsrv.getId());
                dbSrvPreparedStatement.setString(2,dbsrv.getIp());
                dbSrvPreparedStatement.setString(3,dbsrv.getPort());
                dbSrvPreparedStatement.setString(4,dbsrv.getType());
                dbSrvPreparedStatement.setTimestamp(5,DateUtil.getSqlDate(dbsrv.getTime()));
                dbSrvPreparedStatement.execute();
            }

            /*
                Db增量入库
             */
            Db db = result.getSqlResult().getDb();
            if (db != null) {
                dbPreparedStatement = connection.prepareStatement(dbSql);
                dbPreparedStatement.setLong(1,db.getId());
                dbPreparedStatement.setLong(2,db.getDbsrvId());
                dbPreparedStatement.setString(3,db.getName());
                dbPreparedStatement.execute();
            }

            /*
                Account增量入库
             */
            Account account = result.getSqlResult().getAccount();
            if (account != null) {
                accountPreparedStatement = connection.prepareStatement(accountSql);
                accountPreparedStatement.setLong(1,account.getId());
                accountPreparedStatement.setLong(2,account.getDbId());
                accountPreparedStatement.setString(3,account.getUsername());
                accountPreparedStatement.setString(4,account.getPwd());
                accountPreparedStatement.execute();
            }

            /*
                Table增量入库
             */
            Table table = result.getSqlResult().getTable();
            if (table != null) {
                tablePreparedStatement = connection.prepareStatement(tableSql);
                tablePreparedStatement.setLong(1,table.getId());
                tablePreparedStatement.setLong(2,table.getDbId());
                tablePreparedStatement.setString(3,table.getName());
                tablePreparedStatement.setTimestamp(4,DateUtil.getSqlDate(table.getTime()));
                tablePreparedStatement.execute();
            }


            /*
                ApiTable全量入库
             */
            ApiTable apiTable = result.getSqlResult().getApiTable();
            apiTablePreparedStatement = connection.prepareStatement(apiTableSql);
            apiTablePreparedStatement.setLong(1,apiTable.getId());
            apiTablePreparedStatement.setLong(2,apiTable.getApiId());
            apiTablePreparedStatement.setLong(3,apiTable.getTableId());
            apiTablePreparedStatement.setLong(4,apiTable.getAccountId());
            apiTablePreparedStatement.setLong(5,apiTable.getRequestId());
            apiTablePreparedStatement.setTimestamp(6,DateUtil.getSqlDate(apiTable.getTime()));
            apiTablePreparedStatement.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
