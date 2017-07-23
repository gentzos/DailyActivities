package teicrete.gkentzoglanis.dailyactivities.stations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.json.JSONObject;

import teicrete.gkentzoglanis.dailyactivities.R;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapActivity extends Activity {

	public static final double HER_CENTER_LATITUDE = 35.339163;
	public static final double HER_CENTER_LONGITUDE = 25.133240;

	// Google Map
	private GoogleMap googleMap;

	private StationsAdapter mAdapter;

	private String seperation, lat, lng, title, name, adrs, shortAdrs, value,
			s1, curDate, curTime, curTime2, curHour, curMint, mark,
			savedLatLng;
	private String[] sep, sep2, sep3, sep4;
	private String[] dataArray = new String[20];

	private int curTimeInt, stationHour;
	private int[] sepInt = new int[5];

	private float dist;
	private float[] results = new float[1];
	private double myLat = 0, myLong = 0, destLat, destLong, myLastLat,
			myLastLong;

	private HashMap<Float, String> distMap = new HashMap<Float, String>();
	private Map<Float, String> treeMap;
	private List<Marker> markers = new ArrayList<Marker>();
	private boolean nav = false, traffic = false, initiateWaitingLoc = false;

	private Dialog dialog, dialog2, waitingDialog;
	private TextView routeTxtV, routeTxtV2;
	private Button yesBtn, noBtn, okBtn, lastPosBtn, cancelBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		waitingDialog = new Dialog(MapActivity.this);
		waitingDialog.setContentView(R.layout.fragment_waiting);
		waitingDialog.setTitle("Waiting...");
		routeTxtV2 = (TextView) waitingDialog.findViewById(R.id.waiting);
		lastPosBtn = (Button) waitingDialog.findViewById(R.id.lastPosBtn);
		cancelBtn = (Button) waitingDialog.findViewById(R.id.cancel2Btn);

		value = getIntent().getStringExtra("value");

		sep = value.split("\\:");

		if ((sep[0].equals("map")) || (sep[0].equals("list"))
				|| (sep[0].equals("nearest"))) {
			if (sep[1].equals("gas_station.xml")) {
				actionBar.setBackgroundDrawable(new ColorDrawable(Color
						.parseColor("#e46713")));
				seperation = "gas station";
			} else if (sep[1].equals("supermarket.xml")) {
				actionBar.setTitle("Supermarkets");
				actionBar.setIcon(R.drawable.supermarket_ab);
				actionBar.setBackgroundDrawable(new ColorDrawable(Color
						.parseColor("#839c43")));
				seperation = "supermarket";
			}
			mAdapter = new StationsAdapter(MapActivity.this, -1,
					StationsXmlPullParser.getStationsFromFile(MapActivity.this,
							sep[1]));
		}

		try {
			// Loading map
			initilizeMap();
			googleMap.setMyLocationEnabled(true);
			googleMap.clear();

			googleMap
					.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

						@Override
						public void onMyLocationChange(Location arg0) {
							// TODO Auto-generated method stub

							myLat = arg0.getLatitude();
							myLong = arg0.getLongitude();

							if (initiateWaitingLoc) {
								if (myLat != 0) {
									initiateWaitingLoc = false;
									waitingDialog.dismiss();
									findNearest(myLat, myLong);
								}
							}

							// save my position for using it in my last known position
							setValueDoubleToString(String.valueOf(myLat) + ":"
									+ String.valueOf(myLong));

							if (nav) {
								CameraPosition cameraPosition = new CameraPosition.Builder()
										.target(new LatLng(myLat, myLong))
										.zoom(15).build();
								googleMap.animateCamera(CameraUpdateFactory
										.newCameraPosition(cameraPosition));
							}
							Log.d("LAT LON", myLat + "  " + myLong);
						}
					});

			curTime = new SimpleDateFormat("HH:mm", Locale.US)
					.format(new Date());
			curTimeInt = Integer.parseInt(curTime.replaceAll("[\\D]", ""));

			if (sep[0].equals("nearest")) {
				// Iterating through all the locations stored
				for (int i = 0; i < mAdapter.getCount(); i++) {

					// Getting the latitude
					lat = mAdapter.getItem(i).getLatitude();

					// Getting the longitude
					lng = mAdapter.getItem(i).getLongitude();

					// Getting closing
					stationHour = Integer.parseInt(mAdapter.getItem(i)
							.getClosing().replaceAll("[\\D]", ""));

					if (mAdapter.getItem(i).getStrtAddress()
							.equals("North Road Axis of Crete (BOAK), Highway")) {
						shortAdrs = "BOAK";
						name = mAdapter.getItem(i).getCompany();
						adrs = shortAdrs + " "
								+ mAdapter.getItem(i).getStrtNumber();
					} else {
						name = mAdapter.getItem(i).getCompany();
						adrs = mAdapter.getItem(i).getStrtAddress() + " "
								+ mAdapter.getItem(i).getStrtNumber();
					}

					// Drawing markers on the map after 22.30 closing
					if ((curTimeInt >= 2230) || (curTimeInt < 30)) {
						if (stationHour <= 600) {
							// Drawing marker on the map
							drawMarker(new LatLng(Double.parseDouble(lat),
									Double.parseDouble(lng)), name, adrs);
						}
					} else if ((curTimeInt >= 30) && (curTimeInt < 600)) {
						// Drawing markers on the map after 00.30 closing
						if ((stationHour > 30) && (stationHour <= 600)) {
							// Drawing marker on the map
							drawMarker(new LatLng(Double.parseDouble(lat),
									Double.parseDouble(lng)), name, adrs);
						}
					} else { // Drawing all markers on the map
						drawMarker(
								new LatLng(Double.parseDouble(lat),
										Double.parseDouble(lng)), name, adrs);
					}
				}

				if (myLat == 0) {
					lastPosBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							if (getValueStringToDouble("value_key_string2") == null) {
								Toast.makeText(
										getApplicationContext(),
										"Last known position is not available!",
										Toast.LENGTH_SHORT).show();
							} else {
								savedLatLng = getValueStringToDouble("value_key_string2");
								sep2 = savedLatLng.split("\\:");
								myLastLat = Double.parseDouble(sep2[0]);
								myLastLong = Double.parseDouble(sep2[1]);
								findNearest(myLastLat, myLastLong);
								waitingDialog.dismiss();
							}
						}
					});

					cancelBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							waitingDialog.dismiss();
						}
					});
					waitingDialog.show();
					initiateWaitingLoc = true;
				}
			} else if (sep[0].equals("list")) {
				// Iterating through all the locations stored
				for (int i = 0; i < mAdapter.getCount(); i++) {

					// Getting the latitude
					lat = mAdapter.getItem(i).getLatitude();

					// Getting the longitude
					lng = mAdapter.getItem(i).getLongitude();

					if (mAdapter.getItem(i).getStrtAddress()
							.equals("North Road Axis of Crete (BOAK), Highway")) {
						shortAdrs = "BOAK";
						name = mAdapter.getItem(i).getCompany();
						adrs = shortAdrs + " "
								+ mAdapter.getItem(i).getStrtNumber();
					} else {
						name = mAdapter.getItem(i).getCompany();
						adrs = mAdapter.getItem(i).getStrtAddress() + " "
								+ mAdapter.getItem(i).getStrtNumber();
					}

					// Drawing marker on the map
					drawMarker(
							new LatLng(Double.parseDouble(lat),
									Double.parseDouble(lng)), name, adrs);
				}

				lat = sep[2];
				lng = sep[3];
				name = sep[4];

				for (int i = 0; i < markers.size(); i++) {
					Marker j = markers.get(i);
					title = j.getTitle();
					if ((name.equals(title))) {
						CameraPosition cameraPosition = new CameraPosition.Builder()
								.target(new LatLng(Double.parseDouble(lat),
										Double.parseDouble(lng))).zoom(16)
								.build();
						googleMap.animateCamera(CameraUpdateFactory
								.newCameraPosition(cameraPosition));
						j.showInfoWindow();
					}
				}
			} else {
				// Iterating through all the locations stored
				for (int i = 0; i < mAdapter.getCount(); i++) {

					// Getting the latitude
					lat = mAdapter.getItem(i).getLatitude();

					// Getting the longitude
					lng = mAdapter.getItem(i).getLongitude();

					if (mAdapter.getItem(i).getStrtAddress()
							.equals("North Road Axis of Crete (BOAK), Highway")) {
						shortAdrs = "BOAK";
						name = mAdapter.getItem(i).getCompany();
						adrs = shortAdrs + " "
								+ mAdapter.getItem(i).getStrtNumber();
					} else {
						name = mAdapter.getItem(i).getCompany();
						adrs = mAdapter.getItem(i).getStrtAddress() + " "
								+ mAdapter.getItem(i).getStrtNumber();
					}

					// Drawing marker on the map
					drawMarker(
							new LatLng(Double.parseDouble(lat),
									Double.parseDouble(lng)), name, adrs);
				}
				CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(new LatLng(HER_CENTER_LATITUDE,
								HER_CENTER_LONGITUDE)).zoom(11).build();
				googleMap.animateCamera(CameraUpdateFactory
						.newCameraPosition(cameraPosition));
			}

			googleMap
					.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
						@Override
						public void onInfoWindowClick(Marker arg0) {
							// TODO Auto-generated method stub
							lat = String.valueOf(arg0.getPosition().latitude);
							lng = String.valueOf(arg0.getPosition().longitude);
							Log.i("INFO", arg0.getSnippet());
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		if (!sep[0].equals("nearest")) {
			menu.findItem(R.id.action_nav).setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action buttons
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_nav:
			LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			boolean statusOfGPS = manager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if (mark != null) {
				if (statusOfGPS) {
					nav = true;
					sep3 = mark.split("\\-");
					if (initiateWaitingLoc) {
						navMarker(myLastLat, myLastLong,
								Double.parseDouble(sep3[0]),
								Double.parseDouble(sep3[1]));
					} else {
						navMarker(myLat, myLong, Double.parseDouble(sep3[0]),
								Double.parseDouble(sep3[1]));
					}
				} else {
					Toast.makeText(getApplicationContext(), "GPS is disabled!",
							Toast.LENGTH_SHORT).show();
				}
			}
			return true;
		case R.id.action_menu_setnormal:
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			return true;
		case R.id.action_menu_setsatellite:
			googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			return true;
		case R.id.action_menu_sethybrid:
			googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void drawMarker(LatLng point, String name, String adrs) {
		// Creating an instance of MarkerOptions
		MarkerOptions markerOptions = new MarkerOptions();

		// Setting latitude and longitude for the marker
		markerOptions.position(point);

		// Adding marker on the Google Map
		Marker marker = googleMap.addMarker(markerOptions.title(name).snippet(
				adrs));
		markers.add(marker);
	}

	private void findNearest(final double parseLat, final double parseLon) {

		String appointment = checkAppointment();

		dialog2 = new Dialog(MapActivity.this);
		dialog2.setContentView(R.layout.fragment_reroute_note);
		dialog2.setTitle("Note");
		routeTxtV2 = (TextView) dialog2.findViewById(R.id.rerouteNote);
		okBtn = (Button) dialog2.findViewById(R.id.okBtn);

		if (!appointment.equals("no")) {

			sep3 = appointment.split("\\-");

			dialog = new Dialog(MapActivity.this);
			dialog.setContentView(R.layout.fragment_reroute);
			dialog.setTitle("Change Route");
			routeTxtV = (TextView) dialog.findViewById(R.id.reroute);
			yesBtn = (Button) dialog.findViewById(R.id.yesBtn);
			noBtn = (Button) dialog.findViewById(R.id.noBtn);

			if (traffic) {
				if (seperation.equals("gas station")) {
					routeTxtV
							.setText("You have an appointment at "
									+ sep3[1]
									+ " at "
									+ sep3[0]
									+ ". Do you want to find a "
									+ seperation
									+ " near that location? Keep in mind that there is traffic on the road at this hour!");
				} else if (seperation.equals("supermarket")) {
					routeTxtV
							.setText("You have an appointment at "
									+ sep3[1]
									+ " at "
									+ sep3[0]
									+ ". Do you want to find a "
									+ seperation
									+ " near that location? Keep in mind that there is traffic on the road at this hour and you may not have enough time to complete your shopping!");
				}
			} else {
				if (seperation.equals("gas station")) {
					routeTxtV.setText("You have an appointment at " + sep3[1]
							+ " at " + sep3[0] + ". Do you want to find a "
							+ seperation + " near that location?");
				} else if (seperation.equals("supermarket")) {
					routeTxtV
							.setText("You have an appointment at "
									+ sep3[1]
									+ " at "
									+ sep3[0]
									+ " and you may not have enough time to complete your shopping. Do you want to find a "
									+ seperation + " near that location?");
				}
			}

			yesBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

					try {
						String address = sep3[0] + ", Heraklion, Greece";
						List<Address> result = new Geocoder(getBaseContext())
								.getFromLocationName(address, 1);
						if (result != null && result.size() > 0) {
							double lat = result.get(0).getLatitude();
							double lng = result.get(0).getLongitude();
							Log.d("Address", lat + " " + lng);
							mark = nearestMarker(lat, lng);
							sep4 = mark.split("\\-");
							if (Float.parseFloat(sep4[2]) > 2500) {
								routeTxtV2
										.setText("There isn't a "
												+ seperation
												+ " near your appointment. The nearest "
												+ seperation
												+ " in your location will be found.");
								
								okBtn.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										mark = nearestMarker(parseLat, parseLon);
										dialog2.dismiss();
									}
								});
								dialog2.show();
							}
						} else {
							routeTxtV2
									.setText("The address of your appointment couldn't be found. The nearest "
											+ seperation
											+ " in your location will be found.");

							okBtn.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									mark = nearestMarker(parseLat, parseLon);
									dialog2.dismiss();
								}
							});
							dialog2.show();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

						routeTxtV2
								.setText("There isn't internet connection to determine the location of your appointment. The nearest "
										+ seperation
										+ " in your location will be found.");

						okBtn.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								mark = nearestMarker(parseLat, parseLon);
								dialog2.dismiss();
							}
						});
						dialog2.show();
					}
					dialog.dismiss();
				}
			});

			noBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mark = nearestMarker(parseLat, parseLon);
					dialog.dismiss();
				}
			});
			dialog.show();
		} else {
			if (traffic) {
				routeTxtV2
						.setText("Ôhere is traffic on the road and transportation will be slower.");

				okBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						mark = nearestMarker(parseLat, parseLon);
						dialog2.dismiss();
					}
				});
				dialog2.show();
			} else {
				mark = nearestMarker(parseLat, parseLon);
			}
		}
	}

	private String nearestMarker(double latitude, double longitude) {
		// Download the file
		for (int i = 0; i < mAdapter.getCount(); i++) {

			// Getting the latitude
			lat = mAdapter.getItem(i).getLatitude();

			// Getting the longitude
			lng = mAdapter.getItem(i).getLongitude();

			name = mAdapter.getItem(i).getCompany();

			if ((curTimeInt >= 2230) || (curTimeInt < 30)) {
				// Getting closing
				stationHour = Integer.parseInt(mAdapter.getItem(i).getClosing()
						.replaceAll("[\\D]", ""));
				if (stationHour <= 600) {
					Location.distanceBetween(Double.parseDouble(lat),
							Double.parseDouble(lng), latitude, longitude,
							results);

					dist = results[0];

					distMap.put(dist, name);
					Log.d(name, dist + "");
				}
			} else if ((curTimeInt >= 30) && (curTimeInt < 600)) {
				// Getting closing
				stationHour = Integer.parseInt(mAdapter.getItem(i).getClosing()
						.replaceAll("[\\D]", ""));
				if ((stationHour > 30) && (stationHour <= 600)) {
					Location.distanceBetween(Double.parseDouble(lat),
							Double.parseDouble(lng), latitude, longitude,
							results);

					dist = results[0];

					distMap.put(dist, name);
					Log.d(name, dist + "");
				}
			} else {
				Location.distanceBetween(Double.parseDouble(lat),
						Double.parseDouble(lng), latitude, longitude, results);

				dist = results[0];

				distMap.put(dist, name);
				Log.d(name, dist + "");
			}
		}

		treeMap = new TreeMap<Float, String>(distMap);
		distMap.clear();

		Set<Entry<Float, String>> s = treeMap.entrySet();
		Iterator<Entry<Float, String>> it = s.iterator();
		float key = 0;
		// Boolean found = false;
		while (it.hasNext()) {
			Map.Entry<Float, String> entry = (Map.Entry<Float, String>) it
					.next();
			key = (Float) entry.getKey();
			String value = (String) entry.getValue();
			s1 = value;
			System.out.println(key + " => " + value);
			break;
		}

		for (int i = 0; i < markers.size(); i++) {
			Marker j = markers.get(i);
			title = j.getTitle();
			if (s1.equals(title)) {
				// found = true;
				destLat = j.getPosition().latitude;
				destLong = j.getPosition().longitude;
				CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(new LatLng(destLat, destLong)).zoom(12).build();
				googleMap.animateCamera(CameraUpdateFactory
						.newCameraPosition(cameraPosition));
				j.showInfoWindow();
				break;
			}
		}
		return destLat + "-" + destLong + "-" + key;
	}

	private void navMarker(double startLat, double startLong, double endLat,
			double endLong) {
		if (nav) {
			if (isNetworkAvailable()) {

				CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(new LatLng(startLat, startLong)).zoom(15)
						.build();
				googleMap.animateCamera(CameraUpdateFactory
						.newCameraPosition(cameraPosition));

				LatLng fromPosition = new LatLng(startLat, startLong);
				LatLng toPosition = new LatLng(endLat, endLong);

				// Getting URL to the Google Directions API
				String url = getDirectionsUrl(fromPosition, toPosition);

				DownloadTask downloadTask = new DownloadTask();

				// Start downloading json data from Google Directions API
				downloadTask.execute(url);

			} else {
				nav = false;
				Toast.makeText(getApplicationContext(),
						"No Network Available!", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private String getDirectionsUrl(LatLng origin, LatLng dest) {

		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + sensor;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;

		return url;
	}

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	/** A class to download data from Google Directions URL */
	private class DownloadTask extends AsyncTask<String, Void, String> {

		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {

			// For storing data from web service
			String data = "";

			if (isNetworkAvailable()) {
				try {
					// Fetching the data from web service
					data = downloadUrl(url[0]);
				} catch (Exception e) {
					Log.d("Background Task", e.toString());
				}
			}
			return data;
		}

		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {

			super.onPostExecute(result);

			if (result.equals("")) {
				Toast.makeText(getApplicationContext(),
						"No Network Available!", Toast.LENGTH_SHORT).show();
			} else {
				ParserTask parserTask = new ParserTask();

				// Invokes the thread for parsing the JSON data
				parserTask.execute(result);
			}
		}
	}

	/** A class to parse the Google Directions in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				GMapV2Direction parser = new GMapV2Direction();

				// Starts parsing data
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			String distance = "";
			String duration = "";

			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++) {
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					if (j == 0) { // Get distance from the list
						distance = (String) point.get("distance");
						continue;
					} else if (j == 1) { // Get duration from the list
						duration = (String) point.get("duration");
						continue;
					}

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(6);
				lineOptions.color(Color.parseColor("#00b3fd"));
			}

			Log.d("Dist Dur", distance + " " + duration);

			// Drawing polyline in the Google Map for the i-th route
			googleMap.addPolyline(lineOptions);
		}
	}

	private String checkAppointment() {

		int i = getDefaultsInt("value_key_int");

		Log.d("I", i + "");

		String loc = null, startHour = null, ll = null;
		boolean appoint = false;

		curDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US)
				.format(new Date());
		sep3 = curDate.split("\\-");

		for (int j = 0; j < sep3.length; j++) {
			sepInt[j] = Integer.parseInt(sep3[j].toString());
		}

		curTime2 = String.valueOf(curTimeInt);

		if (curTimeInt < 10) {
			curMint = curTime2.substring(0, 1);
			sepInt[3] = 0;
			sepInt[4] = Integer.parseInt(curMint);
		} else if (curTimeInt < 100) {
			curMint = curTime2.substring(0, 2);
			sepInt[3] = 0;
			sepInt[4] = Integer.parseInt(curMint);
		} else if (curTimeInt < 1000) {
			curHour = curTime2.substring(0, 1);
			curMint = curTime2.substring(1, 3);
			sepInt[3] = Integer.parseInt(curHour);
			sepInt[4] = Integer.parseInt(curMint);
		} else {
			curHour = curTime2.substring(0, 2);
			curMint = curTime2.substring(2, 4);
			sepInt[3] = Integer.parseInt(curHour);
			sepInt[4] = Integer.parseInt(curMint);
		}

		Log.d("date hour", sepInt[0] + " " + sepInt[1] + " " + sepInt[2] + " "
				+ sepInt[3] + " " + sepInt[4]);

		if ((sepInt[3] == 8) || (sepInt[3] == 13) || (sepInt[3] == 14)) {
			traffic = true;
		} else {
			traffic = false;
		}

		if (i < 1) {
			appoint = false;
		} else {
			dataArray = loadArray("dataArray", getBaseContext());

			for (int k = 0; k <= i; k++) {
				Log.d("dataArray", dataArray[k]);
				if (!dataArray[k].equals("Your Appointments")) {

					sep3 = dataArray[k].split("\\:");

					int appHour = Integer.parseInt(sep3[7]);
					int appMin = Integer.parseInt(sep3[8]);

					if ((sepInt[0] == Integer.parseInt(sep3[4]))
							&& (sepInt[1] == Integer.parseInt(sep3[5]))
							&& (sepInt[2] == Integer.parseInt(sep3[6]))) {

						int diffHour = appHour - sepInt[3];
						if (diffHour == 0) {
							int diffMnt = appMin - sepInt[4];
							if ((diffMnt >= 0) && (diffMnt <= 30)) {
								appoint = true;
								loc = sep3[3];
								startHour = sep3[7] + ":" + sep3[8];
								break;
							}
						} else if (diffHour == 1) {
							int n = (59 - sepInt[4]) + appMin;
							if ((n >= 0) && (n <= 30)) {
								appoint = true;
								loc = sep3[3];
								startHour = sep3[7] + ":" + sep3[8];
								break;
							}
						}
					}
				}
			}
			if (!appoint) {
				appoint = false;
			}
		}
		if (appoint) {
			ll = loc + "-" + startHour;
		} else {
			ll = "no";
		}
		return ll;
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public void setValueDoubleToString(String newValue) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("value_key_string2", newValue);
		editor.commit();
	}

	public String getValueStringToDouble(String key) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		return preferences.getString(key, null);
	}

	public String[] loadArray(String arrayName, Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences(
				"preferencename", 0);
		int size = prefs.getInt(arrayName + "_size", 0);
		String array[] = new String[size];
		for (int i = 0; i < size; i++)
			array[i] = prefs.getString(arrayName + "_" + i, null);
		return array;
	}

	public int getDefaultsInt(String key) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		return preferences.getInt(key, -1);
	}

}
