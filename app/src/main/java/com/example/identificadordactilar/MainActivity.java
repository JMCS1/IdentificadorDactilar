package com.example.identificadordactilar;

import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private ImageView imageView;
    private FingerprintManager fingerprintManager;
    private FingerprintManager.AuthenticationCallback authenticationCallback;
    private CancellationSignal cancellationSignal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar componentes de la interfaz de usuario
        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);

        // Obtener el servicio del sistema FingerprintManager
        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        // Implementar FingerprintManager.AuthenticationCallback manejando los 4 métodos solicitados
        authenticationCallback = new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                // Dinámico: Muestra el error real del sistema/emulador (ej. "No hay huellas registradas")
                textView.setText("ERROR: " + errString);
                imageView.setImageResource(R.drawable.icono_incorrecto);
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
                textView.setText("AYUDA: " + helpString);
                imageView.setImageResource(R.drawable.icono_carga);
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(MainActivity.this, "Autenticación exitosa", Toast.LENGTH_SHORT).show();
                textView.setText("¡Escaneo de huella dactilar exitoso! \n Iniciando sesión…");
                imageView.setImageResource(R.drawable.icono_correcto);

                // Lanzar la actividad Resultado
                Intent intent = new Intent(MainActivity.this, Resultado.class);
                startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                textView.setText("Escaneo fallido, huella dactilar no registrada");
                imageView.setImageResource(R.drawable.icono_incorrecto);
            }
        };
    }

    /**
     * Método manejador vinculado a través del atributo XML android:onClick="scanButton"
     */
    public void scanButton(View view) {
        if (fingerprintManager == null) {
            textView.setText("ERROR: El hardware de huella no está disponible");
            return;
        }

        // 1. Validar si el emulador/dispositivo tiene soporte de hardware
        if (!fingerprintManager.isHardwareDetected()) {
            textView.setText("ERROR: No se detectó hardware de huella dactilar");
            return;
        }

        // 2. Validar si el usuario ya registró al menos una huella en los Ajustes del sistema
        if (!fingerprintManager.hasEnrolledFingerprints()) {
            textView.setText("ERROR: No hay huellas registradas. Ve a Ajustes -> Seguridad -> Huella dactilar en tu emulador");
            return;
        }

        try {
            textView.setText("Esperando lectura de huella...");
            imageView.setImageResource(R.drawable.icono_carga);

            // Cancelar cualquier escaneo previo activo para evitar colisiones
            if (cancellationSignal != null) {
                cancellationSignal.cancel();
            }
            cancellationSignal = new CancellationSignal();

            // Iniciar la autenticación biométrica nativa de manera robusta
            fingerprintManager.authenticate(null, cancellationSignal, 0, authenticationCallback, null);
        } catch (SecurityException e) {
            textView.setText("ERROR: Permiso denegado");
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Buena práctica: Detener el escaneo si la app pasa a segundo plano
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
            cancellationSignal = null;
        }
    }
}
