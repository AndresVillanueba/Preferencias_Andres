package com.example.preferencias_andres;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {
    // Variables
    private Switch interruptor;
    private Spinner spinnerTelefono;
    private CheckBox checkBox;
    private TextView UltimoContacto;
    // Preferencias de usuario (UserPrefs)
    private SharedPreferences prefsUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Obtener el archivo de preferencias "UserPrefs"
        prefsUsuario = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        // Leer la preferencia de modo nocturno y aplicarlo
        boolean modoNocturno = prefsUsuario.getBoolean("night_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                modoNocturno ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Enlazar vistas del layout
        interruptor = findViewById(R.id.switchNightMode);
        spinnerTelefono = findViewById(R.id.spinnerPhoneFormat);
        checkBox = findViewById(R.id.checkBoxNotifications);
        UltimoContacto = findViewById(R.id.textLastContact);
        //  Cargar en la interfaz los valores guardados
        cargarPreferencias();
        // Listeners para cambiar las preferencias solo cuando el usuario interactúa
        // Listener del modo nocturno (Switch)
        interruptor.setOnCheckedChangeListener((vistaBoton, estaMarcado) -> {
            // Comprueba si es un cambio real del usuario (y no de la UI)
            if (!vistaBoton.isPressed()) return;
            // Guardar la preferencia en "night_mode"
            prefsUsuario.edit().putBoolean("night_mode", estaMarcado).apply();
            // Aplicar modo nocturno (sin recreate() para evitar bucles)
            AppCompatDelegate.setDefaultNightMode(
                    estaMarcado ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        // Listener del Spinner para el formato de teléfono
        spinnerTelefono.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String formatoSeleccionado = parent.getItemAtPosition(position).toString();
                prefsUsuario.edit().putString("phone_format", formatoSeleccionado).apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        // Listener del CheckBox de notificaciones
        checkBox.setOnCheckedChangeListener((vistaBoton, estaMarcado) -> {
            if (!vistaBoton.isPressed()) return;
            prefsUsuario.edit().putBoolean("notifications_enabled", estaMarcado).apply();
        });
    }

    // Cargar en la UI los valores guardados en las preferencias
    private void cargarPreferencias() {
        // // Modo nocturno
        boolean modoNocturno = prefsUsuario.getBoolean("night_mode", false);
        interruptor.setChecked(modoNocturno);
        // // Formato de teléfono
        String formatoTelefono = prefsUsuario.getString("phone_format", "+34 España");
        String[] listaFormatos = getResources().getStringArray(R.array.phone_formats);
        for (int i = 0; i < listaFormatos.length; i++) {
            if (listaFormatos[i].equals(formatoTelefono)) {
                spinnerTelefono.setSelection(i);
                break;
            }
        }

        // Notificaciones habilitadas o no
        boolean notificacionesHabilitadas = prefsUsuario.getBoolean("notifications_enabled", false);
        checkBox.setChecked(notificacionesHabilitadas);

        // Último contacto guardado
        String ultimoContacto = prefsUsuario.getString("last_contact", "Ninguno");
        UltimoContacto.setText("Último contacto guardado: " + ultimoContacto);
    }
}
