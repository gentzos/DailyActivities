package teicrete.gkentzoglanis.dailyactivities;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainListAdapter extends ArrayAdapter<MainListRowItem> {

	Context context;

	public MainListAdapter(Context context, int resourceId, List<MainListRowItem> items) {
		super(context, resourceId, items);
		this.context = context;
	}

	public class ViewHolder {
		ImageView image;
		TextView title;
		RelativeLayout card;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		MainListRowItem rowItem = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_row_main, null);
			holder = new ViewHolder();
			holder.card = (RelativeLayout) convertView.findViewById(R.id.mainlist_card);
			holder.image = (ImageView) convertView
					.findViewById(R.id.mainlist_image);
			holder.title = (TextView) convertView.findViewById(R.id.mainlist_title);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.image.setImageResource(rowItem.getImageId());
		holder.title.setText(rowItem.getTitle());

		return convertView;
	}
	
}
