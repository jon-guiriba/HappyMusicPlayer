package jon.happymusicplayer.com.happymusicplayer.data;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.ListPopupWindow;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.R;
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
    private Button btnSubmitAddNewPlaylist;
    private ListView lvCurrentPlaylist;
    private ListView lvDrawerPlaylist;
    private ListView lvAddToPlaylistCurrentPlayLists;
    private TextView tvCurrentSong;
    private EditText etAddNewPlaylist;
    private SearchView searchView;
    private SeekBar sbSongProgressBar;
    private ListPopupWindow songOptions;
    private PopupWindow addToPlaylistPopupWindow;
    private PopupWindow createNewPlaylistPopupWindow;
    private PopupWindow sleepTimerPopupWindow;
    private Handler songProgressBarHandler = new Handler();
    private boolean isPlayerPrepared = false;
    private int trackProgress = 0;
    private DrawerLayout drawerLayout;

    private Runnable trackBarUpdateTask;
    private ArrayAdapter<SongModel> currentPlaylistAdapter;
    private ListView lvNumberPickerHours;

    public Presenter(Context context) {
        this.context = context;

        btnPlay = (ImageButton) ((Activity) context).findViewById(R.id.btnPlay);
        btnForward = (ImageButton) ((Activity) context).findViewById(R.id.btnForward);
        btnBackward = (ImageButton) ((Activity) context).findViewById(R.id.btnBackward);
        btnRepeat = (ImageButton) ((Activity) context).findViewById(R.id.btnRepeat);
        btnShuffle = (ImageButton) ((Activity) context).findViewById(R.id.btnShuffle);
        lvCurrentPlaylist = (ListView) ((Activity) context).findViewById(R.id.lvCurrentPlayList);
        lvNumberPickerHours = (ListView) ((Activity) context).findViewById(R.id.lvNumberPickerHours);
        tvCurrentSong = (TextView) ((Activity) context).findViewById(R.id.tvCurrentAudioFile);
        lvDrawerPlaylist = (ListView) ((Activity) context).findViewById(R.id.lvDrawerPlaylist);
        sbSongProgressBar = (SeekBar) ((Activity) context).findViewById(R.id.sbTrackProgressBar);
        drawerLayout = (DrawerLayout) ((Activity) context).findViewById(R.id.drawerLayout);
        init();
    }

    private void init() {
        sbSongProgressBar.setMax(100);
        lvCurrentPlaylist.setTextFilterEnabled(false);
        setupSongContextOptionsPopupWindow();
    }

    private void setupSongContextOptionsPopupWindow() {
        songOptions = new ListPopupWindow(context);
        songOptions.setAdapter(new ArrayAdapter<>(context, R.layout.context_menu_item, context.getResources().getTextArray(R.array.context_options)));
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

    public void updatePlaylist(List<SongModel> playlist) {
        if (playlist == null) return;

        currentPlaylistAdapter = new ArrayAdapter<>(context, R.layout.current_playlist_item, playlist);
        lvCurrentPlaylist.setAdapter(currentPlaylistAdapter);
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


    public void updateCurrentSongText(String songName) {
        this.tvCurrentSong.setText(songName);
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

    public String getAddNewPlaylistText() {
        return etAddNewPlaylist.getText().toString();
    }

    public ListView getDrawerPlaylistListView() {
        return this.lvDrawerPlaylist;
    }

    public ListView getAddToPlaylistCurrentPlayListsListView() {
        return lvAddToPlaylistCurrentPlayLists;
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

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public void removeSearchIcon() {
        int magId = context.getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView magImage = (ImageView) searchView.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
    }

    public void setupSearchView() {
        searchView = (SearchView) ((Activity) context).findViewById(R.id.action_search);
        removeSearchIcon();
    }
}
