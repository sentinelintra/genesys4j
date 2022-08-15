package com.cck.genesys4j;

import com.cck.genesys4j.model.*;

import java.util.Collection;
import java.util.Collections;

public interface GenesysApi {
    Integer DEFAULT_TENANT_ID = 101;

    BaseApiResponse connect();

    BaseApiResponse disconnect();

    ObjectIdApiResponse createCampaign(String name, Collection<Integer> callListsId, String path, Integer tenantId);

    default ObjectIdApiResponse createCampaign(String name, Collection<Integer> callListsId, String path) {
        return createCampaign(name, callListsId, path, DEFAULT_TENANT_ID);
    }

    default ObjectIdApiResponse createCampaign(String name, Integer callListId, String path, Integer tenantId) {
        return createCampaign(name, Collections.singletonList(callListId), path, tenantId);
    }

    default ObjectIdApiResponse createCampaign(String name, Integer callListId, String path) {
        return createCampaign(name, callListId, path, DEFAULT_TENANT_ID);
    }

    BaseApiResponse dropCampaign(Integer id);

    BaseApiResponse startCampaign(Integer id);

    BaseApiResponse stopCampaign(Integer id);

    ObjectIdApiResponse createCallList(String name, Integer tableAccessId, String filterName,
                                       Integer maxAttempts, Integer timeFrom, Integer timeUntil,
                                       Collection<String> treatmentsName, Integer tenantId, String path);

    default ObjectIdApiResponse createCallList(String name, Integer tableAccessId, String filterName,
                                               Integer maxAttempts, Integer timeFrom, Integer timeUntil,
                                               Collection<String> treatmentsName, String path) {
        return createCallList(name, tableAccessId, filterName,
                maxAttempts, timeFrom, timeUntil,
                treatmentsName, DEFAULT_TENANT_ID, path);
    }

    BaseApiResponse dropCallList(Integer id);

    ObjectIdApiResponse createTableAccess(String name, String dbAccessPoint, String formatName, String tableName,
                                          Integer tenantId, String path);

    default ObjectIdApiResponse createTableAccess(String name, String dbAccessPoint, String formatName, String tableName,
                                                  String path) {
        return createTableAccess(name, dbAccessPoint, formatName, tableName,
                DEFAULT_TENANT_ID, path);
    }

    BaseApiResponse dropTableAccess(Integer id);

    ObjectIdApiResponse createCampaignGroup(String name, Integer campaignId, Integer agentGroupId, String dealingMode,
                                            String voiceTransferDestinationName, String operationMode,
                                            String optimizationMethod, Integer targetValue, Integer bufferSizeMinimum,
                                            Integer bufferSizeOptimum, Integer numberCpdPorts, Collection<String> connections,
                                            Collection<String> annexOptions);

    ObjectIdApiResponse createCampaignGroup(String name, Integer campaignId, String agentGroupName, String dealingMode,
                                            String voiceTransferDestinationName, String operationMode,
                                            String optimizationMethod, Integer targetValue, Integer bufferSizeMinimum,
                                            Integer bufferSizeOptimum, Integer numberCpdPorts, Collection<String> connections,
                                            Collection<String> annexOptions);

    BaseApiResponse dropCampaignGroup(Integer id);

    ObjectIdApiResponse createSkill(String name, Integer tenantId, String path);

    default ObjectIdApiResponse createSkill(String name, String path) {
        return createSkill(name, DEFAULT_TENANT_ID, path);
    }

    BaseApiResponse dropSkill(Integer id);

    PersonApiResponse queryCfgPerson(String login);

    PersonAgentGroupsApiResponse queryAgentGroups(Integer personId);

    ObjectIdApiResponse createAgentGroup(String name, String script, Integer tenantId, String path);

    default ObjectIdApiResponse createAgentGroup(String name, String script, String path) {
        return createAgentGroup(name, script, DEFAULT_TENANT_ID, path);
    }

    ObjectIdApiResponse createAgentGroup(String name, Integer tenantId, String path);

    default ObjectIdApiResponse createAgentGroup(String name, String path) {
        return createAgentGroup(name, DEFAULT_TENANT_ID, path);
    }

    BaseApiResponse dropAgentGroup(Integer agentGroupId);

    BaseApiResponse addAgentToGroup(Integer groupId, Integer personId);

    BaseApiResponse deleteAgentFromGroup(Integer groupId, Integer personId);

    ObjectIdApiResponse createVirtualQueue(String name, String switchName, Integer tenantId, String path);

    default ObjectIdApiResponse createVirtualQueue(String name, String switchName, String path) {
        return createVirtualQueue(name, switchName, DEFAULT_TENANT_ID, path);
    }

    BaseApiResponse dropVirtualQueue(Integer id);

    BaseApiResponse updatePersonParam(Integer id, String key, String value);

    BaseApiResponse dropPersonParam(Integer id, String key);

    PersonParamApiResponse queryPersonParam(Integer id, String key);

    DnsApiResponse queryDns(String dnType);

    BaseApiResponse updateDn(Integer id, String sectionName, String optionName, String value);
}
