/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.azure;

import com.hazelcast.InstancesDescriptor;
import com.hazelcast.aws.security.CertificateLoader;
import com.hazelcast.aws.security.EC2RequestSigner;
import com.hazelcast.aws.utility.AwsCloudyUtility;
import com.hazelcast.config.AzureConfig;

import javax.net.ssl.*;
import javax.security.auth.kerberos.KerberosKey;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.hazelcast.aws.impl.Constants.*;

public class DescribeAzureInstances implements InstancesDescriptor {

    private final AzureConfig azureConfig;

    private Map<String, String> attributes = new HashMap<String, String>();

    public DescribeAzureInstances(AzureConfig azureConfig) {
        if (azureConfig == null) {
            throw new IllegalArgumentException("AwsConfig is required!");
        }
        if (azureConfig.getSubscriptionId() == null) {
            throw new IllegalArgumentException("Subscription Id is required!");
        }
        attributes.put("embed-detail", azureConfig.getEmbedDetail());
        this.azureConfig = azureConfig;
    }

    public String getQueryString() {
        return AwsCloudyUtility.getQueryString(attributes);
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void putSignature(String value) {
        attributes.put("Signature", value);
    }

    public Collection<String> execute(String endpoint) throws Exception {
        InputStream stream = callService(endpoint);
        return AzureCloudyUtility.unmarshalTheResponse(stream, azureConfig);
    }

    private static String cleanEndpoint(String endpoint)
    {
        if (endpoint == null || endpoint.isEmpty())
            throw new IllegalArgumentException("Valid endpoint is required.");
        if(!endpoint.startsWith("https://"))
        {
            endpoint = "https://" + endpoint;
        }
        if(endpoint.endsWith("/"))
            endpoint = endpoint.substring(0, endpoint.length() - 2);
        return endpoint;
    }

    private InputStream callService(String endpoint) throws Exception {
        endpoint = cleanEndpoint(endpoint);
        URL url = new URL(endpoint + getQueryString());

        HttpsURLConnection https = buildConnection(url);
        return https.getInputStream();
    }

    private HttpsURLConnection buildConnection(URL url) throws IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
        https.setRequestMethod(GET);
        https.setDoOutput(true);
        https.setSSLSocketFactory(CertificateLoader.getSSLSocketFactory(azureConfig.getKeyStoreLocation(), azureConfig.getKeyStorePassword()));
        https.addRequestProperty("x-ms-version", azureConfig.getSchemaVersion());
        https.getHeaderFields();
        return https;
    }
}
