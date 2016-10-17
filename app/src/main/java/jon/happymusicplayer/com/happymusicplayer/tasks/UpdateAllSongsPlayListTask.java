package jon.happymusicplayer.com.happymusicplayer.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import jon.happymusicplayer.com.happymusicplayer.R;
import jon.happymusicplayer.com.happymusicplayer.adapters.OnTaskCompleted;
import jon.happymusicplayer.com.happymusicplayer.data.DatabaseHelper;
import jon.happymusicplayer.com.happymusicplayer.data.daos.PlaylistItemsDao;
import jon.happymusicplayer.com.happymusicplayer.data.daos.PlaylistsDao;
import jon.happymusicplayer.com.happymusicplayer.data.daos.SongsDao;
import jon.happymusicplayer.com.happymusicplayer.data.contracts.PlaylistItemsContract;
import jon.happymusicplayer.com.happymusicplayer.data.managers.MusicFilesManager;
import jon.happymusicplayer.com.happymusicplayer.data.models.PlayListModel;
import jon.happymusicplayer.com.happymusicplayer.data.models.SongModel;
import jon.happymusicplayer.com.happymusicplayer.data.contracts.SongsContract;

/**
 * Created by Jon on 8/29/2016.
 */
public class UpdateAllSongsPlayListTask extends AsyncTask<Object, Void, Void> {

    private final Context context;
    private final OnTaskCompleted onCompleteListener;

    public UpdateAllSongsPlayListTask(Context context, OnTaskCompleted onCompleteListener) {
        this.context = context;
        this.onCompleteListener = onCompleteListener;
    }


    @Override
    protected Void doInBackground(Object... params) {
        MusicFilesManager mfManager = new MusicFilesManager(context);
        mfManager.saveAllAudioFilesToDB();

        PlaylistsDao playlistsDao = new PlaylistsDao(context);
        PlayListModel playList = playlistsDao.getSingleByName(
                context.getResources().getString(R.string.all_songs)
        );

        PlaylistItemsDao playlistItemsDao = new PlaylistItemsDao(context);

        SongsDao songsDao = new SongsDao(context);
        List<SongModel> allSongs = songsDao.getAll();

        for (SongModel song : allSongs) {
            playlistItemsDao.addNewPlaylistItem(playList.getId(), song.getId());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onCompleteListener.onTaskCompleted();
    }
}
