package jon.happymusicplayer.com.happymusicplayer.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import jon.happymusicplayer.com.happymusicplayer.models.PlayListModel;
import jon.happymusicplayer.com.happymusicplayer.models.PlaylistsContract;
import jon.happymusicplayer.com.happymusicplayer.models.SongModel;
import jon.happymusicplayer.com.happymusicplayer.models.SongsContract;

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
                int plId = cursor.getInt(cursor.getColumnIndex(SongsContract.SongsEntry.ID));
                String plName = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.NAME));

                playList = new PlayListModel(plId, plName);
            }
        } finally {
            cursor.close();
        }

        return playList;
    }
}
