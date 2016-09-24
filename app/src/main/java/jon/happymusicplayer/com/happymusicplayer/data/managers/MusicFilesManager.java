package jon.happymusicplayer.com.happymusicplayer.data.managers;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.Date;
import java.util.HashMap;

import jon.happymusicplayer.com.happymusicplayer.data.models.SongModel;


public class MusicFilesManager {
    final String MEDIA_PATH = new String("/sdcard/");
    private Context context;
    private static final String[] audioExtensionsList = {".mp3", ".wma", ".m4a"};

    public MusicFilesManager(Context context) {
        this.context = context;
    }

    public HashMap<String, SongModel> getAllAudioFilesFromDisk() {
        final Cursor songsCursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA},
                null,
                null,
                "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");

        if (songsCursor == null) return null;

        HashMap<String, SongModel> fileModels = new HashMap<String, SongModel>();

        int i = 0;
        if (songsCursor.moveToFirst()) {
            do {
                String fileName = songsCursor.getString(songsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                String filePath = songsCursor.getString(songsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

                if (isMusicFile(filePath)) {
                    SongModel file = new SongModel(0, fileName, filePath);
                    fileModels.put(filePath, file);
                    i++;
                }

            } while (songsCursor.moveToNext());
        }
        songsCursor.close();

        return fileModels;
    }

    private Boolean isMusicFile(String filePath) {
        filePath = filePath.toLowerCase();

        for (String extension : audioExtensionsList) {
            if (filePath.endsWith(extension))
                return true;
        }

        return false;
    }

}