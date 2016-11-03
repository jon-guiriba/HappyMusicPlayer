package jon.happymusicplayer.com.happymusicplayer.data.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.data.DatabaseHelper;
import jon.happymusicplayer.com.happymusicplayer.data.contracts.PlaylistItemsContract;
import jon.happymusicplayer.com.happymusicplayer.data.contracts.PlaylistsContract;
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
        while (cursor.moveToNext()) {
            song = getSongModel(cursor);
        }
        cursor.close();

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
        while (cursor.moveToNext()) {
            song = getSongModel(cursor);
        }
        cursor.close();

        return song;
    }

    public List<SongModel> getAll() {
        Cursor cursor = db.query(
                SongsContract.SongsEntry.TABLE_NAME,
                SongsContract.SongsEntry.ALL,
                null,
                null,
                null,
                null,
                SongsContract.SongsEntry.TITLE);

        List<SongModel> allSongsList = new ArrayList<>();

        while (cursor.moveToNext()) {
            allSongsList.add(getSongModel(cursor));
        }
        cursor.close();

        return allSongsList;
    }

    public List<SongModel> getAllByPlayList(int playListId) {

        String query = "SELECT      s.*" +
                "       FROM        " + PlaylistItemsContract.PlaylistItemsEntry.TABLE_NAME + " pi" +
                "                   INNER JOIN " + PlaylistsContract.PlaylistsEntry.TABLE_NAME + " p ON p.id = pi.playlist_id" +
                "                   INNER JOIN " + SongsContract.SongsEntry.TABLE_NAME + " s ON s.id = pi.song_id" +
                "       WHERE       p." + PlaylistsContract.PlaylistsEntry.ID + " = ? AND " +
                "                   s." + SongsContract.SongsEntry.IS_BLACKLISTED + " = 0" +
                "       ORDER BY    s." + SongsContract.SongsEntry.TITLE;

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(playListId)});

        boolean isCursorEmpty = !(cursor.moveToFirst()) || cursor.getCount() == 0;
        if (isCursorEmpty) return null;

        List<SongModel> songsList = new ArrayList<>();

        while (cursor.moveToNext()) {
            songsList.add(getSongModel(cursor));
        }
        cursor.close();
        return songsList;
    }

    public List<SongModel> getAllByPlayList(int playListId, String orderBy) {
        String query = "SELECT      s.*" +
                "       FROM        " + PlaylistItemsContract.PlaylistItemsEntry.TABLE_NAME + " pi" +
                "                   INNER JOIN " + PlaylistsContract.PlaylistsEntry.TABLE_NAME + " p ON p.id = pi.playlist_id" +
                "                   INNER JOIN " + SongsContract.SongsEntry.TABLE_NAME + " s ON s.id = pi.song_id" +
                "       WHERE       p." + PlaylistsContract.PlaylistsEntry.ID + " = ? AND " +
                "                   s." + SongsContract.SongsEntry.IS_BLACKLISTED + " = 0" +
                "       ORDER BY    s." + orderBy;

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(playListId)});

        boolean isCursorEmpty = !(cursor.moveToFirst()) || cursor.getCount() == 0;
        if (isCursorEmpty) return null;

        List<SongModel> songsList = new ArrayList<>();

        while (cursor.moveToNext()) {
            songsList.add(getSongModel(cursor));
        }
        cursor.close();
        return songsList;
    }

    public List<SongModel> getAllRecentlyAdded() {
        String query = "SELECT  s.*" +
                "       FROM    playlist_items pi" +
                "               INNER JOIN playlists p ON p.id = pi.playlist_id" +
                "               INNER JOIN songs s ON s.id = pi.song_id" +
                "       WHERE   s.is_blacklisted = 0";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor == null) return null;

        List<SongModel> songsList = new ArrayList<>();

        while (cursor.moveToNext()) {
            String dateModified = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.DATE_MODIFIED));
            Date currentDate = Calendar.getInstance().getTime();

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            try {
                if (Utilities.millisToDays(currentDate.getTime() - format.parse(dateModified).getTime()) < 3)
                    songsList.add(getSongModel(cursor));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        cursor.close();

        return songsList;
    }

    public List<String> getAllArtists() {
        Cursor cursor = db.query(
                SongsContract.SongsEntry.TABLE_NAME,
                new String[]{SongsContract.SongsEntry.ARTIST},
                null,
                null,
                SongsContract.SongsEntry.ARTIST,
                null,
                null
        );

        List<String> artists = new ArrayList<>();

        while (cursor.moveToNext()) {
            artists.add(cursor.getString(0));
        }

        cursor.close();

        return artists;
    }

    public List<SongModel> getAllByArtist(String artist) {
        Cursor cursor = db.query(
                SongsContract.SongsEntry.TABLE_NAME,
                SongsContract.SongsEntry.ALL,
                SongsContract.SongsEntry.ARTIST + "=?",
                new String[]{artist},
                null,
                null,
                null
        );

        List<SongModel> songsList = new ArrayList<>();

        while (cursor.moveToNext()) {
            songsList.add(getSongModel(cursor));
        }

        cursor.close();

        return songsList;
    }

    public List<SongModel> getAllByAlbum(String album) {
        Cursor cursor = db.query(
                SongsContract.SongsEntry.TABLE_NAME,
                SongsContract.SongsEntry.ALL,
                SongsContract.SongsEntry.ALBUM + "=?",
                new String[]{album},
                null,
                null,
                null
        );

        List<SongModel> songsList = new ArrayList<>();

        while (cursor.moveToNext()) {
            songsList.add(getSongModel(cursor));
        }

        cursor.close();

        return songsList;
    }

    public List<SongModel> getAllByFolder(String folder) {
        Cursor cursor = db.query(
                SongsContract.SongsEntry.TABLE_NAME,
                SongsContract.SongsEntry.ALL,
                SongsContract.SongsEntry.PATH + " LIKE ?",
                new String[]{folder+"%"},
                null,
                null,
                null
        );

        List<SongModel> songsList = new ArrayList<>();

        while (cursor.moveToNext()) {
            songsList.add(getSongModel(cursor));
        }

        cursor.close();

        return songsList;
    }

    public List<String> getAllAlbums() {
        Cursor cursor = db.query(
                SongsContract.SongsEntry.TABLE_NAME,
                new String[]{SongsContract.SongsEntry.ALBUM},
                null,
                null,
                SongsContract.SongsEntry.ALBUM,
                null,
                null
        );

        List<String> albums = new ArrayList<>();

        while (cursor.moveToNext()) {
            albums.add(cursor.getString(0));
        }

        cursor.close();

        return albums;
    }

    public List<String> getAllFolders() {
        String query =
                "       SELECT      REPLACE(path, '/' || title , '') as folderPath " +
                        "       FROM        songs " +
                        "       GROUP BY    folderPath ";

        Cursor cursor = db.rawQuery(query, null);

        List<String> folders = new ArrayList<>();

        while (cursor.moveToNext()) {
            folders.add(cursor.getString(0));
        }
        cursor.close();

        return folders;
    }

    public void addSong(String title, String artist, String album, int duration, String path, String genre) {
        ContentValues cv = new ContentValues();
        cv.put(SongsContract.SongsEntry.TITLE, title);
        cv.put(SongsContract.SongsEntry.ARTIST, artist);
        cv.put(SongsContract.SongsEntry.ALBUM, album);
        cv.put(SongsContract.SongsEntry.DURATION, duration);
        cv.put(SongsContract.SongsEntry.PATH, path);
        cv.put(SongsContract.SongsEntry.GENRE, genre);
        db.insertWithOnConflict(SongsContract.SongsEntry.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private SongModel getSongModel(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(SongsContract.SongsEntry.ID));
        String title = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.TITLE));
        String artist = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.ARTIST));
        String album = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.ALBUM));
        String path = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.PATH));
        String genre = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.GENRE));
        int duration = cursor.getInt(cursor.getColumnIndex(SongsContract.SongsEntry.DURATION));
        String dateModified = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.DATE_MODIFIED));

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-dd-MM").parse(dateModified);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new SongModel(id, title, artist, album, duration, date, path, genre);
    }

    public void deleteSong(SongModel song) {
        deleteSongFromPlaylistItems(song);
        deleteSongFromSongs(song);
    }

    private void deleteSongFromSongs(SongModel song) {
        db.delete(
                SongsContract.SongsEntry.TABLE_NAME,
                SongsContract.SongsEntry.ID + "=?",
                new String[]{"" + song.getId()}
        );
    }

    private void deleteSongFromPlaylistItems(SongModel song) {
        db.delete(
                PlaylistItemsContract.PlaylistItemsEntry.TABLE_NAME,
                PlaylistItemsContract.PlaylistItemsEntry.SONG_ID + "=?",
                new String[]{"" + song.getId()}
        );
    }

    public void setSongBlacklist(SongModel song, int blacklist) {
        ContentValues cv = new ContentValues();
        cv.put(SongsContract.SongsEntry.IS_BLACKLISTED, blacklist);

        db.update(
                SongsContract.SongsEntry.TABLE_NAME,
                cv,
                SongsContract.SongsEntry.ID + "=?",
                new String[]{"" + song.getId()}
        );
    }

}
