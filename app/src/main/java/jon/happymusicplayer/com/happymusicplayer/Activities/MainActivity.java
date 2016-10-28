package jon.happymusicplayer.com.happymusicplayer.Activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.R;
import jon.happymusicplayer.com.happymusicplayer.data.AppEventHandler;
import jon.happymusicplayer.com.happymusicplayer.data.AppMusicPlayer;
import jon.happymusicplayer.com.happymusicplayer.data.Presenter;
import jon.happymusicplayer.com.happymusicplayer.data.daos.SongsDao;
import jon.happymusicplayer.com.happymusicplayer.data.managers.SettingsManager;
import jon.happymusicplayer.com.happymusicplayer.tasks.UpdateSongDataTask;
import jon.happymusicplayer.com.happymusicplayer.tasks.UpdateProgressBarTask;
import jon.happymusicplayer.com.happymusicplayer.utils.Utilities;


public class MainActivity extends AppCompatActivity {
    private Presenter presenter;
    private AppMusicPlayer player;
    private AppEventHandler eventHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new Presenter(this);
        player = new AppMusicPlayer(this);
        eventHandler = new AppEventHandler(this, presenter, player);

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
        presenter.init(getWindowManager().getDefaultDisplay(), getResources().getConfiguration().orientation);
        init();

        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        ListAdapter adapter = presenter.getCurrentPlaylistListView().getAdapter();
        int position = presenter.getCurrentPlaylistListView().getFirstVisiblePosition();
        presenter.init(getWindowManager().getDefaultDisplay(), config.orientation);
        presenter.updateDrawerPlaylist(player.getAllPlayLists());
        presenter.getCurrentPlaylistListView().setAdapter(adapter);
        presenter.getCurrentPlaylistListView().setSelection(position);
        presenter.updateRepeatButton(player.getRepeatState());
        presenter.updateShuffleButton(player.getIsShuffle());
        presenter.updatePlayButton(player.isPlaying());

        if (player.getSong() != null)
            presenter.updateSongDetails(
                    player.getSong().getTitle(),
                    Utilities.getDurationAsText(player.getCurrentPosition(), player.getDuration())
            );

        setupEventHandlers();
    }

    private void init() {
        logPhoneDetails();
        setupEventHandlers();
        loadUserSettings();
        presenter.updateCurrentPlaylist(player.getPlaylist());
        presenter.updateDrawerPlaylist(player.getAllPlayLists());
        presenter.updateRepeatButton(player.getRepeatState());
        presenter.updateShuffleButton(player.getIsShuffle());
        presenter.setTrackBarUpdateTask(new UpdateProgressBarTask(player, presenter));
        SettingsManager.setContext(this);

        new UpdateSongDataTask(this, eventHandler).execute();
    }

    private void logPhoneDetails() {
        String version = android.os.Build.VERSION.RELEASE;
        Log.i("Android Version: ", version);
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
        presenter.getSearchView().setOnQueryTextListener(eventHandler);
        presenter.getSortButton().setOnClickListener(eventHandler);
        presenter.getActionMenuButton().setOnClickListener(eventHandler);
        presenter.getDrawerLayout().addDrawerListener(eventHandler);
        presenter.getCurrentPlaylistListView().setOnScrollListener(eventHandler);
        player.setOnCompletionListener(eventHandler);
        player.setOnPreparedListener(eventHandler);

        presenter.getFiltersGridView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        SongsDao songsDao = new SongsDao(getApplicationContext());
                        List<String> albums = songsDao.getAllAlbums();
                        ArrayAdapter adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.filter_album_item, albums);
                        presenter.getCurrentPlaylistListView().setAdapter(adapter);
                        break;

                    case 1:
                        songsDao = new SongsDao(getApplicationContext());
                        List<String> artists = songsDao.getAllArtists();
                        adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.filter_artist_item, artists);
                        presenter.getCurrentPlaylistListView().setAdapter(adapter);
                        break;

                    case 2:
                        songsDao = new SongsDao(getApplicationContext());
                        List<String> folders = songsDao.getAllFolders();
                        adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.filter_folder_item, folders);
                        presenter.getCurrentPlaylistListView().setAdapter(adapter);
                        break;

                    case 3:
                        break;
                }
            }
        });

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

    @Override
    protected void onPostResume() {
        super.onPostResume();
        new UpdateSongDataTask(this, eventHandler).execute();
    }
}
