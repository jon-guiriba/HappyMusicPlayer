package jon.happymusicplayer.com.happymusicplayer.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import jon.happymusicplayer.com.happymusicplayer.Fragments.PlaylistFragment;
import jon.happymusicplayer.com.happymusicplayer.Fragments.SongDetailsFragment;
import jon.happymusicplayer.com.happymusicplayer.data.AppMusicPlayer;

/**
 * Created by 80978448 on 10/28/2016.
 */

public class AppPagerAdapter extends FragmentPagerAdapter {
    private static final int FRAGMENTS_COUNT = 2;
    private static final String FRAGMENT_PLAYLIST = "PlaylistFragment";
    private static final String FRAGMENT_SONGDETAILS = "SongDetailsFragment";

    private final AppMusicPlayer player;
    private String fragmentToUpdate;

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

        if (objectClass.equals(fragmentToUpdate))
            update(object);

        return super.getItemPosition(object);
    }

    private void update(Object object) {
        if (object.getClass().getName().equals(PlaylistFragment.class.getName())) {
            Log.i("Updating PL Fragment", "" + player.getPlaylist().size());
            ((PlaylistFragment) object).update(player.getPlaylist());

        } else if (object.getClass().getName().equals(SongDetailsFragment.class.getName())) {
            Log.i("Updating SD Fragment", "" + player.getPlaylist().size());
            ((SongDetailsFragment) object).update(
                    player.getSong().getTitle(),
                    player.getSong().getArtist(),
                    player.getSong().getAlbum(),
                    player.getSong().getGenre()
            );
        }
    }

    public void updateFragment(String fragmentName) {
        this.fragmentToUpdate = fragmentName;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return FRAGMENTS_COUNT;
    }
}
