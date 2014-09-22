package com.hazelcast.azure;

import com.hazelcast.config.AbstractXmlConfigHelper;
import com.hazelcast.config.AwsConfig;
import com.hazelcast.config.AzureConfig;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.util.*;

import static com.hazelcast.config.AbstractXmlConfigHelper.cleanNodeName;

/**
 * Created by bweidlich on 8/1/2014.
 */
public class AzureCloudyUtility {
    static final ILogger LOGGER = Logger.getLogger(AzureCloudyUtility.class);
    private static XPath xPath = XPathFactory.newInstance().newXPath();

    public static Collection<String> unmarshalTheResponse(InputStream stream, AzureConfig azureConfig) {
        return parseAddresses(stream, azureConfig);
    }

    private static Collection<String> parseAddresses(InputStream in, AzureConfig azureConfig) {
        final DocumentBuilder builder;
        List<String> ips = new ArrayList<String>();
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(in);
            Element element = doc.getDocumentElement();

            String expression = "./Deployments/Deployment[1]/RoleInstanceList/RoleInstance";
            NodeList roleInstances;
            try
            {
                roleInstances = (NodeList) xPath.compile(expression).evaluate(element, XPathConstants.NODESET);
            }
            catch (XPathExpressionException e)
            {
                LOGGER.severe(e.getMessage(), e);
                return null;
            }

            for (org.w3c.dom.Node node : new AbstractXmlConfigHelper.IterableNodeList(roleInstances)) {
                ips.add(extractIp(node));
            }

        } catch (Exception e) {
            LOGGER.warning(e);
        }
        return ips;
    }

    private static String extractIp(Node node)
    {
        String expression = "./IpAddress/text()";

        try
        {
            return (String) xPath.compile(expression).evaluate(node, XPathConstants.STRING);
        }
        catch (XPathExpressionException e)
        {
            LOGGER.severe(e.getMessage(), e);
            return null;
        }
    }
}
