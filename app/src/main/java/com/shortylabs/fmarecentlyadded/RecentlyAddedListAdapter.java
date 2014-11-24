package com.shortylabs.fmarecentlyadded;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.shortylabs.fmarecentlyadded.model.RecentlyAddedTrack;
import com.shortylabs.fmarecentlyadded.service.MediaPlayerService;

import java.lang.ref.WeakReference;
import java.util.List;


/**
 * Created by Jeri on 11/20/14.
 */
public class RecentlyAddedListAdapter extends ArrayAdapter<RecentlyAddedTrack> {

    private static final String TAG = RecentlyAddedListAdapter.class.getSimpleName();

    private List<RecentlyAddedTrack> list;
    private final RecentlyAddedListFragment mRecentlyAddedListFragment;
    private String mMediaPlayerState;
    private ListView mListView;


    /**
     * Instantiate the MessengerHandler, passing in the
     * Activity to be stored as a WeakReference
     */
    private MessengerHandler handler;

    public RecentlyAddedListAdapter(RecentlyAddedListFragment fragment, List<RecentlyAddedTrack> list) {
        super(fragment.getActivity(), 0, list);
        this.mRecentlyAddedListFragment = fragment;
        this.list = list;
        this.handler =  new MessengerHandler(fragment);
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (parent instanceof ListView) {
            mListView = (ListView) parent;
        }
        View rowView = convertView;
        ViewHolder view;
        final Context context = parent.getContext();

        /** Set data to your Views. */
        final RecentlyAddedTrack item = list.get(position);

        if(rowView == null)
        {
            // Get a new instance of the row layout view
            LayoutInflater inflater = mRecentlyAddedListFragment.getActivity().getLayoutInflater();
            rowView = inflater.inflate(R.layout.recently_added_item, null);


            // Hold the view objects in an object, that way the don't need to be "re-finded"
            view = new ViewHolder();
            view.track_info_textview= (TextView) rowView.findViewById(R.id.track_info_textview);




            rowView.setTag(view);
        } else {
            view = (ViewHolder) rowView.getTag();
        }


//        if (mLastPlayedTrackId == item.getTrackId()) {
//            rowView.setSelected(true);
//            if (MediaPlayerService.PLAYING.equals(mMediaPlayerState)) {
//                view.play_button.setImageDrawable(parent.getResources().getDrawable(R.drawable.ic_action_pause));
//            }
//            else {
//                view.play_button.setImageDrawable(parent.getResources().getDrawable(R.drawable.ic_media_play));
//            }
//        }

        int formatId = R.string.format_track_entry;
        view.track_info_textview.setText(String.format(parent.getContext().getString(
                formatId,
                item.getArtistName(),
                item.getTrackTitle())));


        return rowView;
    }


    /**
     * This is the handler used for handling messages sent by a
     * Messenger.
     */
    static class MessengerHandler extends Handler {

        // A weak reference to the enclosing class
        WeakReference<RecentlyAddedListFragment> outerClass;

        /**
         * A constructor that gets a weak reference to the enclosing class.
         * We do this to avoid memory leaks during Java Garbage Collection.
         *
         * @ see https://groups.google.com/forum/#!msg/android-developers/1aPZXZG6kWk/lIYDavGYn5UJ
         */
        public MessengerHandler(RecentlyAddedListFragment outer) {
            outerClass = new WeakReference<RecentlyAddedListFragment>(outer);
        }

        // Handle any messages that get sent to this Handler
        @Override
        public void handleMessage(Message msg) {

            Log.d(TAG, "handleMessage");

            final RecentlyAddedListFragment recentlyAddedListFragment = outerClass.get();

            // If RecentlyAddedListFragment hasn't been garbage collected
            // (closed by user), proceed.
            if (recentlyAddedListFragment != null && recentlyAddedListFragment.getActivity() != null ) {

                // Extract the data from Message, which is in the form
                // of a Bundle that can be passed across processes.
                Bundle data = msg.getData();

                // Extract the result from the Bundle.
                final String mediaPlayerState = data.getString(MediaPlayerService.STATE_KEY);
                Log.d(TAG, "MediaPlayer state: " + mediaPlayerState);

                // is this necessary when using a foreground service?
                recentlyAddedListFragment.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
//                        notifyDataSetChanged();
                        RecentlyAddedListAdapter adapter = recentlyAddedListFragment.getRecentlyAddedListAdapter();
                        if (adapter != null) {
                            adapter.mMediaPlayerState = mediaPlayerState;
                            adapter.notifyDataSetChanged();
                        }
                    }
                });


            }
        }
    }

    protected static class ViewHolder{
        protected TextView track_info_textview;
    }
}
