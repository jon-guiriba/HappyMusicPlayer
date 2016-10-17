package jon.happymusicplayer.com.happymusicplayer.data.models;

import java.util.Date;

/**
 * Created by Jon on 8/14/2016.
 */
public class SongModel {


    private int id;
    private String title;
    private String artist;
    private String album;
    private int duration;
    private String path;
    private Date dateModified;

    public SongModel(int id, String title, String artist, String album, int duration, Date dateModified, String path) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.dateModified = dateModified;
        this.path = path;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public int getDuration() {
        return duration;
    }

    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }


    public String toString() {
        return title;
    }

}
