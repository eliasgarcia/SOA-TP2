package com.grupo10.asistenteingesta.servicios;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class InternetStatus {

    private Context context;
    private static InternetStatus status;

    public static InternetStatus getInstance(Context context){
        if(status==null){
            status = new InternetStatus(context);
        }
        return status;
    }

    private InternetStatus(Context context){
        this.context = context;
    }

    public Boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Boolean estaconectado = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
        if(!estaconectado){
            Toast toast= Toast.makeText(context,"No hay acceso a internet. Intente mas tarde.", Toast.LENGTH_LONG);
            View view = toast.getView();
            view.getBackground().setColorFilter(Color.parseColor("#ff0000"), PorterDuff.Mode.SRC_IN);
            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
        return estaconectado;
    }
}
