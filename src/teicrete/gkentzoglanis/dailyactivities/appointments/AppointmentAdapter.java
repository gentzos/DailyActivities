package teicrete.gkentzoglanis.dailyactivities.appointments;

import java.util.List;

import teicrete.gkentzoglanis.dailyactivities.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AppointmentAdapter extends ArrayAdapter<Appointments> {

	private List<Appointments> appList;
	private Context context;

	public AppointmentAdapter(List<Appointments> appList, Context ctx) {
		super(ctx, R.layout.list_row_appointment, appList);
		this.appList = appList;
		this.context = ctx;
	}

	public int getCount() {
		return appList.size();
	}

	public Appointments getItem(int position) {
		return appList.get(position);
	}

	public long getItemId(int position) {
		return appList.get(position).hashCode();
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;

		AppointmentHolder holder = new AppointmentHolder();

		// Verify that the convertView is not null
		if (convertView == null) {
			// This a new view we inflate the new layout
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.list_row_appointment, null);
			// Now we can fill the layout with the right values
			TextView sbjct = (TextView) v.findViewById(R.id.title4);
			TextView about = (TextView) v.findViewById(R.id.title5);

			holder.sbjcView = sbjct;
			holder.aboutView = about;

			v.setTag(holder);
		} else
			holder = (AppointmentHolder) v.getTag();

		Appointments ap = appList.get(position);
		holder.sbjcView.setText(ap.getSubject());
		holder.aboutView.setText("When: " + ap.getDay() + "/" + ap.getMonth()
				+ "/" + ap.getYear() + ", " + ap.getStartHour() + "\nWhere: "
				+ ap.getLocation() + "\nDescription: " + ap.getDescr());

		return v;
	}

	/* *********************************
	 * We use the holder pattern It makes the view faster and avoid finding the
	 * component *********************************
	 */

	private static class AppointmentHolder {
		public TextView sbjcView;
		public TextView aboutView;
	}
}