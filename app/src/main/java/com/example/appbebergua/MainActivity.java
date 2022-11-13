package com.example.appbebergua;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.app.KeyguardManager;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.Executor;

import DAO.PessoaDAO;
import model.Pessoa;

import static androidx.biometric.BiometricManager.Authenticators.*;

public class MainActivity extends AppCompatActivity {

    private EditText edtNome;
    private Button btnIniciar;
    private Pessoa pessoa;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private KeyguardManager keyguardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ao acessar a tela principal, ele verifica no banco de dados se o usuário já se cadastrou anteriormente
        autenticarDispositivo();
        verificaSeTemCadastro();


        pessoa = new Pessoa();
        edtNome = findViewById(R.id.edtNome);
        btnIniciar = findViewById(R.id.btnIniciar);

        edtNome.setOnKeyListener((view, i, keyEvent) -> {
            btnIniciar.setEnabled(edtNome.getText().length() > 0);
            pessoa.Nome = String.valueOf(edtNome.getText());
            return false;
        });
    }

    public void btnIniciar(View view) {
        Intent it = new Intent(this, DadosPessoais.class);
        it.putExtra("Pessoa", pessoa);
        startActivity(it);
    }

    public void verificaSeTemCadastro() {
        PessoaDAO pessoaDAO = new PessoaDAO(this);
        if (pessoaDAO.hasData()) {
            Intent it = new Intent(this, Resultado.class);
            it.putExtra("Pessoa", pessoaDAO.getPessoaFromDb());
            startActivity(it);
        }
    }

    private void autenticarDispositivo() {

        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(MainActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Erro: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Bem-vindo!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Autenticação não está configurada",
                        Toast.LENGTH_SHORT).show();
            }
        });


        if (keyguardManager.isDeviceSecure()) {
            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric login for my app")
                    .setSubtitle("Log in using your biometric credential")
                    .setAllowedAuthenticators(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)
                    .build();

            biometricPrompt.authenticate(promptInfo);

        } else {
            Toast.makeText(getApplicationContext(), "Autenticação não está configurada",
                    Toast.LENGTH_SHORT).show();
        }

    }
}


