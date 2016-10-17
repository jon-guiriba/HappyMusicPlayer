package jon.happymusicplayer.com.happymusicplayer;

import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import jon.happymusicplayer.com.happymusicplayer.data.AppEventHandler;
import jon.happymusicplayer.com.happymusicplayer.data.AppMusicPlayer;
import jon.happymusicplayer.com.happymusicplayer.data.Presenter;
import jon.happymusicplayer.com.happymusicplayer.data.managers.SettingsManager;
import jon.happymusicplayer.com.happymusicplayer.tasks.UpdateAllSongsPlayListTask;
import jon.happymusicplayer.com.happymusicplayer.tasks.UpdateProgressBarTask;


public class MainActivity extends AppCompatActivity  {
    private Presenter presenter;
    private AppMusicPlayer player;
    private AppEventHandler eventHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        presenter = new Presenter(this);
        player = new AppMusicPlayer(this);
        eventHandler = new AppEventHandler(this, presenter, player);
        init();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_items, menu);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.action_bar_view);
        }

        presenter.setupSearchView();
        presenter.getSearchView().setOnQueryTextListener(eventHandler);
        return true;
    }

    private void init() {

        logPhoneDetails();
        setupEventHandlers();
        loadUserSettings();
        loadDisplay();
        SettingsManager.setContext(this);

        new UpdateAllSongsPlayListTask(this, eventHandler).execute();

    }

    private void logPhoneDetails() {
        String version = android.os.Build.VERSION.RELEASE;
        Log.i("Android Version: ", version);
    }

    private void loadDisplay() {
        presenter.updatePlaylist(player.getPlaylist());
        presenter.updateDrawerPlaylist(player.getAllPlayLists());
        presenter.updateRepeatButton(player.getRepeatState());
        presenter.updateShuffleButton(player.getIsShuffle());
        presenter.setTrackBarUpdateTask(new UpdateProgressBarTask(player, presenter));
    }

    private void setupEventHandlers() {
        findViewById(R.id.btnPlay).setOnClickListener(eventHandler);
        findViewById(R.id.btnForward).setOnClickListener(eventHandler);
        findViewById(R.id.btnBackward).setOnClickListener(eventHandler);
        findViewById(R.id.btnRepeat).setOnClickListener(eventHandler);
        findViewById(R.id.btnShuffle).setOnClickListener(eventHandler);
        ((ListView) findViewById(R.id.lvCurrentPlayList)).setOnItemClickListener(eventHandler);
        ((ListView) findViewById(R.id.lvCurrentPlayList)).setOnItemLongClickListener(eventHandler);
        ((ListView) findViewById(R.id.lvDrawerPlaylist)).setOnItemClickListener(eventHandler);
        ((ListView) findViewById(R.id.lvDrawerPlaylist)).setOnItemLongClickListener(eventHandler);
        ((SeekBar) findViewById(R.id.sbTrackProgressBar)).setOnSeekBarChangeListener(eventHandler);

        presenter.getSongOptions().setOnItemClickListener(eventHandler);
        presenter.getDrawerLayout().addDrawerListener(eventHandler);

        player.setOnCompletionListener(eventHandler);
        player.setOnPreparedListener(eventHandler);
    }

    private void loadUserSettings() {
        SharedPreferences defPreference = PreferenceManager.getDefaultSharedPreferences(this);
        String playListName = defPreference.getString(getResources().getString(R.string.last_playlist), getResources().getString(R.string.all_songs));
        player.setPlaylist(playListName);
        player.setRepeatState(defPreference.getString(getResources().getString(R.string.repeat_state), AppMusicPlayer.REPEAT_OFF));
        player.setIsShuffle(defPreference.getBoolean(getResources().getString(R.string.is_shuffle), false));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }

    private boolean isBackPressed = false;
    @Override
    public void onBackPressed() {
        if (this.isBackPressed) {
            presenter.stopUpdateProgressBar();
            super.onBackPressed();
            return;
        }

        this.isBackPressed = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isBackPressed = false;
            }
        }, 2000);
    }




}
