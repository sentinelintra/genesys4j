package com.cck.genesys4j.model;

import com.cck.genesys4j.api.ObjectNotFoundException;
import com.genesyslab.platform.applicationblocks.com.ConfigException;

public class BaseApiResponse implements ApiResponse {
    private final Integer errorCode;
    private final String errorMessage;

    protected BaseApiResponse(Integer errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }


    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static BaseApiResponse success() {
        return new BaseApiResponse(Errors.SUCCESS, ApiResponse.SUCCESS_MESSAGE);
    }

    public static BaseApiResponse failure(Integer errorCode, String errorMessage) {
        return new BaseApiResponse(errorCode, errorMessage);
    }

    public static BaseApiResponse configException(ConfigException e) {
        return failure(ApiResponse.Errors.CFG_ERROR,
                "Unable to request on config server, error: " + e.getMessage());
    }

    public static BaseApiResponse objectNotFoundException(ObjectNotFoundException e) {
        return failure(ApiResponse.Errors.CFG_OBJECT_NOT_FOUND,
                "Object was not found, error: " + e.getMessage());
    }
}
