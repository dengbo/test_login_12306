package com.dengbo.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	Button mButton;
	EditText user_edit, passwd_edit, check_edit;
	ImageView mImageView;
	TextView mTextView;
	BitmapDrawable mBitmapDrawable;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mButton = (Button) findViewById(R.id.submit);
		user_edit = (EditText) findViewById(R.id.user);
		passwd_edit = (EditText) findViewById(R.id.passwd);
		check_edit = (EditText) findViewById(R.id.check_text);
		mImageView = (ImageView) findViewById(R.id.check_img);
		mTextView = (TextView) findViewById(R.id.result);
		mButton.setOnClickListener(mClickListener);
		Thread mThread = new Thread(downCheckImg);
		Log.v("main", "start_thread");
		mThread.start();

		//test
//		HashMap<String, String> map = new HashMap<String, String>();
//		Set<String> set = map.keySet();
//		Log.v("main", set.size()+"");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	// login button
	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Thread loginThread = new Thread(sendLogin);
			loginThread.start();
			Log.v("main", "login");
		}
	};

	// main thread handler
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			Log.v("main", "msg");
			switch (msg.what) {
			case 1:
				// set check image
				if (mBitmapDrawable != null) {
					mImageView.setBackgroundDrawable(mBitmapDrawable);
					Log.v("main", "set");
				}
				break;
			case 2:
				// set return data
				String result = msg.getData().getString("result");
				Document document = Jsoup.parse(result);

				mTextView.setText(result);
				break;
			case 3:
				// yupiao
				String yupiao = msg.getData().getString("yupiao");
				mTextView.setText(yupiao);
				break;
			}
		};
	};

	// download check picture
	private Runnable downCheckImg = new Runnable() {

		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Log.v("main", "start");
				URL mURL = new URL(
						"https://dynamic.12306.cn/otsweb/passCodeAction.do?rand=sjrand");
				initTrustAllSSL();
				HttpsURLConnection mConnection = (HttpsURLConnection) mURL
						.openConnection();
				mConnection.setConnectTimeout(5 * 1000);
				mConnection.setRequestMethod("GET");
				mConnection.setRequestProperty("Accept",
						"image/png, image/svg+xml, image/*;q=0.8, */*;q=0.5");
				InputStream mInputStream = mConnection.getInputStream();
				mBitmapDrawable = new BitmapDrawable(mInputStream);
				mHandler.sendEmptyMessage(1);
				mInputStream.close();
				mConnection.disconnect();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	// refresh check picture
		public Runnable refreshCheckImg = new Runnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					double rand = Math.random();
					URL mURL = new URL(
							"https://dynamic.12306.cn/otsweb/passCodeAction.do?rand=sjrand&"+rand);
					initTrustAllSSL();
					HttpsURLConnection mConnection = (HttpsURLConnection) mURL
							.openConnection();
					mConnection.setConnectTimeout(5 * 1000);
					mConnection.setRequestMethod("GET");
					mConnection.setRequestProperty("Accept",
							"image/png, image/svg+xml, image/*;q=0.8, */*;q=0.5");
					InputStream mInputStream = mConnection.getInputStream();
					mBitmapDrawable = new BitmapDrawable(mInputStream);
					mHandler.sendEmptyMessage(1);
					mInputStream.close();
					mConnection.disconnect();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	// send login request
	private Runnable sendLogin = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String userString = user_edit.getText().toString();
			String passwordString = passwd_edit.getText().toString();
			String checkString = check_edit.getText().toString();
			String randString = "";
			String errorString = "";
			try {
				// get loginrand
				URL mURL = new URL(
						"https://dynamic.12306.cn/otsweb/loginAction.do?method=loginAysnSuggest");
				initTrustAllSSL();
				HttpsURLConnection mConnection = (HttpsURLConnection) mURL
						.openConnection();
				mConnection.setDoOutput(true);// ʹ�� URL ���ӽ������
				mConnection.setDoInput(true);// ʹ�� URL ���ӽ�������
				mConnection.setUseCaches(false);// ���Ի���
				mConnection.setConnectTimeout(5 * 1000);
				mConnection.setRequestMethod("POST");
				mConnection.setRequestProperty("Accept",
						"application/json, text/javascript, */*");
				mConnection.setRequestProperty("Connection", "keep-Alive");
				mConnection.setRequestProperty("Content-Length", "0");
				int respondCode = mConnection.getResponseCode();
				if (respondCode == HttpURLConnection.HTTP_OK) {
					InputStreamReader in = new InputStreamReader(
							mConnection.getInputStream());
					// Ϊ�������BufferedReader
					BufferedReader buffer = new BufferedReader(in);
					String inputLine = null;
					String inputString = "";
					// ʹ��ѭ������ȡ��õ����
					while (((inputLine = buffer.readLine()) != null)) {
						inputString += inputLine + "\n";
					}
					Log.v("main", inputString);
					try {
						JSONObject mJsonObject = new JSONObject(inputString);
						randString = mJsonObject.getString("loginRand");
						errorString = mJsonObject.getString("randError");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// �ر�InputStreamReader
					in.close();
					mConnection.disconnect();
					Log.v("main", randString + "," + errorString);
					// send data
					URL mUrl_post = new URL(
							"https://dynamic.12306.cn/otsweb/loginAction.do?method=login");
					initTrustAllSSL();
					HttpsURLConnection mConnection_post = (HttpsURLConnection) mUrl_post
							.openConnection();
					mConnection_post.setDoOutput(true);// ʹ�� URL ���ӽ������
					mConnection_post.setDoInput(true);// ʹ�� URL ���ӽ�������
					mConnection_post.setUseCaches(false);// ���Ի���
					mConnection_post.setConnectTimeout(5 * 1000);
					mConnection_post.setRequestMethod("POST");
					/*
					 *
Referer	https://dynamic.12306.cn/otsweb/loginAction.do?method=init
Accept-Language	zh-CN
User-Agent	Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)
Content-Type	application/x-www-form-urlencoded
Accept-Encoding	gzip, deflate
Host	dynamic.12306.cn
Content-Length	168
Connection	Keep-Alive
Cache-Control	no-cache
Cookie	JSESSIONID=CBE19EE88FC7AB1F8A07561321D2067E; BIGipServerotsweb=2463367434.62495.0000; BIGipServerotsquery=2379809034.59425.0000
					 */
					mConnection_post.setRequestProperty("Accept",
							"text/html, application/xhtml+xml, */*");
					mConnection_post.setRequestProperty("Referer",
							"https://dynamic.12306.cn/otsweb/loginAction.do?method=init");
					mConnection_post.setRequestProperty("Accept-Language",
							"zh-CN");
					mConnection_post.setRequestProperty("User-Agent",
							"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
					mConnection_post.setRequestProperty("Connection",
							"keep-Alive");
					mConnection_post.setRequestProperty("Host",
							"dynamic.12306.cn");
					mConnection_post.setRequestProperty("Content-Length",
							"168");
					mConnection_post.setRequestProperty("Cache-Control",
							"no-cache");
					mConnection_post.setRequestProperty("Cookie",
							"JSESSIONID=CBE19EE88FC7AB1F8A07561321D2067E; BIGipServerotsweb=2463367434.62495.0000; BIGipServerotsquery=2379809034.59425.0000");
					mConnection_post.setRequestProperty("Accept-Encoding",
							"gzip, deflate");
					mConnection_post.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
					DataOutputStream dos = new DataOutputStream(
							mConnection_post.getOutputStream());
					String postContent = "loginRand=780&refundLogin=N&refundFlag=&loginUser.user_name=dengbodb@sina.com&nameErrorFocus=&user.password=03170822l&passwordErrorFocus=&randCode=2WKT&randErrorFocus=";
					Log.v("main", postContent);
					dos.write(postContent.getBytes());
					dos.flush();
					// ִ����dos.close()��POST�������
					dos.close();
					// �õ���ȡ������(��)
					InputStreamReader in_post = new InputStreamReader(
							mConnection_post.getInputStream());
					// Ϊ�������BufferedReader
					BufferedReader buffer_post = new BufferedReader(in_post);
					String inputLine_post = null;
					StringBuilder resultData = new StringBuilder();
					// ʹ��ѭ������ȡ��õ����
					while (((inputLine_post = buffer_post.readLine()) != null)) {
						resultData.append(inputLine_post);
					}
					Log.v("main resultData", resultData.toString());
					// �ر�InputStreamReader
					in_post.close();
					if (resultData != null && resultData.length() != 0) {
						Message message = new Message();
						message.what = 2;
						Bundle mBundle = new Bundle();
						mBundle.putString("result", resultData.toString());
						message.setData(mBundle);
						mHandler.sendMessage(message);
					}
				}

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	//yupiao
	private Runnable yupiaoRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				URL mURL = new URL(
						"http://dynamic.12306.cn/otsquery/query/queryRemanentTicketAction.do?method=queryLeftTicket&orderRequest.train_date=2012-12-13&orderRequest.from_station_telecode=BJP&orderRequest.to_station_telecode=SHH&orderRequest.train_no=&trainPassType=QB&trainClass=QB#D#Z#T#K#QT#&includeStudent=00&seatTypeAndNum=&orderRequest.start_time_str=00:00--24:00");
				HttpURLConnection mConnection = (HttpURLConnection) mURL.openConnection();
				mConnection.setConnectTimeout(5 * 1000);
				mConnection.setRequestMethod("GET");
				mConnection.setRequestProperty("Accept",
						"application/json, text/javascript, */*");
				mConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				mConnection.setRequestProperty("Cookie","BIGipServerotsquery=2379809034.59425.0000;JSESSIONID=8A2C1F1B4C2D1CB501147EBC613B4250; BIGipServerotsweb=2228486410.48160.0000");
				// �õ���ȡ������(��)
				InputStreamReader in = new InputStreamReader(
						mConnection.getInputStream());
				// Ϊ�������BufferedReader
				BufferedReader buffer_post = new BufferedReader(in);
				String inputLine_post = null;
				StringBuilder resultData = new StringBuilder();
				// ʹ��ѭ������ȡ��õ����
				while (((inputLine_post = buffer_post.readLine()) != null)) {
					resultData.append(inputLine_post);
				}
				// �ر�InputStreamReader
				in.close();
				if (resultData != null && resultData.length() != 0) {
					Message message = new Message();
					message.what = 3;
					Bundle mBundle = new Bundle();
					mBundle.putString("yupiao", resultData.toString());
					message.setData(mBundle);
					mHandler.sendMessage(message);
				}
				mConnection.disconnect();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	// handle https request,ignore CA
	private static void initTrustAllSSL() {
		try {
			SSLContext sslCtx = SSLContext.getInstance("TLS");
			sslCtx.init(null, new TrustManager[] { new X509TrustManager() {
				// do nothing, let the check pass.
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			} }, new SecureRandom());

			HttpsURLConnection.setDefaultSSLSocketFactory(sslCtx
					.getSocketFactory());
			HttpsURLConnection
					.setDefaultHostnameVerifier(new HostnameVerifier() {
						public boolean verify(String hostname,
								SSLSession session) {
							return true;
						}
					});

		} catch (NoSuchAlgorithmException e) {
		} catch (KeyManagementException e) {
		}
	}
}
