package com.alarm.eagle.log.http;

public class HttpResult {
    private Srv srv;
    private Api api;
    private Client client;
    private ClientApi clientApi;

    public Srv getSrv() {
        return srv;
    }

    public void setSrv(Srv srv) {
        this.srv = srv;
    }

    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ClientApi getClientApi() {
        return clientApi;
    }

    public void setClientApi(ClientApi clientApi) {
        this.clientApi = clientApi;
    }

    @Override
    public String toString() {
        return "httpResult{" +
                "srv=" + srv +
                ", api=" + api +
                ", client=" + client +
                ", clientApi=" + clientApi +
                '}';
    }
}
