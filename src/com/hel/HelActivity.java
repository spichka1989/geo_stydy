package com.hel;

//import com.samples.filesrw.EditText;
//import com.samples.filesrw.R;
//import com.samples.filesrw.String;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//import com.samples.filesrw.Override;
//import com.samples.filesrw.R;
//import com.samples.filesrw.String;
//import com.samples.filesrw.StringBuffer;
//import com.samples.filesrw.Throwable;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.widget.TextView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class HelActivity extends Activity {
	/** Called when the activity is first created. */

	public static final int IDM_OPEN = 101;
	public static final int IDM_SAVE = 102;
	public static final int IDM_EXIT = 103;
	public static final String FILENAME = "file.txt";

	private EditText mEdit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		mEdit = (EditText) findViewById(R.id.edit);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, IDM_OPEN, Menu.NONE, "Open")
				.setIcon(R.drawable.ic_menu_open).setAlphabeticShortcut('o');
		menu.add(Menu.NONE, IDM_SAVE, Menu.NONE, "Start")
				.setIcon(R.drawable.ic_menu_save).setAlphabeticShortcut('s');
		menu.add(Menu.NONE, IDM_EXIT, Menu.NONE, "Exit")
				.setIcon(R.drawable.ic_menu_exit).setAlphabeticShortcut('x');

		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case IDM_OPEN:
			openFile(FILENAME);
			break;
		case IDM_SAVE:
			new CountDownTimer(24 * 60 * 60 * 1000, 15000) {

				public void onTick(long millisUntilFinished) {
					startSaveLocation();
				}

				public void onFinish() {
					mEdit.setText("done!");
				}
			}.start();
			// startSaveLocation();
			break;
		case IDM_EXIT:
			finish();
			break;
		default:
			return false;
		}
		return true;
	}

	private void openFile(String fileName) {
		try {
			InputStream inStream = openFileInput(FILENAME);

			if (inStream != null) {
				InputStreamReader tmp = new InputStreamReader(inStream);
				BufferedReader reader = new BufferedReader(tmp);
				String str;
				StringBuffer buffer = new StringBuffer();

				while ((str = reader.readLine()) != null) {
					buffer.append(str + "\n");
				}

				inStream.close();
				mEdit.setText(buffer.toString());
			}
		} catch (Throwable t) {
			Toast.makeText(getApplicationContext(),
					"Exception: " + t.toString(), Toast.LENGTH_LONG).show();
		}
	}

	private void saveFile(String FileName, String textLoc) {
		try {
			OutputStreamWriter outStream = new OutputStreamWriter(
					openFileOutput(FILENAME, Context.MODE_PRIVATE));

			outStream.write(textLoc);
			outStream.close();
		} catch (Throwable t) {
			Toast.makeText(getApplicationContext(),
					"Exception: " + t.toString(), Toast.LENGTH_LONG).show();
		}

	}

	private void startSaveLocation() {
		try {
			LocationManager locationManager;
			String context = Context.LOCATION_SERVICE;
			locationManager = (LocationManager) getSystemService(context);
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE); 
			criteria.setAltitudeRequired(false); 
			criteria.setBearingRequired(false); 
			criteria.setCostAllowed(true);
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			String provider = locationManager.getBestProvider(criteria, true);
			Location location = locationManager.getLastKnownLocation(provider);
			updateWithNewLocation(location);
			locationManager.requestLocationUpdates(provider, 2000, 0,
					 locationListener);
		} catch (Exception e) {
			mEdit.setText("Включите GPS");
		}
		

	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	private void updateWithNewLocation(Location location) {

		String latLongString;
		latLongString = "\n" + getTimeNow();
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			latLongString += ":(" + lat + ", " + lng + ");";
		} else {
			latLongString += "No location found";
		}
		
		saveFile(FILENAME, mEdit.getText().toString() + latLongString);
		openFile(FILENAME);

	}

	private String getTimeNow() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date currentTime = new Date(java.lang.System.currentTimeMillis());
		String dateString = sdf.format(currentTime);
		return dateString;
	}

}