package com.example.appbebergua;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import model.Pessoa;

public class Config extends Activity {

    private TextView txtNome;
    private TextView txtPeso;
    private TextView txtPraticaExercicio;
    private Button btnAlterar;
    private Button btnAlarme;
    private Pessoa pessoa;
    private Intent it;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        txtPraticaExercicio = findViewById(R.id.txtPraticaExercicio);


        btnAlterar = findViewById(R.id.btnAlterar);

        txtPeso = findViewById(R.id.txtPeso);
        it = getIntent();
        pessoa = (Pessoa)it.getSerializableExtra("Pessoa");
        txtPeso.setText((int) pessoa.Peso);

        txtNome = findViewById(R.id.txtNome);
        it = getIntent();
        pessoa = (Pessoa)it.getSerializableExtra("Pessoa");
        txtNome.setText(pessoa.Nome);
    }

    public void btnAlterar(View view) {
        Intent it;
        it = new Intent(this, DadosPessoais.class);
        startActivity(it);
    }

    public void btnAlarme(View view) {
        Intent it;
        it = new Intent(this, activity_alarmes.class);
        startActivity(it);
    }
}

