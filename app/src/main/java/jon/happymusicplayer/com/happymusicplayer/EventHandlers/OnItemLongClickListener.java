package jon.happymusicplayer.com.happymusicplayer.EventHandlers;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import jon.happymusicplayer.com.happymusicplayer.R;
import jon.happymusicplayer.com.happymusicplayer.data.AppMusicPlayer;
import jon.happymusicplayer.com.happymusicplayer.data.Presenter;
import jon.happymusicplayer.com.happymusicplayer.data.models.SongModel;

/**
 * Created by 80978448 on 11/3/2016.
 */

public class OnItemLongClickListener extends Listener{

    OnItemLongClickListener(AppMusicPlayer player, Presenter presenter, Context context, AppEventHandler eventHandler) {
        super(player, presenter, context, eventHandler);
    }

    public boolean handleEvent(AdapterView<?> parent, View v, int position, long id){
        switch (v.getId()) {
            case R.id.currentPlaylistItem:
                presenter.setupSongContextOptionsPopupWindow();
                presenter.getSongOptions().setOnItemClickListener(eventHandler);
                presenter.showSongOptions(v);
//                selectedSong = (SongModel) parent.getItemAtPosition(position);
                break;

        }
        return true;
    }
}
