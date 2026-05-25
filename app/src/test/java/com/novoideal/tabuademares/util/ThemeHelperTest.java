package com.novoideal.tabuademares.util;

import androidx.appcompat.app.AppCompatDelegate;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ThemeHelperTest {

    @Test
    public void toNightMode_light_returnsNightNo() {
        assertEquals(AppCompatDelegate.MODE_NIGHT_NO, ThemeHelper.toNightMode("light"));
    }

    @Test
    public void toNightMode_dark_returnsNightYes() {
        assertEquals(AppCompatDelegate.MODE_NIGHT_YES, ThemeHelper.toNightMode("dark"));
    }

    @Test
    public void toNightMode_system_returnsFollowSystem() {
        assertEquals(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, ThemeHelper.toNightMode("system"));
    }

    @Test
    public void toNightMode_unknown_fallsBackToFollowSystem() {
        assertEquals(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, ThemeHelper.toNightMode("unknown"));
        assertEquals(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, ThemeHelper.toNightMode(""));
        assertEquals(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, ThemeHelper.toNightMode(null));
    }

    @Test
    public void constants_haveExpectedValues() {
        assertEquals("light", ThemeHelper.THEME_LIGHT);
        assertEquals("dark", ThemeHelper.THEME_DARK);
        assertEquals("system", ThemeHelper.THEME_SYSTEM);
        assertEquals("pref_theme", ThemeHelper.PREF_THEME);
    }
}
