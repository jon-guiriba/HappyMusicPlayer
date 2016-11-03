package jon.happymusicplayer.com.happymusicplayer.data.contracts;

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
        public static final String TITLE = "title";
        public static final String ARTIST = "artist";
        public static final String ALBUM = "album";
        public static final String DURATION = "duration";
        public static final String PATH = "path";
        public static final String DATE_MODIFIED = "date_modified";
        public static final String IS_BLACKLISTED = "is_blacklisted";

        public static final String[] ALL = {ID, TITLE, ARTIST,
                ALBUM, DURATION, PATH, DATE_MODIFIED,
                IS_BLACKLISTED
        };

        public static final String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME +
                        "( " +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        TITLE + " VARCHAR(80) NOT NULL, " +
                        ARTIST + " VARCHAR(80) NOT NULL, " +
                        ALBUM + " VARCHAR(80) NOT NULL, " +
                        DURATION + " INTEGER NOT NULL, " +
                        PATH + " VARCHAR(255) NOT NULL UNIQUE, " +
                        DATE_MODIFIED + " DATE NOT NULL DEFAULT CURRENT_DATE, " +
                        IS_BLACKLISTED + " TINYINT NOT NULL DEFAULT 0 " +
                        ")";

        public static final String SQL_DROP = "DROP TABLE " + TABLE_NAME;

        public static final String SQL_GET = "DROP TABLE " + TABLE_NAME;


    }
}