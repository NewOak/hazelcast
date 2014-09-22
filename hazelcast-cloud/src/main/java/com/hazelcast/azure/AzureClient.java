/**
 * 
 */
package com.hazelcast.azure;

import com.hazelcast.CloudClient;
import com.hazelcast.config.AzureConfig;

import java.util.Collection;
import java.util.Map;

public class AzureClient implements CloudClient {

    private String endpoint;
    private final AzureConfig azureConfig;

    public AzureClient(AzureConfig azureConfig) {
        if (azureConfig == null){
            throw new IllegalArgumentException("AzureConfig is required");
        }
        if (azureConfig.getSubscriptionId() == null){
            throw new IllegalArgumentException("Subscription Id is required");
        }
        if (azureConfig.getSchemaVersion() == null){
            throw new IllegalArgumentException("Schema Version is required");
        }
        if (azureConfig.getHost() == null){
            throw new IllegalArgumentException("Host is required");
        }
        if (azureConfig.getKeyStoreLocation() == null){
            throw new IllegalArgumentException("Certificate is required");
        }

        this.azureConfig = azureConfig;
        StringBuilder sb = new StringBuilder();
        sb.append(azureConfig.getHost());
        sb.append("/");
        sb.append(azureConfig.getSubscriptionId());
        sb.append("/services/hostedservices/");
        sb.append(azureConfig.getServiceName());
        this.endpoint = sb.toString();
    }

    @Override
    public Collection<String> getPrivateIpAddresses() throws Exception {
        return new DescribeAzureInstances(this.azureConfig).execute(this.endpoint);
    }

    @Override
    public Map<String, String> getAddresses() {
        return null;
    }
}
