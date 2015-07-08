package utils.eiffelyk.www.httpstest;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.client.ClientProtocolException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends Activity {
    private Button btn1,btn2;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1 = (Button) this.findViewById(R.id.button1);
        btn2 = (Button) this.findViewById(R.id.button2);
        textView = (TextView) this.findViewById(R.id.textView);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            initSSL();
                        } catch (Exception e) {
                            Log.e("馋猫", e.getMessage());
                        }
                    }
                }).start();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            initSSLALL();
                        } catch (Exception e) {
                            Log.e("馋猫", e.getMessage());
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * HttpUrlConnection 方式，支持指定load-der.crt证书验证，此种方式Android官方建议
     * @throws CertificateException
     * @throws IOException
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public void initSSL() throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        Log.e("馋猫", "开始initSSL");
        String httpUrl = "https://kyfw.12306.cn/otn/";
        URL url = new URL(httpUrl);
        InputStream input;
        if (httpUrl!=null&& httpUrl.contains("https")){
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream in = getAssets().open("srca.cer");
            Certificate ca = cf.generateCertificate(in);

            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(null, null);
            keystore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keystore);

            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(false);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            input = urlConnection.getInputStream();
        }else{
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(false);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            input = urlConnection.getInputStream();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        Log.e("馋猫", result.toString());
    }

    /**
     * HttpUrlConnection支持所有Https免验证，不建议使用
     *
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public void initSSLALL() throws KeyManagementException, NoSuchAlgorithmException, IOException {
        Log.e("馋猫", "开始initSSLALL");
        //URL url = new URL("https://egdmarket.com/APS/RequestID");
        String httpUrl = "https://pan.baidu.com/disk/home";
        URL url = new URL(httpUrl);
        InputStream in;
        if (httpUrl != null && httpUrl.contains("https")) {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{new TrustAllManager()}, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.setRequestMethod("GET");
            connection.connect();
            in = connection.getInputStream();
        }else{
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.setRequestMethod("GET");
            connection.connect();
            in = connection.getInputStream();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        Log.e("馋猫", result.toString());
    }



    public class TrustAllManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
    /**
     * HttpClient方式实现，支持所有Https免验证方式链接
     *
     * @throws ClientProtocolException
     * @throws IOException
     *//*
    public void initSSLAllWithHttpClient() throws ClientProtocolException, IOException {
        int timeOut = 30 * 1000;
        HttpParams param = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(param, timeOut);
        HttpConnectionParams.setSoTimeout(param, timeOut);
        HttpConnectionParams.setTcpNoDelay(param, true);

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", TrustAllSSLSocketFactory.getDefault(), 443));
        ClientConnectionManager manager = new ThreadSafeClientConnManager(param, registry);
        DefaultHttpClient client = new DefaultHttpClient(manager, param);

        HttpGet request = new HttpGet("https://certs.cac.washington.edu/CAtest/");
        // HttpGet request = new HttpGet("https://www.alipay.com/");
        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        Log.e("HTTPS TEST", result.toString());
    }

    *//**
     * HttpClient方式实现，支持验证指定证书
     *
     * @throws ClientProtocolException
     * @throws IOException
     *//*
    public void initSSLCertainWithHttpClient() throws ClientProtocolException, IOException {
        int timeOut = 30 * 1000;
        HttpParams param = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(param, timeOut);
        HttpConnectionParams.setSoTimeout(param, timeOut);
        HttpConnectionParams.setTcpNoDelay(param, true);

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", TrustCertainHostNameFactory.getDefault(this), 443));
        ClientConnectionManager manager = new ThreadSafeClientConnManager(param, registry);
        DefaultHttpClient client = new DefaultHttpClient(manager, param);

        // HttpGet request = new
        // HttpGet("https://certs.cac.washington.edu/CAtest/");
        HttpGet request = new HttpGet("https://www.alipay.com/");
        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        Log.e("HTTPS TEST", result.toString());
    }*/
}
