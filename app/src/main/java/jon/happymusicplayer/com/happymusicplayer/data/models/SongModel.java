package jon.happymusicplayer.com.happymusicplayer.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jon on 8/14/2016.
 */
public class SongModel implements Parcelable {


    private int id;
    private String title;
    private String artist;
    private String album;
    private int duration;
    private String path;
    private Date dateModified;

    public SongModel(int id, String title, String artist, String album,
                     int duration, Date dateModified, String path) {
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

    // parcelable below
    protected SongModel(Parcel in) {
        id = in.readInt();
        title = in.readString();
        artist = in.readString();
        album = in.readString();
        duration = in.readInt();
        path = in.readString();
        long tmpDateModified = in.readLong();
        dateModified = tmpDateModified != -1 ? new Date(tmpDateModified) : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(album);
        dest.writeInt(duration);
        dest.writeString(path);
        dest.writeLong(dateModified != null ? dateModified.getTime() : -1L);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SongModel> CREATOR = new Parcelable.Creator<SongModel>() {
        @Override
        public SongModel createFromParcel(Parcel in) {
            return new SongModel(in);
        }

        @Override
        public SongModel[] newArray(int size) {
            return new SongModel[size];
        }
    };
}
