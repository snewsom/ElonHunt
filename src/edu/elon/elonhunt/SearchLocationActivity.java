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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;


public class SearchLocationActivity extends Activity {
	
	private double currentLatitude;
	private double currentLongitude;
	private String thisPlayerName;
	
	private String closestPlayerName;
	private double closestLatitude;
	private double closestLongitude;

	private LocationManager locManager;
	
	// Test
	private TextView latView;
	private TextView longView;
	private TextView nameView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_location);
		
		Intent intent = getIntent();
		thisPlayerName = intent.getExtras().getString("playername");
		
		currentLatitude = intent.getExtras().getDouble("latitude");
		currentLongitude = intent.getExtras().getDouble("longitude");
		
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);	
		
		String URL = "http://170.224.161.119:9081/elonhunt/getclosestlocation?playername=" + thisPlayerName + "&latitude=" + currentLatitude + "&longitude=" + currentLongitude;
		
		new GetNextLocation().execute(URL);
		
		// test
		latView = (TextView) findViewById(R.id.textViewLat);
		longView = (TextView) findViewById(R.id.textViewLong);
		nameView = (TextView) findViewById(R.id.textViewName);
		
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
	}
	

	private void setLocationTest() {
		latView.setText("Latitude: " + closestLatitude);
		longView.setText("Longitude: " + closestLongitude);
		nameView.setText("Name: " + closestPlayerName);
	}
	
	private LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {

			currentLatitude = location.getLatitude();
			currentLongitude = location.getLongitude();
			Log.d("Location", "" + currentLatitude + " + " + currentLongitude);
			
			//need to add the check to see if they are within 10 meters of the location.

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
	
	@SuppressLint("NewApi")
	private class GetNextLocation extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... getLocationURL) {
			
			StringBuilder location = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			for(String locationURL: getLocationURL) {
				HttpGet httpGet = new HttpGet(locationURL);
				try {
					HttpResponse locationResponse = client.execute(httpGet);
					StatusLine locationStatus = locationResponse.getStatusLine();
					if(locationStatus.getStatusCode() == 200) { //OK
						HttpEntity locationEntity = locationResponse.getEntity();
						InputStream locationContent = locationEntity.getContent();
						InputStreamReader locationInput = new InputStreamReader(locationContent);
						BufferedReader nameReader = new BufferedReader(locationInput);
						
						JsonReader result = new JsonReader(nameReader);
						String text = "";
						
						result.beginObject();
						while(result.hasNext()) {
							//JSON object
							text = result.nextName();
							if(text.equals("latitude") || text.equals("longitude")) {
								//the comma at the end might break the nextBoolean but we shall see
								location.append(result.nextDouble() + ",");	
							} else {
								location.append(result.nextString());
							}
							
						}
						result.endObject();
						result.close();
					}
				} catch (IOException ex) {
					Log.d("Fail io", "Fail io");
				}
			}
			
			return location.toString();
		}
		
		@Override
		protected void onPostExecute(String result) { 
			Log.d("result", result);
			try {
				String[] values = result.split(",");
				closestLatitude = Double.parseDouble(values[0]);
				closestLongitude = Double.parseDouble(values[1]);
				closestPlayerName = values[2];
				
				setLocationTest();
				
			} catch (NumberFormatException e) {
				Log.d("double","problems");
			}
		}
	}
	
	


}
