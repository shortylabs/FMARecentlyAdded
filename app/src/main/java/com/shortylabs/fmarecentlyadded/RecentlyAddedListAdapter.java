package com.shortylabs.fmarecentlyadded;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.shortylabs.fmarecentlyadded.model.RecentlyAddedTrack;
import com.shortylabs.fmarecentlyadded.service.MediaPlayerService;

import java.util.List;


/**
 * Created by Jeri on 11/20/14.
 */
public class RecentlyAddedListAdapter extends ArrayAdapter<RecentlyAddedTrack> {

    private List<RecentlyAddedTrack> list;
    private final Activity mActivity;

    public RecentlyAddedListAdapter(Activity activity, List<RecentlyAddedTrack> list) {
        super(activity, 0, list);
        this.mActivity = activity;
        this.list = list;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder view;

        /** Set data to your Views. */
        final RecentlyAddedTrack item = list.get(position);

        if(rowView == null)
        {


            // Get a new instance of the row layout view
            LayoutInflater inflater = mActivity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.recently_added_item, null);

            // Hold the view objects in an object, that way the don't need to be "re-  finded"
            view = new ViewHolder();
            view.track_info_textview= (TextView) rowView.findViewById(R.id.track_info_textview);

            view.play_button = (ImageButton) rowView.findViewById(R.id.play_button);


            view.play_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i= new Intent(mActivity, MediaPlayerService.class);
                    i.setAction(MediaPlayerService.ACTION_PLAY);

                    i.putExtra(MediaPlayerService.EXTRA_TRACK_URL, item.getTrackFileUrl());
                    i.putExtra(MediaPlayerService.EXTRA_TRACK_TITLE, item.getTrackTitle());
                    i.putExtra(MediaPlayerService.EXTRA_TRACK_ARTIST, item.getArtistName());

                    mActivity.startService(i);
                }
            });


            rowView.setTag(view);
        } else {
            view = (ViewHolder) rowView.getTag();
        }


        int formatId = R.string.format_track_entry;
        view.track_info_textview.setText(String.format(parent.getContext().getString(
                formatId,
                item.getArtistName(),
                item.getTrackTitle())));

//        rowView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                v.setSelected(!v.isSelected());
//            }
//        });

        return rowView;
    }

    protected static class ViewHolder{
        protected TextView track_info_textview;
        protected ImageButton play_button;
    }
}
