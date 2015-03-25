package com.gsoc.ssl_connector;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Hide activity name on ActionBar
		getActionBar().setDisplayShowTitleEnabled(false);


		final Button button = (Button) findViewById(R.id.mainpage_button);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String hostString;
				String portString;
				EditText hostText;
				EditText portText;
				Integer portNumber;

				Intent msgWindowIntent = new Intent(getApplicationContext(), MessageViewWindow.class);

				hostText = (EditText) findViewById(R.id.editText_Host);
				portText = (EditText) findViewById(R.id.editText_Port); 

				hostString = hostText.getText().toString();
				portString = portText.getText().toString();
				if(  hostString == null || hostString.equals("") || portString == null || portString.equals(""))
				{
					Toast toast = Toast.makeText(getApplicationContext(), "Please check host/port String", Toast.LENGTH_LONG);
					toast.show();
				}
				else
				{
					Log.i("HostV","Host is "+ hostString);
					Log.i("PortV","Port is "+ portString);
					try
					{
						portNumber = Integer.parseInt(portString);
						msgWindowIntent.putExtra("hostString", hostString);
						msgWindowIntent.putExtra("portString", portNumber);
						startActivity(msgWindowIntent);
					}
					catch(NumberFormatException e)
					{
						Toast toast = Toast.makeText(getApplicationContext(), "Port entered is not a Number", Toast.LENGTH_LONG);
						toast.show();	
					}
					finally{
						hostText.setText("");
						portText.setText("");
					}

				}

			}
		});
	}
}
