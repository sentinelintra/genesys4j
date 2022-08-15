package com.cck.genesys4j.model;

import com.cck.genesys4j.api.ObjectNotFoundException;
import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentLoginInfo;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPerson;

import java.util.Collection;
import java.util.stream.Collectors;

public class PersonApiResponse extends ObjectIdApiResponse {
    private final Collection<Integer> agentLoginIds;
    private final String firstName;
    private final String lastName;
    private final Integer state;

    protected PersonApiResponse(Integer objectId, Collection<Integer> agentLoginIds, String firstName, String lastName,
                             Integer state, Integer errorCode, String errorMessage) {
        super(objectId, errorCode, errorMessage);
        this.agentLoginIds = agentLoginIds;
        this.firstName = firstName;
        this.lastName = lastName;
        this.state = state;
    }

    public static PersonApiResponse success(CfgPerson cfgPerson) {
        return new PersonApiResponse(cfgPerson.getDBID(),
                cfgPerson.getAgentInfo().getAgentLogins().stream().map(CfgAgentLoginInfo::getAgentLoginDBID)
                        .collect(Collectors.toList()),
                cfgPerson.getFirstName(), cfgPerson.getLastName(), cfgPerson.getState().asInteger(),
                Errors.SUCCESS, ApiResponse.SUCCESS_MESSAGE);
    }

    public static PersonApiResponse failure(Integer errorCode, String errorMessage) {
        return new PersonApiResponse(null, null, null, null, null,
                errorCode, errorMessage);
    }

    public static PersonApiResponse configException(ConfigException e) {
        return failure(ApiResponse.Errors.CFG_ERROR,
                "Unable to request on config server, error: " + e.getMessage());
    }

    public static PersonApiResponse objectNotFoundException(ObjectNotFoundException e) {
        return failure(ApiResponse.Errors.CFG_OBJECT_NOT_FOUND,
                "Object was not found, error: " + e.getMessage());
    }

    public Collection<Integer> getAgentLoginIds() {
        return agentLoginIds;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getState() {
        return state;
    }
}
