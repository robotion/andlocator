package com.jaeckel.locator;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * User: biafra
 * Date: Jun 12, 2010
 * Time: 11:35:05 AM
 */
public class EditPreferences extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
