package com.shortylabs.fmarecentlyadded.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jeri on 11/20/14.
 */
public class RecentlyAddedService extends IntentService {

    private static final String RECENTLY_ADDED_SERVICE = "recentlyAddedService";
    private static final String TAG = RecentlyAddedService.class.getSimpleName();

    /**
     * The key used to store/retrieve a Messenger extra from a Bundle.
     */
    public static final String RECENTLY_ADDED_SERVICE_MESSENGER_KEY = "RECENTLY_ADDED_SERVICE_MESSENGER";


    /**
     * The key used to store/retrieve a file's pathname from a Bundle.
     */
    public static final String RESULT = "result";


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public RecentlyAddedService() {
        super(RECENTLY_ADDED_SERVICE);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Messenger messenger;
        if (intent.hasExtra(RECENTLY_ADDED_SERVICE_MESSENGER_KEY)) {
            messenger = (Messenger) intent.getExtras().get(RECENTLY_ADDED_SERVICE_MESSENGER_KEY);
        }
        else {
            Log.e(TAG, "Expected Messenger extra not found in intent");
            return;
        }
        String url = "http://freemusicarchive.org/recent.json";

        StringBuilder builder = new StringBuilder();
        HttpURLConnection con;
        try {
            con = (HttpURLConnection) ( new URL(url)).openConnection();
            con.setRequestMethod("GET");
            con.connect();
            InputStream is = con.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendResult(builder.toString(), messenger);
    }

    /**
     *	Use the provided Messenger to send a Message to a Handler in
     *	another process.
     *
     * 	The provided string, result, should be put into a Bundle
     * 	and included with the sent Message.
     */
    public static void sendResult(String result,
                                  Messenger messenger) {
        Log.d(TAG, "sendResult returning RESULT");
        Message msg = Message.obtain();
        Bundle data = new Bundle();
        data.putString(RESULT,
                result);

        // Make the Bundle the "data" of the Message.
        msg.setData(data);

        try {
            // Send the Message back to the client Activity.
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
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
                                    Handler handler) {

        Messenger messenger = new Messenger(handler);

        Intent intent = new Intent(context,
                RecentlyAddedService.class);
        intent.putExtra(RECENTLY_ADDED_SERVICE_MESSENGER_KEY,
                messenger);

        return intent;

    }
}
