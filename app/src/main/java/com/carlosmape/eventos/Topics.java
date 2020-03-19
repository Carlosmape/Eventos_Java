package com.carlosmape.eventos;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;

public class Topics extends AppCompatActivity {
    CheckBox checkBoxDeportes;
    CheckBox checkBoxTeatro;
    CheckBox checkBoxCine;
    CheckBox checkBoxFiestas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topics);
        checkBoxDeportes = (CheckBox) findViewById(R.id.checkBoxDeportes);
        checkBoxTeatro = (CheckBox) findViewById(R.id.checkBoxTeatro);
        checkBoxCine = (CheckBox) findViewById(R.id.checkBoxCine);
        checkBoxFiestas = (CheckBox) findViewById(R.id.checkBoxFiestas);
        checkBoxDeportes.setChecked(consultarSuscripcionATemaEnPreferencias(getApplicationContext(), "Deportes"));
        checkBoxTeatro.setChecked(consultarSuscripcionATemaEnPreferencias(getApplicationContext(), "Teatro"));
        checkBoxCine.setChecked(consultarSuscripcionATemaEnPreferencias(getApplicationContext(), "Cine"));
        checkBoxFiestas.setChecked(consultarSuscripcionATemaEnPreferencias(getApplicationContext(), "Fiestas"));
        checkBoxDeportes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mantenimientoSuscripcionesATemas("Deportes", isChecked);
            }
        });
        checkBoxTeatro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mantenimientoSuscripcionesATemas("Teatro", isChecked);
            }
        });
        checkBoxCine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mantenimientoSuscripcionesATemas("Cine", isChecked);
            }
        });
        checkBoxFiestas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mantenimientoSuscripcionesATemas("Fiestas", isChecked);
            }
        });
    }

    private void mantenimientoSuscripcionesATemas(String tema, Boolean suscribir) {
        if (suscribir) {
            Common.showDialog(getApplicationContext(), "Te has suscrito a: " + tema);
            FirebaseMessaging.getInstance().subscribeToTopic(tema);
            guardarSuscripcionATemaEnPreferencias(getApplicationContext(), tema, true);
        } else {
            Common.showDialog(getApplicationContext(), "Te has dado de baja de: " + tema);
            FirebaseMessaging.getInstance().unsubscribeFromTopic(tema);
            guardarSuscripcionATemaEnPreferencias(getApplicationContext(), tema, false);
        }
    }

    public static void guardarSuscripcionATemaEnPreferencias(Context context, String tema, Boolean suscrito) {
        final SharedPreferences prefs = context.getSharedPreferences("Temas", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(tema, suscrito);
        editor.commit();
    }

    public static Boolean consultarSuscripcionATemaEnPreferencias(Context context, String tema) {
        final SharedPreferences preferencias = context.getSharedPreferences("Temas", Context.MODE_PRIVATE);
        return preferencias.getBoolean(tema, false);
    }
}

