package com.walhalla.whatismyipaddress.onboarding;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class OnboardingManager {

    private static final String KEY_ONBORDING = "key_onbord9";

    private final Context context;
    private final SharedPreferences preferences;

    public OnboardingManager(Context context) {
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isOnboarding() {
        boolean aa = preferences.getBoolean(KEY_ONBORDING, false);
        return aa;
    }

    public void isOnboarding(boolean b) {
        preferences.edit().putBoolean(KEY_ONBORDING, b).apply();
    }
}
