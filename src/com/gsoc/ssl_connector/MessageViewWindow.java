package com.gsoc.ssl_connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Locale;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

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
	Thread t;
	String line;
	String reply;
	String command = null;
	StringBuilder sb = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_view_window);

		String hostName;
		Integer portNumber; 
		final TextView msgTextView = (TextView) findViewById(R.id.msgTextView);
		final ProgressBar pBar = (ProgressBar) findViewById(R.id.progressBar1);


		//Hide activity name on ActionBar
		getActionBar().setDisplayShowTitleEnabled(false);

		//Get the value from the previous Activity
		Bundle extras = getIntent().getExtras();
		hostName = extras.getString("hostString");
		portNumber = extras.getInt("portString");

		nActions = new EstablishConn(hostName, portNumber);
		nActions.execute(null,null,null);

		final EditText commands = (EditText) findViewById(R.id.commands);
		Button sendMsgBtn = (Button) findViewById(R.id.sendMsgButton);
		sendMsgBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				command = commands.getText().toString();
				msgTextView.setText(command+"");
				if(command != "")
				{
					t = new Thread(new Runnable() {
						public void run() {

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
								BufferedReader in = new BufferedReader(
										new InputStreamReader(socket.getInputStream()));
								sb = new StringBuilder();
								long startTime = System.currentTimeMillis();
								long waitTime = 3000;
								long endTime = startTime + waitTime;
								while (System.currentTimeMillis() < endTime && reply != "Bad Response / No Response") 
								{

									line  = in.readLine();
									if(line != "null" && line !=null)
										sb.append(line);
									reply = sb.toString();
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

								out.close();

							}

						}
					});
					t.start();
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
			t.interrupt();
			t = null;
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

		TextView msgTextView = (TextView) findViewById(R.id.msgTextView);
		ProgressBar pBar = (ProgressBar) findViewById(R.id.progressBar1);

		public EstablishConn(String hostName, Integer portNumber)
		{
			this.hostName = hostName;
			this.portNumber = portNumber;
		}



		@Override
		protected String doInBackground(String... params) {

			try 
			{
				if(socket == null)
				{
					SocketFactory socketFactory = SSLSocketFactory.getDefault();
					socket = (SSLSocket) socketFactory.createSocket(hostName, portNumber);
					session = socket.getSession();
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
			}
			return null;
		}

		private String getDate(long time) {
			Calendar cal = Calendar.getInstance(Locale.ENGLISH);
			cal.setTimeInMillis(time);
			String date = DateFormat.format("yyyy-MM-dd HH:mm:ss", cal).toString();
			return date;
		}
	}
}

