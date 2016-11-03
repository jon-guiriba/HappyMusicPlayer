package jon.happymusicplayer.com.happymusicplayer.EventHandlers;

import android.content.Context;

import jon.happymusicplayer.com.happymusicplayer.data.AppMusicPlayer;
import jon.happymusicplayer.com.happymusicplayer.data.Presenter;

/**
 * Created by 80978448 on 11/3/2016.
 */

public abstract  class Listener {
    protected final AppMusicPlayer player;
    protected final Presenter presenter;
    protected final Context context;
    protected final AppEventHandler eventHandler;

    Listener(AppMusicPlayer player,
                           Presenter presenter, Context context,
                           AppEventHandler eventHandler) {
        this.player = player;
        this.presenter = presenter;
        this.context = context;
        this.eventHandler = eventHandler;
    }

}
