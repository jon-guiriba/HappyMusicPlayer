package jon.happymusicplayer.com.happymusicplayer;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
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
import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.data.DatabaseHelper;
import jon.happymusicplayer.com.happymusicplayer.data.MusicFilesManager;
import jon.happymusicplayer.com.happymusicplayer.adapters.OnTaskCompleted;
import jon.happymusicplayer.com.happymusicplayer.data.PlayListManager;
import jon.happymusicplayer.com.happymusicplayer.data.UpdateAllSongsPlayListTask;
import jon.happymusicplayer.com.happymusicplayer.models.SongModel;
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
    private ListView audioFilesListView;
    private SearchView searchView;
    private TextView tvCurrentAudioFile;
    private SeekBar sbSongProgressBar;
    private Handler songProgressBarHandler = new Handler();

    private List<SongModel> currentPlayList;
    private SongModel currentSong;

    private boolean isShuffle = false;
    private int currentAudioFileIndex = 0;

    static MediaPlayer mediaPlayer;
    private ListView lvPlayList;
    private ListView lvFeatures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnForward = (ImageButton) findViewById(R.id.btnForward);
        btnBackward = (ImageButton) findViewById(R.id.btnBackward);
        btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
        btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
        audioFilesListView = (ListView) findViewById(R.id.audioFilesListView);
        tvCurrentAudioFile = (TextView) findViewById(R.id.tv_currentAudioFile);
        sbSongProgressBar = (SeekBar) findViewById(R.id.trackProgressBar);
        lvPlayList = (ListView) findViewById(R.id.lvPlaylist);

        btnPlay.setOnClickListener(this);
        btnForward.setOnClickListener(this);
        btnBackward.setOnClickListener(this);
        btnRepeat.setOnClickListener(this);
        btnShuffle.setOnClickListener(this);

        updateCurrentPlayList();

        MusicFilesManager musicFilesManager = new MusicFilesManager(this);
        PlayListManager playListManager = new PlayListManager();

        sbSongProgressBar.setOnSeekBarChangeListener(this);

        int fileCount = currentPlayList.size();

        String[] fileNames = new String[fileCount];
        for (int i = 0; i < fileCount; i++) {
            fileNames[i] = currentPlayList.get(i).getName();
        }

        ArrayAdapter<String> musicFilesAdapter = new ArrayAdapter<String>(this, R.layout.audiofile_item, fileNames);
        audioFilesListView.setAdapter(musicFilesAdapter);
        audioFilesListView.setOnItemClickListener(this);

        ArrayAdapter<String> playListAdapater = new ArrayAdapter<String>(this, R.layout.playlist_item, playListManager.getAllPlaylists());
        lvPlayList.setAdapter(playListAdapater);
        lvPlayList.setOnItemClickListener(this);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);

        new UpdateAllSongsPlayListTask(this,this).execute();

    }

    private void toggleMediaPlayerRepeatState() {
        switch (repeatState) {
            case REPEATSTATE_OFF:
                repeatState = REPEATSTATE_ALL;
                mediaPlayer.setLooping(false);
                btnRepeat.setImageResource(R.drawable.img_btn_repeat);
                break;
            case REPEATSTATE_ALL:
                repeatState = REPEATSTATE_ONE;
                mediaPlayer.setLooping(true);
                btnRepeat.setImageResource(R.drawable.img_btn_repeat_one);
                break;
            case REPEATSTATE_ONE:
                repeatState = REPEATSTATE_OFF;
                mediaPlayer.setLooping(false);
                btnRepeat.setImageResource(R.drawable.img_btn_repeat_disabled);
                break;

        }
    }


    private void toggleMediaPlayerPlayState() {
        if (mediaPlayer.isPlaying()) {
            updatePlayStatus(PLAYERSTATE_PAUSED);
            songProgressBarHandler.removeCallbacks(trackBarUpdateTask);
        } else {
            updatePlayStatus(PLAYERSTATE_PLAYING);
            updateProgressBar();
        }
    }


    private void toggleMediaPlayerShuffleState() {
        if (isShuffle) {
            btnShuffle.setImageResource(R.drawable.img_btn_shuffle_disabled);
            isShuffle = false;
        } else {
            btnShuffle.setImageResource(R.drawable.img_btn_shuffle);
            isShuffle = true;
        }

    }

    private void updatePlayStatus(String state) {
        switch (state) {
            case PLAYERSTATE_PLAYING:
                btnPlay.setImageResource(R.drawable.img_btn_paused);
                mediaPlayer.start();
                break;
            case PLAYERSTATE_PAUSED:
                btnPlay.setImageResource(R.drawable.img_btn_play);
                mediaPlayer.pause();
                break;
        }
    }

    private void playSong(int audioFileIndex) {
        currentAudioFileIndex = audioFileIndex;
        currentSong = currentPlayList.get(currentAudioFileIndex);

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();

            tvCurrentAudioFile.setText(currentSong.getName());
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
                playSong(position);
                break;
            case R.id.item_play_list:
                break;
        }
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
                toggleMediaPlayerPlayState();
                break;

            case R.id.btnBackward:
                if (currentAudioFileIndex == currentPlayList.size() - 1)
                    playSong(0);
                else
                    playSong(currentAudioFileIndex - 1);

                ;
                break;

            case R.id.btnForward:
                if (isShuffle) {
                    playSong(Utilities.getRandomInt(currentPlayList.size(), 0));
                }

                if (currentAudioFileIndex == 0)
                    playSong(currentPlayList.size() - 1);
                else
                    playSong(currentAudioFileIndex + 1);


                break;

            case R.id.btnRepeat:
                toggleMediaPlayerRepeatState();
                break;

            case R.id.btnShuffle:
                toggleMediaPlayerShuffleState();
                break;

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        if (repeatState.equals(REPEATSTATE_ALL)) {
            currentAudioFileIndex++;
            if (isShuffle)
                currentAudioFileIndex = Utilities.getRandomInt(currentPlayList.size(), 0);
            playSong(currentAudioFileIndex);
        }

    }

    private Runnable trackBarUpdateTask = new Runnable() {
        @Override
        public void run() {
            int trackProgress = (int) (Utilities.getPercentage(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration()));
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
        int currentPosition = Utilities.getProgressToTimer(seekBar.getProgress(), mediaPlayer.getDuration());
        mediaPlayer.seekTo(currentPosition);
        updateProgressBar();
    }

    public void updateProgressBar() {
        songProgressBarHandler.postDelayed(trackBarUpdateTask, SONG_PROGRESS_BAR_REFRESH_RATE);
    }

    private void updateCurrentPlayList(){
        currentPlayList = DatabaseHelper.getInstance(this).getAllSongs();
    }

    @Override
    public void onTaskCompleted() {
        updateCurrentPlayList();
    }
}
