package com.wf.gts.remoting.netty;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wf.gts.remoting.util.RemotingHelper;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;



public class TlsHelper {

    public interface DecryptionStrategy {
        /**
         * Decrypt the target encrpted private key file.
         *
         * @param privateKeyEncryptPath A pathname string
         * @param forClient tells whether it's a client-side key file
         * @return An input stream for a decrypted key file
         * @throws IOException if an I/O error has occurred
         */
        InputStream decryptPrivateKey(String privateKeyEncryptPath, boolean forClient) throws IOException;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RemotingHelper.ROCKETMQ_REMOTING);

    private static DecryptionStrategy decryptionStrategy = new DecryptionStrategy() {
        @Override
        public InputStream decryptPrivateKey(final String privateKeyEncryptPath,
            final boolean forClient) throws IOException {
            return new FileInputStream(privateKeyEncryptPath);
        }
    };


    public static void registerDecryptionStrategy(final DecryptionStrategy decryptionStrategy) {
        TlsHelper.decryptionStrategy = decryptionStrategy;
    }

    public static SslContext buildSslContext(boolean forClient) throws IOException, CertificateException {
        File configFile = new File(TlsSystemConfig.tlsConfigFile);
        extractTlsConfigFromFile(configFile);
        logTheFinalUsedTlsConfig();

        SslProvider provider;
        if (OpenSsl.isAvailable()) {
            provider = SslProvider.OPENSSL;
            LOGGER.info("Using OpenSSL provider");
        } else {
            provider = SslProvider.JDK;
            LOGGER.info("Using JDK SSL provider");
        }

        if (forClient) {
            if (TlsSystemConfig.tlsTestModeEnable) {
                return SslContextBuilder
                    .forClient()
                    .sslProvider(SslProvider.JDK)
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
            } else {
                SslContextBuilder sslContextBuilder = SslContextBuilder.forClient().sslProvider(SslProvider.JDK);


                if (!TlsSystemConfig.tlsClientAuthServer) {
                    sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);
                } else {
                    if (!isNullOrEmpty(TlsSystemConfig.tlsClientTrustCertPath)) {
                        sslContextBuilder.trustManager(new File(TlsSystemConfig.tlsClientTrustCertPath));
                    }
                }

                return sslContextBuilder.keyManager(
                    !isNullOrEmpty(TlsSystemConfig.tlsClientCertPath) ? new FileInputStream(TlsSystemConfig.tlsClientCertPath) : null,
                    !isNullOrEmpty(TlsSystemConfig.tlsClientKeyPath) ? decryptionStrategy.decryptPrivateKey(TlsSystemConfig.tlsClientKeyPath, true) : null,
                    !isNullOrEmpty(TlsSystemConfig.tlsClientKeyPassword) ? TlsSystemConfig.tlsClientKeyPassword : null)
                    .build();
            }
        } else {

            if (TlsSystemConfig.tlsTestModeEnable) {
                SelfSignedCertificate selfSignedCertificate = new SelfSignedCertificate();
                return SslContextBuilder
                    .forServer(selfSignedCertificate.certificate(), selfSignedCertificate.privateKey())
                    .sslProvider(SslProvider.JDK)
                    .clientAuth(ClientAuth.OPTIONAL)
                    .build();
            } else {
                SslContextBuilder sslContextBuilder = SslContextBuilder.forServer(
                    !isNullOrEmpty(TlsSystemConfig.tlsServerCertPath) ? new FileInputStream(TlsSystemConfig.tlsServerCertPath) : null,
                    !isNullOrEmpty(TlsSystemConfig.tlsServerKeyPath) ? decryptionStrategy.decryptPrivateKey(TlsSystemConfig.tlsServerKeyPath, false) : null,
                    !isNullOrEmpty(TlsSystemConfig.tlsServerKeyPassword) ? TlsSystemConfig.tlsServerKeyPassword : null)
                    .sslProvider(provider);

                if (!TlsSystemConfig.tlsServerAuthClient) {
                    sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);
                } else {
                    if (!isNullOrEmpty(TlsSystemConfig.tlsServerTrustCertPath)) {
                        sslContextBuilder.trustManager(new File(TlsSystemConfig.tlsServerTrustCertPath));
                    }
                }

                sslContextBuilder.clientAuth(parseClientAuthMode(TlsSystemConfig.tlsServerNeedClientAuth));
                return sslContextBuilder.build();
            }
        }
    }

    private static void extractTlsConfigFromFile(final File configFile) {
        if (!(configFile.exists() && configFile.isFile() && configFile.canRead())) {
            LOGGER.info("Tls config file doesn't exist, skip it");
            return;
        }

        Properties properties;
        properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(configFile);
            properties.load(inputStream);
        } catch (IOException ignore) {
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException ignore) {
                }
            }
        }

        TlsSystemConfig.tlsTestModeEnable = Boolean.parseBoolean(properties.getProperty(TlsSystemConfig.TLS_TEST_MODE_ENABLE, String.valueOf(TlsSystemConfig.tlsTestModeEnable)));
        TlsSystemConfig.tlsServerNeedClientAuth = properties.getProperty(TlsSystemConfig.TLS_SERVER_NEED_CLIENT_AUTH, TlsSystemConfig.tlsServerNeedClientAuth);
        TlsSystemConfig.tlsServerKeyPath = properties.getProperty(TlsSystemConfig.TLS_SERVER_KEYPATH, TlsSystemConfig.tlsServerKeyPath);
        TlsSystemConfig.tlsServerKeyPassword = properties.getProperty(TlsSystemConfig.TLS_SERVER_KEYPASSWORD, TlsSystemConfig.tlsServerKeyPassword);
        TlsSystemConfig.tlsServerCertPath = properties.getProperty(TlsSystemConfig.TLS_SERVER_CERTPATH, TlsSystemConfig.tlsServerCertPath);
        TlsSystemConfig.tlsServerAuthClient = Boolean.parseBoolean(properties.getProperty(TlsSystemConfig.TLS_SERVER_AUTHCLIENT, String.valueOf(TlsSystemConfig.tlsServerAuthClient)));
        TlsSystemConfig.tlsServerTrustCertPath = properties.getProperty(TlsSystemConfig.TLS_SERVER_TRUSTCERTPATH, TlsSystemConfig.tlsServerTrustCertPath);

        TlsSystemConfig.tlsClientKeyPath = properties.getProperty(TlsSystemConfig.TLS_CLIENT_KEYPATH, TlsSystemConfig.tlsClientKeyPath);
        TlsSystemConfig.tlsClientKeyPassword = properties.getProperty(TlsSystemConfig.TLS_CLIENT_KEYPASSWORD, TlsSystemConfig.tlsClientKeyPassword);
        TlsSystemConfig.tlsClientCertPath = properties.getProperty(TlsSystemConfig.TLS_CLIENT_CERTPATH, TlsSystemConfig.tlsClientCertPath);
        TlsSystemConfig.tlsClientAuthServer = Boolean.parseBoolean(properties.getProperty(TlsSystemConfig.TLS_CLIENT_AUTHSERVER, String.valueOf(TlsSystemConfig.tlsClientAuthServer)));
        TlsSystemConfig.tlsClientTrustCertPath = properties.getProperty(TlsSystemConfig.TLS_CLIENT_TRUSTCERTPATH, TlsSystemConfig.tlsClientTrustCertPath);
    }

    private static void logTheFinalUsedTlsConfig() {
        LOGGER.info("Log the final used tls related configuration");
        LOGGER.info("{} = {}", TlsSystemConfig.TLS_TEST_MODE_ENABLE, TlsSystemConfig.tlsTestModeEnable);
        LOGGER.info("{} = {}", TlsSystemConfig.TLS_SERVER_NEED_CLIENT_AUTH, TlsSystemConfig.tlsServerNeedClientAuth);
        LOGGER.info("{} = {}", TlsSystemConfig.TLS_SERVER_KEYPATH, TlsSystemConfig.tlsServerKeyPath);
        LOGGER.info("{} = {}", TlsSystemConfig.TLS_SERVER_KEYPASSWORD, TlsSystemConfig.tlsServerKeyPassword);
        LOGGER.info("{} = {}", TlsSystemConfig.TLS_SERVER_CERTPATH, TlsSystemConfig.tlsServerCertPath);
        LOGGER.info("{} = {}", TlsSystemConfig.TLS_SERVER_AUTHCLIENT, TlsSystemConfig.tlsServerAuthClient);
        LOGGER.info("{} = {}", TlsSystemConfig.TLS_SERVER_TRUSTCERTPATH, TlsSystemConfig.tlsServerTrustCertPath);

        LOGGER.info("{} = {}", TlsSystemConfig.TLS_CLIENT_KEYPATH, TlsSystemConfig.tlsClientKeyPath);
        LOGGER.info("{} = {}", TlsSystemConfig.TLS_CLIENT_KEYPASSWORD, TlsSystemConfig.tlsClientKeyPassword);
        LOGGER.info("{} = {}", TlsSystemConfig.TLS_CLIENT_CERTPATH, TlsSystemConfig.tlsClientCertPath);
        LOGGER.info("{} = {}", TlsSystemConfig.TLS_CLIENT_AUTHSERVER, TlsSystemConfig.tlsClientAuthServer);
        LOGGER.info("{} = {}", TlsSystemConfig.TLS_CLIENT_TRUSTCERTPATH, TlsSystemConfig.tlsClientTrustCertPath);
    }

    private static ClientAuth parseClientAuthMode(String authMode) {
        if (null == authMode || authMode.trim().isEmpty()) {
            return ClientAuth.NONE;
        }
        for (ClientAuth clientAuth : ClientAuth.values()) {
            if (clientAuth.name().equals(authMode.toUpperCase())) {
                return clientAuth;
            }
        }
        return ClientAuth.NONE;
    }

    /**
     * Determine if a string is {@code null} or {@link String#isEmpty()} returns {@code true}.
     */
    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
