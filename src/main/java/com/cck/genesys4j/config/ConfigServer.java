package com.cck.genesys4j.config;

public class ConfigServer {
    private final String clientName;
    private final String userName;
    private final String password;
    private final ServerEndpoint primaryEndpoint;
    private final ServerEndpoint backupEndpoint;

    public ConfigServer(String clientName, String userName, String password,
                        ServerEndpoint primaryEndpoint, ServerEndpoint backupEndpoint) {
        this.clientName = clientName;
        this.userName = userName;
        this.password = password;
        this.primaryEndpoint = primaryEndpoint;
        this.backupEndpoint = backupEndpoint;
    }

    public String getClientName() {
        return clientName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public ServerEndpoint getPrimaryEndpoint() {
        return primaryEndpoint;
    }

    public ServerEndpoint getBackupEndpoint() {
        return backupEndpoint;
    }
}
