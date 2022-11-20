package com.example.appbebergua;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import DAO.PessoaDAO;
import model.Pessoa;

public class Config extends Activity {

    private TextView txtNomeConfig, txtPesoConfig, txtPraticaExercicio;
    private Button btnAlterar, btnAlarme;
    private PessoaDAO pessoaDAO;
    private Pessoa pessoa;
    private Intent it;
    private ImageView imgCopoConfig;
    private String praticaExercicio = "Não";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        pessoaDAO = new PessoaDAO(this);
        pessoa = pessoaDAO.getPessoaFromDb();
        /*
        it = getIntent();
        pessoa = (Pessoa) it.getSerializableExtra("Pessoa");*/

        imgCopoConfig = findViewById(R.id.imgCopoConfig);
        txtNomeConfig = findViewById(R.id.txtNomeConfig);
        txtPesoConfig = findViewById(R.id.txtPesoConfig);
        btnAlterar = findViewById(R.id.btnAlterar);
        btnAlarme = findViewById(R.id.btnAlarme);
        txtPraticaExercicio = findViewById(R.id.txtPraticaExercicio);

        txtNomeConfig.setText(pessoa.Nome + ", " + txtNomeConfig.getText().toString().toLowerCase(Locale.ROOT));
        txtPesoConfig.setText(Double.valueOf(pessoa.Peso).toString());
        if(pessoa.PraticaExercicio)
            praticaExercicio = "Sim";
        txtPraticaExercicio.setText(praticaExercicio);

        imgCopoConfig.setImageDrawable(getDrawable(R.drawable.copos));
    }

    //Chama a tela de dados pessoais para atualizar os dados
    public void btnAlterar(View view) {
        Intent it;
        it = new Intent(this, DadosPessoais.class);
        startActivity(it);
        this.finish();
    }

    //Chama a tela de alarme para definir um alarme ou timer
    public void btnAlarme(View view) {
        Intent it;
        it = new Intent(this, Alarmes.class);
        startActivity(it);
        this.finish();
    }
}