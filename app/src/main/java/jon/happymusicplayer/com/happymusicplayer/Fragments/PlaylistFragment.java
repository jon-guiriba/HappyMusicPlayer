package jon.happymusicplayer.com.happymusicplayer.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import jon.happymusicplayer.com.happymusicplayer.R;

/**
 * Created by 80978448 on 10/28/2016.
 */

public class PlaylistFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return (LinearLayout)inflater.inflate(R.layout.fragment_playlist, container, false);
    }
}
