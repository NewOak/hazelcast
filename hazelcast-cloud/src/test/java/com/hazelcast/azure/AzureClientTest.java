package com.hazelcast.azure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.GroupProperties;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class AzureClientTest {
	String xml = "<HostedService xmlns=\"http://schemas.microsoft.com/windowsazure\" "
			+ "xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">"+
		        "<Url></Url>"+
		        "<ServiceName>ncsrvcbbqseus01</ServiceName>"+
		        "<HostedServiceProperties>"+
		            "<Description i:nil=\"true\"/>"+
		            "<AffinityGroup>affinity1</AffinityGroup>"+
		            "<Label>bmNzcnZjcWFldXMkMQ==</Label>"+
		            "<Status>Created</Status>"+
		            "<DateCreated>2014-07-31T14:35:46Z</DateCreated>"+
		            "<DateLastModified>2014-07-31T14:37:10Z</DateLastModified>"+
		            "<ExtendedProperties>"+
		                "<ExtendedProperty>"+
		                    "<Name>ResourceGroup</Name>"+
		                    "<Value>ncsrvcbbqseus01</Value>"+
		                "</ExtendedProperty>"+
		                "<ExtendedProperty>"+
		                    "<Name>ResourceLocation</Name>"+
		                    "<Value>East US</Value>"+
		                "</ExtendedProperty>"+
		            "</ExtendedProperties>"+
		        "</HostedServiceProperties>"+
		        "<Deployments>"+
		            "<Deployment>"+
		                "<Name>d2efd83e516c84685555555557dacsss8af</Name>"+
		                "<DeploymentSlot>Production</DeploymentSlot>"+
		                "<PrivateID>7c6sssf59f55555gaa81e4f04a555b3b8</PrivateID>"+
		                "<Status>Running</Status>"+
		                "<Label>Y3VydmVzZXJ2aWNlc3RhZ2lfdgw==</Label>"+
		                "<Url>http://ncsrvcbbqaeus01.cloudapp.net/</Url>"+
		                "<Configuration></Configuration>"+
		                "<RoleInstanceList>"+
		                    "<RoleInstance>"+
		                        "<RoleName>CurveService</RoleName>"+
		                        "<InstanceName>CurveService_IN_0</InstanceName>"+
		                        "<InstanceStatus>ReadyRole</InstanceStatus>"+
		                        "<InstanceUpgradeDomain>0</InstanceUpgradeDomain>"+
		                        "<InstanceFaultDomain>0</InstanceFaultDomain>"+
		                        "<InstanceSize>Small</InstanceSize>"+
		                        "<InstanceStateDetails/>"+
		                        "<IpAddress>10.101.1.11</IpAddress>"+
		                        "<InstanceEndpoints>"+
		                            "<InstanceEndpoint>"+
		                                "<Name>CacheEndPoint</Name>"+
		                                "<Vip>168.62.555.175</Vip>"+
		                                "<PublicPort>5701</PublicPort>"+
		                                "<LocalPort>5701</LocalPort>"+
		                                "<Protocol>tcp</Protocol>"+
		                            "</InstanceEndpoint>"+
		                            "<InstanceEndpoint>"+
		                                "<Name>Microsoft.WindowsAzure.Plugins.RemoteForwarder.RdpInput</Name>"+
		                                "<Vip>168.62.555.175</Vip>"+
		                                "<PublicPort>3389</PublicPort>"+
		                                "<LocalPort>20000</LocalPort>"+
		                                "<Protocol>tcp</Protocol>"+
		                            "</InstanceEndpoint>"+
		                        "</InstanceEndpoints>"+
		                        "<PowerState>Started</PowerState>"+
		                    "</RoleInstance>"+
		                    "<RoleInstance>"+
		                        "<RoleName>CurveService</RoleName>"+
		                        "<InstanceName>CurveService_IN_1</InstanceName>"+
		                        "<InstanceStatus>ReadyRole</InstanceStatus>"+
		                        "<InstanceUpgradeDomain>1</InstanceUpgradeDomain>"+
		                        "<InstanceFaultDomain>1</InstanceFaultDomain>"+
		                        "<InstanceSize>Small</InstanceSize>"+
		                        "<InstanceStateDetails/>"+
		                        "<IpAddress>10.101.1.18</IpAddress>"+
		                        "<InstanceEndpoints>"+
		                            "<InstanceEndpoint>"+
		                                "<Name>CacheEndPoint</Name>"+
		                                "<Vip>168.62.555.175</Vip>"+
		                                "<PublicPort>5701</PublicPort>"+
		                                "<LocalPort>5701</LocalPort>"+
		                                "<Protocol>tcp</Protocol>"+
		                            "</InstanceEndpoint>"+
		                            "<InstanceEndpoint>"+
		                                "<Name>Microsoft.WindowsAzure.Plugins.RemoteForwarder.RdpInput</Name>"+
		                                "<Vip>168.62.555.175</Vip>"+
		                                "<PublicPort>3389</PublicPort>"+
		                                "<LocalPort>20000</LocalPort>"+
		                                "<Protocol>tcp</Protocol>"+
		                            "</InstanceEndpoint>"+
		                        "</InstanceEndpoints>"+
		                        "<PowerState>Started</PowerState>"+
		                    "</RoleInstance>"+
		                "</RoleInstanceList>"+
		                "<UpgradeDomainCount>2</UpgradeDomainCount>"+
		            "</Deployment>"+
		        "</Deployments>"+
		    "</HostedService>";

    @Test
    public void testNoTags() throws IOException {
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        AzureConfig azureConfig = buildAzureConfig();
        final Collection<String> result = AzureCloudyUtility.unmarshalTheResponse(is, azureConfig);
        assertEquals(2, result.size());
    }

    @Test
    public void testClient() throws Exception {
        AzureClient client = new AzureClient(buildAzureConfig());
        Collection<String> privateIpAddresses = client.getPrivateIpAddresses();
        assertEquals(2, privateIpAddresses.size());
        System.out.println(privateIpAddresses.toString());
    }

    @Test
    public void testDummyAzureEnv() throws Exception {
        Config config = new Config();

        NetworkConfig networkConfig = config.getNetworkConfig();

        networkConfig.getJoin().getMulticastConfig().setEnabled(false);
        networkConfig.getJoin().getTcpIpConfig().setEnabled(false);
        networkConfig.getJoin().setAzureConfig(buildAzureConfig());
        networkConfig.setPortAutoIncrement(true);

        JoinConfig join = networkConfig.getJoin();
        join.getMulticastConfig().setEnabled(false);

        testJoin(config);
    }

    private void testJoin(Config config) throws Exception {
        config.setProperty(GroupProperties.PROP_WAIT_SECONDS_BEFORE_JOIN, "0");

        HazelcastInstance h1 = Hazelcast.newHazelcastInstance(config);
        assertEquals(1, h1.getCluster().getMembers().size());

        HazelcastInstance h2 = Hazelcast.newHazelcastInstance(config);
        assertEquals(2, h1.getCluster().getMembers().size());
        assertEquals(2, h2.getCluster().getMembers().size());

        h1.shutdown();
        h1 = Hazelcast.newHazelcastInstance(config);
        assertEquals(2, h1.getCluster().getMembers().size());
        assertEquals(2, h2.getCluster().getMembers().size());
    }

    private AzureConfig buildAzureConfig() {
        AzureConfig azureConfig = new AzureConfig();
        azureConfig.setEnabled(true);
        azureConfig.setHost("management.core.windows.net/");
        azureConfig.setServiceName("ncsrvcbbqseus01");
        azureConfig.setSubscriptionId("55555555-5130-401c-89d1-5555555555");
        azureConfig.setKeyStoreLocation("somewhere");
        azureConfig.setKeyStorePassword("somePass");
        return azureConfig;
    }

}
