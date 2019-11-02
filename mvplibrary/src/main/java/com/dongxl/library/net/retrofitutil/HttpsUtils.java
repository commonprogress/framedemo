package com.dongxl.library.net.retrofitutil;


import com.dongxl.library.utils.AppMaster;
import com.dongxl.library.utils.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * desc：https认证
 */
public class HttpsUtils {
    public final static String CER_NAME = "＊.gank.tv.cer";

    public static class SSLParams {
        public SSLSocketFactory sSLSocketFactory;
        public X509TrustManager trustManager;
    }

    public static SSLParams getSslSocketFactory() {
        SSLParams sslParams = new SSLParams();
        try {
            InputStream certInput = AppMaster.getInstance().getAppContext().getAssets().open(CER_NAME);
            TrustManager[] trustManagers = prepareTrustManager(certInput);
            KeyManager[] keyManagers = prepareKeyManager(null, null);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            X509TrustManager trustManager;
            if (trustManagers != null) {
                trustManager = new MyTrustManager(chooseTrustManager(trustManagers));
            } else {
                trustManager = new UnSafeTrustManager();
            }
            sslContext.init(keyManagers, new TrustManager[]{trustManager}, null);
            sslParams.sSLSocketFactory = sslContext.getSocketFactory();
            sslParams.trustManager = trustManager;
            return sslParams;
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        } catch (KeyManagementException e) {
            throw new AssertionError(e);
        } catch (KeyStoreException e) {
            throw new AssertionError(e);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public static class AllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static class UnSafeTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static TrustManager[] prepareTrustManager(InputStream... certificates) {
        if (certificates == null || certificates.length <= 0) {
            return null;
        }
        try {

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                Certificate ca = certificateFactory.generateCertificate(certificate);
                keyStore.setCertificateEntry(certificateAlias, ca);
                try {
                    if (certificate != null) {
                        certificate.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            TrustManagerFactory trustManagerFactory;

            trustManagerFactory = TrustManagerFactory.
                    getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            return trustManagers;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private static KeyManager[] prepareKeyManager(InputStream bksFile, String password) {
        try {
            if (bksFile == null || password == null) {
                return null;
            }

            KeyStore clientKeyStore = KeyStore.getInstance("BKS");
            clientKeyStore.load(bksFile, password.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, password.toCharArray());
            return keyManagerFactory.getKeyManagers();

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }

    /**
     * 自定义检验证书合法性
     */
    private static class MyTrustManager implements X509TrustManager {
        private X509TrustManager localTrustManager;

        public MyTrustManager(X509TrustManager localTrustManager) throws NoSuchAlgorithmException, KeyStoreException {
            TrustManagerFactory var4 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            var4.init((KeyStore) null);
            this.localTrustManager = localTrustManager;
        }


        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                InputStream clientCertInput = AppMaster.getInstance().getAppContext().getAssets().open(CER_NAME);
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                X509Certificate clientCert = (X509Certificate) certificateFactory.generateCertificate(clientCertInput);
                for (X509Certificate serverCert : chain) {
                    LogUtils.i("Longer==========>", "key=" + clientCert.getPublicKey());
                    LogUtils.i("Longer==========>", "key=" + serverCert.getPublicKey());
                    // 验证证书的合法性
                    serverCert.checkValidity();
                    // 验证服务端公钥和客户端是否一致，不一致请求会中断
                    serverCert.verify(clientCert.getPublicKey());
                }
            } catch (Exception e) {
                localTrustManager.checkServerTrusted(chain, authType);
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

}
