package teicrete.gkentzoglanis.dailyactivities.appointments;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import teicrete.gkentzoglanis.dailyactivities.R;
import android.app.ActionBar;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class SetAppointmentActivity extends FragmentActivity {

	private String curTime, curTime2, curDate, curDate2, hlp, sbjct, descr,
			lctn, data, parseInfo;
	private String[] temp;
	private String[] sep = new String[3];
	private String[] details = new String[3];
	private String[] sepParseInfo = new String[8];
	private String[] dataArray = new String[40];

	private int curTimeInt, j;
	private int[] sepInt = new int[5];
	private int[] slctDate = new int[5];
	private int[] tempInt = new int[2];
	private Boolean bool = false;

	private Dialog confirmDialog;
	private TextView startDate, startTime, confirmTxtV;
	private EditText subjectText, descrText, locationText;
	private Button doneBtn, cancelBtn, dltBtn, yesBtn, noBtn;
	private View focusView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_appointment);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#318fe7")));

		startDate = (TextView) findViewById(R.id.date);
		startTime = (TextView) findViewById(R.id.startTime);
		subjectText = (EditText) findViewById(R.id.subject);
		descrText = (EditText) findViewById(R.id.description);
		locationText = (EditText) findViewById(R.id.location);
		doneBtn = (Button) findViewById(R.id.doneBtn);
		cancelBtn = (Button) findViewById(R.id.cancelBtn);
		dltBtn = (Button) findViewById(R.id.dltBtn);

		// Using fragment reroute for delete confirmation
		confirmDialog = new Dialog(SetAppointmentActivity.this);
		confirmDialog.setContentView(R.layout.fragment_reroute);
		confirmDialog.setTitle("Delete Confirmation");
		confirmTxtV = (TextView) confirmDialog.findViewById(R.id.reroute);
		yesBtn = (Button) confirmDialog.findViewById(R.id.yesBtn);
		noBtn = (Button) confirmDialog.findViewById(R.id.noBtn);

		// Check if there are any appointments.
		j = getValueInt();
		if (j < 1) {
			j = 0;
		}

		// Check for change or delete appointment.
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			parseInfo = extras.getString("infoParse");
			sepParseInfo = parseInfo.split("\\-");
			bool = true;
		}

		// Use of date in UI.
		curDate = new SimpleDateFormat("EEE, MMMM dd, yyyy", Locale.US)
				.format(new Date());

		// Useful to check the date.
		curDate2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US)
				.format(new Date());
		sep = curDate2.split("\\-");

		// Save the date for validating later in the code.
		for (int i = 0; i < sep.length; i++) {
			// Current date.
			sepInt[i] = Integer.parseInt(sep[i].toString());
			// Selected date.
			slctDate[i] = Integer.parseInt(sep[i].toString());
		}

		// Use of time in UI.
		curTime = new SimpleDateFormat("HH:mm", Locale.US).format(new Date());
		curTimeInt = Integer.parseInt(curTime.replaceAll("[\\D]", ""));

		// Save the time for validating later in the code.
		// Current time.
		sepInt[3] = curTimeInt;
		// Selected time.
		slctDate[3] = curTimeInt;

		if (parseInfo != null) { // Check for changing an appointment. Set
									// current appointment in UI.
			subjectText.setText(sepParseInfo[0]);
			descrText.setText(sepParseInfo[1]);
			locationText.setText(sepParseInfo[2]);
			startDate.setText(sepParseInfo[5] + "/" + sepParseInfo[4] + "/"
					+ sepParseInfo[3]);
			startTime.setText(sepParseInfo[6]);
			dltBtn.setVisibility(View.VISIBLE);
		} else { // Set current date and time in UI.
			startDate.setText(curDate);
			startTime.setText(curTime);
			dltBtn.setVisibility(View.INVISIBLE);
		}

		// Listener for changing the date of the appointment.
		startDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePicker();
			}
		});

		// Listener for changing the time of the appointment.
		startTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hlp = "start";
				showTimePicker(hlp);
			}
		});

		// Listener to cancel current appointment.
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(),
						AppointmentActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		});

		// Listener to save current appointment.
		doneBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				attempt();
			}
		});

		// Listener to delete current appointment.
		dltBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				confirmTxtV
						.setText("Are you sure you want to delete this appointment?");

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

	// Method to show the date picker.
	private void showDatePicker() {
		DatePickerFragment date = new DatePickerFragment();
		/**
		 * Set Up Current Date Into dialog
		 */
		Calendar calender = Calendar.getInstance();
		Bundle args = new Bundle();
		args.putInt("year", calender.get(Calendar.YEAR));
		args.putInt("month", calender.get(Calendar.MONTH));
		args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
		date.setArguments(args);
		/**
		 * Set Call back to capture selected date
		 */
		date.setCallBack(ondate);
		date.show(getSupportFragmentManager(), "Date Picker");
	}

	OnDateSetListener ondate = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			slctDate[0] = year;
			slctDate[1] = monthOfYear + 1;
			if (slctDate[1] == 13) {
				slctDate[1] = 1;
			}
			slctDate[2] = dayOfMonth;
			curDate = formatDate(year, monthOfYear, dayOfMonth);
			startDate.setText(curDate);
		}
	};

	// Method to change the format of the date.
	private static String formatDate(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(year, month, day);
		Date date = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMMM dd, yyyy",
				Locale.US);

		return sdf.format(date);
	}

	// Method to show the time picker.
	private void showTimePicker(String hlp) {
		TimePickerFragment time = new TimePickerFragment();
		/**
		 * Set Up Current Date Into dialog
		 */
		Calendar calender = Calendar.getInstance();
		Bundle args = new Bundle();
		args.putInt("hour", calender.get(Calendar.HOUR_OF_DAY));
		args.putInt("minute", calender.get(Calendar.MINUTE));
		time.setArguments(args);
		/**
		 * Set Call back to capture selected date
		 */
		if (hlp.equals("start")) {
			time.setCallBack(ontime);
		}
		time.show(getSupportFragmentManager(), "Time Picker");
	}

	OnTimeSetListener ontime = new OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hour, int minute) {

			if (minute < 10) {
				curTime = hour + ":0" + minute;
				curTime2 = hour + "0" + minute;
			} else {
				curTime = hour + ":" + minute;
				curTime2 = hour + "" + minute;
			}
			slctDate[3] = Integer.parseInt(curTime2.toString());
			startTime.setText(curTime);
		}
	};

	// Attempt to save current appointment.
	// Check for valid date and time.
	public void attempt() {

		// Reset errors.
		subjectText.setError(null);
		descrText.setError(null);
		locationText.setError(null);
		startDate.setError(null);
		startTime.setError(null);

		// Store values.
		sbjct = subjectText.getText().toString();
		descr = descrText.getText().toString();
		lctn = locationText.getText().toString();

		boolean cancel = false;

		// Check.
		if (TextUtils.isEmpty(descr)) {
			descrText.setText("");
		}

		if (TextUtils.isEmpty(lctn)) {
			locationText.setError(getString(R.string.error_field_required));
			focusView = locationText;
			cancel = true;
		}

		if (TextUtils.isEmpty(sbjct)) {
			subjectText.setError(getString(R.string.error_field_required));
			focusView = subjectText;
			cancel = true;
		}

		if (slctDate[0] < sepInt[0]) {
			Log.d("ERROR YEAR", slctDate[0] + "<" + sepInt[0]);
			startDate.setError(getString(R.string.error_invalid_year));
			Toast.makeText(this, R.string.error_invalid_year,
					Toast.LENGTH_SHORT).show();
			focusView = startDate;
			cancel = true;
		}

		if ((slctDate[0] == sepInt[0]) && (slctDate[1] < sepInt[1])) {
			Log.d("ERROR MONTH", slctDate[1] + "<" + sepInt[1]);
			startDate.setError(getString(R.string.error_invalid_month));
			Toast.makeText(this, R.string.error_invalid_month,
					Toast.LENGTH_SHORT).show();
			focusView = startDate;
			cancel = true;
		}

		if ((slctDate[0] == sepInt[0]) && (slctDate[1] == sepInt[1])
				&& (slctDate[2] < sepInt[2])) {
			Log.d("ERROR DAY", slctDate[2] + "<" + sepInt[2]);
			startDate.setError(getString(R.string.error_invalid_day));
			Toast.makeText(this, R.string.error_invalid_day, Toast.LENGTH_SHORT)
					.show();
			focusView = startDate;
			cancel = true;
		}

		if ((slctDate[0] == sepInt[0]) && (slctDate[1] == sepInt[1])
				&& (slctDate[2] == sepInt[2]) && (slctDate[3] < sepInt[3])) {
			Log.d("ERROR HOUR", slctDate[3] + "<" + sepInt[3]);
			startTime.setError(getString(R.string.error_invalid_start_hour));
			Toast.makeText(this, R.string.error_invalid_start_hour,
					Toast.LENGTH_SHORT).show();
			focusView = startTime;
			cancel = true;
		}

		// there is already an appointment
		if (j >= 1) {
			int diffHour = 0, diffMnt = 0, appHour = 0, appMin = 0, slctdAppointHour = 0, slctdAppointMin = 0;
			dataArray = loadArray("dataArray", getBaseContext());
			if (slctDate[3] < 10) {
				tempInt[0] = 0;
				tempInt[1] = Integer.parseInt(curTime2.substring(0, 1));
			} else if (slctDate[3] < 100) {
				tempInt[0] = 0;
				tempInt[1] = Integer.parseInt(curTime2.substring(0, 2));
			} else if (slctDate[3] < 1000) {
				tempInt[0] = Integer.parseInt(curTime2.substring(0, 1));
				tempInt[1] = Integer.parseInt(curTime2.substring(1, 3));
			} else {
				tempInt[0] = Integer.parseInt(curTime2.substring(0, 2));
				tempInt[1] = Integer.parseInt(curTime2.substring(2, 4));
			}
			for (int k = 0; k <= j; k++) {
				Log.d("dataArray", dataArray[k]);
				if (!dataArray[k].equals("Your Appointments")) {

					temp = dataArray[k].split("\\:");
					
					appHour = Integer.parseInt(temp[7]);
					appMin = Integer.parseInt(temp[8]);
					
					// if statement to not consider the current appointment and move on.
					if (bool) {
						String[] slctdAppointHourMin = sepParseInfo[6].split("\\:");
						slctdAppointHour = Integer.parseInt(slctdAppointHourMin[0]);
						slctdAppointMin = Integer.parseInt(slctdAppointHourMin[1]);
						
						if ((slctdAppointHour == appHour) && (slctdAppointMin == appMin)) {
							continue;
						}
					}

					// if date is current date
					if ((slctDate[0] == Integer.parseInt(temp[4]))
							&& (slctDate[1] == Integer.parseInt(temp[5]))
							&& (slctDate[2] == Integer.parseInt(temp[6]))) {

						diffHour = tempInt[0] - appHour;
						if (diffHour == 0) {
							diffMnt = tempInt[1] - appMin;
							if (((diffMnt >= 0) && (diffMnt <= 30)) || ((diffMnt <= 0) && (diffMnt >= -30))) {
								startTime
										.setError(getString(R.string.error_invalid_start_hour));
								Toast.makeText(this,
										R.string.error_invalid_start_hour2,
										Toast.LENGTH_SHORT).show();
								focusView = startTime;
								cancel = true;
								break;
							}
						} else if (diffHour == 1) {
							diffMnt = (59 - appMin) + tempInt[1];
							if ((diffMnt >= 0) && (diffMnt <= 30)) {
								startTime
										.setError(getString(R.string.error_invalid_start_hour));
								Toast.makeText(this,
										R.string.error_invalid_start_hour2,
										Toast.LENGTH_SHORT).show();
								focusView = startTime;
								cancel = true;
								break;
							}
						} else if (diffHour == -1) {
							diffMnt = (59 - tempInt[1]) + appMin;
							if ((diffMnt >= 0) && (diffMnt <= 30)) {
								startTime
										.setError(getString(R.string.error_invalid_start_hour));
								Toast.makeText(this,
										R.string.error_invalid_start_hour2,
										Toast.LENGTH_SHORT).show();
								focusView = startTime;
								cancel = true;
								break;
							}
						}
					}
				}
			}
		}

		if (cancel) {
			// There was an error; don't attempt and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// There wasn't an error,
			// perform the attempt.
			details[0] = sbjct;
			details[1] = descr;
			details[2] = lctn;

			Intent intent = new Intent(getApplicationContext(),
					AppointmentActivity.class);

			if (parseInfo != null) { // Check for changing an appointment.
				intent.putExtra("yes", "change" + ":" + sepParseInfo[7]);
				// The current appointment.
				j = Integer.parseInt(sepParseInfo[7]) + 1;
				data = j + ":" + details[0] + ":" + details[1] + ":"
						+ details[2] + ":" + slctDate[0] + ":" + slctDate[1]
						+ ":" + slctDate[2] + ":"
						+ startTime.getText().toString();
			} else { // Adding an appointment.
				// j is used for adding an appointment in the next position of
				// the list.
				j++;
				data = j + ":" + details[0] + ":" + details[1] + ":"
						+ details[2] + ":" + slctDate[0] + ":" + slctDate[1]
						+ ":" + slctDate[2] + ":"
						+ startTime.getText().toString();
				setValueInt(j);
			}
			setValueString(data);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}
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

	// Method to save a single appointment, the current one.
	public void setValueString(String newValue) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("value_key_string", newValue);
		editor.commit();
	}

	// Method to save integer. Useful to know how many appointments there are.
	public void setValueInt(int newValue) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("value_key_int", newValue);
		editor.commit();
	}

	// Method to load integer. Useful to know how many appointments there are.
	public int getValueInt() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		return prefs.getInt("value_key_int", -1);
	}

	// Method to remove an appointment.
	private void remove() {
		Intent intent = new Intent(getApplicationContext(),
				AppointmentActivity.class);
		intent.putExtra("yes", "remove" + ":" + sepParseInfo[7]);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}
}