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
        public static final String NAME = "name";
        public static final String PATH = "path";
        public static final String DATE_MODIFIED = "date_modified";

        public static final String[] ALL = {ID, NAME, PATH};

        public static final String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME +
                        "( " +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        NAME + " VARCHAR(80) NOT NULL, " +
                        PATH + " VARCHAR(255) NOT NULL UNIQUE, " +
                        DATE_MODIFIED + " DATE NOT NULL DEFAULT CURRENT_DATE " +
                        ")";

        public static final String SQL_DROP = "DROP TABLE " + TABLE_NAME;

        public static final String SQL_GET = "DROP TABLE " + TABLE_NAME;

    }
}