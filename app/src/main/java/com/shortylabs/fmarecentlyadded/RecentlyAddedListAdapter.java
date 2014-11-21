package com.shortylabs.fmarecentlyadded;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shortylabs.fmarecentlyadded.model.RecentlyAddedTrack;

import java.util.List;


/**
 * Created by Jeri on 11/20/14.
 */
public class RecentlyAddedListAdapter extends ArrayAdapter<RecentlyAddedTrack> {

    private List<RecentlyAddedTrack> list;
    private final Activity activity;

    public RecentlyAddedListAdapter(Activity activity, List<RecentlyAddedTrack> list) {
        super(activity, 0, list);
        this.activity = activity;
        this.list = list;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder view;

        if(rowView == null)
        {
            // Get a new instance of the row layout view
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.recently_added_item, null);

            // Hold the view objects in an object, that way the don't need to be "re-  finded"
            view = new ViewHolder();
            view.track_info_textview= (TextView) rowView.findViewById(R.id.track_info_textview);

            rowView.setTag(view);
        } else {
            view = (ViewHolder) rowView.getTag();
        }

        /** Set data to your Views. */
        RecentlyAddedTrack item = list.get(position);
        view.track_info_textview.setText(item.getArtistName() + " - " + item.getTrackTitle());

        return rowView;
    }

    protected static class ViewHolder{
        protected TextView track_info_textview;
    }
}
