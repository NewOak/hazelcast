package com.hazelcast.aws.security;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/**
 * Created by bweidlich on 8/1/2014.
 */
public class CertificateLoader {

    private static InputStream fullStream (String fname) throws IOException {
        FileInputStream fis = new FileInputStream(fname);
        DataInputStream dis = new DataInputStream(fis);
        byte[] bytes = new byte[dis.available()];
        dis.readFully(bytes);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        return bais;
    }

    public static void loadCertificate(String certificate, String certificateAlias, String keyStoreName, String keyStorePassword) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {

        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(null, keyStorePassword.toCharArray());

        String alias = certificateAlias;
        char[] password = keyStorePassword.toCharArray();

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream certstream = fullStream (certificate);
        Certificate certs =  cf.generateCertificate(certstream);

        // Add the certificate
        keystore.setCertificateEntry(alias, certs);

        // Save the new keystore contents
        FileOutputStream out = new FileOutputStream(keyStoreName);
        keystore.store(out, password);
        out.close();
    }

    private static KeyStore getKeyStore(String keyStoreName, String password) throws IOException
    {
        KeyStore ks = null;
        FileInputStream fis = null;
        try {
            ks = KeyStore.getInstance("JKS");
            char[] passwordArray = password.toCharArray();
            fis = new java.io.FileInputStream(keyStoreName);
            ks.load(fis, passwordArray);
            fis.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            if (fis != null) {
                fis.close();
            }
        }
        return ks;
    }

    public static SSLSocketFactory getSSLSocketFactory(String keyStoreName, String password) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, IOException {
        KeyStore ks = getKeyStore(keyStoreName, password);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(ks, password.toCharArray());

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());

        return context.getSocketFactory();
    }
}
