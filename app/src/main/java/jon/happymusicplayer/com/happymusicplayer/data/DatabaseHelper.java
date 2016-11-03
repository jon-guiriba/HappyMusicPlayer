package jon.happymusicplayer.com.happymusicplayer.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import jon.happymusicplayer.com.happymusicplayer.R;
import jon.happymusicplayer.com.happymusicplayer.data.contracts.PlaylistItemsContract;
import jon.happymusicplayer.com.happymusicplayer.data.contracts.PlaylistsContract;
import jon.happymusicplayer.com.happymusicplayer.data.contracts.SongsContract;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper dbInstance;

    private static final String DATABASE_NAME = "happy_music_player_db";
    private static final int DATABASE_VERSION = 5;


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

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return dbInstance;
    }

}
