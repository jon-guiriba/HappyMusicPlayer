package jon.happymusicplayer.com.happymusicplayer.EventHandlers;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import jon.happymusicplayer.com.happymusicplayer.R;
import jon.happymusicplayer.com.happymusicplayer.data.AppMusicPlayer;
import jon.happymusicplayer.com.happymusicplayer.data.Presenter;
import jon.happymusicplayer.com.happymusicplayer.data.daos.PlaylistsDao;
import jon.happymusicplayer.com.happymusicplayer.data.managers.SettingsManager;

/**
 * Created by 80978448 on 11/3/2016.
 */

public class OnClickListener extends  Listener{

    public OnClickListener(AppMusicPlayer player,
                           Presenter presenter, Context context,
                           AppEventHandler eventHandler) {
        super(player, presenter, context, eventHandler);
    }

    public void handleEvent(View v){
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
                presenter.getActionMenuPopupWindow().setOnItemClickListener(eventHandler);
                presenter.showActionMenu(v);
                break;

            case R.id.actionSort:
                presenter.setupSortPopupView();
                presenter.getSortListView().setOnItemClickListener(eventHandler);
                presenter.showSortPopupView();
                break;
        }
    }
}
