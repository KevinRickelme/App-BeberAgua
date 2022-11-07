package com.example.appbebergua;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private EditText edtNome;
    private Button btnIniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtNome = findViewById(R.id.edtNome);
        btnIniciar = findViewById(R.id.btnIniciar);

        edtNome.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (edtNome.getText().length() > 0) {
                    btnIniciar.setEnabled(true);
                } else {
                    btnIniciar.setEnabled(false);
                }
                return false;
            }
        });
    }

            public void btnIniciar(View view){
                Intent it = new Intent(this, DadosPessoais.class);
                it.putExtra("Nome", edtNome.getText().toString());
                startActivity(it);

            }

        }


