package jon.happymusicplayer.com.happymusicplayer.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.HashMap;
import java.util.Map.Entry;

import jon.happymusicplayer.com.happymusicplayer.R;
import jon.happymusicplayer.com.happymusicplayer.adapters.OnTaskCompleted;
import jon.happymusicplayer.com.happymusicplayer.models.PlaylistsContract;
import jon.happymusicplayer.com.happymusicplayer.models.SongModel;
import jon.happymusicplayer.com.happymusicplayer.models.SongsContract;

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
        HashMap<String, SongModel> songsList = mfManager.getAllAudioFilesFromDisk();

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);

        SongsDao songsDao = new SongsDao(context);
        PlayListsDao playListsDao = new PlayListsDao(context);

        String playListName = context.getResources().getString(R.string.all_songs);

        for (Entry<String, SongModel> entry : songsList.entrySet()) {

            if (songsDao.getSingleByPathAndPlayList(entry.getKey(), playListsDao.getSingleByName(playListName).getId()) == null) {

                ContentValues cv = new ContentValues();
                cv.put(SongsContract.SongsEntry.NAME, entry.getValue().getName());
                cv.put(SongsContract.SongsEntry.PATH, entry.getValue().getPath());
                cv.put(SongsContract.SongsEntry.FK_PLAYLIST, playListsDao.getSingleByName(playListName).getId());

                db.insert(PlaylistsContract.PlaylistsEntry.TABLE_NAME, null, cv);
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onCompleteListener.onTaskCompleted();
    }
}
