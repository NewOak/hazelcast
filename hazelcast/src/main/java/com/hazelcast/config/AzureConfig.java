package com.hazelcast.config;

public class AzureConfig {
	
	private static final int CONNECTION_TIMEOUT = 10;

    private boolean enabled;
    private String subscriptionId;
    private String serviceName;
    private String host = "management.core.windows.net";
    private final String schemaVersion = "2014-06-01";
    private final String embedDetail = "true";
    private String keyStoreLocation;
    private String keyStorePassword;
    private int connectionTimeoutSeconds = CONNECTION_TIMEOUT;

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getKeyStoreLocation() {
        return keyStoreLocation;
    }

    public void setKeyStoreLocation(String keyStoreLocation) {
        this.keyStoreLocation = keyStoreLocation;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public static int getConnectionTimeout() {
        return CONNECTION_TIMEOUT;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getEmbedDetail() {
        return embedDetail;
    }

    public int getConnectionTimeoutSeconds() {
        return connectionTimeoutSeconds;
    }

    public void setConnectionTimeoutSeconds(int connectionTimeoutSeconds) {
        this.connectionTimeoutSeconds = connectionTimeoutSeconds;
    }

    @Override
    public String toString() {
        return "AzureConfig{" +
                "enabled=" + enabled +
                ", subscriptionId='" + subscriptionId + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", host='" + host + '\'' +
                ", schemaVersion='" + schemaVersion + '\'' +
                ", embedDetail='" + embedDetail + '\'' +
                ", keyStoreLocation='" + keyStoreLocation + '\'' +
                ", keyStorePassword='" + keyStorePassword + '\'' +
                ", connectionTimeoutSeconds=" + connectionTimeoutSeconds +
                '}';
    }
}
