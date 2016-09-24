package jon.happymusicplayer.com.happymusicplayer.data.daos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.data.managers.DatabaseHelper;
import jon.happymusicplayer.com.happymusicplayer.data.contracts.PlayListItemsContract;
import jon.happymusicplayer.com.happymusicplayer.data.models.PlayListItemModel;

/**
 * Created by Jon on 8/29/2016.
 */
public class PlayListItemsDao {


    private final SQLiteDatabase db;

    public PlayListItemsDao(Context context) {
        this.db = DatabaseHelper.getInstance(context).getReadableDatabase();
    }

    public List<PlayListItemModel> getAll() {

        Cursor cursor = db.query(
                PlayListItemsContract.PlayListItemsEntry.TABLE_NAME,
                PlayListItemsContract.PlayListItemsEntry.ALL,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor == null) return null;

        List<PlayListItemModel> playListItems = new LinkedList<>();

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(PlayListItemsContract.PlayListItemsEntry.ID));
                int songId = cursor.getInt(cursor.getColumnIndex(PlayListItemsContract.PlayListItemsEntry.SONG_ID));
                int playListId = cursor.getInt(cursor.getColumnIndex(PlayListItemsContract.PlayListItemsEntry.PLAYLIST_ID));

                playListItems.add(new PlayListItemModel(id, songId, playListId));
            }
        } finally {
            cursor.close();
        }

        return playListItems;
    }



}
