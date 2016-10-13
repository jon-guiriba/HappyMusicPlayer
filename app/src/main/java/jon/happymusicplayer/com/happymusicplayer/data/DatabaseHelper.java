package jon.happymusicplayer.com.happymusicplayer.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.R;
import jon.happymusicplayer.com.happymusicplayer.data.contracts.PlaylistItemsContract;
import jon.happymusicplayer.com.happymusicplayer.data.models.PlayListModel;
import jon.happymusicplayer.com.happymusicplayer.data.contracts.PlaylistsContract;
import jon.happymusicplayer.com.happymusicplayer.data.models.SongModel;
import jon.happymusicplayer.com.happymusicplayer.data.contracts.SongsContract;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper dbInstance;

    private static final String DATABASE_NAME = "happy_music_player_db";
    private static final int DATABASE_VERSION = 14;


    private final Context context;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SongsContract.SongsEntry.SQL_CREATE);
        db.execSQL(PlaylistsContract.PlaylistsEntry.SQL_CREATE);
        db.execSQL(PlaylistItemsContract.PlaylistItemsEntry.SQL_CREATE);

        ContentValues cv = new ContentValues();
        cv.put(PlaylistsContract.PlaylistsEntry.NAME, context.getResources().getString(R.string.all_songs));
        db.insert(PlaylistsContract.PlaylistsEntry.TABLE_NAME, null, cv);

        cv = new ContentValues();
        cv.put(PlaylistsContract.PlaylistsEntry.NAME, context.getResources().getString(R.string.recently_added));
        db.insert(PlaylistsContract.PlaylistsEntry.TABLE_NAME, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(PlaylistItemsContract.PlaylistItemsEntry.SQL_DROP);
        db.execSQL(PlaylistsContract.PlaylistsEntry.SQL_DROP);
        db.execSQL(SongsContract.SongsEntry.SQL_DROP);

        db.execSQL(SongsContract.SongsEntry.SQL_CREATE);
        db.execSQL(PlaylistsContract.PlaylistsEntry.SQL_CREATE);
        db.execSQL(PlaylistItemsContract.PlaylistItemsEntry.SQL_CREATE);

        ContentValues cv = new ContentValues();
        cv.put(PlaylistsContract.PlaylistsEntry.NAME, "All Songs");
        db.insert(PlaylistsContract.PlaylistsEntry.TABLE_NAME, null, cv);

        cv = new ContentValues();
        cv.put(PlaylistsContract.PlaylistsEntry.NAME, "Recently Added");
        db.insert(PlaylistsContract.PlaylistsEntry.TABLE_NAME, null, cv);
    }

    public List<SongModel> getAllSongs() {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(SongsContract.SongsEntry.TABLE_NAME, SongsContract.SongsEntry.ALL, null, null, null, null, null);
        List<SongModel> songsList = new LinkedList<>();

        cursor.moveToFirst();
        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(SongsContract.SongsEntry.ID));
                String name = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.NAME));
                String path = cursor.getString(cursor.getColumnIndex(SongsContract.SongsEntry.PATH));

                songsList.add(new SongModel(id, name, path));


            }
        } finally {
            cursor.close();
        }

        return songsList;
    }


    public List<PlayListModel> getAllPlayLists() {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(PlaylistsContract.PlaylistsEntry.TABLE_NAME, PlaylistsContract.PlaylistsEntry.ALL, null, null, null, null, null);

        List<PlayListModel> playListsList = new LinkedList<>();

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

    public List<PlayListModel> getAllUserPlayLists() {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(
                PlaylistsContract.PlaylistsEntry.TABLE_NAME,
                PlaylistsContract.PlaylistsEntry.ALL,
                "id!=1 AND id!=2",
                null,
                null,
                null,
                null);

        List<PlayListModel> playListsList = new LinkedList<>();

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


    public static synchronized DatabaseHelper getInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return dbInstance;
    }

}
