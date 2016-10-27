package jon.happymusicplayer.com.happymusicplayer.data.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceManager;

import jon.happymusicplayer.com.happymusicplayer.R;

/**
 * Created by Jon on 9/25/2016.
 */
public class SettingsManager {

    private static Context context;

    public static void setContext(Context context){
        SettingsManager.context = context;
    }

    public static void updateRepeatStateSettings(String value) {
        SharedPreferences.Editor defPreference = PreferenceManager.getDefaultSharedPreferences(context).edit();
        defPreference.putString(context.getResources().getString(R.string.repeat_state), value);
        defPreference.apply();
        defPreference.clear();
    }

    public static void updateShuffleStateSettings(Boolean value) {
        SharedPreferences.Editor defPreference = PreferenceManager.getDefaultSharedPreferences(context).edit();
        defPreference.putBoolean(context.getString(R.string.is_shuffle), value);
        defPreference.apply();
        defPreference.clear();
    }


}
