package com.cck.genesys4j.model;

public interface ApiResponse {
    String SUCCESS_MESSAGE = "";

    class Errors {
        public static final Integer SUCCESS = 0;
        public static final Integer CFG_NOT_CONNECTED = 1;
        public static final Integer CFG_NOT_DISCONNECTED = 2;
        public static final Integer CFG_ERROR = 3;
        public static final Integer CFG_OBJECT_NOT_FOUND = 4;
        public static final Integer INTERRUPTED_ERROR = 5;
    }
}


