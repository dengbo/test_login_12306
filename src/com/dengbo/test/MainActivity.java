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
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
				mConnection.setDoOutput(true);// 使用 URL 连接进行输出
				mConnection.setDoInput(true);// 使用 URL 连接进行输入
				mConnection.setUseCaches(false);// 忽略缓存
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
					// 为输出创建BufferedReader
					BufferedReader buffer = new BufferedReader(in);
					String inputLine = null;
					String inputString = "";
					// 使用循环来读取获得的数据
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
					// 关闭InputStreamReader
					in.close();
					mConnection.disconnect();
					Log.v("main", randString + "," + errorString);
					// send data
					URL mUrl_post = new URL(
							"https://dynamic.12306.cn/otsweb/loginAction.do?method=login");
					initTrustAllSSL();
					HttpsURLConnection mConnection_post = (HttpsURLConnection) mUrl_post
							.openConnection();
					mConnection_post.setDoOutput(true);// 使用 URL 连接进行输出
					mConnection_post.setDoInput(true);// 使用 URL 连接进行输入
					mConnection_post.setUseCaches(false);// 忽略缓存
					mConnection_post.setConnectTimeout(5 * 1000);
					mConnection_post.setRequestMethod("POST");
					mConnection_post.setRequestProperty("Accept",
							"text/html, application/xhtml+xml, */*");
					mConnection_post.setRequestProperty("Connection",
							"keep-Alive");
					mConnection_post.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
					DataOutputStream dos = new DataOutputStream(
							mConnection_post.getOutputStream());
					String postContent = URLEncoder
							.encode("loginRand="
									+ randString
									+ "&refundLogin=N&refundFlag=Y&loginUser.user_name="
									+ userString
									+ "&nameErrorFocus=&user.password="
									+ passwordString
									+ "&passwordErrorFocus=&randCode="
									+ checkString + "&randErrorFocus=", "UTF-8");
					Log.v("main", postContent);
					dos.write(postContent.getBytes());
					dos.flush();
					// 执行完dos.close()后，POST请求结束
					dos.close();
					// 得到读取的内容(流)
					InputStreamReader in_post = new InputStreamReader(
							mConnection_post.getInputStream());
					// 为输出创建BufferedReader
					BufferedReader buffer_post = new BufferedReader(in_post);
					String inputLine_post = null;
					StringBuilder resultData = new StringBuilder();
					// 使用循环来读取获得的数据
					while (((inputLine_post = buffer_post.readLine()) != null)) {
						resultData.append(inputLine_post);
					}
					Log.v("main resultData", resultData.toString());
					// 关闭InputStreamReader
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
