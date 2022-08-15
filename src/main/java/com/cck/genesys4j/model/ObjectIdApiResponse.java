package com.cck.genesys4j.model;

import com.cck.genesys4j.api.ObjectNotFoundException;
import com.genesyslab.platform.applicationblocks.com.ConfigException;

public class ObjectIdApiResponse extends BaseApiResponse {
    private final Integer objectId;

    protected ObjectIdApiResponse(Integer objectId, Integer errorCode, String errorMessage) {
        super(errorCode, errorMessage);
        this.objectId = objectId;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public static ObjectIdApiResponse success(Integer objectId) {
        return new ObjectIdApiResponse(objectId, Errors.SUCCESS, ApiResponse.SUCCESS_MESSAGE);
    }

    public static ObjectIdApiResponse failure(Integer errorCode, String errorMessage) {
        return new ObjectIdApiResponse(null, errorCode, errorMessage);
    }

    public static ObjectIdApiResponse configException(ConfigException e) {
        return ObjectIdApiResponse.failure(ApiResponse.Errors.CFG_ERROR,
                "Unable to request on config server, error: " + e.getMessage());
    }

    public static ObjectIdApiResponse objectNotFoundException(ObjectNotFoundException e) {
        return failure(ApiResponse.Errors.CFG_OBJECT_NOT_FOUND,
                "Object was not found, error: " + e.getMessage());
    }
}
