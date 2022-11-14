package com.example.appbebergua;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
    private PessoaDAO pessoaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();
        pessoaDAO = new PessoaDAO(this);
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

    @Override
    public void onResume(){
        super.onResume();
        if (!cadastrado()) {
            edtNome.setVisibility(View.VISIBLE);
            btnIniciar.setText("Iniciar");
            btnIniciar.setEnabled(false);
        } else {
            edtNome.setVisibility(View.INVISIBLE);
            btnIniciar.setEnabled(true);
            btnIniciar.setText("Meta diária");
        }
    }

    public void btnIniciar(View view) {
        Intent it;
        if (cadastrado()) {
            PessoaDAO pessoaDAO = new PessoaDAO(this);
            it = new Intent(this, Resultado.class);
            it.putExtra("Pessoa", pessoaDAO.getPessoaFromDb());
        } else {
            it = new Intent(this, DadosPessoais.class);
            it.putExtra("Pessoa", pessoa);
        }
        startActivity(it);
    }

    public void verificaSeTemCadastro() {
        if (cadastrado()) {
            Intent it = new Intent(this, Resultado.class);
            it.putExtra("Pessoa", pessoaDAO.getPessoaFromDb());
            startActivity(it);
        }
    }

    private boolean cadastrado() {
        return (pessoaDAO.hasData());
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
                    .setTitle("Autenticação")
                    .setSubtitle("Utilize biometria ou alguma outra autenticação")
                    .setAllowedAuthenticators(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)
                    .build();

            biometricPrompt.authenticate(promptInfo);

        } else {
            Toast.makeText(getApplicationContext(), "Autenticação não está configurada",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //Método para a criação do canal de notificação
    public void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Lembrete para beber água";
            String description = "Canal para notificar quando beber água";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("drinkWaterApp", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}


