package edu.elon.elonhunt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


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
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LocationHideActivity extends Activity {

	private EditText playerText;
	private Button button;
	private double currentLatitude;
	private double currentLongitude;

	private LocationManager locManager;
	
	private WakeLock wakeLock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_hide);

		playerText = (EditText) findViewById(R.id.playerText);
		playerText.setText("fish"); // DO NOT FORGET TO TAKE OUT THIS LINE
		button = (Button) findViewById(R.id.button);
		button.setEnabled(true); // will change when joel adds the line to stop
									// it
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				hide();

			}
		});
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		//wakeLock = powerManager.new WakeLock(PowerManager.FULL_WAKE_LOCK, "tag");
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
				0, locationListener);
	}

	@Override
	protected void onPause() {
		super.onPause();
		locManager.removeUpdates(locationListener);
		this.wakeLock.release();
	}
	

	private LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {

			currentLatitude = location.getLatitude();
			currentLongitude = location.getLongitude();
			Log.d("Location", "" + currentLatitude + " + " + currentLongitude);

		}

		@Override
		public void onProviderDisabled(String provider) {


		}

		@Override
		public void onProviderEnabled(String provider) {


		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {


		}

	};

	private void hide() {
		// getting player name array to see if the name already exists
		String namesURL = "http://170.224.161.119:9081/elonhunt/getplayers";
		new GetNames().execute(namesURL);

	}

	@SuppressLint("NewApi")
	private class GetNames extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... APIURL) {

			StringBuilder nameArrayBuilder = new StringBuilder();
			HttpClient nameClient = new DefaultHttpClient();
			for (String nameArray : APIURL) {
				HttpGet nameGet = new HttpGet(nameArray);
				try {
					HttpResponse nameResponse = nameClient.execute(nameGet);
					StatusLine nameStatus = nameResponse.getStatusLine();
					if (nameStatus.getStatusCode() == 200) { // OK
						HttpEntity nameEntity = nameResponse.getEntity();
						InputStream nameContent = nameEntity.getContent();
						InputStreamReader nameInput = new InputStreamReader(
								nameContent);
						BufferedReader nameReader = new BufferedReader(
								nameInput);

						JsonReader result = new JsonReader(nameReader);
						String text = "";

						result.beginArray();
						while (result.hasNext() == true) {
							// JSON name Array
							result.beginObject();
							while (result.hasNext()) {
								text = result.nextName();
								if (text.equals("playerName")) {
									nameArrayBuilder.append(result.nextString()
											+ "'");
								}
							}
							result.endObject();	
						} 
						result.endArray();
						result.close();
					}
				} catch (IOException ex) {
					Log.d("Fail io", "Fail io");
				}

			}
			return nameArrayBuilder.toString();
		}

		@Override
		protected void onPostExecute(String result) {

			String name = playerText.getText().toString();
			if (result.contains(name)) {
				playerText.setText("Name already exists");
			} else {

				
					String addPlayerMessage = "addplayer?playername="
							+ playerText.getText().toString() + "&latitude="
							+ currentLatitude + "&longitude="
							+ currentLongitude;

					String URL = "http://170.224.161.119:9081/elonhunt/"
							+ addPlayerMessage;
					Log.d("URL", URL);

					// dont know if i want to make this another async task or
					// not
					
					new AddPlayer().execute(URL);
			}

		}

	}
	
	private class AddPlayer extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... APIURL) {
			
			HttpClient client = new DefaultHttpClient();
			for (String URL : APIURL) {
				HttpGet nameGet = new HttpGet(URL);
				try {
					client.execute(nameGet);
				}catch (IOException ex) {
					Log.d("Fail io", "Fail io");
				}
			}
			return null;
		}
	}

}
