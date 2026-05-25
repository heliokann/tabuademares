package com.novoideal.tabuademares.util;

import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class ThemeHelper {

    public static final String PREF_THEME = "pref_theme";
    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK = "dark";
    public static final String THEME_SYSTEM = "system";

    static int toNightMode(String themeValue) {
        if (THEME_LIGHT.equals(themeValue)) return AppCompatDelegate.MODE_NIGHT_NO;
        if (THEME_DARK.equals(themeValue)) return AppCompatDelegate.MODE_NIGHT_YES;
        return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    }

    public static void applyTheme(Context context) {
        String theme = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_THEME, THEME_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(toNightMode(theme));
    }

    public static int saveAndApply(Context context, String themeValue) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putString(PREF_THEME, themeValue).apply();
        int mode = toNightMode(themeValue);
        AppCompatDelegate.setDefaultNightMode(mode);
        return mode;
    }
}
