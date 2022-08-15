package com.cck.genesys4j;

import com.cck.genesys4j.config.ApiConfig;

public class GenesysApiFactory {

    public static GenesysApi createInstance(ApiConfig config) {
        return new GenesysApiImpl(config);
    }
}
