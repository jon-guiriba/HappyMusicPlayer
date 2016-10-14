package jon.happymusicplayer.com.happymusicplayer.tasks;

import jon.happymusicplayer.com.happymusicplayer.data.AppMusicPlayer;
import jon.happymusicplayer.com.happymusicplayer.data.Presenter;
import jon.happymusicplayer.com.happymusicplayer.utils.Utilities;

/**
 * Created by Jon on 10/14/2016.
 */

public class UpdateProgressBarTask implements Runnable {

    private final Presenter presenter;
    private final AppMusicPlayer player;

    public UpdateProgressBarTask(AppMusicPlayer player, Presenter presenter) {
        this.player = player;
        this.presenter = presenter;
    }

    @Override
    public void run() {
        if (!player.isPrepared()) return;
        int trackProgress = Utilities.getPercentage(player.getCurrentPosition(), player.getDuration());
        presenter.updateProgressBar(trackProgress);
    }
}
