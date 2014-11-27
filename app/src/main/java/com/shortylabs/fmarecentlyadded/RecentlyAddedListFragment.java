package com.shortylabs.fmarecentlyadded;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.MediaController;

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
public class RecentlyAddedListFragment extends Fragment
        implements MediaController.MediaPlayerControl{

    private static String TAG = RecentlyAddedListFragment.class.getSimpleName();

    private RecentlyAddedListAdapter mAdapter;

    private long mTrackId = -1;

    private MediaPlayerService mMediaPlayerService;

    private boolean mBound;

    private FMAMediaController mMediaController;

    public RecentlyAddedListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }



    private void initMediaController() {
        mMediaController = new FMAMediaController(getActivity(), mMediaPlayerService) ;

//        mMediaController.setPrevNextListeners(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                playNext();
//            }
//        }, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                playPrev();
//            }
//        });
        mMediaController.setMediaPlayer(this);
        mMediaController.setAnchorView(ViewHolder.listview);
        mMediaController.setEnabled(true);
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

                if (mConnection == null){
                    mConnection = initServiceConnection();
                }
                getActivity().bindService(i, mConnection, Context.BIND_AUTO_CREATE);
            }
        });

        runService();


        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBound == true) {
            mBound = false;
            getActivity().unbindService(mConnection);
            mConnection = null;
        }
    }

    private ServiceConnection initServiceConnection() {
       return new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                MediaPlayerService.MediaPlayerServiceBinder binder = (MediaPlayerService.MediaPlayerServiceBinder) service;
                mMediaPlayerService = binder.getService();

                initMediaController();

                mBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mMediaPlayerService = null;
                mBound = false;
            }
        };

    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = initServiceConnection();

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


        if (mMediaPlayerService!= null && mMediaPlayerService.isPlaying() ) {
            mTrackId = mMediaPlayerService.getTrackId();
            mMediaController.show(0);

            if (mTrackId >=0) {
                int position = mAdapter.findPosition(mTrackId);
                if (ViewHolder.listview != null && position >= 0) {
                    ViewHolder.listview.setFocusableInTouchMode(true);
                    ViewHolder.listview.requestFocus();
                    ViewHolder.listview.setSelection(position);
                    ViewHolder.listview.setItemChecked(position, true);
                    ViewHolder.listview.setBackgroundColor(getResources().getColor(R.color.selected_track_background));
                    ViewHolder.listview.invalidate();
                    mAdapter.notifyDataSetChanged();
                }
            }

        }


    }

    public RecentlyAddedListAdapter getRecentlyAddedListAdapter() {
        return mAdapter;
    }



    //MediaPlayerControl

    @Override
    public void start() {
        if (mMediaPlayerService != null){
            mMediaPlayerService.play();
            mMediaController.show(0);
        }
    }

    @Override
    public void pause() {
        if (mMediaPlayerService != null){
            mMediaPlayerService.pause();
        }

    }

    @Override
    public int getDuration() {

        if (mMediaPlayerService != null){
            return mMediaPlayerService.getDuration();
        }
        return -1;
    }

    @Override
    public int getCurrentPosition() {
        if (mMediaPlayerService != null){
            return mMediaPlayerService.getPosition();
        }
        return -1;
    }

    @Override
    public void seekTo(int pos) {

        if (mMediaPlayerService != null){
            mMediaPlayerService.seek(pos);
        }

    }

    @Override
    public boolean isPlaying() {
        if (mMediaPlayerService != null){
            return mMediaPlayerService.isPlaying();
        }
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
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
                    if (MediaPlayerService.PREPARED.equals(data.getString(MediaPlayerService.STATE_KEY))) {
                        outerClass.get().start();
                    }
                }


            }
        }
    }


    protected static class ViewHolder{
        protected static ListView listview;
    }

}
