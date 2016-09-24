package jon.happymusicplayer.com.happymusicplayer.data.models;

/**
 * Created by Jon on 8/14/2016.
 */
public class PlayListModel {


    private int id;
    private String name;

    public PlayListModel(int id, String name){
        this.id = id;
        this.name = name;
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

}
