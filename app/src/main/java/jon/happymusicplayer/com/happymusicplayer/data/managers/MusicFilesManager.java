package jon.happymusicplayer.com.happymusicplayer.data.managers;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.HashMap;
import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.data.daos.SongsDao;
import jon.happymusicplayer.com.happymusicplayer.data.models.SongModel;


public class MusicFilesManager {
    final String MEDIA_PATH = new String("/sdcard/");
    private Context context;
    private static final String[] audioExtensionsList = {".mp3", ".wma", ".m4a"};

    public MusicFilesManager(Context context) {
        this.context = context;
    }

    public void saveAllAudioFiles() {
        final Cursor songsCursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.IS_MUSIC,
                },
                null,
                null,
                "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");

        if (songsCursor == null) return;

        SongsDao songsDao = new SongsDao(context);

        if (songsCursor.moveToFirst()) {
            do {
                boolean isMusic = songsCursor.getInt(songsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC)) != 0;

                String title = songsCursor.getString(songsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                String artist = songsCursor.getString(songsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String album = songsCursor.getString(songsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                int duration = songsCursor.getInt(songsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                String path = songsCursor.getString(songsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

                if (isMusic) {
                    songsDao.addSong(title, artist, album, duration, path);
                }

            } while (songsCursor.moveToNext());
        }
        songsCursor.close();
    }

    public void saveAllAudioFilesFromFolders(List<String> folders){
        String folderSelection = getFolderSelection(folders);
        final Cursor songsCursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.IS_MUSIC,
                },
                MediaStore.Audio.Media.DATA + " REGEXP " + folderSelection,
                null,
                "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");

        if (songsCursor == null) return;

        SongsDao songsDao = new SongsDao(context);

        if (songsCursor.moveToFirst()) {
            do {
                boolean isMusic = songsCursor.getInt(songsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC)) != 0;

                String title = songsCursor.getString(songsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                String artist = songsCursor.getString(songsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String album = songsCursor.getString(songsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                int duration = songsCursor.getInt(songsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                String genre = songsCursor.getString(songsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                String path = songsCursor.getString(songsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

                if (isMusic) {
                    songsDao.addSong(title, artist, album, duration, path);
                }

            } while (songsCursor.moveToNext());
        }
        songsCursor.close();
    }

    private String getFolderSelection(List<String> paths) {
        String selection = "";

        for (String path : paths){
            selection.concat(path + "|");
        }

        return selection.substring(0, selection.length()-1);
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