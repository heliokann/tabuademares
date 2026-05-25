package com.novoideal.tabuademares.settings;

import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.util.ThemeHelper;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        ListPreference themePref = findPreference(ThemeHelper.PREF_THEME);
        if (themePref != null) {
            themePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    ThemeHelper.saveAndApply(requireContext(), (String) newValue);
                    requireActivity().recreate();
                    return true;
                }
            });
        }
    }
}
