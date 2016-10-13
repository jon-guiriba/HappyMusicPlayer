package jon.happymusicplayer.com.happymusicplayer.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.HashMap;
import java.util.Map.Entry;

import jon.happymusicplayer.com.happymusicplayer.R;
import jon.happymusicplayer.com.happymusicplayer.adapters.OnTaskCompleted;
import jon.happymusicplayer.com.happymusicplayer.data.DatabaseHelper;
import jon.happymusicplayer.com.happymusicplayer.data.daos.PlayListsDao;
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

        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();

        Cursor cursor = db.query(SongsContract.SongsEntry.TABLE_NAME, SongsContract.SongsEntry.ALL, null, null, null, null, null);

        MusicFilesManager mfManager = new MusicFilesManager(context);
        HashMap<String, SongModel> songsFromDisk = mfManager.getAllAudioFilesFromDisk();

        SongsDao songsDao = new SongsDao(context);
        PlayListsDao playListsDao = new PlayListsDao(context);

        String allSongsPlayList = context.getResources().getString(R.string.all_songs);

        for (Entry<String, SongModel> songEntry : songsFromDisk.entrySet()) {

            String path = songEntry.getKey();
            SongModel song = songsDao.getSingleByPath(path);

            if (song == null) {
                addSongToDB(songEntry.getValue());
                song = songsDao.getSingleByPath(path);
            }

            PlayListModel playList = playListsDao.getSingleByName(allSongsPlayList);

            boolean isSongAlreadyExists = songsDao.getSingleByPathAndPlayList(song.getPath(), playList.getId()) != null;

            if (!isSongAlreadyExists) {
                addPlayListItem(song, playList);
            }
        }

        return null;
    }

    private void addPlayListItem(SongModel song, PlayListModel playlist) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(PlaylistItemsContract.PlaylistItemsEntry.PLAYLIST_ID, playlist.getId());
        cv.put(PlaylistItemsContract.PlaylistItemsEntry.SONG_ID, song.getId());
        db.insert(PlaylistItemsContract.PlaylistItemsEntry.TABLE_NAME, null, cv);
    }

    private void addSongToDB(SongModel songFromDisk) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();

        ContentValues cv = new ContentValues();

        int suffixStart = songFromDisk.getName().lastIndexOf(".");

        String songName = songFromDisk.getName();
        if (suffixStart != -1) {
            songName = songFromDisk.getName().substring(0, suffixStart);
        }


        cv.put(SongsContract.SongsEntry.NAME, songName);
        cv.put(SongsContract.SongsEntry.PATH, songFromDisk.getPath());

        db.insert(SongsContract.SongsEntry.TABLE_NAME, null, cv);

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onCompleteListener.onTaskCompleted();
    }
}
