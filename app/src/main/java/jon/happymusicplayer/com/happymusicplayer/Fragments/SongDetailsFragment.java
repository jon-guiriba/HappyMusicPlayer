package jon.happymusicplayer.com.happymusicplayer.Fragments;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import jon.happymusicplayer.com.happymusicplayer.R;

/**
 * Created by 80978448 on 10/28/2016.
 */

public class SongDetailsFragment extends Fragment {
    private TextView tvTitle;
    private TextView tvArtist;
    private TextView tvAlbum;
    private TextView tvGenre;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_song_details, container, false);
        tvTitle = (TextView) view.findViewById(R.id.song_details_title);
        tvArtist = (TextView) view.findViewById(R.id.song_details_artist);
        tvAlbum = (TextView) view.findViewById(R.id.song_details_album);
        tvGenre = (TextView) view.findViewById(R.id.song_details_genre);
        return view;
    }

    public static SongDetailsFragment newInstance() {
        return new SongDetailsFragment();
    }

    public void update(String title, String artist, String album, String genre) {
        tvTitle.setText(title);
        tvArtist.setText(artist);
        tvAlbum.setText(album);
        tvGenre.setText(genre);
    }
}
