package com.example.appbebergua;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import DAO.PessoaDAO;
import model.Pessoa;

public class DadosPessoais extends AppCompatActivity {
    private TextView txtNome;
    private Intent it;
    private Pessoa pessoa;
    private EditText edtPeso;
    private RadioGroup praticaExercicio;
    private RadioButton botaoSelecionado;
    PessoaDAO pessoaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_pessoais);
        pessoaDAO = new PessoaDAO(this);
        txtNome = findViewById(R.id.txtNomeConfig);
        it = getIntent();
        pessoa = (Pessoa)it.getSerializableExtra("Pessoa");
        if(pessoa == null)
            pessoa = pessoaDAO.getPessoaFromDb();
        txtNome.setText(pessoa.Nome);

        praticaExercicio = findViewById(R.id.radioGroup);
    }

    //Método responsável pelo botão Calcular e caso os inputs estejam certos ele irá chamar a tela
    //de resultado após salvar ou atualizar os dados do usuário
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


        long resultado;
        if (pessoaDAO.hasData()){
            resultado= pessoaDAO.update(pessoa);
        } else {
            resultado = pessoaDAO.insert(pessoa);
        }
        if( resultado != -1){
            it = new Intent(this, Resultado.class);
            startActivity(it);
            finishAfterTransition();
        }
    }

    //Método que calcula a quantidade de água que o usuário deve ingerir
    private double calcularQtdAIngerir(double peso, boolean praticaExercicio) {
        if(praticaExercicio)
            return 45 * peso;
        else
            return 33 * peso;
    }
}