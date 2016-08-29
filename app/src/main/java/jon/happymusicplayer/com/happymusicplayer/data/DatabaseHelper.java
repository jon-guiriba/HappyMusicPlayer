package jon.happymusicplayer.com.happymusicplayer.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.models.PlaylistsContract;
import jon.happymusicplayer.com.happymusicplayer.models.SongModel;
import jon.happymusicplayer.com.happymusicplayer.models.SongsContract;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper dbInstance;

    private static final String DATABASE_NAME = "happy_music_player_db";
    private static final int DATABASE_VERSION = 3;


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

        ContentValues cv = new ContentValues();
        cv.put(PlaylistsContract.PlaylistsEntry.NAME, "All Songs");
        db.insert(PlaylistsContract.PlaylistsEntry.TABLE_NAME,null,cv);

        cv = new ContentValues();
        cv.put(PlaylistsContract.PlaylistsEntry.NAME, "Recently Added");
        db.insert(PlaylistsContract.PlaylistsEntry.TABLE_NAME,null,cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SongsContract.SongsEntry.SQL_DROP);
        db.execSQL(PlaylistsContract.PlaylistsEntry.SQL_DROP);

        db.execSQL(SongsContract.SongsEntry.SQL_CREATE);
        db.execSQL(PlaylistsContract.PlaylistsEntry.SQL_CREATE);

        ContentValues cv = new ContentValues();
        cv.put(PlaylistsContract.PlaylistsEntry.NAME, "All Songs");
        db.insert(PlaylistsContract.PlaylistsEntry.TABLE_NAME,null,cv);

        cv = new ContentValues();
        cv.put(PlaylistsContract.PlaylistsEntry.NAME, "Recently Added");
        db.insert(PlaylistsContract.PlaylistsEntry.TABLE_NAME,null,cv);
    }



    public List<String> getAllPlayLists(){
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(PlaylistsContract.PlaylistsEntry.TABLE_NAME, new String[] {PlaylistsContract.PlaylistsEntry.NAME}, null, null, null, null, null);

        List<String> playListsList = new LinkedList<String>();

        try {
            while(cursor.moveToNext()){

                String playList = cursor.getString(cursor.getColumnIndex(PlaylistsContract.PlaylistsEntry.NAME));

                playListsList.add(playList);
            }
        }finally {
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
