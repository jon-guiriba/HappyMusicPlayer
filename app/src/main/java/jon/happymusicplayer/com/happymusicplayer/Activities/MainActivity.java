package jon.happymusicplayer.com.happymusicplayer.Activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import jon.happymusicplayer.com.happymusicplayer.EventHandlers.AppEventHandler;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new Presenter(this);
        player = new AppMusicPlayer(this);
        AppEventHandler.setContext(this);
        AppEventHandler.setPlayer(player);
        AppEventHandler.setPresenter(presenter);

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
        presenter.init(getWindowManager().getDefaultDisplay(), getResources().getConfiguration().orientation, player);
        init();
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        ListAdapter adapter = presenter.getCurrentPlaylistListView().getAdapter();
        int position = presenter.getCurrentPlaylistListView().getFirstVisiblePosition();
        presenter.init(getWindowManager().getDefaultDisplay(), config.orientation, player);
        presenter.updateDrawerPlaylist(player.getAllPlayLists());
        presenter.getCurrentPlaylistListView().setAdapter(adapter);
        presenter.getCurrentPlaylistListView().setSelection(position);
        presenter.updateRepeatButton(player.getRepeatState());
        presenter.updateShuffleButton(player.getIsShuffle());
        presenter.updatePlayButton(player.isPlaying());

        if (player.getSong() != null)
            presenter.updateSongShortDetails(
                    player.getSong().getTitle(),
                    Utilities.getDurationAsText(player.getCurrentPosition(), player.getDuration())
            );

        setupEventHandlers();
    }

    private void init() {
        logPhoneDetails();
        setupEventHandlers();
        loadUserSettings();
        presenter.updatePagerPlaylist();
        presenter.updateDrawerPlaylist(player.getAllPlayLists());
        presenter.updateRepeatButton(player.getRepeatState());
        presenter.updateShuffleButton(player.getIsShuffle());
        presenter.setTrackBarUpdateTask(new UpdateProgressBarTask(player, presenter));
        SettingsManager.setContext(this);

        new UpdateSongDataTask(this, AppEventHandler.getInstance()).execute();
    }

    private void logPhoneDetails() {
        String version = android.os.Build.VERSION.RELEASE;
        Log.i("Android Version: ", version);
    }

    private void setupEventHandlers() {
        findViewById(R.id.btnPlay).setOnClickListener(AppEventHandler.getInstance());
        findViewById(R.id.btnForward).setOnClickListener(AppEventHandler.getInstance());
        findViewById(R.id.btnBackward).setOnClickListener(AppEventHandler.getInstance());
        findViewById(R.id.btnRepeat).setOnClickListener(AppEventHandler.getInstance());
        findViewById(R.id.btnShuffle).setOnClickListener(AppEventHandler.getInstance());
//        ((ListView) findViewById(R.id.lvCurrentPlayList)).setOnItemClickListener(eventHandler);
//        ((ListView) findViewById(R.id.lvCurrentPlayList)).setOnItemLongClickListener(eventHandler);
//        presenter.getCurrentPlaylistListView().setOnScrollListener(eventHandler);
        ((ListView) findViewById(R.id.lvDrawerPlaylist)).setOnItemClickListener(AppEventHandler.getInstance());
        ((ListView) findViewById(R.id.lvDrawerPlaylist)).setOnItemLongClickListener(AppEventHandler.getInstance());
        ((SeekBar) findViewById(R.id.sbTrackProgressBar)).setOnSeekBarChangeListener(AppEventHandler.getInstance());
        presenter.getSearchView().setOnQueryTextListener(AppEventHandler.getInstance());
        presenter.getSortButton().setOnClickListener(AppEventHandler.getInstance());
        presenter.getActionMenuButton().setOnClickListener(AppEventHandler.getInstance());
        presenter.getDrawerLayout().addDrawerListener(AppEventHandler.getInstance());
        player.setOnCompletionListener(AppEventHandler.getInstance());
        player.setOnPreparedListener(AppEventHandler.getInstance());

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
//                        presenter.getCurrentPlaylistListView().setAdapter(adapter);
                        break;

                    case 2:
                        songsDao = new SongsDao(getApplicationContext());
                        List<String> folders = songsDao.getAllFolders();
                        adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.filter_folder_item, folders);
//                        presenter.getCurrentPlaylistListView().setAdapter(adapter);
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
        new UpdateSongDataTask(this, AppEventHandler.getInstance()).execute();
    }
}
