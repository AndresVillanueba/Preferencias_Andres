package com.example.preferencias_andres;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.os.Bundle;
import android.content.SharedPreferences;

public class MainActivity2 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar el modo nocturno en onCreate usando "UserPrefs"
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean modoNocturno = prefs.getBoolean("night_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                modoNocturno ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        // Aquí se carga el fragmento de Preferencia
    }
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean modoNocturno = prefs.getBoolean("night_mode", false);
        int modoEsperado = modoNocturno ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        if (AppCompatDelegate.getDefaultNightMode() != modoEsperado) {
            AppCompatDelegate.setDefaultNightMode(modoEsperado);
            recreate();
        }
    }
}
