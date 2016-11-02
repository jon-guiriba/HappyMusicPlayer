package jon.happymusicplayer.com.happymusicplayer.data;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.SeekBar;

import java.io.File;
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

            case R.id.actionMenu:
                presenter.setupActionMenuPopupWindow();
                presenter.getActionMenuPopupWindow().setOnItemClickListener(this);
                presenter.showActionMenu(v);
                break;

            case R.id.actionSort:
                presenter.setupSortPopupView();
                presenter.getSortListView().setOnItemClickListener(this);
                presenter.showSortPopupView();
                break;


        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        switch (v.getId()) {
            case R.id.currentPlaylistItem:
                SongModel song = (SongModel) parent.getItemAtPosition(position);
                selectedSong = song;
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
                Log.i("onItemClick ", playListName);
                presenter.updateCurrentPlaylist(player.getPlaylist());
                break;

            case R.id.contextMenuItem:
                String contextOption = (String) parent.getItemAtPosition(position);

                switch (contextOption) {
                    case "Add To Playlist":
                        presenter.hideSongOptions();
                        presenter.setupAddToPlaylistPopupWindow(player.getAllUserPlaylists());
                        presenter.getAddToPlaylistCurrentPlayListsListView().setOnItemClickListener(this);
                        presenter.showAddToPlaylistPopupWindow();
                        break;

                    case "Delete":
                        presenter.hideSongOptions();
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        SongsDao songsDao = new SongsDao(context);
                                        songsDao.deleteSong(selectedSong);
                                        File file = new File(selectedSong.getPath());
                                        file.delete();
                                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                                        player.removeSongFromPlaylist(selectedSong);
                                        List<SongModel> playlist = player.getPlaylist();
                                        presenter.updateCurrentPlaylist(playlist);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };

                        new AlertDialog.Builder(context)
                                .setMessage("Are you sure you want to delete " +
                                        selectedSong.getTitle() + " ?")
                                .setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener)
                                .show();
                        break;

                    case "Blacklist":
                        presenter.hideSongOptions();
                        SongsDao songsDao = new SongsDao(context);
                        songsDao.setSongBlacklist(selectedSong, 1);
                        player.removeSongFromPlaylist(selectedSong);
                        List<SongModel> playlist = player.getPlaylist();
                        presenter.updateCurrentPlaylist(playlist);
                        break;
                }

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
                presenter.updateCurrentPlaylist(playlist);
                presenter.hideSortPopupView();
                break;

            case R.id.filterByArtistItem:
                songsDao = new SongsDao(context);
                String artist = (String) parent.getItemAtPosition(position);
                playlist = songsDao.getAllByArtist(artist);
                player.setPlaylist(playlist);
                presenter.updateCurrentPlaylist(playlist);
                break;

            case R.id.filterByAlbumItem:
                songsDao = new SongsDao(context);
                String album = (String) parent.getItemAtPosition(position);
                playlist = songsDao.getAllByAlbum(album);
                player.setPlaylist(playlist);
                presenter.updateCurrentPlaylist(playlist);
                break;

            case R.id.filterByFolderItem:
                songsDao = new SongsDao(context);
                String folder = (String) parent.getItemAtPosition(position);
                playlist = songsDao.getAllByFolder(folder);
                player.setPlaylist(playlist);
                presenter.updateCurrentPlaylist(playlist);
                break;

            case R.id.filterIcon:
                break;

            case R.id.actionMenuItem: {
                Intent intent = new Intent();
                intent.setClassName(
                        context,
                        "jon.happymusicplayer.com.happymusicplayer.Activities.PreferencesActivity"
                );
                context.startActivity(intent);
            }
            break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        switch (view.getId()) {
            case R.id.currentPlaylistItem:
                presenter.setupSongContextOptionsPopupWindow();
                presenter.getSongOptions().setOnItemClickListener(this);
                presenter.showSongOptions(view);
                selectedSong = (SongModel) parent.getItemAtPosition(position);
                break;

        }
        return true;
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
        presenter.updateCurrentPlaylist(newPlaylist);
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
            presenter.updateCurrentPlaylist(playList);
        }
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
