package jon.happymusicplayer.com.happymusicplayer.Activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.R;
import jon.happymusicplayer.com.happymusicplayer.data.daos.SongsDao;


/**
 * Created by Jon on 10/26/2016.
 */

public class PreferencesActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceFragment()).commit();
    }

    public static class PreferenceFragment extends android.preference.PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            setupThemes();
            setupSourceFolders();
        }

        private void setupSourceFolders() {
            MultiSelectListPreference sourceFolders = (MultiSelectListPreference) findPreference(getResources().getString(R.string.pref_source_folders));
            SongsDao songsDao = new SongsDao(this.getActivity());
            List<String> folders = songsDao.getAllAlbums();
            sourceFolders.setDefaultValue("" + 0);
            sourceFolders.setEntries(folders.toArray(new String[0]));
            sourceFolders.setEntryValues(folders.toArray(new String[0]));
        }

        private void setupThemes() {
            ListPreference themes = (ListPreference) findPreference(getResources().getString(R.string.pref_themes));
            themes.setDefaultValue("" + 0);
            themes.setEntries(getResources().getStringArray(R.array.themes));
            themes.setEntryValues(getResources().getStringArray(R.array.themes));
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);
            view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

            return view;
        }
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return android.preference.PreferenceFragment.class.getName().equals(fragmentName);
    }

}