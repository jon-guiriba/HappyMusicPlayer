package jon.happymusicplayer.com.happymusicplayer.data.contracts;

import android.provider.BaseColumns;

/**
 * Created by Jon on 8/27/2016.
 */
public final class PlaylistItemsContract {
    private PlaylistItemsContract() {
    }

    public static class PlaylistItemsEntry implements BaseColumns {
        public static final String TABLE_NAME = "playlist_items";

        public static final String ID = "id";
        public static final String SONG_ID = "song_id";
        public static final String PLAYLIST_ID = "playlist_id";

        public static final String[] ALL = {ID, SONG_ID, PLAYLIST_ID};

        public static final String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME +
                        "( " +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        PLAYLIST_ID + " INTEGER NOT NULL, " +
                        SONG_ID + " INTEGER NOT NULL, " +
                        " UNIQUE (" + PLAYLIST_ID + "," + SONG_ID + ")," +
                        " FOREIGN KEY (" + PLAYLIST_ID + ") REFERENCES " + PlaylistsContract.PlaylistsEntry.TABLE_NAME + "(" + PlaylistsContract.PlaylistsEntry.ID + ")," +
                        " FOREIGN KEY (" + SONG_ID + ") REFERENCES " + SongsContract.SongsEntry.TABLE_NAME + "(" + SongsContract.SongsEntry.ID + ")" +
                        ")";

        public static final String SQL_DROP = "DROP TABLE " + TABLE_NAME;

    }
}