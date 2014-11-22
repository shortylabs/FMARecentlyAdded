package com.shortylabs.fmarecentlyadded.model;

import android.test.AndroidTestCase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


/**
 * Created by Jeri on 11/21/14.
 */
public class RecentlyAddedTrackTests  extends AndroidTestCase {

    public void testJson() {
        RecentlyAddedTrack expected3 = new RecentlyAddedTrack(111026l,
                "Sum + Difference [disquiet0130]",
                "Westy Reflector",
                "http://freemusicarchive.org/music/listen/34759ef2380544b99a980a121f46d0d30c0f89a6");
        expected3.setTrackFavorites(1);
        expected3.setTrackImageUrl("http://freemusicarchive.org/file/images/tracks/Track_-_20141116144351914");
        expected3.setTrackDownloads(10);
        expected3.setAlbumTitle("Particle Theory");
        expected3.setTrackBitRate(273883);
        expected3.setTrackDuration("01:59");
        expected3.setTrackListens(91);


        JSONObject jsonObject = readJsonFromFile("assets/recent.json");
        List<RecentlyAddedTrack> ratList = RecentlyAddedTrack.fromJson(jsonObject);
        assertNotNull(ratList);
        assertEquals(20, ratList.size());
        RecentlyAddedTrack rat3 = ratList.get(3);
        assertEquals(expected3.getTrackId(), rat3.getTrackId());
        assertEquals(expected3.getTrackTitle(), rat3.getTrackTitle());
        assertEquals(expected3.getArtistName(), rat3.getArtistName());
        assertEquals(expected3.getTrackFileUrl(), rat3.getTrackFileUrl());
        assertEquals(expected3.getAlbumTitle(), rat3.getAlbumTitle());
        assertEquals(expected3.getTrackBitRate(), rat3.getTrackBitRate());
        assertEquals(expected3.getTrackDownloads(), rat3.getTrackDownloads());
        assertEquals(expected3.getTrackFavorites(), rat3.getTrackFavorites());
        assertEquals(expected3.getTrackDuration(), rat3.getTrackDuration());
        assertEquals(expected3.getTrackImageUrl(), rat3.getTrackImageUrl());
        assertEquals(expected3.getTrackListens(), rat3.getTrackListens());

    }

    public void testMissingRequiredField() {
        JSONObject jsonObject = readJsonFromFile("assets/recent-missing-artist-name.json");
        List<RecentlyAddedTrack> ratList = RecentlyAddedTrack.fromJson(jsonObject);
        assertNotNull(ratList);
        assertEquals(19, ratList.size());

    }

    private JSONObject readJsonFromFile(String fileName) {

        JSONObject jsonObject = null;

        try
        {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                json.append(line);
            }
                jsonObject = new JSONObject(json.toString());

        }
        catch ( IOException e ) {
            System.err.println("Couldn't read test data file: " + fileName);
        }
        catch (JSONException e) {
            System.err.println("Couldn't parse JSON from test data file: " + fileName);
        }
        return jsonObject;
    }

}
