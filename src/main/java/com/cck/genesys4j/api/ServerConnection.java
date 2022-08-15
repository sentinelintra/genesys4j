package com.cck.genesys4j.api;

import com.cck.genesys4j.config.ServerEndpoint;
import com.genesyslab.platform.applicationblocks.warmstandby.WarmStandbyConfiguration;
import com.genesyslab.platform.applicationblocks.warmstandby.WarmStandbyService;
import com.genesyslab.platform.commons.protocol.ChannelState;
import com.genesyslab.platform.commons.protocol.Endpoint;
import com.genesyslab.platform.commons.protocol.Protocol;
import com.genesyslab.platform.commons.protocol.ProtocolException;

public class ServerConnection {
    private final Protocol protocol;
    private final WarmStandbyService warmStandbyService;

    public ServerConnection(Protocol protocol,
                            ServerEndpoint endpointConfig) {
        this(protocol, endpointConfig, endpointConfig);
    }

    public ServerConnection(Protocol protocol,
                            ServerEndpoint primaryEndpointConfig, ServerEndpoint backupEndpointConfig) {
        this(protocol, createEndpoint(primaryEndpointConfig), createEndpoint(backupEndpointConfig));
    }

    public ServerConnection(Protocol protocol, Endpoint activeEndpoint, Endpoint standbyEndpoint) {
        this.protocol = protocol;
        WarmStandbyConfiguration warmStandbyConfig = new WarmStandbyConfiguration(activeEndpoint, standbyEndpoint);
        warmStandbyConfig.setTimeout(5000);
        warmStandbyConfig.setAttempts((short) 2);

        this.warmStandbyService = new WarmStandbyService(protocol);
        this.warmStandbyService.applyConfiguration(warmStandbyConfig);
    }



    public void connect() throws ProtocolException, InterruptedException {
        this.warmStandbyService.start();
        this.protocol.open();
    }

    public void disconnect() throws ProtocolException, InterruptedException {
        if (this.protocol.getState() != ChannelState.Closed)
            this.protocol.close();
        this.warmStandbyService.stop();
    }

    private static Endpoint createEndpoint(ServerEndpoint endpointConfig) {
        return new Endpoint(endpointConfig.getHost(), endpointConfig.getPort());
    }
}
