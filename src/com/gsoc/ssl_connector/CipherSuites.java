package com.gsoc.ssl_connector;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class CipherSuites extends Activity {

	String suite = null;
	String hostName = null;
	Integer portNumber;
	Integer certType;
	Map<String,String> ciphers = new HashMap<String,String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cipher_suites);


		getActionBar().setDisplayShowTitleEnabled(false);
		Bundle extras = getIntent().getExtras();
		hostName = extras.getString("hostString");
		portNumber = extras.getInt("portString");
		certType = extras.getInt("certType");

		LoadCipherSuites loadCipher = new LoadCipherSuites(getApplicationContext());
		loadCipher.setCipher();
		ciphers = loadCipher.getCipher();

		final Button button = (Button) findViewById(R.id.ciphersuites_button);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Intent msgWindowIntent = new Intent(getApplicationContext(), MessageViewWindow.class);
				EditText enteredAlgoET = (EditText) findViewById(R.id.ciphersuites_editText);
				String enteredAlgo = enteredAlgoET.getText().toString(); 
				suite = ciphers.get(enteredAlgo);
				if(suite == null || suite.equals(""))
				{
					Toast csToast = Toast.makeText(getApplicationContext(), "Please enter valid cipher suites", Toast.LENGTH_LONG);
					csToast.show();
				}
				else
				{
					msgWindowIntent.putExtra("hostString", hostName);
					msgWindowIntent.putExtra("portString", portNumber);
					msgWindowIntent.putExtra("certType", 3);
					msgWindowIntent.putExtra("cipherSuite", suite);
					startActivity(msgWindowIntent);
					finish();
				}
			}
		});
	}
}
