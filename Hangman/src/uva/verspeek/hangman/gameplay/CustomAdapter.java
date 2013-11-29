package uva.verspeek.hangman.gameplay;

import java.util.ArrayList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

public class CustomAdapter extends BaseAdapter {
	private ArrayList<Button> mButtons = null;

	public CustomAdapter(ArrayList<Button> b) {
		mButtons = b;
	}

	@Override
	public int getCount() {
		return mButtons.size();
	}

	@Override
	public Object getItem(int position) {
		return (Object) mButtons.get(position);
	}

	@Override
	public long getItemId(int position) {
		// in our case position and id are synonymous
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Button button;
		if (convertView == null) {
			button = mButtons.get(position);
		} else {
			button = (Button) convertView;
		}
		return button;
	}
}
