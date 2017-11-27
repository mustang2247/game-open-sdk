package com.openapi.template.tools;

import android.util.Log;

import com.openapi.template.Constants;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;


public class HttpService {
	private  int read_time_out = 10000;
	private  int connect_time_out = 3000;
	private  int retiesTimes = 0;
	
	
	private static ExecutorService executorService = Executors.newFixedThreadPool(2);
	
    public HttpService() {
        super();
    }
    
    public HttpService(int time_out) {
        read_time_out = time_out;
    }
    
    public HttpService(int time_out, int connect_time_out) {
        this.read_time_out = time_out;
        this.connect_time_out = connect_time_out; 
    }
    
    public HttpService(int time_out, int connect_time_out, int retiesTimes) {
        this.read_time_out = time_out;
        this.connect_time_out = connect_time_out;
        this.retiesTimes = retiesTimes;
    }
    
    public String doPost(String url, Map<String,String> params){
        return this.doPost(url, params,null);
    }
    
    public String doPost(String url, Map<String,String> params, Map<String,String> headers){
    	StringBuilder postData = new StringBuilder();
    	for(Entry<String,String> entry:params.entrySet()){
    		if(postData.length()!=0){
    			postData.append("&");
    		}
    		postData.append(entry.getKey()).append("=").append(entry.getValue());
    	}
        return service(false, url, postData.toString(), "POST", headers);
    }

    public String doPost(String url, String postData, Map<String,String> headers){
        return service(false, url, postData, "POST", headers);
    }
    
    public String doGet(String url, Map<String,String> params, Map<String,String> headers){
    	StringBuilder postData = new StringBuilder();
    	for(Entry<String,String> entry:params.entrySet()){
    		if(postData.length()!=0){
    			postData.append("&");
    		}
    		postData.append(entry.getKey()).append("=").append(entry.getValue());
    	}
        return service(false, url, postData.toString(), "GET", headers);
    }
    
    public String doGet(String url, Map<String,String> headers){
        return service(false, url, null, "GET", headers);
    }
    
    public String doGet(String url){
        return service(false, url, null, "GET", null);
    }
    
    public String doHttpsPost(String url, String postData) {
    	return service(true, url, postData, "POST",null);
    }
    
    public String doPost(String url, String postData) {
    	return service(false, url, postData, "POST",null);
    }
    
    public String doHttpsPost(String url, Map<String,String> params){
    	return  doHttpsPost(url,params,null);
    }
    
    public String doHttpsPost(String url, Map<String,String> params, Map<String,String> headers){
    	StringBuilder postData = new StringBuilder();
    	for(Entry<String,String> entry:params.entrySet()){
    		if(postData.length()!=0){
    			postData.append("&");
    		}
    		postData.append(entry.getKey()).append("=").append(entry.getValue());
    	}
        return service(true, url, postData.toString(), "POST", headers);
    }
    
    
    public String doHttpsGet(String url) {
    	return service(true, url, null, "GET",null);
    }
    
    private String service(boolean isHttps, String url, String postData, String method, Map<String,String> headers){
    	int currentRetriesTimes = retiesTimes;
    	while(currentRetriesTimes>=0){
    		try{
    			return service0(isHttps, url, postData, method, headers);
    		}catch(Exception e){
    			currentRetriesTimes--;
    			if(currentRetriesTimes<0){
    				Log.i(Constants.tag, String.format("请求url:[%s]失败", url), e);
    				return "failed to connect to " + url;
    			}
                Log.i(Constants.tag, "http重试,次数:"+(retiesTimes-currentRetriesTimes)+",url:"+url);
    		}
    	}
    	throw new RuntimeException("不可能会执行到这里");
    }
    
    private String service0(final boolean isHttps, final String url, final String postData, final String method, final Map<String,String> headers) throws InterruptedException, ExecutionException, TimeoutException {
    	
    	FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>() {
			@Override
			public String call() throws Exception {
				HttpURLConnection conn = null;
		    	try {
		    		boolean doOutput = isNotEmpty(postData);
		    		conn = isHttps ? createHttpsConn(url, method, doOutput) : createHttpConn(url, method, doOutput);

		    		fillProperties(conn, headers);

		    		if(doOutput) writeMsg(conn, postData);

		    		String msg = readMsg(conn);

		    		return msg;
		    	}catch(Exception e){
		    		Log.i(Constants.tag, String.format("请求url:[%s]失败", url), e);
		    		throw e;
		    	} finally {
		    		if (conn != null) {
		    			conn.disconnect();
		    			conn = null;
		    		}
		    	}
			}
			
		});
    	
    	
    	
    	executorService.submit(futureTask);
    	return futureTask.get(this.read_time_out+connect_time_out, TimeUnit.MILLISECONDS);
    }

    private HttpURLConnection createHttpConn(String url, String method, boolean doOutput) throws IOException {
        URL dataUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) dataUrl.openConnection();
        conn.setReadTimeout(read_time_out);
        conn.setConnectTimeout(connect_time_out);
        conn.setRequestMethod(method);
        conn.setDoOutput(doOutput);
        conn.setDoInput(true);
        return conn;
    }

    private String readMsg(HttpURLConnection conn) throws IOException {
        return readMsg(conn, "UTF-8");
    }

    private String readMsg(HttpURLConnection conn, String charSet) throws IOException {
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), charSet));
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            return sb.toString();
        } finally {
            if(reader != null){
                reader.close();
            }
        }
    }

    private void writeMsg(HttpURLConnection conn, String postData) throws IOException {
        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
        dos.write(postData.getBytes());
        dos.flush();
        dos.close();
    }

    private void fillProperties(HttpURLConnection conn, Map<String,String> params) {
        if(params == null||params.isEmpty()){
            return;
        }

        for (Entry<String,String> entry: params.entrySet()) {
            conn.addRequestProperty(entry.getKey(), entry.getValue());
        }
    }
    
    
    public String httpsPost(String url, String postData)  throws Exception {
        HttpURLConnection conn = null;
        try {
            boolean doOutput = isNotEmpty(postData);
            conn = createHttpsConn(url, "POST", doOutput);
            if (doOutput)
                writeMsg(conn, postData);

            return readMsg(conn);
        }finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
    }

	private boolean isNotEmpty(String postData) {
		return (postData!=null) &&postData.trim().length()>0;
	}
    

    private HttpURLConnection createHttpsConn(String url, String method, boolean doOutput)  throws Exception {
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        };

        HttpsURLConnection.setDefaultHostnameVerifier(hv);
        trustAllHttpsCertificates();

        URL dataUrl = new URL(url);
        
        HttpURLConnection conn = (HttpURLConnection) dataUrl.openConnection();
        conn.setReadTimeout(read_time_out);
        conn.setConnectTimeout(connect_time_out);
        conn.setRequestMethod(method);
        conn.setDoOutput(doOutput);
        conn.setDoInput(true);
        return conn;
    }
    
    public static void trustAllHttpsCertificates() throws Exception {

        //  Create a trust manager that does not validate certificate chains:

        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        
        javax.net.ssl.TrustManager tm = new miTM();

        trustAllCerts[0] = tm;

        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");

        sc.init(null, trustAllCerts, null);

        HttpsURLConnection.setDefaultSSLSocketFactory(
                sc.getSocketFactory());

    }
    
    public static class miTM implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType) throws
                java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType) throws
                java.security.cert.CertificateException {
            return;
        }
    }
    


    
}
