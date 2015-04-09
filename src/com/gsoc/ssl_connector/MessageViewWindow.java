package com.gsoc.ssl_connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.Locale;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MessageViewWindow extends Activity {

	EstablishConn nActions;
	SSLSocket socket;
	SSLSession session;
	PrintWriter out;
	Thread thread;
	String line;
	String reply;
	String command = null;
	StringBuilder sBuilder = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_view_window);

		String hostName;
		Integer portNumber;
		Integer certType;
		String suite = "DEFAULT";
		final TextView msgTextView = (TextView) findViewById(R.id.msgTextView);
		final ProgressBar pBar = (ProgressBar) findViewById(R.id.progressBar1);


		//Hide activity name on ActionBar
		getActionBar().setDisplayShowTitleEnabled(false);

		//Get the value from the previous Activity
		Bundle extras = getIntent().getExtras();
		hostName = extras.getString("hostString");
		portNumber = extras.getInt("portString");
		certType = extras.getInt("certType");
		if(certType.equals(3))
		{
			suite = extras.getString("cipherSuite");
		}

		nActions = new EstablishConn(hostName, portNumber,certType,suite);
		nActions.execute(null,null,null);

		final EditText commands = (EditText) findViewById(R.id.commands);
		Button sendMsgBtn = (Button) findViewById(R.id.sendMsgButton);
		sendMsgBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				command = commands.getText().toString();
				commands.setText("");
				msgTextView.setText(command+"");
				if(command != "")
				{
					thread = new Thread(new Runnable() {
						public void run() {
							BufferedReader in = null;
							try
							{
								reply = "";

								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										pBar.setVisibility(View.VISIBLE);
										msgTextView.setText("");
									}
								});   

								out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
								out.println(command);
								out.println();
								out.flush();
								in = new BufferedReader(
										new InputStreamReader(socket.getInputStream()));
								sBuilder = new StringBuilder();
								long startTime = System.currentTimeMillis();
								long waitTime = 3000;
								long endTime = startTime + waitTime;
								while (System.currentTimeMillis() < endTime && reply != "Bad Response / No Response") 
								{

									line  = in.readLine();
									if(line != "null" && line !=null)
										sBuilder.append(line);
									reply = sBuilder.toString();
									reply.replaceAll("null", "");
									if(reply == null || reply == "")
									{
										reply = "Bad Response / No Response";
									}
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											msgTextView.setText(reply);
											pBar.setVisibility(View.INVISIBLE);
										}
									});

								}




							}
							catch(final Exception e)
							{
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										msgTextView.setText("Exception "+e.getMessage());
										pBar.setVisibility(View.INVISIBLE);
									}
								});
							}
							finally
							{
								try 
								{
									in.close();
								} 
								catch (final IOException e) 
								{
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											msgTextView.setText(e.toString());
											pBar.setVisibility(View.INVISIBLE);
										}
									});
								}
								out.close();
							}

						}
					});
					thread.start();
				}
			}
		});

		Button closeBtn = (Button) findViewById(R.id.closeWindowButton);
		closeBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}


	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		nActions = null;


		if(socket != null)
		{
			if(thread != null)
				thread.interrupt();
			thread = null;
			if(out != null)
				out.close();
			CloseConnection cc = new CloseConnection();
			cc.execute(null,null,null);
			cc = null;   
		}

	}

	class CloseConnection extends AsyncTask<String,Void,String>
	{

		@Override
		protected String doInBackground(String... params) {
			try 
			{
				socket.close();
				session = null;
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			return null;
		}

	}

	class EstablishConn extends AsyncTask<String, Void, String>
	{
		String hostName;
		Integer portNumber;
		Integer certType;
		String suite;

		TextView msgTextView = (TextView) findViewById(R.id.msgTextView);
		ProgressBar pBar = (ProgressBar) findViewById(R.id.progressBar1);

		public EstablishConn(String hostName, Integer portNumber, Integer certType, String suite)
		{
			this.hostName = hostName;
			this.portNumber = portNumber;
			this.certType = certType;
			this.suite = suite;
		}



		@SuppressLint("TrulyRandom")
		@Override
		protected String doInBackground(String... params) {
			if(suite == null)
				suite = "aNULL";

			TrustManager[] trustAllCerts = new TrustManager[] { 
					new X509TrustManager() {
						public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
							return new java.security.cert.X509Certificate[0]; 
						}
						@SuppressWarnings("unused")
						public void checkClientTrusted(X509Certificate[] certs, String authType) {}
						@SuppressWarnings("unused")
						public void checkServerTrusted(X509Certificate[] certs, String authType) {}
						@Override
						public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException {
							// TODO Auto-generated method stub

						}
						@Override
						public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException {
							// TODO Auto-generated method stub

						}
					}};


			try 
			{
				SSLContext sct = SSLContext.getInstance("SSL");
				sct.init(null, trustAllCerts, new SecureRandom());

				if(socket == null)
				{
					if(certType == 1)
					{
						SocketFactory socketFactory = SSLSocketFactory.getDefault();
						socket = (SSLSocket) socketFactory.createSocket(hostName, portNumber);
						session = socket.getSession();
					}
					if(certType == 2)
					{
						SocketFactory socketFactory = sct.getSocketFactory();
						socket = (SSLSocket) socketFactory.createSocket(hostName, portNumber);
						session = socket.getSession();
					}
					if(certType == 3)
					{
						SocketFactory socketFactory = sct.getSocketFactory();
						socket = (SSLSocket) socketFactory.createSocket(hostName, portNumber);
						if(suite != null && suite.equals("aNULL"))
						{
							String[] cipherSuites = getCipherSuitesForaNULL();
							socket.setEnabledCipherSuites(cipherSuites);
						}
						else
						{
							String[] cipherSuites = new String[]{suite};
							socket.setEnabledCipherSuites(cipherSuites);
						}
						session = socket.getSession();
					}

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast toast = Toast.makeText(getApplicationContext(), "New SSL Socket Created", Toast.LENGTH_LONG);
							toast.show();
						}
					});

				}

				final String date = getDate(session.getCreationTime());
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						msgTextView.setText("Connected to  " + " "+ hostName +" \n"+ "Protocol  "+session.getProtocol()+" \n"+"Connected at  "+date+"\n"+"Session Context  "+session.getSessionContext());
						pBar.setVisibility(View.INVISIBLE);
					}
				});
			} 

			catch (final UnknownHostException e) 
			{
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						msgTextView.setText(e.getMessage());
						pBar.setVisibility(View.INVISIBLE);
					}
				});

			}

			catch (final IOException e) 
			{
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						msgTextView.setText(e.getMessage());
						pBar.setVisibility(View.INVISIBLE);
					}
				});
			} catch (final NoSuchAlgorithmException e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						msgTextView.setText(e.getMessage());
						pBar.setVisibility(View.INVISIBLE);
					}
				});
			} catch (final KeyManagementException e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						msgTextView.setText(e.getMessage());
						pBar.setVisibility(View.INVISIBLE);
					}
				});
			}catch (final RuntimeException e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						msgTextView.setText(e.getMessage());
						pBar.setVisibility(View.INVISIBLE);
					}
				});
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if(socket == null)
			{
				EditText commands = (EditText) findViewById(R.id.commands);
				Button sendMsgBtn = (Button) findViewById(R.id.sendMsgButton);

				commands.setVisibility(View.INVISIBLE);
				sendMsgBtn.setVisibility(View.INVISIBLE);
			}
		}

		private String getDate(long time) {
			Calendar cal = Calendar.getInstance(Locale.ENGLISH);
			cal.setTimeInMillis(time);
			String date = DateFormat.format("yyyy-MM-dd HH:mm:ss", cal).toString();
			return date;
		}
		private String[] getCipherSuitesForaNULL()
		{
			String[] cipherSuites = new String[]{
					"SSL_DH_anon_EXPORT_WITH_RC4_40_MD5",
					"SSL_DH_anon_WITH_RC4_128_MD5",
					"SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA",
					"SSL_DH_anon_WITH_DES_CBC_SHA",
					"SSL_DH_anon_WITH_3DES_EDE_CBC_SHA",
					"TLS_DH_anon_WITH_AES_128_CBC_SHA",
					"TLS_ECDH_anon_WITH_NULL_SHA",
					"TLS_ECDH_anon_WITH_RC4_128_SHA", 
					"TLS_ECDH_anon_WITH_3DES_EDE_CBC_SHA",
					"TLS_ECDH_anon_WITH_AES_128_CBC_SHA", 
					"TLS_DH_anon_WITH_AES_128_CBC_SHA256"
			};
			return cipherSuites;
		}
	}
}

