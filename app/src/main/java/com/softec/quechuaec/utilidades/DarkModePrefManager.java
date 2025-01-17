package com.softec.quechuaec.utilidades;

import android.content.Context;
import android.content.SharedPreferences;

public class DarkModePrefManager {
    // Shared preferences file name
    private static final String PREF_NAME = "alumni_dark_mode";
    private static final String IS_NIGHT_MODE = "IsNightMode";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    // shared pref mode
    int PRIVATE_MODE = 0;


    public DarkModePrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setDarkMode(boolean isFirstTime) {
        editor.putBoolean(IS_NIGHT_MODE, isFirstTime);
        editor.commit();
    }

    public boolean isNightMode() {
        return pref.getBoolean(IS_NIGHT_MODE, false);
    }

}