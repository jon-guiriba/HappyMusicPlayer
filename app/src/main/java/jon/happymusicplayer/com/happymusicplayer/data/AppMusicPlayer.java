package jon.happymusicplayer.com.happymusicplayer.data;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.data.models.SongModel;

/**
 * Created by Jon on 9/21/2016.
 */
public class AppMusicPlayer extends MediaPlayer {


    private static AppMusicPlayer appMusicPlayer;
    private List<SongModel> playList;
    private SongModel song;
    private boolean isPrepared;


    public void playSong(int index) throws IOException {
        song = playList.get(index);
        appMusicPlayer.reset();
        appMusicPlayer.setDataSource(song.getPath());
        appMusicPlayer.prepare();
        appMusicPlayer.start();
    }

    public void setPlayList(List<SongModel> playList) {
        this.playList = playList;
    }

    public void play() {
        if (isPrepared) {
            super.start();
        }
    }

    @Override
    public void prepare() throws IOException, IllegalStateException {
        super.prepare();
        isPrepared = true;
    }

    public static synchronized AppMusicPlayer getInstance(Context context) {
        if (appMusicPlayer == null) {
            appMusicPlayer = new AppMusicPlayer();
        }
        return appMusicPlayer;
    }


}
