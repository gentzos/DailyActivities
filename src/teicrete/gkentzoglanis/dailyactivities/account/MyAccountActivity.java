package teicrete.gkentzoglanis.dailyactivities.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import teicrete.gkentzoglanis.dailyactivities.R;
import teicrete.gkentzoglanis.dailyactivities.library.DatabaseHandler;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class MyAccountActivity extends FragmentActivity {
	
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;

	private ArrayList<HashMap<String, String>> test = new ArrayList<HashMap<String, String>>();
	private HashMap<String, String> m = new HashMap<String, String>();
	private HashMap<String, String> user = new HashMap<String, String>();
	private String[] strArr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_account);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
//		getActionBar().setBackgroundDrawable(
//				new ColorDrawable(Color.parseColor("#839c43")));
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#455a64")));

		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		user = db.getUserDetails();

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

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.my_account, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure.
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = null;
			if (position == 0) {
				fragment = new DetailsFragment();
				Bundle args = new Bundle();
				args.putStringArray(DetailsFragment.ARG_SECTION_NUMBER, strArr);
				fragment.setArguments(args);
			}
			if (position == 1) {
				fragment = new EditFragment();
				Bundle args = new Bundle();
				args.putStringArray(EditFragment.ARG_SECTION_NUMBER, strArr);
				fragment.setArguments(args);
			}
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_myac_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_myac_section2).toUpperCase(l);
			}
			return null;
		}
	}
}
