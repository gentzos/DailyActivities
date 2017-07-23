package teicrete.gkentzoglanis.dailyactivities.stations;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import teicrete.gkentzoglanis.dailyactivities.R;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class StationActivity extends Activity {

	private StationsAdapter mAdapter;
	private ListView stationsList;

	private String latitude, longitude, title, info, valueId, seperation,
			curTime;
	private String[] sep;
	private int curTimeInt;
	private Boolean exist = false;
	private Button nearBtn, okBtn;
	private Dialog dialog;
	private TextView noteTxtV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		Log.i("Stations", "OnCreate()");
		setContentView(R.layout.activity_station);

		valueId = getIntent().getStringExtra("value");
		sep = valueId.split("\\:");

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		nearBtn = (Button) findViewById(R.id.nearest);

		dialog = new Dialog(StationActivity.this);
		dialog.setContentView(R.layout.fragment_reroute_note);
		dialog.setTitle("Note");
		noteTxtV = (TextView) dialog.findViewById(R.id.rerouteNote);
		okBtn = (Button) dialog.findViewById(R.id.okBtn);

		if (sep[1].equals("gas_station.xml")) {
			actionBar.setBackgroundDrawable(new ColorDrawable(Color
					.parseColor("#e46713")));
			seperation = "gas station";
		} else if (sep[1].equals("supermarket.xml")) {
			actionBar.setTitle("Supermarkets");
			actionBar.setIcon(R.drawable.supermarket_ab);
			actionBar.setBackgroundDrawable(new ColorDrawable(Color
					.parseColor("#839c43")));
			nearBtn.setText("Nearest Supermarket");
			seperation = "supermarket";
		}

		// Get reference to our ListView
		stationsList = (ListView) findViewById(R.id.stationsList);

		// Set the click listener to launch maps when a row is clicked.
		stationsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int pos,
					long id) {
				latitude = mAdapter.getItem(pos).getLatitude();
				longitude = mAdapter.getItem(pos).getLongitude();
				title = mAdapter.getItem(pos).getCompany();
				info = "list:" + sep[1] + ":" + latitude + ":" + longitude
						+ ":" + title;
				Intent map = new Intent(getApplicationContext(),
						MapActivity.class);
				map.putExtra("value", info);
				startActivity(map);
			}
		});

		/*
		 * If network is available download the xml from the Internet. If not
		 * then try to use the local file from last time.
		 */

		File file = getBaseContext().getFileStreamPath(sep[1]);
		if (file.exists()) {
			exist = true;
			mAdapter = new StationsAdapter(getApplicationContext(), -1,
					StationsXmlPullParser.getStationsFromFile(
							StationActivity.this, sep[1]));
			stationsList.setAdapter(mAdapter);
		} else {
			exist = false;
			Toast.makeText(getApplicationContext(),
					"Press refresh button to see the list.", Toast.LENGTH_SHORT)
					.show();
		}

		nearBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				curTime = new SimpleDateFormat("HH:mm", Locale.US)
						.format(new Date());
				curTimeInt = Integer.parseInt(curTime.replaceAll("[\\D]", ""));
				if (exist) {
					LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
					boolean statusOfGPS = manager
							.isProviderEnabled(LocationManager.GPS_PROVIDER);
					if (statusOfGPS) {
						if (seperation.equals("supermarket")) {
							if ((curTimeInt >= 800) && (curTimeInt < 2100)) {
								if ((curTimeInt >= 2030) && (curTimeInt < 2100)) {
									noteTxtV.setText("Supermarkets are closing at 21:00 and you may not have enough time to complete your shopping!");
									okBtn.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View view) {
											info = "nearest:" + sep[1];
											Intent map = new Intent(
													getApplicationContext(),
													MapActivity.class);
											map.putExtra("value", info);
											startActivity(map);
											dialog.dismiss();
										}
									});
									dialog.show();
								} else {
									info = "nearest:" + sep[1];
									Intent map = new Intent(
											getApplicationContext(),
											MapActivity.class);
									map.putExtra("value", info);
									startActivity(map);
								}
							} else {
								noteTxtV.setText("There are no supermarkets open at this hour.");
								okBtn.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										dialog.dismiss();
									}
								});
								dialog.show();
							}
						} else if (seperation.equals("gas station")) {
							if ((curTimeInt >= 600) && (curTimeInt < 2230)) {
								info = "nearest:" + sep[1];
								Intent map = new Intent(
										getApplicationContext(),
										MapActivity.class);
								map.putExtra("value", info);
								startActivity(map);
							} else {
								noteTxtV.setText("Some gas stations are closed at this hour. The nearest open will be found.");
								okBtn.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										info = "nearest:" + sep[1];
										Intent map = new Intent(
												getApplicationContext(),
												MapActivity.class);
										map.putExtra("value", info);
										startActivity(map);
										dialog.dismiss();
									}
								});
								dialog.show();
							}
						}

					} else {
						Toast.makeText(getApplicationContext(),
								"GPS is disabled!", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(
							getApplicationContext(),
							"Refresh the list to find the closest "
									+ seperation + ".", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.station_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_station_list);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// Handle action buttons
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_refresh:
			item.setVisible(false);
			setProgressBarIndeterminateVisibility(true);
			if (isNetworkAvailable()) {
				Log.i("Stations", "starting download Task");
				StationsDownloadTask download = new StationsDownloadTask();
				download.execute();
				exist = true;
			}

			Runnable r = new Runnable() {
				@Override
				public void run() {
					setProgressBarIndeterminateVisibility(false);
					item.setVisible(true);
				}
			};

			Handler h = new Handler();
			h.postDelayed(r, Downloader.sleep);

			return true;
		case R.id.action_station_list:
			Intent intent = new Intent(getApplicationContext(),
					MapActivity.class);
			intent.putExtra("value", valueId);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// Helper method to determine if Internet connection is available.
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	/*
	 * AsyncTask that will download the xml file for us and store it locally.
	 * After the download is done we'll parse the local file.
	 */
	private class StationsDownloadTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			// Download the file
			try {
				Downloader.DownloadFromUrl("http://www.gentzos.tk/xml/"
						+ sep[1], openFileOutput(sep[1], Context.MODE_PRIVATE));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// setup our Adapter and set it to the ListView.
			mAdapter = new StationsAdapter(StationActivity.this, -1,
					StationsXmlPullParser.getStationsFromFile(
							StationActivity.this, sep[1]));
			stationsList.setAdapter(mAdapter);
			Log.i("Stations", "adapter size = " + mAdapter.getCount());
		}
	}
}
