package jon.happymusicplayer.com.happymusicplayer.models;

/**
 * Created by Jon on 8/14/2016.
 */
public class SongModel {


    private int id;
    private String name;
    private String path;
    private int playListId;

    public SongModel(int id, String name, String path, int playListId) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.playListId = playListId;
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
        return path;
    }

    public int getPlayListId() {
        return playListId;
    }

    public void setPlayListId(int playListId) {
        this.playListId = playListId;
    }

}
