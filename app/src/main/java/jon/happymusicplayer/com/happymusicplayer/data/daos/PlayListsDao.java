package jon.happymusicplayer.com.happymusicplayer.data.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import jon.happymusicplayer.com.happymusicplayer.data.DatabaseHelper;
import jon.happymusicplayer.com.happymusicplayer.data.models.PlayListModel;
import jon.happymusicplayer.com.happymusicplayer.data.contracts.PlaylistsContract;

/**
 * Created by Jon on 8/29/2016.
 */
public class PlayListsDao {


    private final SQLiteDatabase db;

    public PlayListsDao(Context context) {
        this.db = DatabaseHelper.getInstance(context).getReadableDatabase();
    }

    public PlayListModel getSingleByName(String name) {

        Cursor cursor = db.query(
                PlaylistsContract.PlaylistsEntry.TABLE_NAME,
                PlaylistsContract.PlaylistsEntry.ALL,
                PlaylistsContract.PlaylistsEntry.NAME + " = ?",
                new String[]{name},
                null,
                null,
                null
        );

        if (cursor == null) return null;

        PlayListModel playList = null;

        try {
            while (cursor.moveToNext()) {
                int plId = cursor.getInt(cursor.getColumnIndex(PlaylistsContract.PlaylistsEntry.ID));
                String plName = cursor.getString(cursor.getColumnIndex(PlaylistsContract.PlaylistsEntry.NAME));

                playList = new PlayListModel(plId, plName);
            }
        } finally {
            cursor.close();
        }

        return playList;
    }

    public void addNewPlaylist(String playlistName){
        ContentValues cv = new ContentValues();
        cv.put(PlaylistsContract.PlaylistsEntry.NAME, playlistName);
        db.insert(PlaylistsContract.PlaylistsEntry.TABLE_NAME, null, cv);
    }
}
