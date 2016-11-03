package jon.happymusicplayer.com.happymusicplayer.EventHandlers;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.R;
import jon.happymusicplayer.com.happymusicplayer.adapters.OnTaskCompleted;
import jon.happymusicplayer.com.happymusicplayer.data.AppMusicPlayer;
import jon.happymusicplayer.com.happymusicplayer.data.Presenter;
import jon.happymusicplayer.com.happymusicplayer.data.daos.PlaylistsDao;
import jon.happymusicplayer.com.happymusicplayer.data.daos.SongsDao;
import jon.happymusicplayer.com.happymusicplayer.data.models.PlayListModel;
import jon.happymusicplayer.com.happymusicplayer.data.models.SongModel;
import jon.happymusicplayer.com.happymusicplayer.utils.Utilities;

/**
 * Created by Jon on 10/17/2016.
 */

public class AppEventHandler implements View.OnClickListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener,
        SeekBar.OnSeekBarChangeListener, OnTaskCompleted, SearchView.OnQueryTextListener,
        MediaPlayer.OnPreparedListener, DrawerLayout.DrawerListener, MediaPlayer.OnCompletionListener, AbsListView.OnScrollListener {

    private static AppEventHandler eventHandler;
    private static AppMusicPlayer player;
    private static Context context;
    private static Presenter presenter;
    private SongModel selectedSong;

    public AppEventHandler() {
    }

    @Override
    public void onClick(final View v) {
        OnClickListener listener = new OnClickListener(
                player,
                presenter,
                context,
                this);

        listener.handleEvent(v);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        OnItemClickListener listener = new OnItemClickListener(
                player,
                presenter,
                context,
                this);

        listener.handleEvent(parent, v, position, id);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
        OnItemLongClickListener listener = new OnItemLongClickListener(
                player,
                presenter,
                context,
                this);

        return listener.handleEvent(parent, v, position, id);

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        presenter.resetProgressBar();

        switch (player.getRepeatState()) {
            case AppMusicPlayer.REPEAT_ALL:
                player.playNextSong();
                break;
            case AppMusicPlayer.REPEAT_ONE:
                player.play();
                break;
            case AppMusicPlayer.REPEAT_OFF:
                break;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        player.setIsPrepared(true);
        player.play();

        presenter.updateSongShortDetails(
                player.getSong().getTitle(),
                Utilities.getDurationAsText(0, player.getDuration())
        );
        presenter.resetProgressBar();
        presenter.updatePlayButton(player.isPlaying());
        presenter.startUpdateProgressBar();
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        SongsDao songsDao = new SongsDao(context);
        PlaylistsDao playlistsDao = new PlaylistsDao(context);
        PlayListModel playlistEntity = playlistsDao.getSingleByName(player.getPlaylistName());
        List<SongModel> playlist = songsDao.getAllByPlayList(playlistEntity.getId());
        List<SongModel> newPlaylist = new ArrayList<>();

        for (int i = 0; i < playlist.size(); i++) {
            SongModel song = playlist.get(i);
            if (song.getTitle().toLowerCase().contains(newText))
                newPlaylist.add(song);
        }

        player.setPlaylist(newPlaylist);
        presenter.updatePagerPlaylist();
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        presenter.stopUpdateProgressBar();
        if (!player.isPrepared()) return;

        int currentPosition = Utilities.getProgressToTimer(seekBar.getProgress(), player.getDuration());
        player.seekTo(currentPosition);
        presenter.startUpdateProgressBar();
    }

    @Override
    public void onTaskCompleted() {
        SongsDao songsDao = new SongsDao(context);
        List<SongModel> playList = songsDao.getAllByPlayList(1);
        if (player.getPlaylist() == null) {
            player.setPlaylist(playList);
            presenter.updatePagerPlaylist();
        }
    }



    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
//                presenter.getScrollTextToast().cancel();
                break;

            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
//                presenter.getScrollTextToast().show();
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:

                break;


        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        switch (view.getId()) {
            case R.id.lvCurrentPlaylist:
//                SongModel song = (SongModel) presenter.getCurrentPlaylistListView().getItemAtPosition(firstVisibleItem);
//                if (song == null) return;
//                String scrollText = "" + song.getTitle().charAt(0);
//                int scrolly =  presenter.getCurrentPlaylistListView().getFirstVisiblePosition() +
//                        (int)(presenter.getCurrentPlaylistListView().getHeight() * 0.2);
//
//                ((TextView)presenter.getScrollTextToast().getView().findViewById(R.id.tvScrollText))
//                        .setText(scrollText);
//                presenter.getScrollTextToast().setDuration(Toast.LENGTH_SHORT);
//                Log.i("Scroll" , ""+scrolly);
//                presenter.getScrollTextToast().setGravity(Gravity.TOP | Gravity.END, 15, 0 + scrolly);
//                presenter.getScrollTextToast().show();
                break;
        }
    }

    public static synchronized AppEventHandler getInstance() {
        if (eventHandler == null) {
            eventHandler = new AppEventHandler();
        }
        return eventHandler;
    }

    public static void setContext(Context context) {
        AppEventHandler.context = context;
    }

    public static void setPlayer(AppMusicPlayer player) {
        AppEventHandler.player = player;
    }

    public static void setPresenter(Presenter presenter) {
        AppEventHandler.presenter = presenter;
    }

}
