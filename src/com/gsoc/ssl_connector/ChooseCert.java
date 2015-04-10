package com.gsoc.ssl_connector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;



public class ChooseCert extends Activity {

	String hostName = null;
	Integer portNumber;
	RadioButton selectedRadioBtn;

	/*
	 * selectedOption = 1 - Valid
	 * selectedOption = 2 - Own
	 * selectedOption = 3 - No
	 * 
	 */


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_cert);





		getActionBar().setDisplayShowTitleEnabled(false);
		Bundle extras = getIntent().getExtras();
		hostName = extras.getString("hostString");
		portNumber = extras.getInt("portString");

		final RadioGroup certificateType = (RadioGroup) findViewById(R.id.radio_certs);
		final Button button = (Button) findViewById(R.id.choose_cert_button);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Integer selectedRadio;
				Intent msgWindowIntent = new Intent(getApplicationContext(), MessageViewWindow.class);
				Intent selectCipherSuiteIntent = new Intent(getApplicationContext(), CipherSuites.class);
				selectedRadio = certificateType.getCheckedRadioButtonId();
				selectedRadioBtn = (RadioButton) findViewById(selectedRadio);

				if(selectedRadioBtn.getText().equals("Server with valid certificate"))
				{
					msgWindowIntent.putExtra("hostString", hostName);
					msgWindowIntent.putExtra("portString", portNumber);
					msgWindowIntent.putExtra("certType", 1);
					startActivity(msgWindowIntent);
					finish();
				}
				if(selectedRadioBtn.getText().equals("Server with own certificate"))
				{
					msgWindowIntent.putExtra("hostString", hostName);
					msgWindowIntent.putExtra("portString", portNumber);
					msgWindowIntent.putExtra("certType", 2);
					startActivity(msgWindowIntent);
					finish();
				}
				if(selectedRadioBtn.getText().equals("Server with no certificate"))
				{
					selectCipherSuiteIntent.putExtra("hostString", hostName);
					selectCipherSuiteIntent.putExtra("portString", portNumber);
					selectCipherSuiteIntent.putExtra("certType", 3);
					startActivity(selectCipherSuiteIntent);
					finish();
				}

			}
		});
	}
}
