package com.shortylabs.fmarecentlyadded.model;

/**
 * Created by Jeri on 11/20/14.
 */
public class RecentlyAddedTrack {

    private long trackId;
    private String trackTitle;
    private String artistName;

    public RecentlyAddedTrack(long trackId, String trackTitle, String artistName) {
        this.trackId = trackId;
        this.trackTitle = trackTitle;
        this.artistName = artistName;
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
}
