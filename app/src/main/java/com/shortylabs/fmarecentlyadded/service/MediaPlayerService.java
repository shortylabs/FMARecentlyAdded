package com.shortylabs.fmarecentlyadded.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.shortylabs.fmarecentlyadded.RecentlyAddedListActivity;

import java.io.IOException;

/**
 * Created by Jeri on 11/21/14.
 */
public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = MediaPlayerService.class.getSimpleName();

    /**
     * key to bundle extra containing Messenger
     */
    public static final String MEDIA_PLAYER_SERVICE_MESSENGER_KEY = "MEDIA_PLAYER_SERVICE_MESSENGER";

    private static final int NOTIFICATION_ID = 1;   // must not be zero

    private static final String WIFI_LOCK = "wifi_lock";
    private WifiManager.WifiLock mWifiLock;

    public static final String EXTRA_TRACK_ID = "extra_track_id";
    public static final String EXTRA_TRACK_URL = "extra_track_url";
    public static final String EXTRA_TRACK_TITLE = "extra_track_title";
    public static final String EXTRA_TRACK_ARTIST = "extra_track_artist";

    public static final String ACTION_PLAY = "com.shortylabs.fmarecentlyadded.service.PLAY";
    public static final String ACTION_PAUSE = "com.shortylabs.fmarecentlyadded.service.PAUSE";
    public static final String ACTION_STOP = "com.shortylabs.fmarecentlyadded.service.STOP";

    private MediaPlayer mMediaPlayer = null;
    private long mTrackId;
    private String mTrackUrl;
    private String mTrackTitle;
    private String mTrackArtist;
    private Messenger mMessenger;

    // states
    public static final String STATE_KEY = "state";
    public static final String COMPLETED = "completed";
    public static final String STOPPED = "stopped";
    public static final String PAUSED = "paused";
    public static final String PLAYING = "playing";
    public static final String ERROR = "error";

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_PLAY)) {

            Log.d(TAG, "onStartCommand - ACTION_PLAY");


            if (intent.hasExtra(MEDIA_PLAYER_SERVICE_MESSENGER_KEY)) {
                mMessenger = (Messenger) intent.getExtras().get(MEDIA_PLAYER_SERVICE_MESSENGER_KEY);
            } else {
                Log.e(TAG, "Expected Messenger extra not found in intent");
            }

            if (intent.hasExtra(MediaPlayerService.EXTRA_TRACK_ID)) {
                mTrackId = intent.getLongExtra(MediaPlayerService.EXTRA_TRACK_ID, 0l);
            }

            if (intent.hasExtra(MediaPlayerService.EXTRA_TRACK_URL)) {
                mTrackUrl = intent.getStringExtra(MediaPlayerService.EXTRA_TRACK_URL);
            }

            if (intent.hasExtra(MediaPlayerService.EXTRA_TRACK_TITLE)) {
                mTrackTitle = intent.getStringExtra(MediaPlayerService.EXTRA_TRACK_TITLE);
            }

            if (intent.hasExtra(MediaPlayerService.EXTRA_TRACK_ARTIST)) {
                mTrackArtist = intent.getStringExtra(MediaPlayerService.EXTRA_TRACK_ARTIST);
            }


            init();

        }
        else if (intent.getAction().equals(ACTION_PAUSE)) {

        }
        else if (intent.getAction().equals(ACTION_STOP)) {

        }

        return START_NOT_STICKY;
    }

    private void init() {

        Log.d(TAG, "init");

        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            stop();
        }

        Intent activityIntent = new Intent(getApplicationContext(), RecentlyAddedListActivity.class);
        activityIntent.putExtra(EXTRA_TRACK_ID, mTrackId);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);


        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
                );
        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(mTrackTitle)
                .setContentText(mTrackArtist)    // 2nd line of text
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setOngoing(true)
                .setContentIntent(pi)
                .build();


        startForeground(NOTIFICATION_ID, notification);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.e(TAG, "Could not get audio focus, returning...");
            return;
        }


        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        mWifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, WIFI_LOCK);
        mWifiLock.acquire();

        try {
            mMediaPlayer.setDataSource(mTrackUrl);
            mMediaPlayer.prepareAsync(); // might take long! (for buffering, etc)
        } catch (IOException | IllegalArgumentException e) {
            Log.e(TAG, e.getMessage());
        }

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        stop();
        super.onDestroy();
    }

    /**
     * Implement MediaPlayer.OnErrorListener
     *
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {


        Log.d(TAG, "onError");
        // ... react appropriately ...
        // The MediaPlayer has moved to the Error state, must be reset!
        mMediaPlayer.reset();
        sendStateMessage(ERROR);
        stop();
        // call OnCompletionListener
        return false;
    }


    private void play() {
        Log.w(TAG, "play()");
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            Log.w(TAG, "play() - starting player");
            mMediaPlayer.start();
            sendStateMessage(PLAYING);
        }
    }

    private void stop() {
        Log.w(TAG, "stop()");
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            Log.w(TAG, "stop() - stopping player");
            mMediaPlayer.stop();
            sendStateMessage(STOPPED);
        }
        release();
    }

    private void release() {

        Log.w(TAG, "release()");

        if (mMediaPlayer != null) {

            Log.w(TAG, "release() - releasing player");
            mMediaPlayer.release();
            mMediaPlayer = null;
            mWifiLock.release();
            stopForeground(true);
        }
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Called when MediaPlayer is ready
     */
    public void onPrepared(MediaPlayer player) {
        Log.w(TAG, "onPrepared()");
        play();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.w(TAG, "onCompletion()");
        release();
        sendStateMessage(COMPLETED);

    }

    private void sendStateMessage(String state) {

        Message msg = Message.obtain();
        Bundle data = new Bundle();
        data.putString(STATE_KEY,  state);

        // Make the Bundle the "data" of the Message.
        msg.setData(data);

        try {
            // Send the Message back to the client Activity.
            mMessenger.send(msg);
        }

        catch(RemoteException e){
            e.printStackTrace();
        }

    }

    /**
     * Implement AudioManager.OnAudioFocusChangeListener
     * @param focusChange
     */
    @Override
    public void onAudioFocusChange(int focusChange) {

        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                Log.w(TAG, "AUDIOFOCUS_GAIN");
                // resume playback
                if (mMediaPlayer == null) {
                    init();

                }
                else if (!mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                }
                mMediaPlayer.setVolume(1.0f, 1.0f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                Log.w(TAG, "AUDIOFOCUS_LOSS");
                // Lost focus for an unbounded amount of time: stop playback and release media player
                stop();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Log.w(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mMediaPlayer.isPlaying()){
                    mMediaPlayer.pause();
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.w(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.setVolume(0.1f, 0.1f);
                }
                break;
        }

    }

    /**
     * Make an intent that will start this service if supplied to
     * startService() as a parameter.
     *
     * @param context		The context of the calling component.
     * @param handler		The handler that the service should
     *                          use to respond with a result
     *
     */
    public static Intent makeIntent(Context context,
                                    Handler handler,
                                    long trackId,
                                    String trackUrl,
                                    String trackTitle,
                                    String trackArtist) {

        Messenger messenger = new Messenger(handler);

        Intent i = new Intent(context,
                MediaPlayerService.class);
        i.putExtra(MEDIA_PLAYER_SERVICE_MESSENGER_KEY,
                messenger);

        i.setAction(MediaPlayerService.ACTION_PLAY);

        i.putExtra(MediaPlayerService.EXTRA_TRACK_ID, trackId);
        i.putExtra(MediaPlayerService.EXTRA_TRACK_URL, trackUrl);
        i.putExtra(MediaPlayerService.EXTRA_TRACK_TITLE, trackTitle);
        i.putExtra(MediaPlayerService.EXTRA_TRACK_ARTIST, trackArtist);

        return i;

    }
}
