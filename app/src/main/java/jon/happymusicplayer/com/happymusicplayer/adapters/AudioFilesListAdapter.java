package jon.happymusicplayer.com.happymusicplayer.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import jon.happymusicplayer.com.happymusicplayer.data.models.SongModel;

/**
 * Created by Jon on 8/20/2016.
 */
public class AudioFilesListAdapter extends ArrayAdapter<SongModel> {

    private SongModel[] items;

    public AudioFilesListAdapter(Context context, int resource, SongModel[] items) {
        super(context, resource, items);

    }


}
