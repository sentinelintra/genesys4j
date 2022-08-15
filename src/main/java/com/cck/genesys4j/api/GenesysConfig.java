package com.cck.genesys4j.api;

import com.genesyslab.platform.applicationblocks.com.*;
import com.genesyslab.platform.applicationblocks.com.objects.*;
import com.genesyslab.platform.applicationblocks.com.queries.*;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import com.genesyslab.platform.configuration.protocol.types.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GenesysConfig {
    private final IConfService confService;

    public GenesysConfig(IConfService confService) {
        this.confService = confService;
    }

    public void release() {
        ConfServiceFactory.releaseConfService(confService);
    }

    public <T extends ICfgObject> Optional<T> findObject(ICfgQuery<T> query) throws ConfigException {
        return Optional.ofNullable(confService.retrieveObject(query));
    }

    public <T extends ICfgObject> T retrieveObject(ICfgQuery<T> query) throws ConfigException, ObjectNotFoundException {
        Optional<T> maybeObject = findObject(query);
        if (maybeObject.isPresent()) {
            return maybeObject.get();
        } else {
            throw new ObjectNotFoundException(query);
        }
    }

    public <T extends ICfgObject> Collection<T> retrieveObjects(Class<T> clazz, ICfgQuery<T> query)
            throws ConfigException, InterruptedException {
        return confService.retrieveMultipleObjects(clazz, query);
    }

    public <T extends ICfgObject> Collection<T> retrieveObjects(Collection<ICfgQuery<T>> elementQueries)
            throws ConfigException, ObjectNotFoundException {
        Collection<T> cfgObjects = new ArrayList<>();
        for (ICfgQuery<T> query : elementQueries) {
            cfgObjects.add(retrieveObject(query));
        }
        return cfgObjects;
    }

    public <T extends ICfgObject> T createOrUpdateObject(Function<IConfService, T> cfgObjectCreator) throws ConfigException {
        T cfgObject = cfgObjectCreator.apply(confService);
        return createOrUpdateObject(cfgObject);
    }

    public <T extends ICfgObject> T createOrUpdateObject(T cfgObject) throws ConfigException {
        cfgObject.save();
        return cfgObject;
    }

    public <T extends ICfgObject> void dropObject(ICfgQuery<T> query) throws ConfigException, ObjectNotFoundException {
        T obj = retrieveObject(query);
        obj.delete();
    }

    public static Function<IConfService, CfgCampaign> cfgCampaignCreator(String name, Collection<Integer> callListsId,
                                                                         String path, Integer tenantId) {
        return confService -> {
            CfgCampaign newCfgCampaign = new CfgCampaign(confService);
            //newCfgCampaign.setFolderId();
            newCfgCampaign.setName(name);
            newCfgCampaign.setTenantDBID(tenantId);

            Function<Integer, CfgCallingListInfo> createCfgCallingListInfo = callListDbId -> {
                CfgCallingListInfo callingListInfo = new CfgCallingListInfo(confService, newCfgCampaign);
                callingListInfo.setCallingListDBID(callListDbId);
                return callingListInfo;
            };
            List<CfgCallingListInfo> callingListInfos =
                    callListsId.stream().map(createCfgCallingListInfo).collect(Collectors.toList());
            newCfgCampaign.setCallingLists(callingListInfos);
            return newCfgCampaign;
        };
    }

    public static Function<IConfService, CfgCallingList> cfgCallingListCreator(String name, CfgTableAccess cfgTableAccess,
                                                                               CfgFilter cfgFilter, Integer maxAttempts,
                                                                               Integer timeFrom, Integer timeUntil,
                                                                               Collection<CfgTreatment> cfgTreatments,
                                                                               Integer tenantId, String path) {
        return confService -> {
            CfgCallingList newCallingList = new CfgCallingList(confService);
            newCallingList.setTenantDBID(tenantId);
            newCallingList.setName(name);
            newCallingList.setTableAccess(cfgTableAccess);
            newCallingList.setFilter(cfgFilter);
            newCallingList.setMaxAttempts(maxAttempts);
            newCallingList.setTimeFrom(timeFrom);
            newCallingList.setTimeUntil(timeUntil);
            newCallingList.setTreatments(cfgTreatments);
            return newCallingList;
        };
    }

    public static Function<IConfService, CfgTableAccess> cfgTableAccessCreator(String name,
                                                                               CfgApplication dbAccessPointApplication,
                                                                               CfgFormat cfgFormat, String tableName,
                                                                               CfgTableType type,
                                                                               Integer tenantId, String path) {
        return confService -> {
            CfgTableAccess newObj = new CfgTableAccess(confService);
            newObj.setTenantDBID(tenantId);
            newObj.setName(name);
            newObj.setDbAccess(dbAccessPointApplication);
            newObj.setFormat(cfgFormat);
            newObj.setDbTableName(tableName);
            newObj.setType(type);
            newObj.setIsCachable(CfgFlag.CFGFalse);
            return newObj;
        };
    }

    public static Function<IConfService, CfgTableAccess> cfgTableAccessCallListCreator(String name,
                                                                                       CfgApplication dbAccessPointApplication,
                                                                                       CfgFormat cfgFormat, String tableName,
                                                                                       Integer tenantId, String path) {
        return cfgTableAccessCreator(name, dbAccessPointApplication, cfgFormat, tableName,
                CfgTableType.CFGTTCallingList, tenantId, path);
    }

    private static KeyValueCollection kvcFromAnnex(Collection<String> annexOptions) {
        Map<String, Map<String, String>> parsedAnnexValues = new HashMap<>();
//        for (String annex : annexOptions) {
//            parseAnnexToCvl(annex)
//            String[] annexParts = annex.split("/");
//            String sectionName = annexParts[0]
//        }
        KeyValueCollection result = new KeyValueCollection();
        parsedAnnexValues.forEach((sectionName, options) -> {
            KeyValueCollection kvcList = new KeyValueCollection();
            options.forEach(kvcList::addString);
            result.addList(sectionName, kvcList);
        });
        return result;
    }

    public static Function<IConfService, CfgCampaignGroup> cfgCampaignGroupCreator(String name, Integer campaignId,
                                                                                   Integer agentGroupId,
                                                                                   String dealingMode,
                                                                                   CfgDN vtd,
                                                                                   String operationMode,
                                                                                   String optimizationMethod,
                                                                                   Integer targetValue,
                                                                                   Integer bufferSizeMinimum,
                                                                                   Integer bufferSizeOptimum,
                                                                                   Integer numberCpdPorts,
                                                                                   Collection<CfgApplication> servers,
                                                                                   Collection<String> annexOptions) {
        return confService -> {
            CfgCampaignGroup newCfgCampaignGroup = new CfgCampaignGroup(confService);
            newCfgCampaignGroup.setName(name);
            newCfgCampaignGroup.setCampaignDBID(campaignId);
            newCfgCampaignGroup.setGroupType(CfgObjectType.CFGAgentGroup);
            newCfgCampaignGroup.setGroupDBID(agentGroupId);
            newCfgCampaignGroup.setDialMode(CfgDialMode.valueOf(dealingMode));
            newCfgCampaignGroup.setOrigDN(vtd);
            newCfgCampaignGroup.setOperationMode(CfgOperationMode.valueOf(operationMode));
            newCfgCampaignGroup.setOptMethod(CfgOptimizationMethod.valueOf(optimizationMethod));
            newCfgCampaignGroup.setOptMethodValue(targetValue);
            newCfgCampaignGroup.setMinRecBuffSize(bufferSizeMinimum);
            newCfgCampaignGroup.setOptRecBuffSize(bufferSizeOptimum);
            newCfgCampaignGroup.setNumOfChannels(numberCpdPorts);
            newCfgCampaignGroup.setServers(servers);
            newCfgCampaignGroup.setUserProperties(kvcFromAnnex(annexOptions));
            return newCfgCampaignGroup;
        };
    }

    public static Function<IConfService, CfgSkill> cfgSkillCreator(String name, Integer tenantId, String path) {
        return confService -> {
            CfgSkill newCfgSkill = new CfgSkill(confService);
            newCfgSkill.setTenantDBID(tenantId);
            newCfgSkill.setName(name);
            return newCfgSkill;
        };
    }

    private static KeyValueCollection virtualGroupAnnex(String script) {
        KeyValueCollection section = new KeyValueCollection();
        KeyValueCollection option = new KeyValueCollection();
        option.addString("script", script);
        section.addList("virtual", option);
        return section;
    }

    public static Function<IConfService, CfgAgentGroup> cfgAgentGroupCreator(String name, Integer tenantId, String path) {
        return confService -> {
            CfgAgentGroup newAgentGroup = new CfgAgentGroup(confService);
            CfgGroup cfgGroup = new CfgGroup(confService, newAgentGroup);
            cfgGroup.setName(name);
            cfgGroup.setTenantDBID(tenantId);
            newAgentGroup.setGroupInfo(cfgGroup);
            return newAgentGroup;
        };
    }

    public static Function<IConfService, CfgAgentGroup> cfgAgentGroupCreator(String name, String script,
                                                                             Integer tenantId, String path) {
        return confService -> {
            CfgAgentGroup newAgentGroup = cfgAgentGroupCreator(name, tenantId, path).apply(confService);
            newAgentGroup.getGroupInfo().setUserProperties(virtualGroupAnnex(script));
            return newAgentGroup;
        };
    }

    public static Function<IConfService, CfgDN> cfgDnCreator(String number, CfgSwitch cfgSwitch, CfgDNType type,
                                                             CfgRouteType cfgRouteType, Integer tenantId, String path) {
        return confService -> {
            CfgDN cfgDN = new CfgDN(confService);
            cfgDN.setSwitch(cfgSwitch);
            cfgDN.setTenantDBID(tenantId);
            cfgDN.setNumber(number);
            cfgDN.setType(type);
            cfgDN.setRouteType(cfgRouteType);
            return cfgDN;
        };
    }

    public static Function<IConfService, CfgFilter> cfgFilterCreator(String name, CfgFormat cfgFormat, String path) {
        return confService -> {
            CfgFilter cfgDN = new CfgFilter(confService);
            cfgDN.setName(name);
            cfgDN.setFormat(cfgFormat);
            return cfgDN;
        };
    }

    public static CfgSwitchQuery cfgSwitchQuery(String switchName) {
        return new CfgSwitchQuery(switchName);
    }

    public static CfgAgentGroupQuery cfgAgentGroupQueryByPersonId(Integer personId) {
        CfgAgentGroupQuery agentGroupQuery = new CfgAgentGroupQuery();
        agentGroupQuery.setPersonDbid(personId);
        return agentGroupQuery;
    }

    public static CfgAgentGroupQuery cfgAgentGroupQuery(Integer id) {
        return new CfgAgentGroupQuery(id);
    }

    public static CfgAgentGroupQuery cfgAgentGroupQuery(String name) {
        return new CfgAgentGroupQuery(name);
    }

    public static CfgPersonQuery cfgPersonQuery(String login) {
        CfgPersonQuery cfgPersonQuery = new CfgPersonQuery();
        cfgPersonQuery.setUserName(login);
        return cfgPersonQuery;
    }

    public static CfgPersonQuery cfgPersonQuery(Integer id) {
        return new CfgPersonQuery(id);
    }

    public static CfgSkillQuery cfgSkillQuery(Integer id) {
        return new CfgSkillQuery(id);
    }

    public static CfgCallingListQuery cfgCallingListQuery(Integer id) {
        return new CfgCallingListQuery(id);
    }

    public static CfgCampaignGroupQuery cfgCampaignGroupQuery(Integer id) {
        return new CfgCampaignGroupQuery(id);
    }

    public static CfgDNQuery cfgDnQuery(CfgDNType cfgDNType) {
        CfgDNQuery cfgDNQuery = new CfgDNQuery();
        cfgDNQuery.setDnType(cfgDNType);
        return cfgDNQuery;
    }

    public static CfgDNQuery cfgDnQuery(String number, CfgDNType cfgDNType) {
        CfgDNQuery cfgDNQuery = cfgDnQuery(cfgDNType);
        cfgDNQuery.setDnNumber(number);
        return cfgDNQuery;
    }

    public static CfgDNQuery cfgDnQuery(Integer id) {
        return new CfgDNQuery(id);
    }

    public static CfgCampaignQuery cfgCampaignQuery(Integer id) {
        return new CfgCampaignQuery(id);
    }

    public static CfgTableAccessQuery cfgTableAccessQuery(Integer id) {
        return new CfgTableAccessQuery(id);
    }

    public static CfgFilterQuery cfgFilterQuery(Integer id) {
        return new CfgFilterQuery(id);
    }

    public static CfgFilterQuery cfgFilterQuery(String name) {
        return new CfgFilterQuery(name);
    }

    public static CfgTreatmentQuery cfgTreatmentQuery(String name) {
        return new CfgTreatmentQuery(name);
    }

    public static CfgApplicationQuery cfgApplicationQuery(String name) {
        return new CfgApplicationQuery(name);
    }

    public static CfgFormatQuery cfgFormatQuery(Integer id) {
        return new CfgFormatQuery(id);
    }

    public static CfgFormatQuery cfgFormatQuery(String name) {
        return new CfgFormatQuery(name);
    }

    public CfgCampaignGroup createCampaignGroup(String name, Integer campaignId, Integer agentGroupId, String dealingMode,
                                                String voiceTransferDestinationName, String operationMode,
                                                String optimizationMethod, Integer targetValue, Integer bufferSizeMinimum,
                                                Integer bufferSizeOptimum, Integer numberCpdPorts,
                                                List<String> connections, List<String> annexOptions) throws ObjectNotFoundException, ConfigException {
        CfgDN vtd = retrieveObject(new CfgDNQuery(voiceTransferDestinationName));
        Collection<CfgApplication> servers =
                retrieveObjects(connections.stream().map(CfgApplicationQuery::new).collect(Collectors.toList()));
        KeyValueCollection userProperties = kvcFromAnnex(annexOptions);

        CfgCampaignGroup newCfgCampaignGroup = new CfgCampaignGroup(confService);
        newCfgCampaignGroup.setName(name);
        newCfgCampaignGroup.setCampaignDBID(campaignId);
        newCfgCampaignGroup.setGroupType(CfgObjectType.CFGAgentGroup);
        newCfgCampaignGroup.setGroupDBID(agentGroupId);
        newCfgCampaignGroup.setDialMode(CfgDialMode.valueOf(dealingMode));
        newCfgCampaignGroup.setOrigDN(vtd);
        newCfgCampaignGroup.setOperationMode(CfgOperationMode.valueOf(operationMode));
        newCfgCampaignGroup.setOptMethod(CfgOptimizationMethod.valueOf(optimizationMethod));
        newCfgCampaignGroup.setOptMethodValue(targetValue);
        newCfgCampaignGroup.setMinRecBuffSize(bufferSizeMinimum);
        newCfgCampaignGroup.setOptRecBuffSize(bufferSizeOptimum);
        newCfgCampaignGroup.setNumOfChannels(numberCpdPorts);
        newCfgCampaignGroup.setServers(servers);
        newCfgCampaignGroup.setUserProperties(userProperties);
        return createOrUpdateObject(newCfgCampaignGroup);
    }

//    CfgFolder retrieveFolderByPath(String path, CfgObjectType objectType) throws ConfigException {
//        Arrays.stream(path.split("/")).collect()
//        CfgFolder folder = new CfgFolder(confService);
//        folder.setType(objectType);
//        folder.save();setFolderId();
//        CfgFolderQuery query = new CfgFolderQuery(confService);
//        query.setFolderClass();
//        return confService.retrieveObject(query);
//    }
//
//    CfgFolder retrieveFolderByName(String name, CfgFolder parent, CfgObjectType objectType) {
//        CfgFolderQuery folder = new CfgFolder(confService);
//        folder.setType(objectType);
//        folder.
//    }
//
//    private CfgFolder getFolderRecursive(LinkedList<String> folderStructure, CfgFolder currentFolder) {
//        if (folderStructure.isEmpty()) {
//            return currentFolder;
//        } else {
//            String head = folderStructure.poll();
//            return getFolderRecursive(folderStructure, )
//        }
//    }
}
