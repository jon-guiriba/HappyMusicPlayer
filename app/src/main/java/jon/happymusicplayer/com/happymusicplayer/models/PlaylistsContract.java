package jon.happymusicplayer.com.happymusicplayer.models;

import android.provider.BaseColumns;

/**
 * Created by Jon on 8/27/2016.
 */
public final class PlaylistsContract {
    private PlaylistsContract() {}

    public static class PlaylistsEntry implements BaseColumns {
        public static final String TABLE_NAME = "playlists";

        public static final String ID = "id";
        public static final String NAME = "name";

        public static final String[] ALL = {ID, NAME};

        public static final String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME +
                "( " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                       NAME + " VARCHAR(125) NOT NULL" +
                ")";

        public static final String SQL_DROP = "DROP TABLE " + TABLE_NAME;

    }
}