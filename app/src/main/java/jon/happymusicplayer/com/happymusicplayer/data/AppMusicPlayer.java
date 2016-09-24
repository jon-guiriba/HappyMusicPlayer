package jon.happymusicplayer.com.happymusicplayer.data;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.R;
import jon.happymusicplayer.com.happymusicplayer.data.daos.PlayListsDao;
import jon.happymusicplayer.com.happymusicplayer.data.daos.SongsDao;
import jon.happymusicplayer.com.happymusicplayer.data.models.PlayListModel;
import jon.happymusicplayer.com.happymusicplayer.data.models.SongModel;

/**
 * Created by Jon on 9/21/2016.
 */
public class AppMusicPlayer extends MediaPlayer {


    private static AppMusicPlayer appMusicPlayer;
    private List<SongModel> playList;
    private SongModel song;
    private boolean isPrepared;
    private Context context;

    public AppMusicPlayer(Context context) {
        this.context = context;
    }

    public void playSong(int index) {
        song = playList.get(index);

        try {
            appMusicPlayer.reset();
            appMusicPlayer.setDataSource(song.getPath());
            appMusicPlayer.prepare();
            appMusicPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setPlayList(String playListName) {
        PlayListsDao playListsDao = new PlayListsDao(this.context);
        SongsDao songsDao = new SongsDao(this.context);

        if (playListName.equals(context.getResources().getString(R.string.recently_added))) {
            playList = songsDao.getAllRecentlyAdded();
            return;
        }

        int playListId = playListsDao.getSingleByName(playListName).getId();
        playList = songsDao.getAllByPlayList(playListId);
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
            appMusicPlayer = new AppMusicPlayer(context);
        }
        return appMusicPlayer;
    }


    public List<SongModel> getPlayList() {
        return this.playList;
    }

    public SongModel getSong() {
        return song;
    }


}
