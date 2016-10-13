package jon.happymusicplayer.com.happymusicplayer.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.R;
import jon.happymusicplayer.com.happymusicplayer.data.daos.PlayListsDao;
import jon.happymusicplayer.com.happymusicplayer.data.daos.SongsDao;
import jon.happymusicplayer.com.happymusicplayer.data.managers.SettingsManager;
import jon.happymusicplayer.com.happymusicplayer.data.models.PlayListModel;
import jon.happymusicplayer.com.happymusicplayer.data.models.SongModel;
import jon.happymusicplayer.com.happymusicplayer.utils.Utilities;

/**
 * Created by Jon on 9/21/2016.
 */
public class AppMusicPlayer extends MediaPlayer {

    public static final String REPEAT_OFF = "repeat_off";
    public static final String REPEAT_ONE = "repeat_one";
    public static final String REPEAT_ALL = "repeat_all";

    private Context context;
    private List<SongModel> playList;
    private SongModel song;
    private boolean isPrepared;
    private boolean isShuffle;
    private int playListIndex;
    private String repeatState;
    private int prevPlayListIndex;
    private boolean isPlaying;
    private final BroadcastReceiver headsetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("onRecieve", "Wassap");
            if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
                pause();
            }
        }
    };

    public AppMusicPlayer(Context context) {
        this.context = context;
        setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                start();
                isPrepared = true;
            }
        });
    }


    public void playSong(int index) {
        playListIndex = index;
        song = playList.get(index);
        isPlaying = false;
        context.registerReceiver(headsetReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));

        try {
            reset();
            setDataSource(song.getPath());
            isPlaying = true;
            prepareAsync();
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
            start();
        }
    }

    public void playNextSong() {
        prevPlayListIndex = playListIndex;
        if (isShuffle) {
            playSong(Utilities.getRandomInt(playList.size(), 0));
        } else if (playListIndex == playList.size() - 1) {
            playSong(0);
        } else {
            playSong(playListIndex + 1);
        }

    }

    public void playPrevSong() {
        if (isShuffle && prevPlayListIndex != -1) {
            playSong(prevPlayListIndex);
            prevPlayListIndex = -1;
        } else if (playListIndex == 0)
            playSong(playList.size() - 1);
        else
            playSong(playListIndex - 1);
    }

    @Override
    public void prepare() throws IOException, IllegalStateException {
        super.prepare();
        isPrepared = true;
    }


    public void toggleRepeatState() {
        switch (repeatState) {
            case REPEAT_OFF:
                repeatState = REPEAT_ALL;
                setLooping(false);
                break;
            case REPEAT_ALL:
                repeatState = REPEAT_ONE;
                setLooping(true);
                break;
            case REPEAT_ONE:
                repeatState = REPEAT_OFF;
                setLooping(false);
                break;
        }

        SettingsManager.updateRepeatStateSettings(repeatState);
    }

    public void togglePlayState() {
        if (isPlaying) {
            isPlaying = false;
            pause();
        } else {
            isPlaying = true;
            play();
        }
    }

    @Override
    public void pause() {
        super.pause();

        try {
            context.unregisterReceiver(headsetReceiver);
        } catch (IllegalArgumentException e) {

        }
    }

    public void toggleShuffleState() {
        isShuffle = !isShuffle;
    }

    public boolean isShuffle() {
        return isShuffle;
    }

    public boolean getIsShuffle() {
        return this.isShuffle;
    }

    public void setIsShuffle(boolean isShuffle) {
        this.isShuffle = isShuffle;
    }

    public String getRepeatState() {
        return repeatState;
    }

    public List<SongModel> getPlayList() {
        return this.playList;
    }

    public List<String> getAllPlayLists() {
        List<PlayListModel> playLists = DatabaseHelper.getInstance(context).getAllPlayLists();
        List<String> playListNames = new LinkedList<>();

        for (PlayListModel playList : playLists) {
            playListNames.add(playList.getName());
        }

        return playListNames;
    }

    public List<String> getAllUserPlayLists() {
        List<PlayListModel> playLists = DatabaseHelper.getInstance(context).getAllUserPlayLists();
        List<String> playListNames = new LinkedList<>();

        for (PlayListModel playList : playLists) {
            playListNames.add(playList.getName());
        }

        return playListNames;
    }

    public SongModel getSong() {
        return song;
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isPrepared() {
        return isPrepared;
    }


    public void setRepeatState(String repeatState) {
        this.repeatState = repeatState;
    }
}
