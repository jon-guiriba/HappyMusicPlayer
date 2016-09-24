package jon.happymusicplayer.com.happymusicplayer.data.models;

/**
 * Created by Jon on 8/14/2016.
 */
public class PlayListItemModel {


    private int id;
    private int songId;
    private int playListId;

    public PlayListItemModel(int id, int songId, int playListId ){
        this.id = id;
        this.songId = songId;
        this.playListId = playListId;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayListId() {
        return playListId;
    }

    public void setPlayListId(int playListId) {
        this.playListId = playListId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public String toString(){
        return "id: " + id  +" songId: " + songId + " playListId: " + playListId;
    }

}
