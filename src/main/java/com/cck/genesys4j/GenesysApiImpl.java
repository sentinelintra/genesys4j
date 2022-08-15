package com.cck.genesys4j;

import com.cck.genesys4j.api.GenesysConfig;
import com.cck.genesys4j.api.ObjectNotFoundException;
import com.cck.genesys4j.api.ServerConnection;
import com.cck.genesys4j.config.ApiConfig;
import com.cck.genesys4j.config.ConfigServer;
import com.cck.genesys4j.model.*;
import com.genesyslab.platform.applicationblocks.com.ConfServiceFactory;
import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.platform.applicationblocks.com.objects.*;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import com.genesyslab.platform.commons.protocol.ProtocolException;
import com.genesyslab.platform.configuration.protocol.ConfServerProtocol;
import com.genesyslab.platform.configuration.protocol.types.CfgDNType;
import com.genesyslab.platform.configuration.protocol.types.CfgRouteType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class GenesysApiImpl implements GenesysApi {
    final private ApiConfig config;

    private ServerConnection configurationServerConnection;
    private GenesysConfig genesysConfigApi;

    public GenesysApiImpl(ApiConfig config) {
        this.config = config;
    }

    @Override
    public BaseApiResponse connect() {
        try {

            ConfServerProtocol protocol = createConfProtocol(config.getConfigServer());
            configurationServerConnection = new ServerConnection(protocol,
                    config.getConfigServer().getPrimaryEndpoint(),
                    config.getConfigServer().getBackupEndpoint()
            );
            genesysConfigApi = new GenesysConfig(ConfServiceFactory.createConfService(protocol));
            configurationServerConnection.connect();
            return BaseApiResponse.success();
        } catch (ProtocolException | InterruptedException e) {
            return BaseApiResponse.failure(ApiResponse.Errors.CFG_NOT_CONNECTED,
                    "Unable to connect to config server, error: " + e.getMessage());
        }
    }

    private ConfServerProtocol createConfProtocol(ConfigServer config) {
        final ConfServerProtocol protocol = new ConfServerProtocol();
        protocol.setClientName(config.getClientName());
        protocol.setUserName(config.getUserName());
        protocol.setUserPassword(config.getPassword());
        return protocol;
    }

    @Override
    public BaseApiResponse disconnect() {
        try {
            configurationServerConnection.disconnect();
            return BaseApiResponse.success();
        } catch (ProtocolException | InterruptedException e) {
            return BaseApiResponse.failure(ApiResponse.Errors.CFG_NOT_DISCONNECTED,
                    "Unable to disconnect from config server, error: " + e.getMessage());
        } finally {
            genesysConfigApi.release();
        }
    }

    @Override
    public ObjectIdApiResponse createCampaign(String name, Collection<Integer> callListsId,
                                              String path, Integer tenantId) {
        try {
            CfgCampaign obj = genesysConfigApi.createOrUpdateObject(
                    GenesysConfig.cfgCampaignCreator(name, callListsId, path, tenantId)
            );
            return ObjectIdApiResponse.success(obj.getObjectDbid());
        } catch (ConfigException e) {
            return ObjectIdApiResponse.configException(e);
        }
    }

    @Override
    public BaseApiResponse dropCampaign(Integer id) {
        try {
            genesysConfigApi.dropObject(GenesysConfig.cfgCampaignQuery(id));
            return BaseApiResponse.success();
        } catch (ConfigException e) {
            return BaseApiResponse.configException(e);
        } catch (ObjectNotFoundException e) {
            return BaseApiResponse.objectNotFoundException(e);
        }
    }

    @Override
    public BaseApiResponse startCampaign(Integer id) {
        //RequestRegisterCampaign
        // RequestLoadCampaign
        //RequestSetDialingMode
        return null;
    }

    @Override
    public BaseApiResponse stopCampaign(Integer id) {
        return null;
    }

    @Override
    public ObjectIdApiResponse createCallList(String name, Integer tableAccessId, String filterName,
                                              Integer maxAttempts, Integer timeFrom, Integer timeUntil,
                                              Collection<String> treatmentsName, Integer tenantId, String path) {
        try {
            CfgTableAccess cfgTableAccess = genesysConfigApi.retrieveObject(
                    GenesysConfig.cfgTableAccessQuery(tableAccessId)
            );
            CfgFilter cfgFilter = genesysConfigApi.retrieveObject(GenesysConfig.cfgFilterQuery(filterName));

            Collection<CfgTreatment> cfgTreatments = genesysConfigApi.retrieveObjects(
                    treatmentsName.stream().map(GenesysConfig::cfgTreatmentQuery).collect(Collectors.toList())
            );
            CfgCallingList cfgCallingList = genesysConfigApi.createOrUpdateObject(
                    GenesysConfig.cfgCallingListCreator(name, cfgTableAccess, cfgFilter, maxAttempts, timeFrom,
                            timeUntil, cfgTreatments, tenantId, path)
            );
            return ObjectIdApiResponse.success(cfgCallingList.getDBID());
        } catch (ObjectNotFoundException e) {
            return ObjectIdApiResponse.objectNotFoundException(e);
        } catch (ConfigException e) {
            return ObjectIdApiResponse.configException(e);
        }
    }

    @Override
    public BaseApiResponse dropCallList(Integer id) {
        try {
            genesysConfigApi.dropObject(GenesysConfig.cfgCallingListQuery(id));
            return BaseApiResponse.success();
        } catch (ConfigException e) {
            return BaseApiResponse.configException(e);
        } catch (ObjectNotFoundException e) {
            return BaseApiResponse.objectNotFoundException(e);
        }
    }

    @Override
    public ObjectIdApiResponse createTableAccess(String name, String dbAccessPoint, String formatName, String tableName,
                                                 Integer tenantId, String path) {
        try {
            CfgApplication dbAccessPointApplication =
                    genesysConfigApi.retrieveObject(GenesysConfig.cfgApplicationQuery(dbAccessPoint));
            CfgFormat cfgFormat = genesysConfigApi.retrieveObject(GenesysConfig.cfgFormatQuery(formatName));
            CfgTableAccess newTableAccess = genesysConfigApi.createOrUpdateObject(
                    GenesysConfig.cfgTableAccessCallListCreator(name, dbAccessPointApplication, cfgFormat, tableName,
                            tenantId, path)
            );
            return ObjectIdApiResponse.success(newTableAccess.getObjectDbid());
        } catch (ConfigException e) {
            return ObjectIdApiResponse.configException(e);
        } catch (ObjectNotFoundException e) {
            return ObjectIdApiResponse.objectNotFoundException(e);
        }
    }

    @Override
    public BaseApiResponse dropTableAccess(Integer id) {
        try {
            genesysConfigApi.dropObject(GenesysConfig.cfgTableAccessQuery(id));
            return BaseApiResponse.success();
        } catch (ConfigException e) {
            return BaseApiResponse.configException(e);
        } catch (ObjectNotFoundException e) {
            return BaseApiResponse.objectNotFoundException(e);
        }
    }

    @Override
    public ObjectIdApiResponse createCampaignGroup(String name, Integer campaignId, Integer agentGroupId,
                                                   String dealingMode, String voiceTransferDestinationName,
                                                   String operationMode, String optimizationMethod, Integer targetValue,
                                                   Integer bufferSizeMinimum, Integer bufferSizeOptimum,
                                                   Integer numberCpdPorts, Collection<String> connections,
                                                   Collection<String> annexOptions) {
        try {
            CfgDN vtd = genesysConfigApi.retrieveObject(
                    GenesysConfig.cfgDnQuery(voiceTransferDestinationName, CfgDNType.CFGRoutingPoint)
            );
            Collection<CfgApplication> servers =
                    genesysConfigApi.retrieveObjects(connections.stream().map(GenesysConfig::cfgApplicationQuery)
                            .collect(Collectors.toList()));

            CfgCampaignGroup cfgCampaignGroup = genesysConfigApi.createOrUpdateObject(
                    GenesysConfig.cfgCampaignGroupCreator(name, campaignId,
                            agentGroupId, dealingMode, vtd, operationMode, optimizationMethod, targetValue,
                            bufferSizeMinimum, bufferSizeOptimum, numberCpdPorts, servers, annexOptions)
            );
            return ObjectIdApiResponse.success(cfgCampaignGroup.getObjectDbid());
        } catch (ConfigException e) {
            return ObjectIdApiResponse.configException(e);
        } catch (ObjectNotFoundException e) {
            return ObjectIdApiResponse.objectNotFoundException(e);
        }
    }

    @Override
    public ObjectIdApiResponse createCampaignGroup(String name, Integer campaignId, String agentGroupName,
                                                   String dealingMode, String voiceTransferDestinationName,
                                                   String operationMode, String optimizationMethod, Integer targetValue,
                                                   Integer bufferSizeMinimum, Integer bufferSizeOptimum,
                                                   Integer numberCpdPorts, Collection<String> connections,
                                                   Collection<String> annexOptions) {
        try {
            CfgAgentGroup cfgAgentGroup = genesysConfigApi.retrieveObject(
                    GenesysConfig.cfgAgentGroupQuery(agentGroupName)
            );
            return createCampaignGroup(name, campaignId, cfgAgentGroup.getDBID(), dealingMode,
                    voiceTransferDestinationName, operationMode, optimizationMethod, targetValue, bufferSizeMinimum,
                    bufferSizeOptimum, numberCpdPorts, connections, annexOptions);
        } catch (ObjectNotFoundException e) {
            return ObjectIdApiResponse.objectNotFoundException(e);
        } catch (ConfigException e) {
            return ObjectIdApiResponse.configException(e);
        }
    }

    @Override
    public BaseApiResponse dropCampaignGroup(Integer id) {
        try {
            genesysConfigApi.dropObject(GenesysConfig.cfgCampaignGroupQuery(id));
            return BaseApiResponse.success();
        } catch (ConfigException e) {
            return BaseApiResponse.configException(e);
        } catch (ObjectNotFoundException e) {
            return BaseApiResponse.objectNotFoundException(e);
        }
    }

    @Override
    public ObjectIdApiResponse createSkill(String name, Integer tenantId, String path) {
        try {
            CfgSkill cfgSkill = genesysConfigApi.createOrUpdateObject(
                    GenesysConfig.cfgSkillCreator(name, tenantId, path)
            );
            return ObjectIdApiResponse.success(cfgSkill.getObjectDbid());
        } catch (ConfigException e) {
            return ObjectIdApiResponse.configException(e);
        }
    }

    @Override
    public BaseApiResponse dropSkill(Integer id) {
        try {
            genesysConfigApi.dropObject(GenesysConfig.cfgSkillQuery(id));
            return BaseApiResponse.success();
        } catch (ConfigException e) {
            return BaseApiResponse.configException(e);
        } catch (ObjectNotFoundException e) {
            return BaseApiResponse.objectNotFoundException(e);
        }
    }

    @Override
    public PersonApiResponse queryCfgPerson(String login) {
        try {
            CfgPerson cfgPerson = genesysConfigApi.retrieveObject(GenesysConfig.cfgPersonQuery(login));
            return PersonApiResponse.success(cfgPerson);
        } catch (ConfigException e) {
            return PersonApiResponse.configException(e);
        } catch (ObjectNotFoundException e) {
            return PersonApiResponse.objectNotFoundException(e);
        }
    }

    @Override
    public PersonAgentGroupsApiResponse queryAgentGroups(Integer personId) {
        try {
            Collection<CfgAgentGroup> cfgAgentGroups = genesysConfigApi.retrieveObjects(CfgAgentGroup.class,
                    GenesysConfig.cfgAgentGroupQueryByPersonId(personId)
            );
            return PersonAgentGroupsApiResponse.success(cfgAgentGroups);
        } catch (ConfigException e) {
            return PersonAgentGroupsApiResponse.configException(e);
        } catch (InterruptedException e) {
            return PersonAgentGroupsApiResponse.interruptedException(e);
        }
    }

    @Override
    public ObjectIdApiResponse createAgentGroup(String name, String script, Integer tenantId, String path) {
        try {
            CfgAgentGroup cfgAgentGroup = genesysConfigApi.createOrUpdateObject(
                    GenesysConfig.cfgAgentGroupCreator(name, script, tenantId, path)
            );
            return ObjectIdApiResponse.success(cfgAgentGroup.getObjectDbid());
        } catch (ConfigException e) {
            return ObjectIdApiResponse.configException(e);
        }
    }

    @Override
    public ObjectIdApiResponse createAgentGroup(String name, Integer tenantId, String path) {
        try {
            CfgAgentGroup cfgAgentGroup = genesysConfigApi.createOrUpdateObject(
                    GenesysConfig.cfgAgentGroupCreator(name, tenantId, path)
            );
            return ObjectIdApiResponse.success(cfgAgentGroup.getObjectDbid());
        } catch (ConfigException e) {
            return ObjectIdApiResponse.configException(e);
        }
    }

    @Override
    public BaseApiResponse dropAgentGroup(Integer agentGroupId) {
        try {
            genesysConfigApi.dropObject(GenesysConfig.cfgAgentGroupQuery(agentGroupId));
            return BaseApiResponse.success();
        } catch (ConfigException e) {
            return BaseApiResponse.configException(e);
        } catch (ObjectNotFoundException e) {
            return BaseApiResponse.objectNotFoundException(e);
        }
    }

    @Override
    public BaseApiResponse addAgentToGroup(Integer groupId, Integer personId) {
        try {
            CfgAgentGroup cfgAgentGroup = genesysConfigApi.retrieveObject(GenesysConfig.cfgAgentGroupQuery(groupId));
            Collection<Integer> agentsDbId = Optional.ofNullable(cfgAgentGroup.getAgentDBIDs()).orElse(new ArrayList<>());

            if (!agentsDbId.contains(personId)) {
                agentsDbId.add(personId);
                cfgAgentGroup.setAgentDBIDs(agentsDbId);
                genesysConfigApi.createOrUpdateObject(cfgAgentGroup);
            }
            return BaseApiResponse.success();
        } catch (ConfigException e) {
            return BaseApiResponse.configException(e);
        } catch (ObjectNotFoundException e) {
            return BaseApiResponse.objectNotFoundException(e);
        }
    }

    @Override
    public BaseApiResponse deleteAgentFromGroup(Integer groupId, Integer personId) {
        try {
            CfgAgentGroup cfgAgentGroup = genesysConfigApi.retrieveObject(GenesysConfig.cfgAgentGroupQuery(groupId));
            Collection<Integer> agentsDbId = Optional.ofNullable(cfgAgentGroup.getAgentDBIDs()).orElse(new ArrayList<>());
            if (agentsDbId.contains(personId)) {
                agentsDbId.remove(personId);
                cfgAgentGroup.setAgentDBIDs(agentsDbId);
                genesysConfigApi.createOrUpdateObject(cfgAgentGroup);
            }
            return BaseApiResponse.success();
        } catch (ConfigException e) {
            return BaseApiResponse.configException(e);
        } catch (ObjectNotFoundException e) {
            return BaseApiResponse.objectNotFoundException(e);
        }
    }

    @Override
    public ObjectIdApiResponse createVirtualQueue(String name, String switchName, Integer tenantId, String path) {
        try {
            CfgSwitch cfgSwitch = genesysConfigApi.retrieveObject(GenesysConfig.cfgSwitchQuery(switchName));
            CfgDN cfgDN = genesysConfigApi.createOrUpdateObject(
                    GenesysConfig.cfgDnCreator(name, cfgSwitch, CfgDNType.CFGVirtACDQueue,
                            CfgRouteType.CFGXRouteTypeDefault, tenantId, path)
            );
            return ObjectIdApiResponse.success(cfgDN.getObjectDbid());
        } catch (ConfigException e) {
            return ObjectIdApiResponse.configException(e);
        } catch (ObjectNotFoundException e) {
            return ObjectIdApiResponse.objectNotFoundException(e);
        }
    }

    @Override
    public BaseApiResponse dropVirtualQueue(Integer id) {
        try {
            genesysConfigApi.dropObject(GenesysConfig.cfgDnQuery(id));
            return BaseApiResponse.success();
        } catch (ConfigException e) {
            return BaseApiResponse.configException(e);
        } catch (ObjectNotFoundException e) {
            return BaseApiResponse.objectNotFoundException(e);
        }
    }

    @Override
    public BaseApiResponse updatePersonParam(Integer id, String key, String value) {
        try {
            CfgPerson cfgPerson = genesysConfigApi.retrieveObject(GenesysConfig.cfgPersonQuery(id));
            cfgPerson.setProperty(key, value);
            genesysConfigApi.createOrUpdateObject(cfgPerson);
            return BaseApiResponse.success();
        } catch (ConfigException e) {
            return BaseApiResponse.configException(e);
        } catch (ObjectNotFoundException e) {
            return BaseApiResponse.objectNotFoundException(e);
        }
    }

    @Override
    public BaseApiResponse dropPersonParam(Integer id, String key) {
        try {
            CfgPerson cfgPerson = genesysConfigApi.retrieveObject(GenesysConfig.cfgPersonQuery(id));
            cfgPerson.setProperty(key, null);
            genesysConfigApi.createOrUpdateObject(cfgPerson);
            return BaseApiResponse.success();
        } catch (ConfigException e) {
            return BaseApiResponse.configException(e);
        } catch (ObjectNotFoundException e) {
            return BaseApiResponse.objectNotFoundException(e);
        }
    }

    @Override
    public PersonParamApiResponse queryPersonParam(Integer id, String key) {
        try {
            CfgPerson cfgPerson = genesysConfigApi.retrieveObject(GenesysConfig.cfgPersonQuery(id));
            Object property = cfgPerson.getProperty(key);
            return PersonParamApiResponse.success(property);
        } catch (ConfigException e) {
            return PersonParamApiResponse.configException(e);
        } catch (ObjectNotFoundException e) {
            return PersonParamApiResponse.objectNotFoundException(e);
        }
    }

    @Override
    public DnsApiResponse queryDns(String dnType) {
        try {
            Collection<CfgDN> dns = genesysConfigApi.retrieveObjects(CfgDN.class,
                    GenesysConfig.cfgDnQuery(CfgDNType.valueOf(dnType))
            );
            return DnsApiResponse.success(dns);
        } catch (ConfigException e) {
            return DnsApiResponse.configException(e);
        } catch (InterruptedException e) {
            return DnsApiResponse.interruptedException(e);
        }
    }

    @Override
    public BaseApiResponse updateDn(Integer id, String sectionName, String optionName, String value) {
        try {
            CfgDN cfgDN = genesysConfigApi.retrieveObject(GenesysConfig.cfgDnQuery(id));
            KeyValueCollection userProperties =
                    Optional.ofNullable(cfgDN.getUserProperties()).orElse(new KeyValueCollection());
            KeyValueCollection section = Optional.ofNullable(userProperties.getList(sectionName)).orElse(new KeyValueCollection());
            section.remove(optionName);
            section.addString(optionName, value);
            userProperties.remove(sectionName);
            userProperties.addList(sectionName, section);
            cfgDN.setUserProperties(userProperties);
            genesysConfigApi.createOrUpdateObject(cfgDN);
            return BaseApiResponse.success();
        } catch (ObjectNotFoundException e) {
            return BaseApiResponse.objectNotFoundException(e);
        } catch (ConfigException e) {
            return BaseApiResponse.configException(e);
        }
    }
}
