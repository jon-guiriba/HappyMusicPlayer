package jon.happymusicplayer.com.happymusicplayer;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.data.AppMusicPlayer;
import jon.happymusicplayer.com.happymusicplayer.data.daos.PlayListsDao;
import jon.happymusicplayer.com.happymusicplayer.data.managers.DatabaseHelper;
import jon.happymusicplayer.com.happymusicplayer.adapters.OnTaskCompleted;
import jon.happymusicplayer.com.happymusicplayer.data.daos.SongsDao;
import jon.happymusicplayer.com.happymusicplayer.tasks.UpdateAllSongsPlayListTask;
import jon.happymusicplayer.com.happymusicplayer.data.models.PlayListModel;
import jon.happymusicplayer.com.happymusicplayer.data.models.SongModel;
import jon.happymusicplayer.com.happymusicplayer.utils.Utilities;


public class MainActivity extends AppCompatActivity implements OnClickListener,
        AdapterView.OnItemClickListener, MediaPlayer.OnCompletionListener,
        SeekBar.OnSeekBarChangeListener, OnTaskCompleted {

    private static final String PLAYERSTATE_PLAYING = "playing";
    private static final String PLAYERSTATE_PAUSED = "paused";
    private static final String REPEATSTATE_OFF = "repeat_off";
    private static final String REPEATSTATE_ALL = "repeat_all";
    private static final String REPEATSTATE_ONE = "repeat_one";

    private static final int SONG_PROGRESS_BAR_REFRESH_RATE = 40;

    private String repeatState = REPEATSTATE_OFF;

    private ImageButton btnPlay;
    private ImageButton btnForward;
    private ImageButton btnBackward;
    private ImageButton btnRepeat;
    private ImageButton btnShuffle;
    private ListView lvCurrentPlayList;
    private ListView lvDrawerPlayList;
    private ListView lvFeatures;
    private TextView tvCurrentAudioFile;
    private SearchView searchView;
    private SeekBar sbSongProgressBar;
    private Handler songProgressBarHandler = new Handler();

    private List<SongModel> playList;
    private SongModel song;

    private boolean isShuffle = false;
    private int songIndex = 0;

    AppMusicPlayer player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        init();
        loadPreferences();
        setupDrawerPlayLists();

        new UpdateAllSongsPlayListTask(this, this).execute();
    }

    private void init() {
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnForward = (ImageButton) findViewById(R.id.btnForward);
        btnBackward = (ImageButton) findViewById(R.id.btnBackward);
        btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
        btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
        lvCurrentPlayList = (ListView) findViewById(R.id.lvCurrentPlayList);
        tvCurrentAudioFile = (TextView) findViewById(R.id.tvCurrentAudioFile);
        lvDrawerPlayList = (ListView) findViewById(R.id.lvDrawerPlaylist);
        sbSongProgressBar = (SeekBar) findViewById(R.id.sbTrackProgressBar);

        btnPlay.setOnClickListener(this);
        btnForward.setOnClickListener(this);
        btnBackward.setOnClickListener(this);
        btnRepeat.setOnClickListener(this);
        btnShuffle.setOnClickListener(this);
        sbSongProgressBar.setOnSeekBarChangeListener(this);

        player = AppMusicPlayer.getInstance(this);
        player.setOnCompletionListener(this);

        lvCurrentPlayList.setOnItemClickListener(this);
    }

    private void loadPreferences() {
        SharedPreferences defPreference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isShuffle = defPreference.getBoolean(getResources().getString(R.string.is_shuffle), false);
        String repeatState = defPreference.getString(getResources().getString(R.string.repeat_state), REPEATSTATE_OFF);
        String playListName = defPreference.getString(getResources().getString(R.string.last_playlist), getResources().getString(R.string.all_songs));

        updatePlayList(playListName);
        updateRepeatState(repeatState);
        updateShuffleState(isShuffle);
    }

    private void updatePlayList(String playListName) {
        PlayListsDao playListsDao = new PlayListsDao(this);
        SongsDao songsDao = new SongsDao(this);

        PlayListModel playList = playListsDao.getSingleByName(playListName);
        List<SongModel> songs = songsDao.getAllByPlayList(playList.getId());

        player.setPlayList(songs);
    }

    private void setupDrawerPlayLists() {
        List<String> playLists = getPlayLists();
        ArrayAdapter<String> playListAdapater = new ArrayAdapter<String>(this, R.layout.playlist_item, playLists);

        lvDrawerPlayList.setAdapter(playListAdapater);
        lvDrawerPlayList.setOnItemClickListener(this);
    }

    private List<String> getPlayLists() {
        List<PlayListModel> playLists = DatabaseHelper.getInstance(this).getAllPlayLists();
        List<String> playListNames = new LinkedList<>();

        for (PlayListModel playList : playLists) {
            playListNames.add(playList.getName());
        }

        return playListNames;
    }


    private void toggleMediaPlayerRepeatState(String state) {
        switch (state) {
            case REPEATSTATE_OFF:
                updateRepeatState(REPEATSTATE_ALL);
                break;
            case REPEATSTATE_ALL:
                updateRepeatState(REPEATSTATE_ONE);
                break;
            case REPEATSTATE_ONE:
                updateRepeatState(REPEATSTATE_OFF);
                break;
        }
    }

    private void togglePlayState() {
        if (player.isPlaying()) {
            updatePlayStatus(PLAYERSTATE_PAUSED);
            songProgressBarHandler.removeCallbacks(trackBarUpdateTask);
        } else {
            updatePlayStatus(PLAYERSTATE_PLAYING);
            updateProgressBar();
        }
    }

    private void updateRepeatState(String state) {

        switch (state) {
            case REPEATSTATE_ALL:
                repeatState = REPEATSTATE_ALL;
                player.setLooping(false);
                btnRepeat.setImageResource(R.drawable.img_btn_repeat);
                break;
            case REPEATSTATE_ONE:
                repeatState = REPEATSTATE_ONE;
                player.setLooping(true);
                btnRepeat.setImageResource(R.drawable.img_btn_repeat_one);
                break;
            case REPEATSTATE_OFF:
                repeatState = REPEATSTATE_OFF;
                player.setLooping(false);
                btnRepeat.setImageResource(R.drawable.img_btn_repeat_disabled);
                break;
        }

        updateRepeatStateSettings();

    }

    private void toggleShuffleState(Boolean isShuffle) {
        updateShuffleState(!isShuffle);
    }

    private void updateShuffleState(Boolean isShuffle) {
        if (isShuffle) {
            btnShuffle.setImageResource(R.drawable.img_btn_shuffle_disabled);
        } else {
            btnShuffle.setImageResource(R.drawable.img_btn_shuffle);
        }
        this.isShuffle = isShuffle;
        updateShuffleStateSettings();

    }

    private void updatePlayStatus(String state) {
        switch (state) {
            case PLAYERSTATE_PLAYING:
                btnPlay.setImageResource(R.drawable.img_btn_paused);
                player.start();
                break;
            case PLAYERSTATE_PAUSED:
                btnPlay.setImageResource(R.drawable.img_btn_play);
                player.pause();
                break;
        }
    }

    private void playSong(int audioFileIndex) {
        songIndex = audioFileIndex;
        song = playList.get(songIndex);
        Log.i("here", song.getDateModified().toString());
        try {
            player.reset();
            player.setDataSource(song.getPath());
            player.prepare();
            player.start();

            tvCurrentAudioFile.setText(song.getName());
            updatePlayStatus(PLAYERSTATE_PLAYING);

            sbSongProgressBar.setProgress(0);
            sbSongProgressBar.setMax(100);

            updateProgressBar();

        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        switch (v.getId()) {
            case R.id.item_audio_files_list:
                Log.i("clickList", "" + position);
                playSong(position);
                break;
            case R.id.drawer_item_play_list:
                String playListName = (String) lvDrawerPlayList.getItemAtPosition(position);
                updatePlayList(playListName);
                updatePlayListDisplay(getFileNamesFromPlayList(playListName));
                break;
        }
    }

    private String[] getFileNamesFromPlayList(String playListName) {
        PlayListsDao playListsDao = new PlayListsDao(this);
        SongsDao songsDao = new SongsDao(this);

        PlayListModel playList = playListsDao.getSingleByName(playListName);
        List<SongModel> songs = songsDao.getAllByPlayList(playList.getId());

        String[] fileNames = new String[songs.size()];

        for (int i = 0; i < fileNames.length; i++) {
            fileNames[i] = songs.get(i).getName();
        }

        return fileNames;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_items, menu);
        return true;
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btnPlay:
                togglePlayState();
                break;

            case R.id.btnBackward:
                if (songIndex == playList.size() - 1)
                    playSong(0);
                else
                    playSong(songIndex - 1);
                break;

            case R.id.btnForward:
                if (isShuffle) {
                    playSong(Utilities.getRandomInt(playList.size(), 0));
                }
                if (songIndex == 0)
                    playSong(playList.size() - 1);
                else
                    playSong(songIndex + 1);
                break;

            case R.id.btnRepeat:
                toggleMediaPlayerRepeatState(repeatState);
                break;

            case R.id.btnShuffle:
                toggleShuffleState(isShuffle);
                break;

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        if (repeatState.equals(REPEATSTATE_ALL)) {
            songIndex++;
            if (isShuffle)
                songIndex = Utilities.getRandomInt(playList.size(), 0);
            playSong(songIndex);
        }

    }

    private Runnable trackBarUpdateTask = new Runnable() {
        @Override
        public void run() {
            int trackProgress = (int) (Utilities.getPercentage(player.getCurrentPosition(), player.getDuration()));
            sbSongProgressBar.setProgress(trackProgress);
            updateProgressBar();
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        songProgressBarHandler.removeCallbacks(trackBarUpdateTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        songProgressBarHandler.removeCallbacks(trackBarUpdateTask);
        int currentPosition = Utilities.getProgressToTimer(seekBar.getProgress(), player.getDuration());
        player.seekTo(currentPosition);
        updateProgressBar();
    }

    @Override
    public void onTaskCompleted() {
        SongsDao songsDao = new SongsDao(this);
        playList = songsDao.getAllByPlayList(1);
        int fileCount = playList.size();
        String[] fileNames = new String[fileCount];

        for (int i = 0; i < fileCount; i++) {
            fileNames[i] = playList.get(i).getName();
        }

        updatePlayListDisplay(fileNames);
    }

    public void updateProgressBar() {
        songProgressBarHandler.postDelayed(trackBarUpdateTask, SONG_PROGRESS_BAR_REFRESH_RATE);
    }

    private void updatePlayListDisplay(String[] fileNames) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.audiofile_item, fileNames);
        lvCurrentPlayList.setAdapter(adapter);
    }


    private void updateRepeatStateSettings() {
        SharedPreferences.Editor defPreference = PreferenceManager.getDefaultSharedPreferences(this).edit();
        defPreference.putString(getResources().getString(R.string.repeat_state), repeatState);
        defPreference.apply();
        defPreference.clear();
    }

    private void updateShuffleStateSettings() {
        SharedPreferences.Editor defPreference = PreferenceManager.getDefaultSharedPreferences(this).edit();
        defPreference.putBoolean(getResources().getString(R.string.is_shuffle), isShuffle);
        defPreference.apply();
        defPreference.clear();
    }
}
