package com.cck.genesys4j.model;

import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentGroup;

import java.util.Collection;

public class PersonAgentGroupsApiResponse extends BaseApiResponse {
    private final Collection<CfgAgentGroup> agentGroups;

    public PersonAgentGroupsApiResponse(Collection<CfgAgentGroup> agentGroups, Integer errorCode, String errorMessage) {
        super(errorCode, errorMessage);
        this.agentGroups = agentGroups;
    }

    public static PersonAgentGroupsApiResponse success(Collection<CfgAgentGroup> agentGroups) {
        return new PersonAgentGroupsApiResponse(agentGroups, Errors.SUCCESS, ApiResponse.SUCCESS_MESSAGE);
    }

    public static PersonAgentGroupsApiResponse failure(Integer errorCode, String errorMessage) {
        return new PersonAgentGroupsApiResponse(null, errorCode, errorMessage);
    }

    public static PersonAgentGroupsApiResponse configException(ConfigException e) {
        return failure(ApiResponse.Errors.CFG_ERROR,
                "Unable to request on config server, error: " + e.getMessage());
    }

    public static PersonAgentGroupsApiResponse interruptedException(InterruptedException e) {
        return failure(ApiResponse.Errors.INTERRUPTED_ERROR,
                "Retrieving multiple objects process was interrupted, error: " + e.getMessage());
    }

    public Collection<CfgAgentGroup> getAgentGroups() {
        return agentGroups;
    }
}
