package com.shortylabs.fmarecentlyadded;


import android.content.Context;
import android.util.Log;
import android.widget.MediaController;

import com.shortylabs.fmarecentlyadded.service.MediaPlayerService;

/**
 * Created by Jeri on 11/26/14.
 */
public class FMAMediaController  extends MediaController{
    private static final String TAG = FMAMediaController.class.getSimpleName();

    private MediaPlayerService mMediaPlayerService;

    public FMAMediaController(Context context, MediaPlayerService service) {
        super(context);
        Log.d(TAG, "MediaPlayerService service: " + service);
        this.mMediaPlayerService = service;
    }

    @Override
    public void hide() {
        if (mMediaPlayerService == null) {
            Log.d(TAG, "mMediaPlayerService is null, hiding control");
            super.hide();
            return;
        }

        int duration = mMediaPlayerService.getDuration();
        int position = mMediaPlayerService.getPosition();
        Log.d(TAG, "hide, duration: " + duration + ", position: " + position);
        if (duration <= position)  {
            super.hide();
        }
    }

    public void hideController() {

        Log.d(TAG, "hideController calling super.hide()");
        super.hide();
    }
}
