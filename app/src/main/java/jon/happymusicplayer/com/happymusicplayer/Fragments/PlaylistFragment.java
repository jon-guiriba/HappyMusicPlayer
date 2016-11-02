package jon.happymusicplayer.com.happymusicplayer.Fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.R;
import jon.happymusicplayer.com.happymusicplayer.data.models.SongModel;

/**
 * Created by 80978448 on 10/28/2016.
 */

public class PlaylistFragment extends Fragment {

    private ListView lvPlaylist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        lvPlaylist = (ListView) view.findViewById(R.id.lvCurrentPlaylist);

        return view;
    }

    public static PlaylistFragment newInstance() {
        PlaylistFragment fragment = new PlaylistFragment();
        return fragment;
    }

    public void update(List<SongModel> playlist) {
        if (lvPlaylist == null) return;

        ArrayAdapter<SongModel> playlistAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.current_playlist_item,
                playlist
        );

        lvPlaylist.setAdapter(playlistAdapter);
    }


}
