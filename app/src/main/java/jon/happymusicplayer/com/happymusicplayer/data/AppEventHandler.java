package jon.happymusicplayer.com.happymusicplayer.data;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.R;
import jon.happymusicplayer.com.happymusicplayer.adapters.OnTaskCompleted;
import jon.happymusicplayer.com.happymusicplayer.data.contracts.SongsContract;
import jon.happymusicplayer.com.happymusicplayer.data.daos.PlaylistItemsDao;
import jon.happymusicplayer.com.happymusicplayer.data.daos.PlaylistsDao;
import jon.happymusicplayer.com.happymusicplayer.data.daos.SongsDao;
import jon.happymusicplayer.com.happymusicplayer.data.managers.SettingsManager;
import jon.happymusicplayer.com.happymusicplayer.data.models.PlayListModel;
import jon.happymusicplayer.com.happymusicplayer.data.models.SongModel;
import jon.happymusicplayer.com.happymusicplayer.utils.Utilities;

/**
 * Created by Jon on 10/17/2016.
 */

public class AppEventHandler implements View.OnClickListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener,
        SeekBar.OnSeekBarChangeListener, OnTaskCompleted, SearchView.OnQueryTextListener,
        MediaPlayer.OnPreparedListener, DrawerLayout.DrawerListener, MediaPlayer.OnCompletionListener {

    private final AppMusicPlayer player;
    private final Context context;
    private Presenter presenter;
    private SongModel selectedSong;

    public AppEventHandler(Context context, Presenter presenter, AppMusicPlayer player) {
        this.presenter = presenter;
        this.player = player;
        this.context = context;
    }

    @Override
    public void onClick(final View v) {
        int songIndex;
        switch (v.getId()) {
            case R.id.btnPlay:
                player.togglePlayState();
                presenter.updatePlayButton(player.isPlaying());
                presenter.toggleTrackBarUpdate(player.isPlaying());
                break;
            case R.id.btnBackward:
                songIndex = player.playPrevSong();
                presenter.highlightSelectedPlaylistItem(songIndex);
                break;
            case R.id.btnForward:
                songIndex = player.playNextSong();
                presenter.highlightSelectedPlaylistItem(songIndex);
                break;

            case R.id.btnRepeat:
                player.toggleRepeatState();
                presenter.updateRepeatButton(player.getRepeatState());
                SettingsManager.updateRepeatStateSettings(player.getRepeatState());
                break;

            case R.id.btnShuffle:
                player.toggleShuffleState();
                presenter.updateShuffleButton(player.getIsShuffle());
                SettingsManager.updateShuffleStateSettings(player.getIsShuffle());
                break;

            case R.id.btnSubmitAddNewPlaylist:
                PlaylistsDao plDao = new PlaylistsDao(context);
                plDao.addNewPlaylist(presenter.getAddNewPlaylistText());
                presenter.updateDrawerPlaylist(player.getAllPlayLists());
                presenter.hideCreateNewPlaylistPopupWindow();
                break;

            case R.id.actionSort:
                presenter.setupSortPopupView();
                presenter.getSortListView().setOnItemClickListener(this);
                presenter.showSortPopupView();
                break;

        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        presenter.showSongOptions(view);
        selectedSong = (SongModel) parent.getItemAtPosition(position);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

        switch (v.getId()) {
            case R.id.currentPlaylistItem:
                SongModel song = (SongModel) parent.getItemAtPosition(position);
                player.playSong(song.getPath());
                break;

            case R.id.drawerPlaylistItem:
                String playListName = (String) parent.getItemAtPosition(position);

                if (playListName.equals(context.getResources().getString(R.string.add_new))) {
                    presenter.setupCreateNewPlaylistPopupWindow();
                    presenter.getSubmitAddNewPlaylistButton().setOnClickListener(this);
                    presenter.showCreateNewPlaylistPopupWindow();
                    break;
                }

                player.setPlaylist(playListName);
                presenter.updatePlaylist(player.getPlaylist());
                break;

            case R.id.contextMenuItem:
                presenter.setupAddToPlaylistPopupWindow(player.getAllUserPlaylists());
                presenter.getAddToPlaylistCurrentPlayListsListView().setOnItemClickListener(this);
                presenter.showAddToPlaylistPopupWindow();
                presenter.hideSongOptions();
                break;

            case R.id.addToPlaylistItem:
                playListName = (String) parent.getItemAtPosition(position);
                PlaylistsDao plDao = new PlaylistsDao(context);
                PlayListModel playlistEntity = plDao.getSingleByName(playListName);

                PlaylistItemsDao plItemsDao = new PlaylistItemsDao(context);
                plItemsDao.addNewPlaylistItem(playlistEntity.getId(), selectedSong.getId());

                presenter.hideAddPlaylistPopupWindow();
                break;

            case R.id.sortOptionsItem:
                SongsDao songsDao = new SongsDao(context);
                PlaylistsDao playlistDao = new PlaylistsDao(context);
                int playlistId = playlistDao.getSingleByName(player.getPlaylistName()).getId();

                List<SongModel> playlist = songsDao.getAllByPlayList(playlistId, getColumnName(position));
                player.setPlaylist(playlist);
                presenter.updatePlaylist(playlist);
                presenter.hideSortPopupView();
                break;
        }
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

        presenter.updateSongDetails(
                player.getSong().getTitle(),
                Utilities.getDurationAsText(0, player.getDuration())
        );
        presenter.resetProgressBar();
        presenter.updatePlayButton(player.isPlaying());
        presenter.startUpdateProgressBar();
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        LayoutAnimationController animation =
                new LayoutAnimationController(AnimationUtils.loadAnimation(context, R.anim.table_row_appear), 1f);
        presenter.getDrawerPlaylistListView().setLayoutAnimation(animation);
        presenter.getDrawerPlaylistListView().clearAnimation();
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
        presenter.updatePlaylist(newPlaylist);
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
        if (player.getPlaylist() == null)
            player.setPlaylist(playList);
        presenter.updatePlaylist(playList);
    }


    private String getColumnName(int position) {
        switch (position) {
            case 0:
                return SongsContract.SongsEntry.TITLE;
            case 1:
                return SongsContract.SongsEntry.ARTIST;
            case 2:
                return SongsContract.SongsEntry.DATE_MODIFIED;
            case 3:
                return SongsContract.SongsEntry.DURATION;
            default:
                return SongsContract.SongsEntry.TITLE;
        }

    }


}
