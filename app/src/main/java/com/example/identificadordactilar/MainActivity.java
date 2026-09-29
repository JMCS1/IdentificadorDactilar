package com.example.identificadordactilar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private TextView tvStatus;
    private ImageView imgStatus;
    private Button btnAuthenticate;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ajuste de insets para EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar componentes
        tvStatus = findViewById(R.id.tvStatus);
        imgStatus = findViewById(R.id.imgStatus);
        btnAuthenticate = findViewById(R.id.btnAuthenticate);

        // Configurar Biometría
        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                imgStatus.setImageResource(R.drawable.ic_error);
                tvStatus.setText("Error: " + errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                imgStatus.setImageResource(R.drawable.ic_success);
                tvStatus.setText("¡Escaneo de huella dactilar exitoso! Iniciando sesión…");

                // Transición automática tras 1.5 segundos
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(MainActivity.this, ResultadoActivity.class);
                    startActivity(intent);
                }, 1500);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                imgStatus.setImageResource(R.drawable.ic_error);
                tvStatus.setText("Huella no reconocida. Intente de nuevo.");
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticación Biométrica")
                .setSubtitle("Use su huella dactilar para ingresar")
                .setNegativeButtonText("Cancelar")
                .build();

        // Evento de clic para iniciar la autenticación
        btnAuthenticate.setOnClickListener(v -> {
            biometricPrompt.authenticate(promptInfo);
        });
    }
}
