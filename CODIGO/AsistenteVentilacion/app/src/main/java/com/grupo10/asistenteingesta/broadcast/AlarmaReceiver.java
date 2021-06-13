package com.grupo10.asistenteingesta.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.grupo10.asistenteingesta.ui.ConfirmarIngestaActivity;
import com.grupo10.asistenteingesta.ui.MainActivity;

public class AlarmaReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "ALARMAAAAAAAAAAAAa", Toast.LENGTH_LONG).show();

        Intent i = new Intent(context,ConfirmarIngestaActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
