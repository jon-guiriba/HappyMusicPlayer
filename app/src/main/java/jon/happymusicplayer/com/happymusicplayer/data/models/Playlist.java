package jon.happymusicplayer.com.happymusicplayer.data.models;

import java.util.List;

/**
 * Created by 80978448 on 11/3/2016.
 */

public class Playlist {


    private String name;
    private List<SongModel> songs;

    public Playlist(String name) {
        this.name = name;
    }

    public List<SongModel> getSongs() {
        return songs;
    }

    public void setSongList(List<SongModel> songs) {
        this.songs = songs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int size(){
        return songs.size();
    }

    public SongModel get(int index){
        return songs.get(index);
    }

    public void remove(SongModel song) {
        for (SongModel s :  songs) {
            if (s.getId() == song.getId()) {
                songs.remove(s);
            }
        }
    }
}
