package jon.happymusicplayer.com.happymusicplayer.data.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.data.DatabaseHelper;
import jon.happymusicplayer.com.happymusicplayer.data.contracts.PlaylistItemsContract;
import jon.happymusicplayer.com.happymusicplayer.data.models.PlayListItemModel;

/**
 * Created by Jon on 8/29/2016.
 */
public class PlaylistItemsDao {


    private final SQLiteDatabase db;

    public PlaylistItemsDao(Context context) {
        this.db = DatabaseHelper.getInstance(context).getReadableDatabase();
    }

    public List<PlayListItemModel> getAll() {

        Cursor cursor = db.query(
                PlaylistItemsContract.PlaylistItemsEntry.TABLE_NAME,
                PlaylistItemsContract.PlaylistItemsEntry.ALL,
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
                int id = cursor.getInt(cursor.getColumnIndex(PlaylistItemsContract.PlaylistItemsEntry.ID));
                int songId = cursor.getInt(cursor.getColumnIndex(PlaylistItemsContract.PlaylistItemsEntry.SONG_ID));
                int playListId = cursor.getInt(cursor.getColumnIndex(PlaylistItemsContract.PlaylistItemsEntry.PLAYLIST_ID));

                playListItems.add(new PlayListItemModel(id, songId, playListId));
            }
        } finally {
            cursor.close();
        }

        return playListItems;
    }

    public void addNewPlaylistItem(int playlistId, int songId) {
        ContentValues cv = new ContentValues();
        cv.put(PlaylistItemsContract.PlaylistItemsEntry.SONG_ID, songId);
        cv.put(PlaylistItemsContract.PlaylistItemsEntry.PLAYLIST_ID, playlistId);

        db.insert(PlaylistItemsContract.PlaylistItemsEntry.TABLE_NAME, null, cv);
    }


}
