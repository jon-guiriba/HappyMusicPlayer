package jon.happymusicplayer.com.happymusicplayer.eventhandlers;

import android.media.MediaPlayer;
import android.widget.SeekBar;
import android.widget.TextView;

import jon.happymusicplayer.com.happymusicplayer.data.AppMusicPlayer;
import jon.happymusicplayer.com.happymusicplayer.data.Presenter;

/**
 * Created by Jon on 10/8/2016.
 */

public class OnMediaPlayerCompletionListener implements MediaPlayer.OnCompletionListener {

    private final AppMusicPlayer appMusicPlayer;
    private Presenter presenter;

    public OnMediaPlayerCompletionListener(AppMusicPlayer appMusicPlayer, Presenter presenter) {
        this.presenter = presenter;
        this.appMusicPlayer = appMusicPlayer;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        presenter.resetProgressBar();

        switch (appMusicPlayer.getRepeatState()) {
            case AppMusicPlayer.REPEAT_ALL:
                appMusicPlayer.playNextSong();
                presenter.updateCurrentSongText(appMusicPlayer.getSong().getName());
                break;
            case AppMusicPlayer.REPEAT_ONE:
                appMusicPlayer.play();
                break;
            case AppMusicPlayer.REPEAT_OFF:
                break;
        }
    }
}
