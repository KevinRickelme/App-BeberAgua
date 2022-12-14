package com.example.appbebergua;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import DAO.PessoaDAO;
import model.Pessoa;

public class DadosPessoais extends AppCompatActivity {
    private TextView txtNome;
    private Intent it;
    private Pessoa pessoa;
    private EditText edtPeso;
    private RadioGroup praticaExercicio;
    private RadioButton botaoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_pessoais);

        txtNome = findViewById(R.id.txtNome);
        it = getIntent();
        pessoa = (Pessoa)it.getSerializableExtra("Pessoa");
        txtNome.setText(pessoa.Nome);

        praticaExercicio = findViewById(R.id.radioGroup);
    }

    public void btnCalcular(View view) {
        if(praticaExercicio.getCheckedRadioButtonId() == -1)
            Toast.makeText(getApplicationContext(), "Por favor, selecione uma opção de prática de exercícios", Toast.LENGTH_SHORT).show();
        else{
            int selectedId = praticaExercicio.getCheckedRadioButtonId();
            // find the radiobutton by returned id
            botaoSelecionado = findViewById(selectedId);
            pessoa.PraticaExercicio = botaoSelecionado.getText().equals("Sim");
        }
        edtPeso = findViewById(R.id.edtPeso);
        pessoa.Peso = Float.valueOf(String.valueOf(edtPeso.getText()));
        pessoa.MetaDiaria = calcularQtdAIngerir(pessoa.Peso, pessoa.PraticaExercicio);

        PessoaDAO pessoaDao = new PessoaDAO(this);

        long resultado = pessoaDao.insert(pessoa);
        if( resultado != -1){
            it = new Intent(this, Resultado.class);
            startActivity(it);
            finishAfterTransition();
        }
    }

    private double calcularQtdAIngerir(double peso, boolean praticaExercicio) {
        if(praticaExercicio)
            return 45 * peso;
        else
            return 33 * peso;
    }
}