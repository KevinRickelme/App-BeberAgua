package com.example.appbebergua;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import DAO.PessoaDAO;
import model.Pessoa;

public class Resultado extends AppCompatActivity {
    Button btnAlarme;
    private Intent it;
    private Pessoa pessoa;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        it = getIntent();
        pessoa = (Pessoa)it.getSerializableExtra("Pessoa");
        setContentView(R.layout.activity_resultado);
        TextView txtViewApresentacao = findViewById(R.id.textView2);
        TextView txtResultado = findViewById(R.id.txtResultado);
        txtResultado.setText(Double.valueOf(pessoa.QtdIngerida).intValue()+"/"+ Double.valueOf(pessoa.MetaDiaria).intValue()+"ml");
        txtViewApresentacao.setText(pessoa.Nome + ", " + txtViewApresentacao.getText().toString().toLowerCase(Locale.ROOT));


        btnAlarme = findViewById(R.id.btnAlarme);

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

    public void btnAlarme (View view) {
        Intent it = new Intent(this, activity_alarmes.class);
        startActivity(it);
    }
}