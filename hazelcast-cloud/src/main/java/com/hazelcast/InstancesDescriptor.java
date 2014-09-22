package com.hazelcast;

import java.util.Map;

/**
 * Created by bweidlich on 8/1/2014.
 */
public interface InstancesDescriptor {
    void putSignature(String value);
    Map<String, String> getAttributes();
}
