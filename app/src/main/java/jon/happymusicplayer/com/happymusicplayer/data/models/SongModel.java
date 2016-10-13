package jon.happymusicplayer.com.happymusicplayer.data.models;

import java.util.Date;

/**
 * Created by Jon on 8/14/2016.
 */
public class SongModel {


    private int id;
    private String name;
    private String path;
    private Date dateModified;

    public SongModel(int id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }

    public SongModel(int id, String name, String path, Date dateModified) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.dateModified = dateModified;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String toString() {
        return name;
    }

    public Date getDateModified() {
        return dateModified;
    }

}
