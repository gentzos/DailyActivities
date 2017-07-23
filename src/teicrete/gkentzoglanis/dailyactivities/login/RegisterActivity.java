package teicrete.gkentzoglanis.dailyactivities.login;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import teicrete.gkentzoglanis.dailyactivities.MainActivity;
import teicrete.gkentzoglanis.dailyactivities.R;
import teicrete.gkentzoglanis.dailyactivities.library.DatabaseHandler;
import teicrete.gkentzoglanis.dailyactivities.library.UserFunctions;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a register screen to the user.
 */
public class RegisterActivity extends Activity {

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values at the time of the register attempt.
	private String mFname, mLname, mPnumber, mCrdtcard, mUsername, mEmail, mPassword, cfmPassword;
//	private String mAddress;
	private View focusView = null;

	// UI references.
	private EditText mFnameView, mLnameView, mAddressView, mPnumberView, mCrdtcardView, mUsernameView, mEmailView, mPasswordView, cfmPasswordView;
	private View mRegisterFormView, mRegisterStatusView;
	private TextView mRegisterStatusMessageView;

	private static String KEY_SUCCESS = "success";
	private static String KEY_UID = "uid";
	private static String KEY_FIRSTNAME = "fname";
	private static String KEY_LASTNAME = "lname";
	private static String KEY_ADDRESS = "address";
	private static String KEY_PHONENUMBER = "pnumber";
	private static String KEY_CREDITCARD = "crdtcard";
	private static String KEY_USERNAME = "uname";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";
	private static String KEY_ERROR = "error";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#839c43")));
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

		// Set up the register form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mFnameView = (EditText) findViewById(R.id.fname);
		mLnameView = (EditText) findViewById(R.id.lname);
		mAddressView = (EditText) findViewById(R.id.address);
		mPnumberView = (EditText) findViewById(R.id.pnumber);
		mCrdtcardView = (EditText) findViewById(R.id.crdtcard);
		mUsernameView = (EditText) findViewById(R.id.username);
		mPasswordView = (EditText) findViewById(R.id.password);
		cfmPasswordView = (EditText) findViewById(R.id.cfmpassword);

		mCrdtcardView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.register || id == EditorInfo.IME_NULL) {
							attemptRegister();
							return true;
						}
						return false;
					}
				});

		mRegisterFormView = findViewById(R.id.register_form);
		mRegisterStatusView = findViewById(R.id.register_status);
		mRegisterStatusMessageView = (TextView) findViewById(R.id.register_status_message);

		findViewById(R.id.register_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptRegister();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptRegister() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mFnameView.setError(null);
		mLnameView.setError(null);
		mAddressView.setError(null);
		mPnumberView.setError(null);
		mCrdtcardView.setError(null);
		mUsernameView.setError(null);
		mEmailView.setError(null);
		mPasswordView.setError(null);
		cfmPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mFname = mFnameView.getText().toString();
		mLname = mLnameView.getText().toString();
//		mAddress = mAddressView.getText().toString();
		mPnumber = mPnumberView.getText().toString();
		mCrdtcard = mCrdtcardView.getText().toString();
		mUsername = mUsernameView.getText().toString();
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		cfmPassword = cfmPasswordView.getText().toString();

		boolean cancel = false;

		// Check for a valid email address, username, password...
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		} else if (TextUtils.isEmpty(mUsername)) {
			mUsernameView.setError(getString(R.string.error_field_required));
			focusView = mUsernameView;
			cancel = true;
		} else if (mUsername.length() < 4) {
			mUsernameView.setError(getString(R.string.error_invalid_username));
			focusView = mUsernameView;
			cancel = true;
		} else if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		} else if (TextUtils.isEmpty(cfmPassword)) {
			cfmPasswordView.setError(getString(R.string.error_field_required));
			focusView = cfmPasswordView;
			cancel = true;
		} else if (!(cfmPassword.equals(mPassword))) {
			cfmPasswordView
					.setError(getString(R.string.error_not_match_password));
			focusView = cfmPasswordView;
			cancel = true;
		} else if (TextUtils.isEmpty(mFname)) {
			mFnameView.setError(getString(R.string.error_field_required));
			focusView = mFnameView;
			cancel = true;
		} else if (TextUtils.isEmpty(mLname)) {
			mLnameView.setError(getString(R.string.error_field_required));
			focusView = mLnameView;
			cancel = true;
		} else if ((!TextUtils.isEmpty(mPnumber)) && (mPnumber.length() < 10)) {
			mPnumberView.setError(getString(R.string.error_invalid_pnumber));
			focusView = mPnumberView;
			cancel = true;
		} else if ((!TextUtils.isEmpty(mCrdtcard)) && (mCrdtcard.length() < 15)) {
			mCrdtcardView.setError(getString(R.string.error_invalid_crdtcard));
			focusView = mCrdtcardView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt register and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mRegisterStatusMessageView
					.setText(R.string.register_progress_signing_up);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the register form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mRegisterStatusView.setVisibility(View.VISIBLE);
			mRegisterStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mRegisterStatusView
									.setVisibility(show ? View.VISIBLE
											: View.GONE);
						}
					});

			mRegisterFormView.setVisibility(View.VISIBLE);
			mRegisterFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mRegisterFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mRegisterStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			/**
			 * Gets current device state and checks for working internet
			 * connection by trying Google.
			 **/
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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
			showProgress(false);

			if (success) {
				new ProcessLogin().execute();
			} else {
				Toast.makeText(getApplicationContext(),
						"Error in Network Connection", Toast.LENGTH_SHORT)
						.show();
			}
		}

		/**
		 * Async Task to get and send data to My Sql database through JSON
		 * respone.
		 **/
		private class ProcessLogin extends AsyncTask<Void, Void, JSONObject> {

			String email, password, fname, lname, address, pnumber, crdtcard,
					uname;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mFnameView = (EditText) findViewById(R.id.fname);
				mLnameView = (EditText) findViewById(R.id.lname);
				mAddressView = (EditText) findViewById(R.id.address);
				mPnumberView = (EditText) findViewById(R.id.pnumber);
				mCrdtcardView = (EditText) findViewById(R.id.crdtcard);
				mUsernameView = (EditText) findViewById(R.id.username);
				mEmailView = (EditText) findViewById(R.id.email);
				mPasswordView = (EditText) findViewById(R.id.password);
				fname = mFnameView.getText().toString();
				lname = mLnameView.getText().toString();
				address = mAddressView.getText().toString();
				pnumber = mPnumberView.getText().toString();
				crdtcard = mCrdtcardView.getText().toString();
				uname = mUsernameView.getText().toString();
				email = mEmailView.getText().toString();
				password = mPasswordView.getText().toString();
			}

			@Override
			protected JSONObject doInBackground(Void... params) {
				UserFunctions userFunction = new UserFunctions();
				JSONObject json = userFunction.registerUser(fname, lname,
						address, pnumber, crdtcard, email, uname, password);
				return json;
			}

			@Override
			protected void onPostExecute(JSONObject json) {
				try {
					if (json.getString(KEY_SUCCESS) != null) {
						String res = json.getString(KEY_SUCCESS);
						String red = json.getString(KEY_ERROR);
						if (Integer.parseInt(res) == 1) {
							DatabaseHandler db = new DatabaseHandler(
									getApplicationContext());
							JSONObject json_user = json.getJSONObject("user");
							/**
							 * Clear all previous data in SQlite database.
							 **/
							UserFunctions logout = new UserFunctions();
							logout.logoutUser(getApplicationContext());
							db.addUser(json_user.getString(KEY_FIRSTNAME),
									json_user.getString(KEY_LASTNAME),
									json_user.getString(KEY_EMAIL),
									json_user.getString(KEY_USERNAME),
									json_user.getString(KEY_UID),
									json_user.getString(KEY_CREATED_AT),
									json_user.getString(KEY_ADDRESS),
									json_user.getString(KEY_PHONENUMBER),
									json_user.getString(KEY_CREDITCARD));
							/**
							 * Stores registered data in SQlite Database Launch
							 * Registered screen
							 **/
							Intent upanel = new Intent(getApplicationContext(),
									MainActivity.class);
							upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(upanel);
							/**
							 * Close Register Screen
							 **/
							finish();
						} else if (Integer.parseInt(red) == 2) {
							mUsernameView
									.setError(getString(R.string.error_registered_username));
							focusView = mUsernameView;
						} else if (Integer.parseInt(red) == 4) {
							mEmailView
									.setError(getString(R.string.error_registered_email));
							focusView = mEmailView;
						} else if (Integer.parseInt(red) == 3) {
							mEmailView
									.setError(getString(R.string.error_invalid_email));
							focusView = mEmailView;
						} else {
							Toast.makeText(getApplicationContext(),
									"Error occured in registration",
									Toast.LENGTH_SHORT).show();
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
			showProgress(false);
		}
	}
	
}
