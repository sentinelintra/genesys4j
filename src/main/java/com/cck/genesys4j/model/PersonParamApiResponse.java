package com.cck.genesys4j.model;

import com.cck.genesys4j.api.ObjectNotFoundException;
import com.genesyslab.platform.applicationblocks.com.ConfigException;

public class
PersonParamApiResponse extends BaseApiResponse {
    private final String value;

    protected PersonParamApiResponse(String value, Integer errorCode, String errorMessage) {
        super(errorCode, errorMessage);
        this.value = value;
    }

    public static PersonParamApiResponse success(Object paramValue) {
        return new PersonParamApiResponse(paramValue.toString(), Errors.SUCCESS, ApiResponse.SUCCESS_MESSAGE);
    }

    public static PersonParamApiResponse failure(Integer errorCode, String errorMessage) {
        return new PersonParamApiResponse(null, errorCode, errorMessage);
    }

    public static PersonParamApiResponse configException(ConfigException e) {
        return failure(Errors.CFG_ERROR,
                "Unable to request on config server, error: " + e.getMessage());
    }

    public static PersonParamApiResponse objectNotFoundException(ObjectNotFoundException e) {
        return failure(ApiResponse.Errors.CFG_OBJECT_NOT_FOUND,
                "Object was not found, error: " + e.getMessage());
    }

    public String getValue() {
        return value;
    }
}
