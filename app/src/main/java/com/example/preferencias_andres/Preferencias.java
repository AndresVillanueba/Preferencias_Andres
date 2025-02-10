package com.example.preferencias_andres;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;

public class Preferencias extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //Cargar el archivo res/xml/preferencias.xml
        setPreferencesFromResource(R.xml.preferencias, rootKey);
    }
}
