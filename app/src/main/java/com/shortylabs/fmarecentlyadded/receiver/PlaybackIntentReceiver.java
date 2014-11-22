package com.shortylabs.fmarecentlyadded.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Jeri on 11/22/14.
 */
public class PlaybackIntentReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(
                android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            // signal your service to stop playback
            // (via an Intent, for instance)
        }

    }
}
