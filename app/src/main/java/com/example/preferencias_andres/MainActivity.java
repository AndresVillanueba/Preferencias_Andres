package com.example.preferencias_andres;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    // Variables
    EditText Nombre, Email, Empresa, Edad, Sueldo;
    TextView Bienvenida;
    SharedPreferences preferencias;
    SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Cargar o crear "UserPrefs"
        preferencias = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        // Aplicar modo nocturno si está habilitado
        boolean modoNocturno = preferencias.getBoolean("night_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                modoNocturno ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Agregar solicitud de permiso para notificaciones en API 33 o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        Nombre = findViewById(R.id.edNombre);
        Email = findViewById(R.id.edEmail);
        Empresa = findViewById(R.id.edEmpresa);
        Edad = findViewById(R.id.edEdad);
        Sueldo = findViewById(R.id.edSueldo);
        Bienvenida = findViewById(R.id.tvBienvenida);
        // Cargar datos guardados
        cargarPreferencias();
        // Listener para actualizar el saludo si cambia "last_contact"
        listener = (sharedPrefs, key) -> {
            if (key.equals("last_contact")) {
                String ultimoContacto = sharedPrefs.getString("last_contact", "Ninguno");
                Bienvenida.setText("Bienvenido: " + ultimoContacto);
            }
        };
        preferencias.registerOnSharedPreferenceChangeListener(listener);
        // Botón "Guardar" para almacenar datos
        Button botonGuardar = findViewById(R.id.btnGuardar);
        botonGuardar.setOnClickListener(v -> {
            guardarPreferencias();
            Toast.makeText(getApplicationContext(), "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
        });

        // Botón "Siguiente" para abrir MainActivity2
        Button botonSiguiente = findViewById(R.id.btnSiguiente);
        botonSiguiente.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(i);
        });

        // Botón "Enviar Notif" para crear la notificación solo si está habilitada
        Button botonNotificacion = findViewById(R.id.btnNotificacion);
        botonNotificacion.setOnClickListener(v -> {
            boolean notificacionesHabilitadas = preferencias.getBoolean("notifications_enabled", false);
            if (notificacionesHabilitadas) {
                crearNotificacion("¡Notificación de pedido nuevo!");
            } else {
                Toast.makeText(MainActivity.this, "Las notificaciones están deshabilitadas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Cargar los datos guardados desde "UserPrefs"
    private void cargarPreferencias() {
        String nombre = preferencias.getString("nombre", "");
        String empresa = preferencias.getString("empresa", "Ribera del Tajo");
        String email = preferencias.getString("email", "cambiame@riberadeltajo.es");
        int edad = preferencias.getInt("edad", 18);
        float sueldo = preferencias.getFloat("sueldo", 15000);
        String ultimoContacto = preferencias.getString("last_contact", "Ninguno");

        Nombre.setText(nombre);
        Empresa.setText(empresa);
        Email.setText(email);
        Edad.setText(String.valueOf(edad));
        Sueldo.setText(String.valueOf(sueldo));
        Bienvenida.setText("Bienvenido, último contacto: " + ultimoContacto);
    }

    // Guardar datos (nombre, empresa, email, edad, sueldo, last_contact) en "UserPrefs"
    private void guardarPreferencias() {
        SharedPreferences.Editor editor = preferencias.edit();

        String nombre = Nombre.getText().toString().trim();
        editor.putString("nombre", nombre);
        editor.putString("empresa", Empresa.getText().toString().trim());
        editor.putString("email", Email.getText().toString().trim());

        String edadStr = Edad.getText().toString().trim();
        if (!edadStr.isEmpty() && edadStr.matches("\\d+")) {
            editor.putInt("edad", Integer.parseInt(edadStr));
        }

        String sueldoStr = Sueldo.getText().toString().trim();
        if (!sueldoStr.isEmpty()) {
            try {
                editor.putFloat("sueldo", Float.parseFloat(sueldoStr));
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Ingrese un sueldo válido", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Actualizar "last_contact" con el "nombre"
        editor.putString("last_contact", nombre);
        editor.apply();
    }

    // Crear y enviar la notificación
    public void crearNotificacion(String texto) {
        // Construir la notificación
        NotificationCompat.Builder constructorNotif = new NotificationCompat.Builder(this, "miCanal")
                .setSmallIcon(android.R.drawable.btn_star)
                .setContentTitle("Has recibido una notificación")
                .setContentText(texto)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Definir la actividad que se abre al pulsar la notificación
        Intent intentNotificacion = new Intent(this, NotificacionPulsada.class);
        TaskStackBuilder pila = TaskStackBuilder.create(this);
        pila.addParentStack(MainActivity.class);
        pila.addNextIntent(intentNotificacion);

        PendingIntent pendingIntentNotif = pila.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        constructorNotif.setContentIntent(pendingIntentNotif);

        // Crear el canal de notificación
        NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel("miCanal", "Canal de notificación de prueba",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notifManager.createNotificationChannel(canal);
        }

        notifManager.notify(1, constructorNotif.build());
    }
    // Eliminar el listener al destruir la actividad
    @Override
    protected void onDestroy(){
        super.onDestroy();
        preferencias.unregisterOnSharedPreferenceChangeListener(listener);
    }
    // Menú de los 3 puntitos para abrir SettingsActivity
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.action_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

