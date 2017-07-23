package teicrete.gkentzoglanis.dailyactivities.library;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.content.Context;

//This is the functions file where login, register, registered, password reset, forgot password functions are defined 
//which sends and receives data to the server through JSON response. If you want to test the email feature change the 
//url from http://10.0.2.2/learn2crack_login_api to http://api.learn2crack.com/android/loginapi/
//http://10.0.2.2/android_connect/gentzos_login_api/
//http://gentzos.melidonis.gr/index.php

public class UserFunctions {
	private JSONParser jsonParser;
	// URL of the PHP API
	// private static String loginURL =
	// "http://10.0.2.2/android_connect/gentzos_login_api/";
	// private static String registerURL =
	// "http://10.0.2.2/android_connect/gentzos_login_api/";
	// private static String forpassURL =
	// "http://10.0.2.2/android_connect/gentzos_login_api/";
	// private static String chgpassURL =
	// "http://10.0.2.2/android_connect/gentzos_login_api/";
	// private static String chgadrssURL =
	// "http://10.0.2.2/android_connect/gentzos_login_api/";
	private static String loginURL = "http://gentzos.tk/gentzos_login_api/index.php";
	private static String registerURL = "http://gentzos.tk/gentzos_login_api/index.php";
	private static String forpassURL = "http://gentzos.tk/gentzos_login_api/index.php";
	private static String chgpassURL = "http://gentzos.tk/gentzos_login_api/index.php";
	private static String chgadrssURL = "http://gentzos.tk/gentzos_login_api/index.php";
	private static String chgphoneURL = "http://gentzos.tk/gentzos_login_api/index.php";
	private static String chgcrdtURL = "http://gentzos.tk/gentzos_login_api/index.php";
	private static String login_tag = "login";
	private static String register_tag = "register";
	private static String forpass_tag = "forpass";
	private static String chgpass_tag = "chgpass";
	private static String chgadrs_tag = "chgadrss";
	private static String chgphone_tag = "chgphone";
	private static String chgcrdt_tag = "chgcrdt";

	// constructor
	public UserFunctions() {
		jsonParser = new JSONParser();
	}

	/**
	 * Function to Login
	 **/
	public JSONObject loginUser(String uname, String password) {
		// Building Parameters
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("tag", login_tag));
		params.add(new BasicNameValuePair("uname", uname));
		params.add(new BasicNameValuePair("password", password));
		JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
		return json;
	}

	/**
	 * Function to change password
	 **/
	public JSONObject chgPass(String newpas, String email, String uname,
			String password) {
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("tag", chgpass_tag));
		params.add(new BasicNameValuePair("newpas", newpas));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("uname", uname));
		params.add(new BasicNameValuePair("password", password));
		JSONObject json = jsonParser.getJSONFromUrl(chgpassURL, params);
		return json;
	}

	/**
	 * Function to reset the password
	 **/
	public JSONObject forPass(String forgotpassword) {
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("tag", forpass_tag));
		params.add(new BasicNameValuePair("forgotpassword", forgotpassword));
		JSONObject json = jsonParser.getJSONFromUrl(forpassURL, params);
		return json;
	}

	/**
	 * Function to Register
	 **/
	public JSONObject registerUser(String fname, String lname, String address,
			String pnumber, String crdtcard, String email, String uname,
			String password) {
		// Building Parameters
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("tag", register_tag));
		params.add(new BasicNameValuePair("fname", fname));
		params.add(new BasicNameValuePair("lname", lname));
		params.add(new BasicNameValuePair("address", address));
		params.add(new BasicNameValuePair("pnumber", pnumber));
		params.add(new BasicNameValuePair("crdtcard", crdtcard));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("uname", uname));
		params.add(new BasicNameValuePair("password", password));
		JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
		return json;
	}

	/**
	 * Function to logout user Resets the temporary data stored in SQLite
	 * Database
	 * */
	public boolean logoutUser(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetTables();
		return true;
	}

	/**
	 * Function to change address
	 **/
	public JSONObject chgAdrss(String newadr, String email) {
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("tag", chgadrs_tag));
		params.add(new BasicNameValuePair("newadr", newadr));
		params.add(new BasicNameValuePair("email", email));
		JSONObject json = jsonParser.getJSONFromUrl(chgadrssURL, params);
		return json;
	}

	/**
	 * Function to change phone
	 **/
	public JSONObject chgPhone(String newphone, String email) {
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("tag", chgphone_tag));
		params.add(new BasicNameValuePair("newphone", newphone));
		params.add(new BasicNameValuePair("email", email));
		JSONObject json = jsonParser.getJSONFromUrl(chgphoneURL, params);
		return json;
	}

	/**
	 * Function to change credit card
	 **/
	public JSONObject chgCredit(String newcrdt, String email) {
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("tag", chgcrdt_tag));
		params.add(new BasicNameValuePair("newcrdt", newcrdt));
		params.add(new BasicNameValuePair("email", email));
		JSONObject json = jsonParser.getJSONFromUrl(chgcrdtURL, params);
		return json;
	}

}