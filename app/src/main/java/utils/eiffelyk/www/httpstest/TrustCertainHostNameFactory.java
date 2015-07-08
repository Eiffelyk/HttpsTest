package utils.eiffelyk.www.httpstest;

import android.content.Context;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

/**
 * 信任特定的Host     httpsClient方式使用
 * Created by 馋猫 on 2015/7/8.
 */
public class TrustCertainHostNameFactory extends SSLSocketFactory {

    private static TrustCertainHostNameFactory mInstance;

    public TrustCertainHostNameFactory(KeyStore keyStore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(keyStore);
    }

    public static TrustCertainHostNameFactory getDefault(Context context) {
        KeyStore keystore;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream in;
            in = context.getAssets().open("load-der.crt");
            Certificate ca = cf.generateCertificate(in);

            keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(null, null);
            keystore.setCertificateEntry("ca", ca);

            if (null == mInstance) {
                mInstance = new TrustCertainHostNameFactory(keystore);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mInstance;
    }

    @Override
    public Socket createSocket() throws IOException {
        return super.createSocket();
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException{
        return super.createSocket(socket, host, port, autoClose);
    }
}
