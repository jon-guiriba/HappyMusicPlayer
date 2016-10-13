package jon.happymusicplayer.com.happymusicplayer.data.daos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.data.DatabaseHelper;
import jon.happymusicplayer.com.happymusicplayer.data.models.SongModel;
import jon.happymusicplayer.com.happymusicplayer.data.contracts.SongsContract;
import jon.happymusicplayer.com.happymusicplayer.utils.Utilities;

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

                song = new SongModel(sId, sName, sPath);
            }
        } finally {
            cursor.close();
        }

        return song;
    }

    public SongModel getSingleByPathAndPlayList(String path, int playListId) {

        String query = "SELECT  s.*" +
                "       FROM    playlist_items pi" +
                "               INNER JOIN playlists p ON p.id = pi.playlist_id" +
                "               INNER JOIN songs s ON s.id = pi.song_id" +
                "       WHERE   p.id = ?" +
                "       AND     s.path = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(playListId), path});
        if (cursor == null) return null;

        SongModel song = null;
        try {
            while (cursor.moveToNext()) {
                int sId = cursor.getInt(cursor.getColumnIndex(SongsContract.SongsEntry.ID));
                String sName = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.NAME));
                String sPath = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.PATH));

                song = new SongModel(sId, sName, sPath);
            }
        } finally {
            cursor.close();
        }

        return song;
    }

    public List<SongModel> getAll() {
        Cursor cursor = db.query(SongsContract.SongsEntry.TABLE_NAME, SongsContract.SongsEntry.ALL, null, null, null, null, SongsContract.SongsEntry.NAME);

        List<SongModel> allSongsList = new LinkedList<>();
        try {
            while (cursor.moveToNext()) {

                int id = cursor.getInt(cursor.getColumnIndex(SongsContract.SongsEntry.ID));
                String name = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.NAME));
                String path = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.PATH));

                SongModel song = new SongModel(id, name, path);

                allSongsList.add(song);
            }
        } finally {
            cursor.close();
        }

        return allSongsList;
    }

    public List<SongModel> getAllByPlayList(int playListId) {
        String query = "SELECT  s.*" +
                "       FROM    playlist_items pi" +
                "               INNER JOIN playlists p ON p.id = pi.playlist_id" +
                "               INNER JOIN songs s ON s.id = pi.song_id" +
                "       WHERE   p.id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(playListId)});
        if (cursor == null) return null;

        List<SongModel> songsList = new LinkedList<>();

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(SongsContract.SongsEntry.ID));
                String name = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.NAME));
                String path = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.PATH));
                String dateModified = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.DATE_MODIFIED));

                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                songsList.add(new SongModel(id, name, path, format.parse(dateModified)));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return songsList;
    }

    public List<SongModel> getAllRecentlyAdded() {
        String query = "SELECT  s.*" +
                "       FROM    playlist_items pi" +
                "               INNER JOIN playlists p ON p.id = pi.playlist_id" +
                "               INNER JOIN songs s ON s.id = pi.song_id";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor == null) return null;

        List<SongModel> songsList = new LinkedList<>();

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(SongsContract.SongsEntry.ID));
                String name = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.NAME));
                String path = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.PATH));
                String dateModified = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.DATE_MODIFIED));
                Date currentDate = Calendar.getInstance().getTime();

                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                if (Utilities.millisToDays(currentDate.getTime() - format.parse(dateModified).getTime()) < 3)
                    songsList.add(new SongModel(id, name, path, format.parse(dateModified)));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return songsList;
    }

}
