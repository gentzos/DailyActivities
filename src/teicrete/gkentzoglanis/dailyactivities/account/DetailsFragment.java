package teicrete.gkentzoglanis.dailyactivities.account;

import teicrete.gkentzoglanis.dailyactivities.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailsFragment extends Fragment {

	public static final String ARG_SECTION_NUMBER = "section_number";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_details, container,
				false);
		String[] key_array;

		TextView fnameView = (TextView) rootView.findViewById(R.id.fname2);
		TextView lnameView = (TextView) rootView.findViewById(R.id.lname2);
		TextView addressView = (TextView) rootView.findViewById(R.id.address2);
		TextView pnumberView = (TextView) rootView.findViewById(R.id.pnumber2);
		TextView crdtcardView = (TextView) rootView
				.findViewById(R.id.crdtcard2);
		TextView emailView = (TextView) rootView.findViewById(R.id.email2);
		TextView unameView = (TextView) rootView.findViewById(R.id.username2);
		TextView crtatView = (TextView) rootView.findViewById(R.id.crtat2);

		key_array = getArguments().getStringArray(ARG_SECTION_NUMBER);

		lnameView.setText(key_array[1]);
		addressView.setText(key_array[2]);
		emailView.setText(key_array[3]);
		unameView.setText(key_array[4]);
		crtatView.setText(key_array[5]);
		pnumberView.setText(key_array[6]);
		crdtcardView.setText(key_array[7]);
		fnameView.setText(key_array[8]);

		return rootView;
	}
	
}
