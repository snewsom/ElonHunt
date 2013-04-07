package edu.elon.elonhunt;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
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
		button.setClickable(false);
		button.setBackgroundColor(Color.GRAY);
		button.setText("Hide");
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
		String name = playerText.toString();
	}

}
