package jon.happymusicplayer.com.happymusicplayer.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import jon.happymusicplayer.com.happymusicplayer.Fragments.PlaylistFragment;
import jon.happymusicplayer.com.happymusicplayer.Fragments.SongDetailsFragment;
import jon.happymusicplayer.com.happymusicplayer.data.AppMusicPlayer;

/**
 * Created by 80978448 on 10/28/2016.
 */

public class AppPagerAdapter extends FragmentPagerAdapter {


    private static final int FRAGMENTS_COUNT = 2;
    private final AppMusicPlayer player;

    public AppPagerAdapter(FragmentManager fm, AppMusicPlayer player) {
        super(fm);
        this.player = player;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                PlaylistFragment playlistFragment = PlaylistFragment.newInstance();
                return playlistFragment;

            case 1:
                SongDetailsFragment songDetailsFragment = SongDetailsFragment.newInstance();
                return songDetailsFragment;

            default:
                return null;
        }
    }


    @Override
    public int getItemPosition(Object object) {
        String objectClass = object.getClass().getName();
        boolean isObjectPlaylistFragment = objectClass.equals(PlaylistFragment.class.getName());
        boolean isSongDetailsFragment = objectClass.equals(SongDetailsFragment.class.getName());

        if(isObjectPlaylistFragment){
            ((PlaylistFragment) object).update(player.getPlaylist());
        } else if (isSongDetailsFragment) {
            // TODO
        }

        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return FRAGMENTS_COUNT;
    }
}
