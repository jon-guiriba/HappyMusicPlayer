package jon.happymusicplayer.com.happymusicplayer.Fragments;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import jon.happymusicplayer.com.happymusicplayer.R;
import jon.happymusicplayer.com.happymusicplayer.data.daos.SongsDao;

/**
 * Created by 80978448 on 10/28/2016.
 */

public class PreferencesFragment extends android.preference.PreferenceFragment {
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
        view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));

        return view;
    }
}