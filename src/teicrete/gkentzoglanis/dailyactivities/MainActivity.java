package teicrete.gkentzoglanis.dailyactivities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import teicrete.gkentzoglanis.dailyactivities.account.MyAccountActivity;
import teicrete.gkentzoglanis.dailyactivities.appointments.AppointmentActivity;
import teicrete.gkentzoglanis.dailyactivities.library.DatabaseHandler;
import teicrete.gkentzoglanis.dailyactivities.library.UserFunctions;
import teicrete.gkentzoglanis.dailyactivities.login.LoginActivity;
import teicrete.gkentzoglanis.dailyactivities.stations.StationActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private String itemValue, usr;
	// A variable to separate if we click gas stations or supermarkets.
	private String uniqueId;
	private String[] strArr;

	private List<MainListRowItem> rowItems;

	private static Integer[] images = { R.drawable.gas_station, R.drawable.supermarket,
			R.drawable.appointment, R.drawable.account };
//	private static Integer[] images = { R.drawable.cinema,
//		R.drawable.gas_station, R.drawable.supermarket,
//		R.drawable.appointment, R.drawable.account };
	
	private HashMap<String, String> user = new HashMap<String, String>();
	private HashMap<String, String> m = new HashMap<String, String>();
	private ArrayList<HashMap<String, String>> test = new ArrayList<HashMap<String, String>>();
	
	private TextView welcomeTxt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#839c43")));
		actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

		welcomeTxt = (TextView) findViewById(R.id.welcome);
		
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		user = db.getUserDetails();
		usr = user.get("uname");
		
		if (usr == null) {
			usr = "empty";
		} else {
			// from hash to string
			test.add(user);
			m = test.get(0);
			strArr = new String[m.size()];
			int i = 0;
			for (HashMap<String, String> hash : test) {
				for (String current : hash.values()) {
					strArr[i] = current;
					i++;
				}
			}
		}

		// Get ListView object from xml
		final ListView listView = (ListView) findViewById(R.id.mainList);
		rowItems = new ArrayList<MainListRowItem>();

		// Defined Array values to show in ListView
		String[] titles = { "Gas Stations", "Supermarkets",
				"Appointments" };
//		String[] titles = { "Movies", "Gas Stations", "Supermarkets",
//		"Appointments" };
		
		// Defined Array values to show in ListView
		String[] titles2 = { "Gas Stations", "Supermarkets",
						"Appointments", "My Account" };

		if (usr.equals("empty")) {
			welcomeTxt.setVisibility(View.GONE);
			// Populate the List
			for (int i = 0; i < titles.length; i++) {
				MainListRowItem item = new MainListRowItem(images[i], titles[i]);
				rowItems.add(item);
			}
		} else {
			if (strArr[8].length() <= 7) {
				welcomeTxt.setText("Welcome " + strArr[8]);
			} else {
				welcomeTxt.setText("Welcome \n" + strArr[8]);
			}
			// Populate the List
			for (int i = 0; i < titles2.length; i++) {
				MainListRowItem item = new MainListRowItem(images[i], titles2[i]);
				rowItems.add(item);
			}
		}

		// Set the adapter on the ListView
		MainListAdapter adapter = new MainListAdapter(MainActivity.this,
				R.layout.list_row_main, rowItems);
		listView.setAdapter(adapter);

		// ListView Item Click Listener
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// ListView Clicked item value
				itemValue = rowItems.get(position).getTitle();

//				if (itemValue.equals("Movies")) {
					// Intent intent = new Intent(view.getContext(),
					// StationActivity.class);
					// startActivity(intent);
//				} else 
				if (itemValue.equals("Gas Stations")) {
					uniqueId = "map:gas_station.xml";
					Intent intent = new Intent(view.getContext(),
							StationActivity.class);
					intent.putExtra("value", uniqueId);
					startActivity(intent);
				} else if (itemValue.equals("Supermarkets")) {
					uniqueId = "map:supermarket.xml";
					Intent intent = new Intent(view.getContext(),
							StationActivity.class);
					intent.putExtra("value", uniqueId);
					startActivity(intent);
				} else if (itemValue.equals("Appointments")) {
					Intent intent = new Intent(view.getContext(),
							AppointmentActivity.class);
					startActivity(intent);
				} else if (itemValue.equals("My Account")) {
					Intent intent = new Intent(view.getContext(),
							MyAccountActivity.class);
					startActivity(intent);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the user is signed in then show sign out button and other way around.
		if (usr.equals("empty")) {
			menu.findItem(R.id.action_signin).setVisible(true);
			menu.findItem(R.id.action_signout).setVisible(false);
		} else {
			menu.findItem(R.id.action_signin).setVisible(false);
			menu.findItem(R.id.action_signout).setVisible(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_signin) {
			Intent intent = new Intent(getApplicationContext(),
					LoginActivity.class);
			startActivity(intent);
			return true;
		} else if (id == R.id.action_signout) {
			UserFunctions logout = new UserFunctions();
			logout.logoutUser(getApplicationContext());
			Intent intent = new Intent(getApplicationContext(),
					MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
}
