package jon.happymusicplayer.com.happymusicplayer.models;

import android.provider.BaseColumns;

/**
 * Created by Jon on 8/27/2016.
 */
public final class SongsContract {
    private SongsContract() {
    }

    public static class SongsEntry implements BaseColumns {
        public static final String TABLE_NAME = "songs";

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String PATH = "path";
        public static final String FK_PLAYLIST = "fk_play_list";

        public static final String[] ALL = {ID, NAME, PATH, FK_PLAYLIST};

        public static final String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME +
                        "( " +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        NAME + " VARCHAR(80) NOT NULL, " +
                        PATH + " VARCHAR(255) NOT NULL, " +
                        FK_PLAYLIST + " INTEGER REFERENCES " + PlaylistsContract.PlaylistsEntry.TABLE_NAME +
                        ")";

        public static final String SQL_DROP = "DROP TABLE " + TABLE_NAME;

        public static final String SQL_GET = "DROP TABLE " + TABLE_NAME;

    }
}