package com.cck.genesys4j.config;

public class ServerEndpoint {
    private final String host;
    private final Integer port;

    public ServerEndpoint(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public Integer getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }
}
