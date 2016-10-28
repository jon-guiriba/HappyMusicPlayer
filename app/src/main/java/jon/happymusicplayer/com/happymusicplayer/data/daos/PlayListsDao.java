package jon.happymusicplayer.com.happymusicplayer.data.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.data.DatabaseHelper;
import jon.happymusicplayer.com.happymusicplayer.data.models.PlayListModel;
import jon.happymusicplayer.com.happymusicplayer.data.contracts.PlaylistsContract;

/**
 * Created by Jon on 8/29/2016.
 */
public class PlaylistsDao {

    private final SQLiteDatabase db;

    public PlaylistsDao(Context context) {
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

    public List<PlayListModel> getAllUserPlayLists() {
        Cursor cursor = db.query(
                PlaylistsContract.PlaylistsEntry.TABLE_NAME,
                PlaylistsContract.PlaylistsEntry.ALL,
                PlaylistsContract.PlaylistsEntry.ID + "!=1 AND " +
                        PlaylistsContract.PlaylistsEntry.ID + " !=2",
                null,
                null,
                null,
                PlaylistsContract.PlaylistsEntry.NAME
        );

        List<PlayListModel> playListsList = new ArrayList<>();

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(PlaylistsContract.PlaylistsEntry.ID));
                String name = cursor.getString(cursor.getColumnIndex(PlaylistsContract.PlaylistsEntry.NAME));

                playListsList.add(new PlayListModel(id, name));
            }
        } finally {
            cursor.close();
        }

        return playListsList;
    }

    public List<PlayListModel> getAllPlayLists() {
        Cursor cursor = db.query(
                PlaylistsContract.PlaylistsEntry.TABLE_NAME,
                PlaylistsContract.PlaylistsEntry.ALL,
                null,
                null,
                null,
                null,
                PlaylistsContract.PlaylistsEntry.NAME
        );

        List<PlayListModel> playListsList = new ArrayList<>();

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(PlaylistsContract.PlaylistsEntry.ID));
                String name = cursor.getString(cursor.getColumnIndex(PlaylistsContract.PlaylistsEntry.NAME));

                playListsList.add(new PlayListModel(id, name));
            }
        } finally {
            cursor.close();
        }

        return playListsList;
    }

    public void addNewPlaylist(String playlistName) {
        ContentValues cv = new ContentValues();
        cv.put(PlaylistsContract.PlaylistsEntry.NAME, playlistName);
        db.insert(PlaylistsContract.PlaylistsEntry.TABLE_NAME, null, cv);
    }
}
