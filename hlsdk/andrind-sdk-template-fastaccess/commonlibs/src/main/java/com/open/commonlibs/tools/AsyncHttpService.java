package com.open.commonlibs.tools;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class AsyncHttpService {
	private int read_time_out = 5000;
	private int connect_time_out = 1000;
	private int retiesTimes = 1;
	private AtomicInteger atomicInteger = new AtomicInteger(0);

	public AsyncHttpService() {
		super();
	}

	public AsyncHttpService(int time_out) {
		read_time_out = time_out;
	}

	public AsyncHttpService(int time_out, int connect_time_out) {
		this.read_time_out = time_out;
		this.connect_time_out = connect_time_out;
	}

	public AsyncHttpService(int time_out, int connect_time_out, int retiesTimes) {
		this.read_time_out = time_out;
		this.connect_time_out = connect_time_out;
		this.retiesTimes = retiesTimes;
	}

	public void doGet(String url, String postData, Map<String, String> headers, Loadding loadding, AsyncHttpResponse iResponse) {
		service(false, url, postData, "GET", headers, loadding, 0, iResponse);
	}

	public void doGet(String url, Loadding loadding, AsyncHttpResponse iResponse) {
		this.doGet(url, null, null, loadding, iResponse);
	}

	public void doHttpsGet(String url, Map<String, String> headers, Loadding loadding, AsyncHttpResponse iResponse) {
		service(true, url, null, "GET", headers, loadding, 0, iResponse);
	}

	public void doHttpsGet(String url, Loadding loadding, AsyncHttpResponse iResponse) {
		this.doHttpsGet(url, null, iResponse);
	}

	public void doPost(String url, Map<String, String> params, Map<String, String> headers, Loadding loadding, AsyncHttpResponse iResponse) {
		StringBuilder postData = new StringBuilder();
		for (Entry<String, String> entry : params.entrySet()) {
			if (postData.length() != 0) {
				postData.append("&");
			}
			postData.append(entry.getKey()).append("=").append(entry.getValue());
		}
		service(false, url, postData.toString(), "POST", headers, loadding, 0, iResponse);
	}

	public void doHttpsPost(String url, Loadding loadding, AsyncHttpResponse iResponse) {
		this.doHttpsPost(url, null, null, loadding, iResponse);
	}

	public void doHttpsPost(String url, Map<String, String> params, Map<String, String> headers, Loadding loadding, AsyncHttpResponse iResponse) {
		StringBuilder postData = new StringBuilder();
		for (Entry<String, String> entry : params.entrySet()) {
			if (postData.length() != 0) {
				postData.append("&");
			}
			postData.append(entry.getKey()).append("=").append(entry.getValue());
		}
		service(true, url, postData.toString(), "POST", headers, loadding, 0, iResponse);
	}

	public void doPost(String url, Loadding loadding, AsyncHttpResponse iResponse) {
		this.doPost(url, null, null, loadding, iResponse);
	}

	private void service(final boolean isHttps, final String url, final String postData, final String method, final Map<String, String> headers, final Loadding loadding, final int count, final AsyncHttpResponse response) {
		if (loadding != null) {
			loadding.show();
		}
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection conn = null;
				try {
					boolean doOutput = !isNullOrEmpty(postData);
					conn = isHttps ? createHttpsConn(url, method, doOutput) : createHttpConn(url, method, doOutput);

					fillProperties(conn, headers);

					if (doOutput)
						writeMsg(conn, postData);

					String msg = readMsg(conn);
					response.getMsg(AsyncHttpResponse.STATUS_SUCCESS, msg);
				} catch (SocketTimeoutException e) {
					if (count < retiesTimes) {
						service(isHttps, url, postData, method, headers, null, count + 1, response);
					} else {
						response.getMsg(AsyncHttpResponse.STATUS_READ_TIMEOUT, "read timeout,url:" + url + ",msg:" + e.getMessage());
					}
				} catch (ConnectException e) {
					if (count < retiesTimes) {
						service(isHttps, url, postData, method, headers, null, count + 1, response);
					} else {
						response.getMsg(AsyncHttpResponse.STATUS_CONNECT_TIMEOUT, "connect timeot,url:" + url + ",msg:" + e.getMessage());
					}
				} catch (Exception ex) {
					if (count < retiesTimes) {
						service(isHttps, url, postData, method, headers, null, count + 1, response);
					} else {
						response.getMsg(AsyncHttpResponse.STATUS_OTHER_ERRORS, "other errors,url:" + url + ",msg:" + ex.getMessage());
					}
				} finally {
					if (conn != null) {
						conn.disconnect();
						conn = null;
					}
					if (loadding != null) {
						loadding.hidden();
					}
				}
			}
		});
		thread.setName("AsyncHttpService-" + atomicInteger.getAndIncrement());
		thread.start();
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
		try {
			int responseCode = conn.getResponseCode();
			boolean error = false;
			if (responseCode >= 200 && responseCode < 400) {
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), charSet));
			} else {
				error = true;
				reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), charSet));
			}
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			if (error) {
				throw new RuntimeException(sb.toString());
			}
			return sb.toString();
		} finally {
			if (reader != null) {
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

	private void fillProperties(HttpURLConnection conn, Map<String, String> params) {
		if (params == null || params.isEmpty()) {
			return;
		}

		for (Entry<String, String> entry : params.entrySet()) {
			conn.addRequestProperty(entry.getKey(), entry.getValue());
		}
	}

	private HttpURLConnection createHttpsConn(String url, String method, boolean doOutput) throws Exception {
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

	private static void trustAllHttpsCertificates() throws Exception {
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];

		javax.net.ssl.TrustManager tm = new miTM();

		trustAllCerts[0] = tm;

		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");

		sc.init(null, trustAllCerts, null);

		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

	}

	public static class miTM implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) throws java.security.cert.CertificateException {
			return;
		}

		public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) throws java.security.cert.CertificateException {
			return;
		}
	}

	private boolean isNullOrEmpty(String s) {
		return s == null || s.length() == 0 || s.trim().length() == 0;
	}

	public static void main(String[] args) {
		AsyncHttpService httpService = new AsyncHttpService(1000, 1000, 1);
		httpService.doGet("http://access1.hoolai.com/access_open_api/test/ok.hl", new Loadding() {

			@Override
			public void show() {

			}

			@Override
			public void hidden() {

			}
		}, new AsyncHttpResponse() {
			@Override
			public void getMsg(int status, String msg) {
				System.out.println(status);
				System.out.println(msg);
			}
		});

	}
}
