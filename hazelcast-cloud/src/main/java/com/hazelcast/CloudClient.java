package com.hazelcast;

import java.util.Collection;
import java.util.Map;

/**
 * Created by bweidlich on 8/1/2014.
 */
public interface CloudClient {
    public Collection<String> getPrivateIpAddresses() throws Exception;
    public Map<String, String> getAddresses() throws Exception;
}
