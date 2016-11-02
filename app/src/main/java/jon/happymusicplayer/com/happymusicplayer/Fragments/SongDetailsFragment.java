package jon.happymusicplayer.com.happymusicplayer.Fragments;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import jon.happymusicplayer.com.happymusicplayer.R;

/**
 * Created by 80978448 on 10/28/2016.
 */

public class SongDetailsFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_song_details, container, false);
        ImageView image = (ImageView) view.findViewById(R.id.albumArt);
        image.setBackground(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.greenyYellow)));

        return view;
    }

    public static SongDetailsFragment newInstance() {
        return new SongDetailsFragment();
    }
}
