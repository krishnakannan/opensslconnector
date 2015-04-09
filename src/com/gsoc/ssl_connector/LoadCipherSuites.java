package com.gsoc.ssl_connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class LoadCipherSuites extends Application {

	private Context lcsContext;

	public LoadCipherSuites(Context context)
	{
		lcsContext = context;
	}

	private Map<String,String> cipherSuites = new HashMap<String,String>();

	public Map<String,String> getCipher()
	{
		return cipherSuites;
	}

	public void setCipher()
	{
		String data = null;
		String[] dataArray = new String[2];
		Map<String,String> ciphers = new HashMap<String,String>();
		InputStream fStream = null;
		BufferedReader fReader = null;
		try {

			fStream =  lcsContext.getAssets().open("ciphers.csv");
			fReader = new BufferedReader(new InputStreamReader(fStream));
			while((data = fReader.readLine()) != null)
			{
				dataArray = data.split(",");
				ciphers.put(dataArray[0], dataArray[1]);
			}
		}catch (Exception e) 
		{
			Log.i("READFILE", e.getMessage());
			ciphers.put("err", e.getMessage());
		} 
		finally
		{
			try 
			{
				fStream.close();
				fReader.close();
				this.cipherSuites = ciphers;
			} catch (IOException e) {

				ciphers.put("closeErr", e.getMessage());
			}

		}
	}
}
