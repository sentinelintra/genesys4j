package com.cck.genesys4j;

import com.cck.genesys4j.config.ApiConfig;
import com.cck.genesys4j.model.*;
import com.genesyslab.platform.configuration.protocol.types.CfgDNType;
import com.genesyslab.platform.configuration.protocol.types.CfgDialMode;
import com.genesyslab.platform.configuration.protocol.types.CfgOperationMode;
import com.genesyslab.platform.configuration.protocol.types.CfgOptimizationMethod;
import org.junit.jupiter.api.*;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GenesysApiTest {

    private static class ExistedObjects {
        static Integer callList = 108;
        static String vtd = "2101";
        static String dbServerName = "OC_DBServer";
        static String databaseAccessPointName = "OC_DAP";
        static String format = "FMT_Outbound_Misc";
        static String filter = "FLTR_OB_Misc_push";
        static String agentUserName = "agent01";
        static String switchName = "SipSwitch";
    }

    private final static GenesysApi genesysApi = GenesysApiFactory.createInstance(
            ApiConfig.load("default", "default", "",
                    "config-pr", 2020, "config-bkp", 2020)
    );

    private static void assertIsSuccess(BaseApiResponse response) {
        assertEquals(response.getErrorCode(), ApiResponse.Errors.SUCCESS);
        assertEquals(response.getErrorMessage(), ApiResponse.SUCCESS_MESSAGE);
    }

    @BeforeAll
    static void testConnect() {
        BaseApiResponse response = genesysApi.connect();
        assertIsSuccess(response);
    }

    @Test
    void testCreateAndDropCampaign() {
        ObjectIdApiResponse createAgentGroupResponse = genesysApi.createAgentGroup("TestAgentGroup", "");
        assertIsSuccess(createAgentGroupResponse);
        try {
            ObjectIdApiResponse createCampaignResponse = genesysApi.createCampaign("TestCampaign",
                    ExistedObjects.callList, "");
            assertIsSuccess(createCampaignResponse);
            try {
                ObjectIdApiResponse createCampaignGroupResponse = genesysApi.createCampaignGroup("TestCampaignGroup",
                        createCampaignResponse.getObjectId(), createAgentGroupResponse.getObjectId(),
                        CfgDialMode.CFGDMProgress.name(), ExistedObjects.vtd,
                        CfgOperationMode.CFGOMNoOperationMode.name(), CfgOptimizationMethod.CFGOMBusyFactor.name(),
                        10, 1, 2, 100,
                        Collections.singletonList(ExistedObjects.dbServerName), Collections.emptyList());
                assertIsSuccess(createCampaignGroupResponse);
                BaseApiResponse dropCampaignGroupResponse = genesysApi.dropCampaign(createCampaignGroupResponse.getObjectId());
                assertIsSuccess(dropCampaignGroupResponse);
            } finally {
                BaseApiResponse dropCampaignResponse = genesysApi.dropCampaign(createCampaignResponse.getObjectId());
                assertIsSuccess(dropCampaignResponse);
            }
        } finally {
            BaseApiResponse dropAgentGroupResponse = genesysApi.dropAgentGroup(createAgentGroupResponse.getObjectId());
            assertIsSuccess(dropAgentGroupResponse);
        }
    }

    @Test
    void testCreateAndDropCallList() {
        ObjectIdApiResponse tableAccessCreateResponse = genesysApi.createTableAccess("testTableAccess",
                ExistedObjects.databaseAccessPointName, ExistedObjects.format, "TestCallingListTable", "");
        assertIsSuccess(tableAccessCreateResponse);
        try {
            ObjectIdApiResponse callListCreateResponse = genesysApi.createCallList("testCallList",
                    tableAccessCreateResponse.getObjectId(), ExistedObjects.filter, 10, 0,
                    86280, Collections.emptyList(), "");
            assertIsSuccess(callListCreateResponse);
            BaseApiResponse dropCallListResponse = genesysApi.dropCallList(callListCreateResponse.getObjectId());
            assertIsSuccess(dropCallListResponse);
        } finally {
            BaseApiResponse dropTableAccessResponse = genesysApi.dropTableAccess(tableAccessCreateResponse.getObjectId());
            assertIsSuccess(dropTableAccessResponse);
        }
    }

    @Test
    void testCreateAndDropSkill() {
        ObjectIdApiResponse skillCreateResponse = genesysApi.createSkill("testSkill", "");
        assertIsSuccess(skillCreateResponse);
        BaseApiResponse dropCallListResponse = genesysApi.dropSkill(skillCreateResponse.getObjectId());
        assertIsSuccess(dropCallListResponse);
    }

    @Test
    void testAddAndDeleteAgentToGroup() {
        ObjectIdApiResponse createAgentGroupResponse = genesysApi.createAgentGroup("TestAgentGroup", "");
        assertIsSuccess(createAgentGroupResponse);
        try {
            PersonApiResponse personApiResponse = genesysApi.queryCfgPerson(ExistedObjects.agentUserName);
            assertIsSuccess(personApiResponse);
            BaseApiResponse addAgentToGroupResponse = genesysApi.addAgentToGroup(createAgentGroupResponse.getObjectId(),
                    personApiResponse.getObjectId());
            assertIsSuccess(addAgentToGroupResponse);
            BaseApiResponse deleteAgentFromGroupResponse = genesysApi.deleteAgentFromGroup(createAgentGroupResponse.getObjectId(),
                    personApiResponse.getObjectId());
            assertIsSuccess(deleteAgentFromGroupResponse);
        } finally {
            BaseApiResponse dropAgentGroupResponse = genesysApi.dropAgentGroup(createAgentGroupResponse.getObjectId());
            assertIsSuccess(dropAgentGroupResponse);
        }
    }

    @Test
    void testCreateAndDropVirtualQueue() {
        ObjectIdApiResponse createVqResponse = genesysApi.createVirtualQueue("TestVQ", ExistedObjects.switchName,
                "");
        assertIsSuccess(createVqResponse);
        BaseApiResponse dropVqResponse = genesysApi.dropVirtualQueue(createVqResponse.getObjectId());
        assertIsSuccess(dropVqResponse);
    }

    @Test
    void testQueryDns() {
        DnsApiResponse response = genesysApi.queryDns(CfgDNType.CFGTrunk.name());
        response.getDns().forEach(cfgDN -> {
                    String optionValue = cfgDN.getUserProperties().getList("TServer").getString("priority");

                }
        );
        assertIsSuccess(response);
    }

    @Test
    void testUpdateDn() {
        BaseApiResponse response = genesysApi.updateDn(373, "TServer", "priority", "1");
        assertIsSuccess(response);
    }

    @AfterAll
    static void testDisconnect() {
        BaseApiResponse response = genesysApi.disconnect();
        assertIsSuccess(response);
    }
}