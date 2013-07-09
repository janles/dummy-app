package com.example.neuegruppeerstellen;

import java.util.ArrayList;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.MapActivity;

public class MapOverviewActivity extends MapActivity implements
		LocationListener, LocationSource {
	private GoogleMap map;
	private LocationManager locationManager;
	private OnLocationChangedListener mListener;

	// in diesen Arrays sind die jeweiligen Koordinaten gespeichert
	private ArrayList<LatLng> points_schwartau = new ArrayList<LatLng>();
	private ArrayList<LatLng> points_drangstedt = new ArrayList<LatLng>();
	private ArrayList<LatLng> points_flensburg = new ArrayList<LatLng>();
	private ArrayList<LatLng> points_hamburg = new ArrayList<LatLng>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapoverview);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		map.setMyLocationEnabled(true);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 400, 1000, this);
		String provider = locationManager
				.getBestProvider(new Criteria(), false);
		Location location = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		LatLng currentPosition = new LatLng(location.getLatitude(),
				location.getLongitude());
		LatLng wackenPosition = new LatLng(54.025117, 9.379721); 
		Marker wacken = map.addMarker(new MarkerOptions()
		.position(wackenPosition)
		.title("Wacken")
		.icon(BitmapDescriptorFactory
				.fromResource(R.drawable.wacken)));

		map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15));

		// Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

		// hier wird die Async Task gestartet
		// THREAD_POOL_EXECUTOR = bis zu 5 Async-Tasks gleichzeitig erlaubt!
		FakePointsAsyncTask fakePointsAsyncTask = new FakePointsAsyncTask();
		fakePointsAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
	}

	@Override
	public void deactivate() {
		mListener = null;
	}

	@Override
	public void onLocationChanged(Location location) {
		// if (mListener != null) {
		// mListener.onLocationChanged(location);
		LatLng newLatLng = new LatLng(location.getLatitude(),
				location.getLongitude());
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
				newLatLng, 15);
		map.animateCamera(cameraUpdate);
		locationManager.removeUpdates(this);
		// map.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));
		// }
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, provider + " disabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, provider + " enabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Toast.makeText(this, "Status changed", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	// INNERE KLASSE - ASYNC TASK
	private class FakePointsAsyncTask extends AsyncTask<Void, Integer, Void> {
		final int LIST_SIZE = 100;
		int MIN_WAIT_TIME = 1000; // 1 Sekunde
		// TODO: Zeit setzen
		int MAX_WAIT_TIME = 2000; // 10 Sekunden

		Marker schwartau, drangstedt, ort_3, ort_4;

		// wird als erstes im Async-Task aufgerufen
		// hier können Variablen initialisiert werden
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			// Liste mit Koordinaten füllen; pro Kontakt eine Liste
			fillPointsListSchwartau();
			fillPointsListDrangstedt();
			fillPointsListFlensburg();
			fillPointsListHamburg();

			// Initialpunkte
			LatLng SCHWARTAU = points_schwartau.remove(0);
			schwartau = map.addMarker(new MarkerOptions()
					.position(SCHWARTAU)
					.title("Baris")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.baris_rund)));

			LatLng DRANGSTEDT = points_drangstedt.remove(0);
			drangstedt = map.addMarker(new MarkerOptions()
					.position(DRANGSTEDT)
					.title("Alexandra")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.alex_rund)));

			LatLng FLENSBURG = points_flensburg.remove(0);
			ort_3 = map.addMarker(new MarkerOptions()
					.position(FLENSBURG)
					.title("Sascha")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.sascha_rund)));

			LatLng HAMBURG = points_hamburg.remove(0);
			ort_4 = map.addMarker(new MarkerOptions()
					.position(HAMBURG)
					.title("Ich")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.jan_rund)));
		}

		private void fillPointsListSchwartau() {
			GMaps_fake_data fakeData = new GMaps_fake_data(points_schwartau);
			fakeData.fillSchwartau();
		}

		private void fillPointsListDrangstedt() {
			GMaps_fake_data fakeData = new GMaps_fake_data(points_drangstedt);
			fakeData.fillDrangstedt();
		}

		private void fillPointsListFlensburg() {
			GMaps_fake_data fakeData = new GMaps_fake_data(points_flensburg);
			fakeData.fillFlensburg();
		}

		private void fillPointsListHamburg() {
			GMaps_fake_data fakeData = new GMaps_fake_data(points_hamburg);
			fakeData.fillHamburg();
		}

		// Hauptaufgabe der Task (in unserem Fall warten)
		@Override
		protected Void doInBackground(Void... params) {
			SystemClock.sleep(3000); // am Anfang (neuer Intent geöffnet) warten

			// größste Liste bestimmen
			int max_1 = Math
					.max(points_schwartau.size(), points_drangstedt.size());
			int max_2 = Math.max(points_flensburg.size(), points_hamburg.size());
			int max = Math.max(max_1, max_2);

			for (int i = 0; i < max; i++) {
				publishProgress(); // onProgressUpdate sollte i-mal aufgerufen werden

				// zufällige Wartezeit
				int rand = (int) (Math.random()
						* (MAX_WAIT_TIME - MIN_WAIT_TIME) + MIN_WAIT_TIME);

				SystemClock.sleep(rand);
			}
			return null;
		}

		// hier kann das UI geupdated werden
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);

			if (!points_drangstedt.isEmpty()) {
				drangstedt.remove();
				// Hamburg Marker updaten
				LatLng HAMBURG = points_drangstedt.remove(0);
				drangstedt = map.addMarker(new MarkerOptions()
						.position(HAMBURG)
						.title("Alexandra")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.alex_rund)));
			}

			if (!points_schwartau.isEmpty()) {
				schwartau.remove();
				// Berlin Marker updaten
				LatLng BERLIN = points_schwartau.remove(0);
				schwartau = map.addMarker(new MarkerOptions()
						.position(BERLIN)
						.title("Baris")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.baris_rund)));
			}
			if (!points_flensburg.isEmpty()) {
				ort_3.remove();
				LatLng ORT_DREI = points_flensburg.remove(0);
				ort_3 = map.addMarker(new MarkerOptions()
						.position(ORT_DREI)
						.title("Sascha")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.sascha_rund)));
			}

			if (!points_hamburg.isEmpty()) {
				ort_4.remove();
				LatLng ORT_VIER = points_hamburg.remove(0);
				ort_4 = map.addMarker(new MarkerOptions()
						.position(ORT_VIER)
						.title("Ich")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.jan_rund)));
			}
		}

		// wird ausgeführt nachdem die Aufgabe abgeschlossen wurde
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	}
}