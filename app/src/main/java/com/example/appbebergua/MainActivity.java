package com.example.appbebergua;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import DAO.PessoaDAO;
import model.Pessoa;

public class MainActivity extends AppCompatActivity {
    private EditText edtNome;
    private Button btnIniciar;
    private Pessoa pessoa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Ao acessar a tela principal, ele verifica no banco de dados se o usuário já se cadastrou anteriormente
        verificaSeTemCadastro();


        pessoa = new Pessoa();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtNome = findViewById(R.id.edtNome);
        btnIniciar = findViewById(R.id.btnIniciar);

        edtNome.setOnKeyListener((view, i, keyEvent) -> {
            if (edtNome.getText().length() > 0) {
                btnIniciar.setEnabled(true);
            } else {
                btnIniciar.setEnabled(false);
            }
            pessoa.Nome = String.valueOf(edtNome.getText());
            return false;
        });
    }

    public void btnIniciar(View view){
        Intent it = new Intent(this, DadosPessoais.class);
        it.putExtra("Pessoa", pessoa);
        startActivity(it);
        }

    public void verificaSeTemCadastro() {
        PessoaDAO pessoaDAO = new PessoaDAO(this);
        if(pessoaDAO.hasData()) {
            Intent it = new Intent(this, Resultado.class);
            it.putExtra("Pessoa",pessoaDAO.getPessoaFromDb());
            startActivity(it);
        }
    }
}


