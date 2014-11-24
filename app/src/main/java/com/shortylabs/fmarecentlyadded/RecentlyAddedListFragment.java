package com.shortylabs.fmarecentlyadded;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.shortylabs.fmarecentlyadded.model.RecentlyAddedTrack;
import com.shortylabs.fmarecentlyadded.service.MediaPlayerService;
import com.shortylabs.fmarecentlyadded.service.RecentlyAddedService;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class RecentlyAddedListFragment extends Fragment {

    private static String TAG = RecentlyAddedListFragment.class.getSimpleName();

    private RecentlyAddedListAdapter mAdapter;

    private long mTrackId = -1;

    public RecentlyAddedListFragment() {
    }

    /**
     * Instantiate the MessengerHandler, passing in the
     * Activity to be stored as a WeakReference
     */
    MessengerHandler handler = new MessengerHandler(this);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();

        mTrackId = -1;
        if (intent != null && intent.hasExtra(MediaPlayerService.EXTRA_TRACK_ID)) {
            mTrackId = intent.getLongExtra(MediaPlayerService.EXTRA_TRACK_ID, 0);

        }

        View rootView = inflater.inflate(R.layout.fragment_recently_added_list, container, false);
        ViewHolder.listview =  (ListView)rootView.findViewById(R.id.listView);
        ViewHolder.listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        ViewHolder.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);

                // if isPlaying, stop
                RecentlyAddedTrack item = mAdapter.getItem(position);

                // else, play
                // send a handler to get callbacks when the playback is paused, stopped, or completed
                Intent i = MediaPlayerService.makeIntent(getActivity(), handler, item.getTrackId(), item.getTrackFileUrl(),
                        item.getTrackTitle(), item.getArtistName());

                getActivity().startService(i);
            }
        });

        runService();

        return rootView;
    }


    public void runService() {

        Intent intent =
                RecentlyAddedService.makeIntent(getActivity(),
                        handler);

        getActivity().startService(intent);
    }

    private void showResults(String json) {

        Log.d(TAG, json);
        List<RecentlyAddedTrack> trackList;
        try {
            trackList = RecentlyAddedTrack.fromJson(new JSONObject(json));
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            return;
        }

        mAdapter = new RecentlyAddedListAdapter(this,
                trackList);

        ViewHolder.listview.setAdapter(mAdapter);

        if (mTrackId >=0) {
            int position = mAdapter.findPosition(mTrackId);
            if (ViewHolder.listview != null && position >= 0) {
                ViewHolder.listview.requestFocus();
                ViewHolder.listview.setSelection(position);
                mAdapter.notifyDataSetChanged();
            }
        }

    }

    public RecentlyAddedListAdapter getRecentlyAddedListAdapter() {
        return mAdapter;
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


            final RecentlyAddedListFragment recentlyAddedListFragment = outerClass.get();

            // If RecentlyAddedListFragment hasn't been garbage collected
            // (closed by user), proceed.
            if (recentlyAddedListFragment != null && recentlyAddedListFragment.getActivity() != null) {

                // Extract the data from Message, which is in the form
                // of a Bundle that can be passed across processes.
                Bundle data = msg.getData();

                if (data.containsKey(RecentlyAddedService.EXTRA_RESULTS_KEY)) {
                    Log.d(TAG, "handleMessage: " + RecentlyAddedService.EXTRA_RESULTS_KEY);
                    // Extract the result from the Bundle.
                    final String jsonResult = data.getString(RecentlyAddedService.EXTRA_RESULTS_KEY);
                    Log.d(TAG, "Result: " + jsonResult);
                    recentlyAddedListFragment.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            recentlyAddedListFragment.showResults(jsonResult);
                        }
                    });
                }
                else if (data.containsKey(MediaPlayerService.STATE_KEY)) {
                    Log.d(TAG, "handleMessage: " + MediaPlayerService.STATE_KEY);
                }


            }
        }
    }


    protected static class ViewHolder{
        protected static ListView listview;
    }

}
