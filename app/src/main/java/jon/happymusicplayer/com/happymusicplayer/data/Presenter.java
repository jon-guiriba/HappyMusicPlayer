package jon.happymusicplayer.com.happymusicplayer.data;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.Fragments.PlaylistFragment;
import jon.happymusicplayer.com.happymusicplayer.Fragments.SongDetailsFragment;
import jon.happymusicplayer.com.happymusicplayer.R;
import jon.happymusicplayer.com.happymusicplayer.adapters.AppPagerAdapter;
import jon.happymusicplayer.com.happymusicplayer.data.models.Playlist;
import jon.happymusicplayer.com.happymusicplayer.data.models.SongModel;

/**
 * Created by Jon on 10/13/2016.
 */

public class Presenter {

    Context context;
    private static final int SONG_PROGRESS_BAR_REFRESH_RATE = 40;

    private ImageButton btnPlay;
    private ImageButton btnForward;
    private ImageButton btnBackward;
    private ImageButton btnRepeat;
    private ImageButton btnShuffle;
    private ImageButton btnSort;
    private ImageButton btnArtist;
    private ImageButton btnAlbum;
    private ImageButton btnActionMenu;
    private Button btnSubmitAddNewPlaylist;
    private ListView lvCurrentPlaylist;
    private ListView lvDrawerPlaylist;
    private ListView lvAddToPlaylistCurrentPlayLists;
    private TextView tvSongTitle;
    private TextView tvSongDuration;
    private GridView gvFilters;
    private EditText etAddNewPlaylist;
    private SearchView searchView;
    private ViewPager viewPager;
    private SeekBar sbSongProgressBar;
    private ListPopupWindow songOptions;
    private ListPopupWindow actionMenu;
    private PopupWindow addToPlaylistPopupWindow;
    private PopupWindow createNewPlaylistPopupWindow;
    private PopupWindow sleepTimerPopupWindow;
    private PopupWindow sortPopupWindow;
    private Handler songProgressBarHandler = new Handler();
    private boolean isPlayerPrepared = false;
    private int trackProgress = 0;
    private DrawerLayout drawerLayout;
    private LinearLayout trackProgressLayout;
    private RelativeLayout playerControlsLayout;
    private Runnable trackBarUpdateTask;
    private ListView lvNumberPickerHours;
    private ListView lvSortOptions;
    private Toast toast;

    public Presenter(Context context) {
        this.context = context;
    }

    public void init(Display display, int orientation, AppMusicPlayer player) {
        switch (orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                ((Activity) context).setContentView(R.layout.activity_main_land);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                ((Activity) context).setContentView(R.layout.activity_main_port);
                break;
        }
        btnPlay = (ImageButton) ((Activity) context).findViewById(R.id.btnPlay);
        btnForward = (ImageButton) ((Activity) context).findViewById(R.id.btnForward);
        btnBackward = (ImageButton) ((Activity) context).findViewById(R.id.btnBackward);
        btnRepeat = (ImageButton) ((Activity) context).findViewById(R.id.btnRepeat);
        btnShuffle = (ImageButton) ((Activity) context).findViewById(R.id.btnShuffle);
        lvCurrentPlaylist = (ListView) ((Activity) context).findViewById(R.id.lvCurrentPlaylist);
        lvNumberPickerHours = (ListView) ((Activity) context).findViewById(R.id.lvNumberPickerHours);
        lvDrawerPlaylist = (ListView) ((Activity) context).findViewById(R.id.lvDrawerPlaylist);
        tvSongTitle = (TextView) ((Activity) context).findViewById(R.id.tvSongTitle);
        tvSongDuration = (TextView) ((Activity) context).findViewById(R.id.tvSongDuration);
        sbSongProgressBar = (SeekBar) ((Activity) context).findViewById(R.id.sbTrackProgressBar);
        drawerLayout = (DrawerLayout) ((Activity) context).findViewById(R.id.drawerLayout);
        playerControlsLayout = (RelativeLayout) ((Activity) context).findViewById(R.id.playerControlsLayout);
        trackProgressLayout = (LinearLayout) ((Activity) context).findViewById(R.id.trackProgressLayout);
        searchView = (SearchView) ((Activity) context).findViewById(R.id.actionSearch);
        btnSort = (ImageButton) ((Activity) context).findViewById(R.id.actionSort);
        btnActionMenu = (ImageButton) ((Activity) context).findViewById(R.id.actionMenu);
        removeSearchIcon();

        sbSongProgressBar.setMax(100);
        drawerLayout.setScrimColor(Color.TRANSPARENT);

        loadScrollText();
        loadFilters();
        loadPager(player);
    }

    private void loadPager(AppMusicPlayer player) {
        List<Fragment> fragments = new ArrayList<>();

        AppPagerAdapter adapter = new AppPagerAdapter(
                ((AppCompatActivity) context).getSupportFragmentManager(),
                player
        );
        viewPager = (ViewPager) ((Activity) context).findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
    }

    private void loadScrollText() {
        toast = new Toast(context);
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.scroll_textview,
                (ViewGroup) ((Activity) context).findViewById(R.id.lrScrollTextRoot));
        toast.setView(view);
    }

    public void setupSongContextOptionsPopupWindow() {
        songOptions = new ListPopupWindow(context);
        songOptions.setAdapter(new ArrayAdapter<>(context, R.layout.context_menu_item, context.getResources().getTextArray(R.array.context_options)));
        songOptions.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary)));
        songOptions.setWidth(300);
        songOptions.setHorizontalOffset(400);
        songOptions.setModal(true);
    }

    public void setupCreateNewPlaylistPopupWindow() {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupLayout = layoutInflater.inflate(R.layout.popup_add_new_playlist, null, false);
        createNewPlaylistPopupWindow = new PopupWindow(
                popupLayout,
                300,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        createNewPlaylistPopupWindow.setOutsideTouchable(true);
        createNewPlaylistPopupWindow.setFocusable(true);
        createNewPlaylistPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        etAddNewPlaylist = (EditText) popupLayout.findViewById(R.id.etAddNewPlaylist);
        etAddNewPlaylist.requestFocus();

        btnSubmitAddNewPlaylist = (Button) popupLayout.findViewById(R.id.btnSubmitAddNewPlaylist);

    }

    public void showCreateNewPlaylistPopupWindow() {
        createNewPlaylistPopupWindow.showAtLocation(((Activity) context).findViewById(R.id.main_relative_layout), Gravity.CENTER, 0, 0);

    }

    public void setupAddToPlaylistPopupWindow(List<String> playlists) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupLayout = layoutInflater.inflate(R.layout.popup_add_to_playlist, null, false);
        addToPlaylistPopupWindow = new PopupWindow(
                popupLayout,
                250,
                300,
                true);
        addToPlaylistPopupWindow.setOutsideTouchable(true);
        addToPlaylistPopupWindow.setFocusable(true);
        addToPlaylistPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        lvAddToPlaylistCurrentPlayLists = (ListView) popupLayout.findViewById(R.id.lvAddToPlayListCurrentPlaylists);

        ArrayAdapter addPlaylistCurrentPlayListsAdapter = new ArrayAdapter<>(context, R.layout.popup_playlist_item, playlists);
        lvAddToPlaylistCurrentPlayLists.setAdapter(addPlaylistCurrentPlayListsAdapter);
    }


    public void showAddToPlaylistPopupWindow() {
        addToPlaylistPopupWindow.showAtLocation(((Activity) context).findViewById(R.id.main_relative_layout), Gravity.CENTER, 0, 0);
    }

    public void updateCurrentPlaylist(Playlist playlist) {
        if (playlist == null) return;
        Log.i("playlist ", playlist.getName());
        viewPager.getAdapter().notifyDataSetChanged();
    }

    public void loadFilters() {
        gvFilters = (GridView) ((Activity) context).findViewById(R.id.gvFilters);

        List<HashMap<String, String>> list = new ArrayList<>();

        int[] icons = {
                R.drawable.img_album_action,
                R.drawable.img_artist_action,
                R.drawable.img_folder_action,
                R.drawable.img_genre_action
        };

        for (int i = 0; i < icons.length; i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("icon", "" + icons[i]);
            list.add(map);
        }

        String[] from = {"icon"};
        int[] to = {R.id.filterIcon};

        SimpleAdapter adapter = new SimpleAdapter(
                context,
                list,
                R.layout.filter_item,
                from,
                to
        );

        gvFilters.setAdapter(adapter);
    }

    public void updateDrawerPlaylist(List<String> drawerPlaylists) {
        ArrayAdapter<String> drawerPlaylistAdapater = new ArrayAdapter<String>(context, R.layout.playlist_item, drawerPlaylists);
        drawerPlaylistAdapater.add(context.getResources().getString(R.string.add_new));
        lvDrawerPlaylist.setAdapter(drawerPlaylistAdapater);
    }

    public void updateRepeatButton(String repeatState) {
        switch (repeatState) {
            case AppMusicPlayer.REPEAT_ALL:
                btnRepeat.setImageResource(R.drawable.img_btn_repeat);
                break;
            case AppMusicPlayer.REPEAT_ONE:
                btnRepeat.setImageResource(R.drawable.img_btn_repeat_one);
                break;
            case AppMusicPlayer.REPEAT_OFF:
                btnRepeat.setImageResource(R.drawable.img_btn_repeat_disabled);
                break;
        }
    }

    public void updateShuffleButton(boolean isShuffle) {
        if (isShuffle) {
            btnShuffle.setImageResource(R.drawable.img_btn_shuffle);
        } else {
            btnShuffle.setImageResource(R.drawable.img_btn_shuffle_disabled);
        }
    }

    public void updatePlayButton(boolean isPlaying) {
        if (isPlaying) {
            btnPlay.setImageResource(R.drawable.img_btn_paused);
        } else {
            btnPlay.setImageResource(R.drawable.img_btn_play);
        }
    }

    public void startUpdateProgressBar() {
        songProgressBarHandler.postDelayed(trackBarUpdateTask, SONG_PROGRESS_BAR_REFRESH_RATE);
    }

    public void updateProgressBar(int trackProgress) {
        sbSongProgressBar.setProgress(trackProgress);
        songProgressBarHandler.postDelayed(trackBarUpdateTask, SONG_PROGRESS_BAR_REFRESH_RATE);
    }

    public void stopUpdateProgressBar() {
        songProgressBarHandler.removeCallbacks(trackBarUpdateTask);
    }

    public void resetProgressBar() {
        sbSongProgressBar.setProgress(0);
    }

    public ListPopupWindow getSongOptions() {
        return songOptions;
    }


    public void updateSongDetails(String songName, String songDuration) {
        tvSongTitle.setText(songName);
        tvSongDuration.setText(songDuration);
    }

    public void showSongOptions(View view) {
        songOptions.setAnchorView(view);
        songOptions.show();
    }

    public void hideSongOptions() {
        songOptions.dismiss();
    }

    public void hideCreateNewPlaylistPopupWindow() {
        createNewPlaylistPopupWindow.dismiss();
    }

    public void hideAddPlaylistPopupWindow() {
        addToPlaylistPopupWindow.dismiss();
    }


    public void toggleTrackBarUpdate(boolean isPlayerPlaying) {
        if (isPlayerPlaying) {
            startUpdateProgressBar();
        } else {
            stopUpdateProgressBar();
        }
    }

    public void setTrackBarUpdateTask(Runnable trackBarUpdateTask) {
        this.trackBarUpdateTask = trackBarUpdateTask;
    }

    public void highlightSelectedPlaylistItem(int selectedPlaylistItemIndex) {
        lvCurrentPlaylist.setItemChecked(selectedPlaylistItemIndex, true);
    }

    public void setupSleepTimerPopupView() {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupLayout = layoutInflater.inflate(R.layout.popup_sleep_timer, null, false);
        sleepTimerPopupWindow = new PopupWindow(
                popupLayout,
                250,
                300,
                true);
        sleepTimerPopupWindow.setOutsideTouchable(true);
        sleepTimerPopupWindow.setFocusable(true);
        sleepTimerPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ArrayAdapter<String> numberPickerAdapter =
                new ArrayAdapter<>(
                        context,
                        R.layout.popup_number_picker_item,
                        context.getResources().getStringArray(R.array.number_set)
                );

        lvNumberPickerHours =
                (ListView) popupLayout.findViewById(R.id.lvNumberPickerHours);
        ListView lvNumberPickerMin =
                (ListView) popupLayout.findViewById(R.id.lvNumberPickerSecTens);
        ListView lvNumberPickerSec =
                (ListView) popupLayout.findViewById(R.id.lvNumberPickerSecOnes);

        lvNumberPickerHours.setAdapter(numberPickerAdapter);
        lvNumberPickerMin.setAdapter(numberPickerAdapter);
        lvNumberPickerSec.setAdapter(numberPickerAdapter);

    }

    public void showSetSleepTimerPopupWindow() {
        this.sleepTimerPopupWindow.showAtLocation(
                ((Activity) context).findViewById(R.id.main_relative_layout), Gravity.CENTER,
                0,
                0
        );
    }

    public void hideSleepTimerPopupWindow() {
        this.sleepTimerPopupWindow.dismiss();
    }

    public void removeSearchIcon() {
        int magId = context.getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView magImage = (ImageView) searchView.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
    }


    public void setupSortPopupView() {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupLayout = layoutInflater.inflate(R.layout.popup_sort, null, false);
        sortPopupWindow = new PopupWindow(
                popupLayout,
                250,
                350,
                true);
        sortPopupWindow.setOutsideTouchable(true);
        sortPopupWindow.setFocusable(true);
        sortPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ArrayAdapter<String> sortAdapter =
                new ArrayAdapter<>(
                        context,
                        R.layout.sort_options_item,
                        context.getResources().getStringArray(R.array.sort_options)
                );

        lvSortOptions = (ListView) popupLayout.findViewById(R.id.lvSortOptions);
        lvSortOptions.setAdapter(sortAdapter);

    }

    public void showSortPopupView() {
        sortPopupWindow.showAtLocation(((Activity) context).findViewById(R.id.main_relative_layout), Gravity.CENTER, 0, 0);
    }

    public void hideSortPopupView() {
        sortPopupWindow.dismiss();
    }

    public void updateSongDuration(String durationAsText) {
        tvSongDuration.setText(durationAsText);
    }


    public void setupActionMenuPopupWindow() {
        actionMenu = new ListPopupWindow(context);
        actionMenu.setAdapter(new ArrayAdapter<>(context, R.layout.action_menu_item, context.getResources().getTextArray(R.array.action_menu)));
        actionMenu.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary)));
        actionMenu.setWidth(250);
        actionMenu.setModal(true);
    }

    public void showActionMenu(View view) {
        actionMenu.setAnchorView(view);
        actionMenu.setVerticalOffset(-2);
        actionMenu.setHorizontalOffset(0);
        actionMenu.show();
    }

    public void hideActionMenu() {
        actionMenu.dismiss();
    }

    public ListPopupWindow getActionMenuPopupWindow() {
        return actionMenu;
    }

    public Toast getScrollTextToast() {
        return toast;
    }

    public ImageButton getActionMenuButton() {
        return btnActionMenu;
    }

    public ImageButton getSortButton() {
        return btnSort;
    }

    public ListView getSortListView() {
        return lvSortOptions;
    }

    public ImageButton getArtistButton() {
        return btnArtist;
    }

    public ImageButton getAlbumButton() {
        return btnAlbum;
    }

    public ImageButton getShuffleButton() {
        return btnShuffle;
    }

    public ImageButton getBackwardButton() {
        return btnBackward;
    }

    public ImageButton getPlayButton() {
        return btnPlay;
    }

    public ImageButton getForwardButton() {
        return btnForward;
    }

    public ImageButton getRepeatButton() {
        return btnRepeat;
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public SearchView getSearchView() {
        return searchView;
    }

    public ListView getCurrentPlaylistListView() {
        return lvCurrentPlaylist;
    }

    public ListView getNumberPickerHoursListView() {
        return lvNumberPickerHours;
    }

    public Button getSubmitAddNewPlaylistButton() {
        return btnSubmitAddNewPlaylist;
    }

    public String getAddNewPlaylistText() {
        return etAddNewPlaylist.getText().toString();
    }

    public ListView getDrawerPlaylistListView() {
        return this.lvDrawerPlaylist;
    }

    public ListView getAddToPlaylistCurrentPlayListsListView() {
        return lvAddToPlaylistCurrentPlayLists;
    }

    public GridView getFiltersGridView() {
        return gvFilters;
    }

    public ViewPager getPager() {
        return viewPager;
    }
}
