package com.shortylabs.fmarecentlyadded.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Jeri on 11/21/14.
 */
public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener {

    private static final String TAG = MediaPlayerService.class.getSimpleName();

    public static final String EXTRA_TRACK_URL = "extra_track_url";

    public static final String ACTION_PLAY = "com.shortylabs.fmarecentlyadded.service.PLAY";
    MediaPlayer mMediaPlayer = null;
    private boolean isPlaying=false;

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_PLAY)) {

            String url = null;
            if (intent.hasExtra(MediaPlayerService.EXTRA_TRACK_URL)) {
                url = intent.getStringExtra(MediaPlayerService.EXTRA_TRACK_URL) ;
            }

//            url = "http://freemusicarchive.org/music/listen/4e70998ddd2e84bd1a6bc24217e3f7bfce122186";
//            url = "http://freemusicarchive.org/music/Primavera_Sound/OM/Live_At_Primavera_Sound_2013_OM/OM_-_01_-_Sinai.mp3";
//            url = "http://freemusicarchive.org/music/download/1373693bb1b21fe2db1be5b4579ed440337e311f";

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(this);
            try {
                mMediaPlayer.setDataSource(url);
                mMediaPlayer.prepareAsync(); // might take long! (for buffering, etc)
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        stop();
    }


    private void play() {
        if (!isPlaying) {
            Log.w(TAG, "play()");
            mMediaPlayer.start();
            isPlaying=true;
        }
    }

    private void stop() {
        if (isPlaying) {
            Log.w(TAG, "stop()");
            mMediaPlayer.stop();
            isPlaying=false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /** Called when MediaPlayer is ready */
    public void onPrepared(MediaPlayer player) {
        play();
    }
}
