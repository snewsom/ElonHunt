package edu.elon.elonhunt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;


public class LocationHideActivity extends Activity {
	
	private EditText playerText;
	private Button button;
	private double currentLatitude;
	private double currentLongitude;
	
	private LocationManager locManager;
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_hide);
		
		playerText = (EditText) findViewById(R.id.playerText);
		button = (Button) findViewById(R.id.button);
		button.setEnabled(false);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}
	
	@Override
	protected void onPause() {
		locManager.removeUpdates(locationListener);
	}
	
	
	
	private LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			
			
			currentLatitude = location.getLatitude();
			currentLongitude = location.getLongitude();
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	private void hide() {
		//getting player name array to see if the name already exists

			String namesURL = "http://170.224.161.119:9081/elonhunt/getplayers";
			
			new GetNames().execute(namesURL);
			
		
	}

	
	
	@SuppressLint("NewApi")
	private class GetNames extends AsyncTask<String,Void,String> {

		@Override
		protected String doInBackground(String... APIURL) {
			
			StringBuilder nameArrayBuilder = new StringBuilder();
			HttpClient nameClient = new DefaultHttpClient();
			for(String nameArray : APIURL) {
				HttpGet nameGet = new HttpGet(nameArray);
				try {
					HttpResponse nameResponse = nameClient.execute(nameGet);
					StatusLine nameStatus = nameResponse.getStatusLine();
					if(nameStatus.getStatusCode() == 200) { //OK
						HttpEntity nameEntity = nameResponse.getEntity();
						InputStream nameContent = nameEntity.getContent();
						InputStreamReader nameInput = new InputStreamReader(nameContent);
						BufferedReader nameReader = new BufferedReader(nameInput);
						
						JsonReader result = new JsonReader(nameReader);
						String text = "";
						
						//JSON name Array
						result.beginArray();
						while(result.hasNext()) {
							text = result.nextName();
							if(text.equals("PlayerName")) {
								nameArrayBuilder.append(result.nextString() + "'");
							}
							
						}
					}
				} catch (IOException ex) { }
					
				
			}
			return nameArrayBuilder.toString();
		}
		
		@Override
		protected void onPostExecute(String result) {

			Log.d("tag", result);
			if(result.contains(playerText.toString())) {
				playerText.setText("Name already exists");
			} else {
				
				try {
					String addPlayerMessage = "addplayer?playername=" + playerText.toString() + "&latitude=" + currentLatitude + "&longitude=" + currentLongitude;
					String encodedString = URLEncoder.encode(addPlayerMessage, "UTF-8");
					String URL = "http://170.224.161.119:9081/elonhunt/" + encodedString;
					
					//dont know if i want to make this another async task or not
					
					HttpClient client = new DefaultHttpClient();
					HttpGet nameGet = new HttpGet(URL);
					
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			
			
		}
		
	}
	

}
