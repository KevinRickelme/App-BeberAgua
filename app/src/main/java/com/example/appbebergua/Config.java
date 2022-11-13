package com.example.appbebergua;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import DAO.PessoaDAO;
import model.Pessoa;

public class Config extends Activity {

    private TextView txtNome, txtPeso, txtPraticaExercicio;
    private Button btnAlterar;
    private Button btnAlarme;
    private PessoaDAO pessoaDAO;
    private Pessoa pessoa;
    private Intent it;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pessoaDAO = new PessoaDAO(this);
        it = getIntent();
        pessoa = (Pessoa) it.getSerializableExtra("Pessoa");
        setContentView(R.layout.activity_config);

        txtNome = findViewById(R.id.txtNome);
        txtNome.setText(pessoa.Nome + ", " + txtNome.getText().toString().toLowerCase(Locale.ROOT));

        txtPeso = findViewById(R.id.txtPeso);
        txtPeso.setText(Double.valueOf(pessoa.Peso).intValue());

        txtPraticaExercicio = findViewById(R.id.txtPraticaExercicio);

        btnAlterar = findViewById(R.id.btnAlterar);
        btnAlarme = findViewById(R.id.btnAlarme);

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

