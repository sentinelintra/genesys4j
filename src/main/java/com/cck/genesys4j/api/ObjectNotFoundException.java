package com.cck.genesys4j.api;

import com.genesyslab.platform.applicationblocks.com.ICfgQuery;

public class ObjectNotFoundException extends Exception {
    public ObjectNotFoundException(ICfgQuery query) {
        super("Object was not found in ConfigServer, query: " + query.toString());
    }
}
