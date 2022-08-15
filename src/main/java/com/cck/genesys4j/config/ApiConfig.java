package com.cck.genesys4j.config;

public class ApiConfig {
    private final ConfigServer configServer;

    private ApiConfig(ConfigServer configServerConfig) {
        this.configServer = configServerConfig;
    }

    public static ApiConfig load(ConfigServer configServerConfig) {
        return new ApiConfig(configServerConfig);
    }

    public static ApiConfig load(String clientName, String userName, String password,
                                 String primaryConfigHost, Integer primaryConfigPort,
                                 String bkpConfigHost, Integer bkpConfigPort) {
        return load(new ConfigServer(clientName, userName, password,
                new ServerEndpoint(primaryConfigHost, primaryConfigPort),
                new ServerEndpoint(bkpConfigHost, bkpConfigPort)
        ));
    }

    public ConfigServer getConfigServer() {
        return configServer;
    }
}
