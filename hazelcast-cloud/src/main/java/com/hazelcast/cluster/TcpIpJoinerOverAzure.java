package com.hazelcast.cluster;

import com.hazelcast.azure.AzureClient;
import com.hazelcast.cluster.impl.TcpIpJoiner;
import com.hazelcast.config.AzureConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.util.ExceptionUtil;

import java.util.Collection;

public class TcpIpJoinerOverAzure extends TcpIpJoiner
{

    final AzureClient azureClient;

	public TcpIpJoinerOverAzure(Node node) {
		super(node);
        AzureConfig azureConfig = node.getConfig().getNetworkConfig().getJoin().getAzureConfig();
        azureClient = new AzureClient(azureConfig);
    }

	@Override
	protected Collection<String> getMembers() {
        try {
            Collection<String> list = azureClient.getPrivateIpAddresses();
            if(list.isEmpty()){
                logger.warning("No Azure instances found!");
            }else{
                if(logger.isFinestEnabled()){
                    StringBuilder sb = new StringBuilder("Found the following Azure instances:\n");
                    for(String ip: list){
                        sb.append("    ").append(ip).append("\n");
                    }
                    logger.finest(sb.toString());
                }
            }
            return list;
        } catch (Exception e) {
            logger.warning(e);
            throw ExceptionUtil.rethrow(e);
        }
	}

	@Override
	protected int getConnTimeoutSeconds() {
        AzureConfig azureConfig = node.getConfig().getNetworkConfig().getJoin().getAzureConfig();
        return azureConfig.getConnectionTimeoutSeconds();
	}

	@Override
	public String getType() {
		return "azure";
	}

}
