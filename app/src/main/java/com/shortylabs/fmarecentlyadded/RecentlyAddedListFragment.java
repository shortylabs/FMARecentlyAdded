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
import android.widget.ListView;

import com.shortylabs.fmarecentlyadded.model.RecentlyAddedTrack;
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
        View rootView = inflater.inflate(R.layout.fragment_recently_added_list, container, false);
        ViewHolder.listview =  (ListView)rootView.findViewById(R.id.listView);
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


        RecentlyAddedListAdapter adapter = new RecentlyAddedListAdapter(getActivity(),
                trackList);

        ViewHolder.listview.setAdapter(adapter);

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
            if (recentlyAddedListFragment != null) {

                // Extract the data from Message, which is in the form
                // of a Bundle that can be passed across processes.
                Bundle data = msg.getData();

                // Extract the result from the Bundle.
                final String jsonResult = data.getString("result");
                Log.d(TAG, "Result: " + jsonResult);
                recentlyAddedListFragment.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        recentlyAddedListFragment.showResults(jsonResult);
                    }
                });


            }
        }
    }


    protected static class ViewHolder{
        protected static ListView listview;
    }

}
