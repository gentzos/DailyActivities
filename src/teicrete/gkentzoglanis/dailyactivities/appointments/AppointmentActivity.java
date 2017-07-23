package teicrete.gkentzoglanis.dailyactivities.appointments;

import java.util.ArrayList;
import java.util.List;

import teicrete.gkentzoglanis.dailyactivities.R;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class AppointmentActivity extends Activity {

	private Appointments curapAp = null;

	private List<Appointments> appointmentsList = new ArrayList<Appointments>();
	private AppointmentAdapter aAdpt;
	private ListView lv;

	private String parseInfo, tempParseInfo, subj, descr, loc, year, month,
			day, strtHour;
	private String[] dataArray = new String[40];
	private String[] sep;
	private int i, tempI, newPlus;

	private Dialog confirmDialog;
	private TextView appointTxtV, confirmTxtV;
	private Button yesBtn, noBtn, dltBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appointment);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#318fe7")));

		// Using fragment reroute for delete confirmation
		confirmDialog = new Dialog(AppointmentActivity.this);
		confirmDialog.setContentView(R.layout.fragment_reroute);
		confirmDialog.setTitle("Delete Confirmation");
		confirmTxtV = (TextView) confirmDialog.findViewById(R.id.reroute);
		yesBtn = (Button) confirmDialog.findViewById(R.id.yesBtn);
		noBtn = (Button) confirmDialog.findViewById(R.id.noBtn);

		// Check for change or delete appointment.
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			tempParseInfo = extras.getString("yes");
		}

		// Get reference to our ListView
		lv = (ListView) findViewById(R.id.appList);
		appointTxtV = (TextView) findViewById(R.id.appoint);
		dltBtn = (Button) findViewById(R.id.deleteBtn);

		aAdpt = new AppointmentAdapter(appointmentsList, this);
		lv.setAdapter(aAdpt);

		curapAp = new Appointments();
		
		// Launch activity for adding new appointments.
		findViewById(R.id.new_plus).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						saveArray(dataArray, "dataArray", getBaseContext());
						// adding a new appointment.
						newPlus = 1;
						setValueInt2(newPlus);
						Intent intent = new Intent(view.getContext(),
								SetAppointmentActivity.class);
						startActivity(intent);
					}
				});

		// Check if there are any appointments.
		i = getDefaultsInt("value_key_int");

		if (i < 1) {
			dataArray[0] = "No Appointments";
			appointTxtV.setText(dataArray[0]);
		} else {
			dataArray = loadArray("dataArray", getBaseContext());
			dataArray[0] = "Your Appointments";
			appointTxtV.setText(dataArray[0]);

			// Change or delete appointments.
			if (tempParseInfo != null) {
				sep = tempParseInfo.split("\\:");
				tempI = Integer.parseInt(sep[1]) + 1;
				
				if (sep[0].equals("change")) {
					dataArray[tempI] = getDefaultsString("value_key_string");
					saveArray(dataArray, "dataArray", getBaseContext());
				} else if (sep[0].equals("remove")){
					for (int k = tempI; k <= i; k++) {
						if (k == i) {
							dataArray[k] = null;
							i--;
							setValueInt(i);
						} else {
							dataArray[k] = dataArray[k + 1];
						}
					}
				}
				
			} else { // View all appointments.
				newPlus = getDefaultsInt("value_key_int2");
				if (newPlus == 1) {
					dataArray[i] = getDefaultsString("value_key_string");
					newPlus = 0;
					setValueInt2(newPlus);
				}
			}

			saveArray(dataArray, "dataArray", getBaseContext());

			for (int k = 0; k <= i; k++) {
				if (!dataArray[k].equals(null)) {
					Log.d("dataArray", dataArray[k]);
				}

				if (!dataArray[k].equals("Your Appointments")) {
					sep = dataArray[k].split("\\:");
					curapAp.setSubject(sep[1]);
					curapAp.setDescr(sep[2]);
					curapAp.setLocation(sep[3]);
					curapAp.setYear(sep[4]);
					curapAp.setMonth(sep[5]);
					curapAp.setDay(sep[6]);
					curapAp.setStartHour(sep[7] + ":" + sep[8]);

					appointmentsList.add(new Appointments(curapAp.getSubject(),
							curapAp.getDescr(), curapAp.getLocation(), curapAp
									.getYear(), curapAp.getMonth(), curapAp
									.getDay(), curapAp.getStartHour()));
				}
			}
			aAdpt.notifyDataSetChanged();
		}

		// Set the click listener to edit appointments when a row is clicked.
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int pos,
					long id) {
				subj = aAdpt.getItem(pos).getSubject();
				descr = aAdpt.getItem(pos).getDescr();
				loc = aAdpt.getItem(pos).getLocation();
				year = aAdpt.getItem(pos).getYear();
				month = aAdpt.getItem(pos).getMonth();
				day = aAdpt.getItem(pos).getDay();
				strtHour = aAdpt.getItem(pos).getStartHour();
				parseInfo = subj + "-" + descr + "-" + loc + "-" + year + "-"
						+ month + "-" + day + "-" + strtHour + "-" + pos;
				Intent intent = new Intent(getApplicationContext(),
						SetAppointmentActivity.class);
				intent.putExtra("infoParse", parseInfo);
				startActivity(intent);
			}
		});

		// Delete all appointments.
		dltBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (dataArray[0].equals("Your Appointments")) {
					confirmTxtV
							.setText("Are you sure you want to delete all of your appointments?");

					yesBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							remove();
							confirmDialog.dismiss();
						}
					});

					noBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							confirmDialog.dismiss();
						}
					});
					confirmDialog.show();
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.set_appointment, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Method to save the array of appointments.
	public boolean saveArray(String[] array, String arrayName, Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences(
				"preferencename", 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(arrayName + "_size", array.length);
		for (int i = 0; i < array.length; i++)
			editor.putString(arrayName + "_" + i, array[i]);
		return editor.commit();
	}

	// Method to load the array of appointments.
	public String[] loadArray(String arrayName, Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences(
				"preferencename", 0);
		int size = prefs.getInt(arrayName + "_size", 0);
		String array[] = new String[size];
		for (int i = 0; i < size; i++)
			array[i] = prefs.getString(arrayName + "_" + i, null);
		return array;
	}

	// Method to get a single appointment, the current one.
	public String getDefaultsString(String key) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		return preferences.getString(key, null);
	}

	// Method to save integer. Useful to know how many appointments there are.
	public void setValueInt(int newValue) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("value_key_int", newValue);
		editor.commit();
	}
	
	// Method to save integer. Useful for adding new appointment.
		public void setValueInt2(int newValue) {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt("value_key_int2", newValue);
			editor.commit();
		}

	// Method to restore integer. Useful to check how many appointments there are.
	public int getDefaultsInt(String key) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		return preferences.getInt(key, -1);
	}

	// Method to remove all appointments.
	private void remove() {
		dataArray = loadArray("dataArray", getBaseContext());
		appointmentsList.removeAll(appointmentsList);

		for (int k = 0; k < dataArray.length; k++) {
			dataArray[k] = null;
		}
		dataArray[0] = "No Appointments";
		appointTxtV.setText(dataArray[0]);
		i = getDefaultsInt("value_key_int");
		i = 0;
		setValueInt(i);
		saveArray(dataArray, "dataArray", getBaseContext());
		aAdpt.notifyDataSetChanged();
	}
}
