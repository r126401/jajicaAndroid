package com.iotcontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.widget.ProgressBar;

public class DialogosAplicacion extends DialogFragment {


    Context contexto;
    COMANDO_IOT idComando;
    String mensaje;
    String titulo;
    int icono;
    ProgressBar animacion;




    public interface OnDialogosAplicacion {
        void OnAceptarListener(COMANDO_IOT idComando);
        void OnCancelarListener();
    }

    OnDialogosAplicacion listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        return ventanaDialogo(contexto, idComando, titulo, mensaje, icono);
        //return super.onCreateDialog(savedInstanceState);
    }

    public void setOnDialogosAplicacion(OnDialogosAplicacion listener) {

        this.listener = listener;
    }



    public AlertDialog ventanaDialogo(Context contexto, final COMANDO_IOT idComando, String titulo, String mensaje, int icono) {

        this.contexto = contexto;
        this.mensaje = mensaje;
        this.icono = icono;
        this.titulo = titulo;

        AlertDialog.Builder ventana = new AlertDialog.Builder(contexto);
        this.idComando = idComando;
        ventana.setTitle(titulo);
        ventana.setMessage(mensaje);
        ventana.setIcon(icono);
        ventana.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.OnAceptarListener(idComando);


            }
        });

        ventana.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.OnCancelarListener();

            }
        });

        return ventana.create();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (OnDialogosAplicacion) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(
                    activity.toString() +
                            " no implementó OnSimpleDialogListener");

        }
    }





    }


