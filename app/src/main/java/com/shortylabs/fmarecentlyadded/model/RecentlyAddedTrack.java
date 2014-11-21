package com.shortylabs.fmarecentlyadded.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeri on 11/20/14.
 */
public class RecentlyAddedTrack {

    static final private String TAG = RecentlyAddedTrack.class.getSimpleName();

    static final private String NULL_STRING = "null";

    private long trackId;
    private String trackTitle;
    private String artistName;
    private String trackImageUrl;
    private String trackListenUrl;
    private String trackDuration ;
    private int trackBitRate;
    private String albumTitle;
    private int trackListens;
    private int trackDownloads;
    private int trackFavorites;

    public RecentlyAddedTrack(long trackId, String trackTitle, String artistName, String trackListenUrl) {
        this.trackId = trackId;
        this.trackTitle = trackTitle;
        this.artistName = artistName;
        this.trackListenUrl = trackListenUrl;
    }

    public long getTrackId() {
        return trackId;
    }

    public void setTrackId(long trackId) {
        this.trackId = trackId;
    }

    public String getTrackTitle() {
        return trackTitle;
    }

    public void setTrackTitle(String trackTitle) {
        this.trackTitle = trackTitle;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getTrackImageUrl() {
        return trackImageUrl;
    }

    public void setTrackImageUrl(String trackImageUrl) {
        this.trackImageUrl = trackImageUrl;
    }

    public String getTrackListenUrl() {
        return trackListenUrl;
    }

    public void setTrackListenUrl(String trackListenUrl) {
        this.trackListenUrl = trackListenUrl;
    }

    public String getTrackDuration() {
        return trackDuration;
    }

    public void setTrackDuration(String trackDuration) {
        this.trackDuration = trackDuration;
    }

    public int getTrackBitRate() {
        return trackBitRate;
    }

    public void setTrackBitRate(int trackBitRate) {
        this.trackBitRate = trackBitRate;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public int getTrackListens() {
        return trackListens;
    }

    public void setTrackListens(int trackListens) {
        this.trackListens = trackListens;
    }

    public int getTrackDownloads() {
        return trackDownloads;
    }

    public void setTrackDownloads(int trackDownloads) {
        this.trackDownloads = trackDownloads;
    }

    public int getTrackFavorites() {
        return trackFavorites;
    }

    public void setTrackFavorites(int trackFavorites) {
        this.trackFavorites = trackFavorites;
    }

    @Override
    public String toString() {
        return "RecentlyAddedTrack{" +
                "trackId=" + trackId +
                ", trackTitle='" + trackTitle + '\'' +
                ", artistName='" + artistName + '\'' +
                ", trackImageUrl='" + trackImageUrl + '\'' +
                ", trackListenUrl='" + trackListenUrl + '\'' +
                ", trackDuration='" + trackDuration + '\'' +
                ", trackBitRate=" + trackBitRate +
                ", albumTitle='" + albumTitle + '\'' +
                ", trackListens=" + trackListens +
                ", trackDownloads=" + trackDownloads +
                ", trackFavorites=" + trackFavorites +
                '}';
    }

    public static List<RecentlyAddedTrack> fromJson(JSONObject jsonObject) {

        if (jsonObject == null) {
            return null;
        }

        List<RecentlyAddedTrack> tracks = new ArrayList<>();

        JSONArray jsonTracks = jsonObject.optJSONArray("aTracks");
        if (jsonTracks == null) {
            return null;
        }

        for (int i = 0; i < jsonTracks.length(); i++) {
            JSONObject jsonTrack = jsonTracks.optJSONObject(i);
            if (jsonTrack == null) {
                continue;
            }


            long trackId;
            String trackTitle;
            String artistName;
            String trackListenUrl;

            try {
                trackId = jsonTrack.getLong("track_id");
                trackTitle = jsonTrack.getString("track_title");
                artistName = jsonTrack.getString("artist_name");
                trackListenUrl = jsonTrack.getString("track_listen_url");
            } catch (JSONException e) {
                Log.e(TAG, "Required field from track data is not available, skipping...");
                continue;
            }

            if (NULL_STRING.equals(trackTitle) ||
                    NULL_STRING.equals(artistName) ||
                    NULL_STRING.equals(trackListenUrl)){

                Log.e(TAG, "Required field from track data is not available, skipping...");
                continue;

            }

            RecentlyAddedTrack rat = new RecentlyAddedTrack(trackId, trackTitle, artistName, trackListenUrl);

            rat.setTrackImageUrl(jsonTrack.optString("track_image_file"));
            rat.setTrackDuration(jsonTrack.optString("track_duration"));
            rat.setTrackBitRate(jsonTrack.optInt("track_bit_rate"));
            rat.setAlbumTitle(jsonTrack.optString("album_title"));
            rat.setTrackListens(jsonTrack.optInt("track_listens"));
            rat.setTrackDownloads(jsonTrack.optInt("track_downloads"));
            rat.setTrackFavorites(jsonTrack.optInt("track_favorites"));
            tracks.add(rat);

        }

        return tracks;
    }
}
