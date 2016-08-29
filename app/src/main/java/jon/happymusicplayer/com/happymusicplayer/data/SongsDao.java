package jon.happymusicplayer.com.happymusicplayer.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.models.SongModel;
import jon.happymusicplayer.com.happymusicplayer.models.SongsContract;

/**
 * Created by Jon on 8/29/2016.
 */
public class SongsDao {


    private final SQLiteDatabase db;

    public SongsDao(Context context) {
        this.db = DatabaseHelper.getInstance(context).getReadableDatabase();
    }

    public SongModel getSingleByPath(String path) {

        Cursor cursor = db.query(
                SongsContract.SongsEntry.TABLE_NAME,
                SongsContract.SongsEntry.ALL,
                SongsContract.SongsEntry.PATH + " = ?",
                new String[]{path},
                null,
                null,
                null);

        if (cursor == null) return null;

        SongModel song = null;
        try {
            while (cursor.moveToNext()) {
                int sId = cursor.getInt(cursor.getColumnIndex(SongsContract.SongsEntry.ID));
                String sName = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.NAME));
                String sPath = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.PATH));
                int sPlayListId = cursor.getInt(cursor.getColumnIndex(SongsContract.SongsEntry.FK_PLAYLIST));

                song = new SongModel(sId, sName, sPath, sPlayListId);
            }
        } finally {
            cursor.close();
        }

        return song;
    }

    public SongModel getSingleByPathAndPlayList(String path, int playListId) {

        Cursor cursor = db.query(
                SongsContract.SongsEntry.TABLE_NAME,
                SongsContract.SongsEntry.ALL,
                SongsContract.SongsEntry.PATH + " = ? AND" +
                        SongsContract.SongsEntry.FK_PLAYLIST + " = ?",
                new String[]{path, String.valueOf(playListId)},
                null,
                null,
                null);

        if (cursor == null) return null;

        SongModel song = null;
        try {
            while (cursor.moveToNext()) {
                int sId = cursor.getInt(cursor.getColumnIndex(SongsContract.SongsEntry.ID));
                String sName = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.NAME));
                String sPath = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.PATH));
                int sPlayListId = cursor.getInt(cursor.getColumnIndex(SongsContract.SongsEntry.FK_PLAYLIST));

                song = new SongModel(sId, sName, sPath, sPlayListId);
            }
        } finally {
            cursor.close();
        }

        return song;
    }

    public List<SongModel> getAllSongs() {
        Cursor cursor = db.query(SongsContract.SongsEntry.TABLE_NAME, SongsContract.SongsEntry.ALL, null, null, null, null, SongsContract.SongsEntry.NAME);

        List<SongModel> allSongsList = new LinkedList<>();
        try {
            while (cursor.moveToNext()) {

                int id = cursor.getInt(cursor.getColumnIndex(SongsContract.SongsEntry.ID));
                String name = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.NAME));
                String path = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.PATH));
                int playListId = cursor.getInt(cursor.getColumnIndex(SongsContract.SongsEntry.FK_PLAYLIST));

                SongModel song = new SongModel(id, name, path, playListId);

                allSongsList.add(song);
            }
        } finally {
            cursor.close();
        }

        return allSongsList;
    }
}
