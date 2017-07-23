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
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user.
 */
public class LoginActivity extends Activity {

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for username and password at the time of the login attempt.
	private String mUsername, mPassword, mEmail, choice;

	// UI references.
	private EditText mUsernameView, mPasswordView, mEmailView;
	private View mLoginFormView, mLoginStatusView, focusView = null;
	private TextView mLoginStatusMessageView;
	private Dialog dialog;
	private Button resetBtn, cancelBtn;

	private static String KEY_SUCCESS = "success";
	private static String KEY_UID = "uid";
	private static String KEY_USERNAME = "uname";
	private static String KEY_FIRSTNAME = "fname";
	private static String KEY_LASTNAME = "lname";
	private static String KEY_ADDRESS = "address";
	private static String KEY_PHONENUMBER = "pnumber";
	private static String KEY_CREDITCARD = "crdtcard";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#839c43")));
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

		// Set up the login form.
		mUsernameView = (EditText) findViewById(R.id.username);
		mUsernameView.setText(mUsername);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						choice = "sign";
						attemptLogin();
					}
				});

		findViewById(R.id.register).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(view.getContext(),
								RegisterActivity.class);
						startActivity(intent);
					}
				});

		findViewById(R.id.forgot_password_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						// use reroute fragment for reseting the password
						dialog = new Dialog(LoginActivity.this);
						dialog.setContentView(R.layout.fragment_rstpass);
						dialog.setTitle("Reset Password");
						mEmailView = (EditText) dialog
								.findViewById(R.id.rstemail);
						resetBtn = (Button) dialog.findViewById(R.id.chgbtn);
						cancelBtn = (Button) dialog
								.findViewById(R.id.cancelbtn);
						choice = "rst";
						resetBtn.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								attemptLogin();
							}
						});
						cancelBtn
								.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										dialog.dismiss();
									}
								});
						dialog.show();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		// int id = item.getItemId();
		// if (id == R.id.action_settings) {
		// return true;
		// }
		// return super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Attempts to sign in the account specified by the login form. If there are
	 * form errors (invalid email, missing fields, etc.), the errors are
	 * presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}
		boolean cancel = false;

		if (choice == "sign") {
			// Reset errors.
			mUsernameView.setError(null);
			mPasswordView.setError(null);

			// Store values at the time of the login attempt.
			mUsername = mUsernameView.getText().toString();
			mPassword = mPasswordView.getText().toString();

			// Check for a valid password.
			if (TextUtils.isEmpty(mPassword)) {
				mPasswordView
						.setError(getString(R.string.error_field_required));
				focusView = mPasswordView;
				cancel = true;
			} else if (mPassword.length() < 4) {
				mPasswordView
						.setError(getString(R.string.error_invalid_password));
				focusView = mPasswordView;
				cancel = true;
			}

			// Check for a valid username.
			if (TextUtils.isEmpty(mUsername)) {
				mUsernameView
						.setError(getString(R.string.error_field_required));
				focusView = mUsernameView;
				cancel = true;
			} else if (mUsername.length() < 4) {
				mUsernameView
						.setError(getString(R.string.error_invalid_username));
				focusView = mUsernameView;
				cancel = true;
			}
		} else if (choice == "rst") {
			// Reset errors.
			mEmailView.setError(null);

			// Store values at the time of the reset attempt.
			mEmail = mEmailView.getText().toString();

			// Check for a valid email.
			if (TextUtils.isEmpty(mEmail)) {
				mEmailView.setError(getString(R.string.error_field_required));
				focusView = mUsernameView;
				cancel = true;
			} else if (!mEmail.contains("@")) {
				mEmailView.setError(getString(R.string.error_invalid_email));
				focusView = mEmailView;
				cancel = true;
			}
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			if (choice == "sign") {
				mLoginStatusMessageView
						.setText(R.string.login_progress_signing_in);
				showProgress(true);
				mAuthTask = new UserLoginTask();
				mAuthTask.execute((Void) null);
			} else if (choice == "rst") {
				mAuthTask = new UserLoginTask();
				mAuthTask.execute((Void) null);
			}
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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

			String username, password;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				if (choice == "sign") {
					mUsernameView = (EditText) findViewById(R.id.username);
					mPasswordView = (EditText) findViewById(R.id.password);
					username = mUsernameView.getText().toString();
					password = mPasswordView.getText().toString();
				} 
//				else if (choice == "rst") {
//					mEmailView = (EditText) findViewById(R.id.rstemail);
//					email = mEmailView.getText().toString();
//				}
			}

			@Override
			protected JSONObject doInBackground(Void... params) {
				UserFunctions userFunction = new UserFunctions();
				JSONObject json = null;
				if (choice == "sign") {
					json = userFunction.loginUser(username, password);
				} else if (choice == "rst") {
					json = userFunction.forPass(mEmail);
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
									getApplicationContext());
							if (choice == "sign") {
								JSONObject json_user = json
										.getJSONObject("user");
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
								 * If JSON array details are stored in SQlite it
								 * launches the User Panel.
								 **/
								Intent upanel = new Intent(
										getApplicationContext(),
										MainActivity.class);
								upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(upanel);
								/**
								 * Close Login Screen
								 **/
								finish();
							} else if (choice == "rst") {
								dialog.dismiss();
								Toast.makeText(getApplicationContext(),
										"Check your email to proceed.",
										Toast.LENGTH_SHORT).show();
							}
						} else {
							if (choice == "sign") {
								mUsernameView
										.setError(getString(R.string.error_incorrect));
								mPasswordView
										.setError(getString(R.string.error_incorrect));
								focusView = mUsernameView;
								focusView = mPasswordView;
							}
							if (choice == "rst") {
								mEmailView
										.setError(getString(R.string.error_dontexist));
								focusView = mEmailView;
							}
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

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

}
