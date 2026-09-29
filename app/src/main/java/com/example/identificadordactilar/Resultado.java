package com.example.identificadordactilar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Resultado extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);
    }

    /**
     * Método manejador vinculado a través del atributo XML android:onClick="acceptButton"
     */
    public void acceptButton(View view) {
        // Navegar de regreso a MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        // Cerrar esta actividad
        finish();
    }
}
