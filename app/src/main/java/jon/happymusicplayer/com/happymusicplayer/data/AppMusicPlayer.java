package jon.happymusicplayer.com.happymusicplayer.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.R;
import jon.happymusicplayer.com.happymusicplayer.data.daos.PlaylistsDao;
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
    private List<SongModel> playlist;
    private SongModel song;
    private boolean isPrepared;
    private boolean isShuffle;
    private int playlistIndex;
    private String repeatState;
    private int prevPlayListIndex;
    private String playlistName;

    private final BroadcastReceiver headsetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
                pause();
            }
        }
    };

    public AppMusicPlayer(Context context) {
        this.context = context;
    }


    public void playSong(int index) {
        playlistIndex = index;
        song = playlist.get(index);
        context.registerReceiver(headsetReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));

        try {
            reset();
            isPrepared = false;
            setDataSource(song.getPath());
            prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void playSong(String path) {
        int index = getPathIndex(path);

        playlistIndex = index;
        song = playlist.get(index);
        context.registerReceiver(headsetReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));

        try {
            reset();
            isPrepared = false;
            setDataSource(song.getPath());
            prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private int getPathIndex(String path) {
        for (int i = 0; i < playlist.size(); i++) {
            if (playlist.get(i).getPath().equals(path))
                return i;
        }
        return -1;
    }

    public void setPlaylist(String playListName) {
        this.playlistName = playListName;
        PlaylistsDao playlistsDao = new PlaylistsDao(this.context);
        SongsDao songsDao = new SongsDao(this.context);

        if (playListName.equals(context.getResources().getString(R.string.recently_added))) {
            playlist = songsDao.getAllRecentlyAdded();
            return;
        }

        int playlistId = playlistsDao.getSingleByName(playListName).getId();
        playlist = songsDao.getAllByPlayList(playlistId);
    }

    public void setPlaylist(List<SongModel> playlist) {
        this.playlist = playlist;
    }

    public void play() {
        if (isPrepared) {
            start();
        }
    }

    public int playNextSong() {
        prevPlayListIndex = playlistIndex;

        boolean isLastPlaylistIndex = playlistIndex == playlist.size() - 1;

        if (isShuffle) {
            playlistIndex = (Utilities.getRandomInt(playlist.size(), 0));
        } else if (isLastPlaylistIndex) {
            playlistIndex = 0;
        } else {
            playlistIndex++;
        }

        playSong(playlistIndex);
        return playlistIndex;
    }

    public int playPrevSong() {

        boolean isValidPrevPlaylistIndex = prevPlayListIndex != -1;

        if (isValidPrevPlaylistIndex) {
            playlistIndex = prevPlayListIndex;
            prevPlayListIndex = -1;
        } else if (isShuffle) {
            playlistIndex = (Utilities.getRandomInt(playlist.size(), 0));
        } else if (playlistIndex == 0)
            playlistIndex = playlist.size() - 1;
        else
            playlistIndex--;

        playSong(playlistIndex);

        return playlistIndex;
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
        if (isPlaying()) {
            pause();
        } else {
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

    public List<SongModel> getPlaylist() {
        return this.playlist;
    }

    public List<String> getAllPlayLists() {
        PlaylistsDao playlistsDao = new PlaylistsDao(context);
        List<PlayListModel> playLists = playlistsDao.getAllPlayLists();
        List<String> playListNames = new ArrayList<>();

        for (PlayListModel playList : playLists) {
            playListNames.add(playList.getName());
        }

        return playListNames;
    }

    public List<String> getAllUserPlaylists() {
        PlaylistsDao playlistsDao = new PlaylistsDao(context);
        List<PlayListModel> playLists = playlistsDao.getAllUserPlayLists();
        List<String> playListNames = new ArrayList<>();

        for (PlayListModel playList : playLists) {
            playListNames.add(playList.getName());
        }

        return playListNames;
    }

    public SongModel getSong() {
        return song;
    }

    public String getPlaylistName() {
        return this.playlistName;
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public void setIsPrepared(boolean isPrepared) {
        this.isPrepared = isPrepared;
    }


    public void setRepeatState(String repeatState) {
        this.repeatState = repeatState;
    }

    public void removeSongFromPlaylist(SongModel song) {
        for (SongModel s : playlist) {
            if (s.getId() == song.getId()) {
                playlist.remove(s);
            }
        }
    }
}
