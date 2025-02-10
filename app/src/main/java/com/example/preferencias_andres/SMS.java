package com.example.preferencias_andres;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMS extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // // Procesar el SMS recibido
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus == null) return;
            SmsMessage[] msgs = new SmsMessage[pdus.length];
            String origen = null;
            String mensaje = null;
            for (int i = 0; i < msgs.length; i++) {
                String formato = bundle.getString("format");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], formato);
                } else {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                origen = msgs[i].getOriginatingAddress();
                mensaje = msgs[i].getMessageBody();
            }
            Toast.makeText(context, "SMS Recibido de " + origen + ": " + mensaje, Toast.LENGTH_LONG).show();
            // // Continúa el proceso normal de broadcast
            this.clearAbortBroadcast();
        }
    }
}
