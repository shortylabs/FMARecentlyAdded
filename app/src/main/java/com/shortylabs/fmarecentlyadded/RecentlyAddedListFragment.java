package com.shortylabs.fmarecentlyadded;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.shortylabs.fmarecentlyadded.model.RecentlyAddedTrack;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class RecentlyAddedListFragment extends Fragment {

    public RecentlyAddedListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recently_added_list, container, false);
        ListView list = (ListView)rootView.findViewById(R.id.listView);


        // dummy data
        List<RecentlyAddedTrack> trackList = new ArrayList<RecentlyAddedTrack>();
        trackList.add( new RecentlyAddedTrack(1l, "narrow", "Le Matin"));
        trackList.add( new RecentlyAddedTrack(1l, "Lucie est malade", "Le Matin"));
        trackList.add( new RecentlyAddedTrack(1l, "She Turns To Dust", "Auto Bonfire"));
        trackList.add( new RecentlyAddedTrack(1l, "Living Inside My Computer", "Auto Bonfire"));
        trackList.add( new RecentlyAddedTrack(1l, "I Have A Feeling That I've Been Here Before", "Le Matin"));
        trackList.add( new RecentlyAddedTrack(1l, "Angry: A Love Song", "Auto Bonfire"));
        trackList.add( new RecentlyAddedTrack(1l, "Photograph", "Auto Bonfire"));
        trackList.add( new RecentlyAddedTrack(1l, "I Sleep Fine", "Auto Bonfire"));
        trackList.add( new RecentlyAddedTrack(1l, "Just Like Meryl Streep", "Auto Bonfire"));
        trackList.add( new RecentlyAddedTrack(1l, "Agro-Culture", "Auto Bonfire"));
        trackList.add( new RecentlyAddedTrack(1l, "What Am I Looking For?", "Auto Bonfire"));
        trackList.add( new RecentlyAddedTrack(1l, "Moved By Joy", "Sondra Sun-Odeon"));
        trackList.add( new RecentlyAddedTrack(1l, "Oaks", "Sondra Sun-Odeon"));
        trackList.add( new RecentlyAddedTrack(1l, "Paradise", "Sondra Sun-Odeon"));
        trackList.add( new RecentlyAddedTrack(1l, "Belonging", "Sondra Sun-Odeon"));

        RecentlyAddedListAdapter adapter = new RecentlyAddedListAdapter(getActivity(),
                trackList);

        list.setAdapter(adapter);


        return rootView;
    }
}
