package jon.happymusicplayer.com.happymusicplayer.EventHandlers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.io.File;
import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.R;
import jon.happymusicplayer.com.happymusicplayer.data.AppMusicPlayer;
import jon.happymusicplayer.com.happymusicplayer.data.Presenter;
import jon.happymusicplayer.com.happymusicplayer.data.contracts.SongsContract;
import jon.happymusicplayer.com.happymusicplayer.data.daos.PlaylistItemsDao;
import jon.happymusicplayer.com.happymusicplayer.data.daos.PlaylistsDao;
import jon.happymusicplayer.com.happymusicplayer.data.daos.SongsDao;
import jon.happymusicplayer.com.happymusicplayer.data.models.PlayListModel;
import jon.happymusicplayer.com.happymusicplayer.data.models.SongModel;

/**
 * Created by 80978448 on 11/3/2016.
 */

public class OnItemClickListener  extends Listener{

    SongModel selectedSong;

    OnItemClickListener(AppMusicPlayer player, Presenter presenter, Context context, AppEventHandler eventHandler) {
        super(player, presenter, context, eventHandler);
    }

    public void handleEvent(AdapterView<?> parent, View v, int position, long id) {

        switch (v.getId()) {
            case R.id.currentPlaylistItem:
                SongModel song = (SongModel) parent.getItemAtPosition(position);
                selectedSong = song;
                player.playSong(song.getId());
                presenter.updatePagerSongDetails();
                break;

            case R.id.drawerPlaylistItem:
                String playListName = (String) parent.getItemAtPosition(position);

                if (playListName.equals(context.getResources().getString(R.string.add_new))) {
                    presenter.setupCreateNewPlaylistPopupWindow();
                    presenter.getSubmitAddNewPlaylistButton().setOnClickListener(eventHandler);
                    presenter.showCreateNewPlaylistPopupWindow();
                    break;
                }

                player.setPlaylist(playListName);
                Log.i("onItemClick ", playListName);
                presenter.updatePagerPlaylist();
                break;

            case R.id.contextMenuItem:
                String contextOption = (String) parent.getItemAtPosition(position);

                switch (contextOption) {
                    case "Add To Playlist":
                        presenter.hideSongOptions();
                        presenter.setupAddToPlaylistPopupWindow(player.getAllUserPlaylists());
                        presenter.getAddToPlaylistCurrentPlayListsListView().setOnItemClickListener(eventHandler);
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
                                        presenter.updatePagerPlaylist();
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
                        presenter.updatePagerPlaylist();
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
                presenter.updatePagerPlaylist();
                presenter.hideSortPopupView();
                break;

            case R.id.filterByArtistItem:
                songsDao = new SongsDao(context);
                String artist = (String) parent.getItemAtPosition(position);
                playlist = songsDao.getAllByArtist(artist);
                player.setPlaylist(playlist);
                presenter.updatePagerPlaylist();
                break;

            case R.id.filterByAlbumItem:
                songsDao = new SongsDao(context);
                String album = (String) parent.getItemAtPosition(position);
                playlist = songsDao.getAllByAlbum(album);
                player.setPlaylist(playlist);
                presenter.updatePagerPlaylist();
                break;

            case R.id.filterByFolderItem:
                songsDao = new SongsDao(context);
                String folder = (String) parent.getItemAtPosition(position);
                playlist = songsDao.getAllByFolder(folder);
                player.setPlaylist(playlist);
                presenter.updatePagerPlaylist();
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
