package teicrete.gkentzoglanis.dailyactivities.account;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import teicrete.gkentzoglanis.dailyactivities.R;
import teicrete.gkentzoglanis.dailyactivities.library.DatabaseHandler;
import teicrete.gkentzoglanis.dailyactivities.library.UserFunctions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditFragment extends Fragment {

	private View focusView = null;
	private UserChangeTask mAuthTask = null;
	private Dialog dialog;
	private String[] key_array;
	private String chg, chg1, chg2, newChange, email, choice, uname, oldChange;
	private Button chgBtn, cancel;
	private EditText newView, oldView, newViewCnf;
	
	private static String KEY_SUCCESS = "success";
	public static final String ARG_SECTION_NUMBER = "section_number";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_edit, container,
				false);

		key_array = getArguments().getStringArray(ARG_SECTION_NUMBER);

		rootView.findViewById(R.id.chg_pass_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog = new Dialog(getActivity());
						dialog.setContentView(R.layout.fragment_chgpass);
						dialog.setTitle("Change Password");
						choice = "pas";
						oldView = (EditText) dialog
								.findViewById(R.id.oldpassword);
						newView = (EditText) dialog
								.findViewById(R.id.newpassword);
						newViewCnf = (EditText) dialog
								.findViewById(R.id.cfmnewpassword);
						oldView.setText(chg1);
						newView.setText(chg);
						newViewCnf.setText(chg2);
						chgBtn = (Button) dialog.findViewById(R.id.chgbtn);
						chgBtn.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								attempt();
							}
						});
						cancel = (Button) dialog.findViewById(R.id.cancelbtn);
						cancel.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								dialog.dismiss();
							}
						});
						dialog.show();
					}
				});

		rootView.findViewById(R.id.chg_adrss_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog = new Dialog(getActivity());
						dialog.setContentView(R.layout.fragment_chgadrs);
						dialog.setTitle("Change Address");
						choice = "adr";
						newView = (EditText) dialog.findViewById(R.id.newadrs);
						newView.setText(chg);
						chgBtn = (Button) dialog.findViewById(R.id.chgbtn);
						chgBtn.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								attempt();
							}
						});
						cancel = (Button) dialog.findViewById(R.id.cancelbtn);
						cancel.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								dialog.dismiss();
							}
						});
						dialog.show();
					}
				});

		rootView.findViewById(R.id.chg_pnumb_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog = new Dialog(getActivity());
						dialog.setContentView(R.layout.fragment_chgph);
						dialog.setTitle("Change Phone Number");
						choice = "phn";
						newView = (EditText) dialog.findViewById(R.id.newphone);
						newView.setText(chg);
						chgBtn = (Button) dialog.findViewById(R.id.chgbtn);
						chgBtn.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								attempt();
							}
						});
						cancel = (Button) dialog.findViewById(R.id.cancelbtn);
						cancel.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								dialog.dismiss();
							}
						});
						dialog.show();
					}
				});

		rootView.findViewById(R.id.chg_crdtn_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog = new Dialog(getActivity());
						dialog.setContentView(R.layout.fragment_chgcrdt);
						dialog.setTitle("Change Credit Card Number");
						choice = "crdt";
						newView = (EditText) dialog.findViewById(R.id.newcrdt);
						newView.setText(chg);
						chgBtn = (Button) dialog.findViewById(R.id.chgbtn);
						chgBtn.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								attempt();
							}
						});
						cancel = (Button) dialog.findViewById(R.id.cancelbtn);
						cancel.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								dialog.dismiss();
							}
						});
						dialog.show();
					}
				});

		return rootView;
	}

	public void attempt() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		newView.setError(null);

		// Store values.
		chg = newView.getText().toString();

		boolean cancel = false;

		// Check.
		if (choice == "pas") {
			oldView.setError(null);
			chg1 = oldView.getText().toString();
			newViewCnf.setError(null);
			chg2 = newViewCnf.getText().toString();
			if (TextUtils.isEmpty(chg1)) {
				oldView.setError(getString(R.string.error_field_required));
				focusView = oldView;
				cancel = true;
			}
			if (chg.length() < 4) {
				newView.setError(getString(R.string.error_invalid_password));
				focusView = newView;
				cancel = true;
			}
			if (!(chg2.equals(chg))) {
				newViewCnf
						.setError(getString(R.string.error_not_match_password));
				focusView = newViewCnf;
				cancel = true;
			}
		}

		if ((!TextUtils.isEmpty(chg)) && (chg.length() < 10) && (choice == "phn")) {
			newView.setError(getString(R.string.error_invalid_pnumber));
			focusView = newView;
			cancel = true;
		}

		if ((!TextUtils.isEmpty(chg)) && (chg.length() < 15) && (choice == "crdt")) {
			newView.setError(getString(R.string.error_invalid_crdtcard));
			focusView = newView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Kick off a background task to
			// perform the attempt.
			mAuthTask = new UserChangeTask();
			mAuthTask.execute((Void) null);
		}
	}

	public class UserChangeTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			/**
			 * Gets current device state and checks for working internet
			 * connection by trying Google.
			 **/
			ConnectivityManager cm = (ConnectivityManager) getActivity()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnected()) {
				try {
					URL url = new URL("http://www.google.com");
					HttpURLConnection urlc = (HttpURLConnection) url
							.openConnection();
					urlc.setConnectTimeout(3000);
					urlc.connect();
					if (urlc.getResponseCode() == 200) {
						return true;
					}
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;

			if (success) {
				new ProcessChange().execute();
			} else {
				Toast.makeText(getActivity().getApplicationContext(),
						"Error in Network Connection", Toast.LENGTH_SHORT)
						.show();
			}
		}

		/**
		 * Async Task to get and send data to My Sql database through JSON
		 * respone.
		 **/
		private class ProcessChange extends AsyncTask<String, Void, JSONObject> {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				email = key_array[3];
				chg = newView.getText().toString();
				newChange = chg;
				if (choice == "pas") {
					uname = key_array[4];
					chg1 = oldView.getText().toString();
					oldChange = chg1;
				}
			}

			@Override
			protected JSONObject doInBackground(String... args) {
				UserFunctions userFunction = new UserFunctions();
				JSONObject json = null;
				if (choice == "pas") {
					json = userFunction.chgPass(newChange, email, uname,
							oldChange);
				} else if (choice == "adr") {
					json = userFunction.chgAdrss(newChange, email);
				} else if (choice == "phn") {
					json = userFunction.chgPhone(newChange, email);
				} else if (choice == "crdt") {
					json = userFunction.chgCredit(newChange, email);
				}
				return json;
			}

			@Override
			protected void onPostExecute(JSONObject json) {
				try {
					if (json.getString(KEY_SUCCESS) != null) {
						String res = json.getString(KEY_SUCCESS);
						if (Integer.parseInt(res) == 1) {
							DatabaseHandler db = new DatabaseHandler(
									getActivity().getApplicationContext());

							Toast.makeText(
									getActivity().getApplicationContext(),
									"Your edit was successfull.",
									Toast.LENGTH_SHORT).show();

							if (choice != "pas") {
								db.updateRow(key_array[0], newChange, choice);
							}
							dialog.dismiss();

							Intent maccount = new Intent(getActivity()
									.getApplicationContext(),
									MyAccountActivity.class);
							maccount.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(maccount);
						} else {
							oldView.setError(getString(R.string.error_incorrect1));
							focusView = oldView;
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		}
	}
	
}
