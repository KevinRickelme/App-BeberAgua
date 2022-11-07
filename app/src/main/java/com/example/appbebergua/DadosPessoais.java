package com.example.appbebergua;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DadosPessoais extends AppCompatActivity {
    private TextView txtNome;
    private Intent it;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_pessoais);

        txtNome = findViewById(R.id.txtNome);
        it = getIntent();
        String nome = it.getStringExtra("Nome");
        txtNome.setText(nome);
    }

    public void btnCalcular(View view) {
        Intent it = new Intent(this, Resultado.class);
        startActivity(it);
    }
}