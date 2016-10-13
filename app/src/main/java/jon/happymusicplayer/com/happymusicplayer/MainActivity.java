package jon.happymusicplayer.com.happymusicplayer;

import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.widget.SearchView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.data.AppMusicPlayer;
import jon.happymusicplayer.com.happymusicplayer.adapters.OnTaskCompleted;
import jon.happymusicplayer.com.happymusicplayer.data.Presenter;
import jon.happymusicplayer.com.happymusicplayer.data.daos.PlaylistItemsDao;
import jon.happymusicplayer.com.happymusicplayer.data.daos.PlayListsDao;
import jon.happymusicplayer.com.happymusicplayer.data.daos.SongsDao;
import jon.happymusicplayer.com.happymusicplayer.data.managers.SettingsManager;
import jon.happymusicplayer.com.happymusicplayer.data.models.PlayListModel;
import jon.happymusicplayer.com.happymusicplayer.eventhandlers.OnMediaPlayerCompletionListener;
import jon.happymusicplayer.com.happymusicplayer.tasks.UpdateAllSongsPlayListTask;
import jon.happymusicplayer.com.happymusicplayer.data.models.SongModel;
import jon.happymusicplayer.com.happymusicplayer.utils.Utilities;


public class MainActivity extends AppCompatActivity implements OnClickListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener,
        SeekBar.OnSeekBarChangeListener, OnTaskCompleted, SearchView.OnQueryTextListener {
    private Presenter presenter;

    private List<SongModel> playList;
    private SongModel selectedSong;
    AppMusicPlayer player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        player = new AppMusicPlayer(this);
        presenter = new Presenter(this);

        init();

        new UpdateAllSongsPlayListTask(this, this).execute();
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

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setIconifiedByDefault(false);

        int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView magImage = (ImageView) searchView.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

        searchView.setQueryHint("Search");
        searchView.setMaxWidth(250);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    private void init() {
        setupEventHandlers();
        loadPreferences();
        loadDisplay();
        SettingsManager.setContext(this);
    }

    private void loadDisplay() {
        presenter.updatePlaylist(player.getPlayList());
        presenter.updateDrawerPlaylist(player.getAllPlayLists());
        presenter.updatePlaylist(player.getPlayList());
        presenter.updateRepeatButton(player.getRepeatState());
        presenter.updateShuffleButton(player.getIsShuffle());
    }

    private void setupEventHandlers() {
        findViewById(R.id.btnPlay).setOnClickListener(this);
        findViewById(R.id.btnForward).setOnClickListener(this);
        findViewById(R.id.btnBackward).setOnClickListener(this);
        findViewById(R.id.btnRepeat).setOnClickListener(this);
        findViewById(R.id.btnShuffle).setOnClickListener(this);
        ((ListView) findViewById(R.id.lvCurrentPlayList)).setOnItemClickListener(this);
        ((ListView) findViewById(R.id.lvCurrentPlayList)).setOnItemLongClickListener(this);
        ((ListView) findViewById(R.id.lvDrawerPlaylist)).setOnItemClickListener(this);
        ((ListView) findViewById(R.id.lvDrawerPlaylist)).setOnItemLongClickListener(this);
        ((SeekBar) findViewById(R.id.sbTrackProgressBar)).setOnSeekBarChangeListener(this);
        presenter.getSongOptions().setOnItemClickListener(this);

        OnMediaPlayerCompletionListener onMediaPlayerCompletionListener =
                new OnMediaPlayerCompletionListener(player, presenter);
        player.setOnCompletionListener(onMediaPlayerCompletionListener);
    }

    private void loadPreferences() {
        SharedPreferences defPreference = PreferenceManager.getDefaultSharedPreferences(this);
        String playListName = defPreference.getString(getResources().getString(R.string.last_playlist), getResources().getString(R.string.all_songs));
        player.setPlayList(playListName);
        player.setRepeatState(defPreference.getString(getResources().getString(R.string.repeat_state), AppMusicPlayer.REPEAT_OFF));
        player.setIsShuffle(defPreference.getBoolean(getResources().getString(R.string.is_shuffle), false));
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        presenter.showSongOptions(view);
        selectedSong = (SongModel) parent.getItemAtPosition(position);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

        switch (v.getId()) {
            case R.id.current_playlist_item:
                player.playSong(position);
                presenter.updateCurrentSongText(player.getSong().getName());
                presenter.resetProgressBar();

                presenter.updatePlayButton(player.isPlaying());

                int trackProgress = Utilities.getPercentage(player.getCurrentPosition(), player.getDuration());
                presenter.startUpdateProgressBar(player.isPrepared(), trackProgress);
                break;

            case R.id.drawer_item_play_list:
                String playListName = (String) parent.getItemAtPosition(position);

                if (playListName.equals(getResources().getString(R.string.add_new))) {
                    presenter.setupCreateNewPlaylistPopupWindow();
                    presenter.getSubmitAddNewPlaylistButton().setOnClickListener(this);
                    presenter.showCreateNewPlaylistPopupWindow();
                    break;
                }

                player.setPlayList(playListName);
                presenter.updatePlaylist(player.getPlayList());
                break;

            case R.id.context_menu_item:
                presenter.setupAddToPlaylistPopupWindow(player.getAllUserPlayLists());
                presenter.getAddToPlaylistCurrentPlayListsListView().setOnItemClickListener(this);
                presenter.showAddToPlaylistPopupWindow();
                presenter.hideSongOptions();
                break;

            case R.id.popup_add_to_playlist_item:
                playListName = (String) parent.getItemAtPosition(position);
                PlayListsDao plDao = new PlayListsDao(this);
                PlayListModel playlist = plDao.getSingleByName(playListName);

                PlaylistItemsDao plItemsDao = new PlaylistItemsDao(this);
                plItemsDao.addNewPlaylistItem(playlist.getId(), selectedSong.getId());

                presenter.hideAddPlaylistPopupWindow();
                break;
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btnPlay:
                player.togglePlayState();
                presenter.updatePlayButton(player.isPlaying());
                break;

            case R.id.btnBackward:
                player.playPrevSong();
                updateViewCurrentSongText();
                presenter.updatePlayButton(player.isPlaying());
                presenter.resetProgressBar();
                break;
            case R.id.btnForward:
                player.playNextSong();
                updateViewCurrentSongText();
                presenter.updatePlayButton(player.isPlaying());
                presenter.resetProgressBar();
                break;

            case R.id.btnRepeat:
                player.toggleRepeatState();
                presenter.updateRepeatButton(player.getRepeatState());
                SettingsManager.updateRepeatStateSettings(player.getRepeatState());
                break;

            case R.id.btnShuffle:
                player.toggleShuffleState();
                presenter.updateShuffleButton(player.getIsShuffle());
                SettingsManager.updateShuffleStateSettings(player.getIsShuffle());
                break;

            case R.id.btnSubmitAddNewPlaylist:
                PlayListsDao plDao = new PlayListsDao(this);
                plDao.addNewPlaylist(presenter.getAddNewPlaylistText());
                presenter.updateDrawerPlaylist(player.getAllPlayLists());
                presenter.hideCreateNewPlaylistPopupWindow();
                break;
        }
    }

    private void updateViewCurrentSongText() {
        presenter.updateCurrentSongText(player.getSong().getName());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        presenter.stopUpdateProgressBar();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        presenter.stopUpdateProgressBar();
        if (!player.isPlaying()) return;

        int currentPosition = Utilities.getProgressToTimer(seekBar.getProgress(), player.getDuration());
        player.seekTo(currentPosition);

        int trackProgress = Utilities.getPercentage(player.getCurrentPosition(), player.getDuration());
        presenter.startUpdateProgressBar(player.isPlaying(), trackProgress);
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
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
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
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ArrayAdapter<SongModel> lvCurrentPlaylistAdapter =
                (ArrayAdapter<SongModel>) presenter.getCurrentPlaylistListView().getAdapter();
        lvCurrentPlaylistAdapter.getFilter().filter(newText);
        return true;
    }
}
