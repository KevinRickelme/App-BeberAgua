package com.example.appbebergua;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;

import DAO.PessoaDAO;
import model.Pessoa;

public class Resultado extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Button btnAlarme, btnConfirma, btnAdiar;
    private Intent it;
    private Pessoa pessoa;
    private Spinner opcoesQtdAguaIngerida;
    private TextView txtQtdAgua;
    private String respostaUser;

    private boolean primeiroAcesso;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        it = getIntent();
        it.getBooleanExtra("primeiroAcesso", false);
        pessoa = (Pessoa)it.getSerializableExtra("Pessoa");
        setContentView(R.layout.activity_resultado);
        TextView txtViewApresentacao = findViewById(R.id.textView2);
        TextView txtResultado = findViewById(R.id.txtResultado);
        txtResultado.setText(Double.valueOf(pessoa.QtdIngerida).intValue()+"/"+ Double.valueOf(pessoa.MetaDiaria).intValue()+"ml");
        txtViewApresentacao.setText(pessoa.Nome + ", " + txtViewApresentacao.getText().toString().toLowerCase(Locale.ROOT));


        btnAlarme = findViewById(R.id.btnAlarme);
        btnConfirma = findViewById(R.id.btnConfirma);
        btnAdiar = findViewById(R.id.btnAdiar);
        opcoesQtdAguaIngerida = findViewById(R.id.opcoesQtdAguaIngerida);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.medidas_ml, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        opcoesQtdAguaIngerida.setAdapter(adapter);


        if(primeiroAcesso){
            btnConfirma.setVisibility(View.INVISIBLE);
            btnAdiar.setVisibility(View.INVISIBLE);
            txtQtdAgua.setVisibility(View.INVISIBLE);
            opcoesQtdAguaIngerida.setVisibility(View.INVISIBLE);
        } else {
            btnConfirma.setVisibility(View.VISIBLE);
            btnAdiar.setVisibility(View.VISIBLE);
//            txtQtdAgua.setVisibility(View.VISIBLE);
            opcoesQtdAguaIngerida.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemConfiguracoes:
                Intent it = new Intent(this, DadosPessoais.class);
                startActivity(it);
                return true;

            case R.id.itemSair:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        respostaUser = parent.getItemAtPosition(pos).toString();

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    public void btnAlarme (View view) {
        Intent it = new Intent(this, activity_alarmes.class);
        startActivity(it);
    }
}