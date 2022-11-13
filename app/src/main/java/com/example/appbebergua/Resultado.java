package com.example.appbebergua;

import static com.example.appbebergua.Notificacao.setStartTimeInMillis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import DAO.PessoaDAO;
import model.Pessoa;

public class Resultado extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Button btnAlarme, btnConfirma, btnAdiar;
    private Intent it;
    private Pessoa pessoa;
    private PessoaDAO pessoaDAO;
    private Spinner opcoesQtdAguaIngerida;
    private TextView txtQtdAgua, txtViewApresentacao, txtResultado;
    private String respostaUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pessoaDAO = new PessoaDAO(this);
        it = getIntent();
        it.getBooleanExtra("primeiroAcesso", false);
        pessoa = (Pessoa)it.getSerializableExtra("Pessoa");
        setContentView(R.layout.activity_resultado);
        txtViewApresentacao = findViewById(R.id.textView2);
        txtResultado = findViewById(R.id.txtResultado);
        txtViewApresentacao.setText(pessoa.Nome + ", " + txtViewApresentacao.getText().toString().toLowerCase(Locale.ROOT));
        txtResultado.setText(Double.valueOf(pessoa.QtdIngerida).intValue()+"/"+ Double.valueOf(pessoa.MetaDiaria).intValue()+"ml");
//        createNotificationChannel();

        btnAlarme = findViewById(R.id.btnAlarme);
        btnConfirma = findViewById(R.id.btnConfirma);
        btnAdiar = findViewById(R.id.btnAdiar);
        opcoesQtdAguaIngerida = findViewById(R.id.opcoesQtdAguaIngerida);

        //Documentação para vincular a lista ao controle
        //https://developer.android.com/guide/topics/ui/controls/spinner.html
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.medidas_ml, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        opcoesQtdAguaIngerida.setAdapter(adapter);
        opcoesQtdAguaIngerida.setOnItemSelectedListener(this);

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
                Intent it = new Intent(this, Config.class);
                startActivity(it);
                return true;

            case R.id.itemSair:
                finish();

            case R.id.itemReset:
                resetMeta();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        //Obter o item selecionado
        respostaUser = parent.getItemAtPosition(pos).toString();
        //Retirar o numero que está no índice 0 da string
        respostaUser = respostaUser.substring(1);
        //Tirar todas as letras
        respostaUser = respostaUser.replaceAll("\\D", "");

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void btnConfirma(View view) {
        double qtdIngerida = Double.parseDouble(respostaUser);
        AlertDialog.Builder confirmaAcao = new AlertDialog.Builder(Resultado.this);
        confirmaAcao.setTitle("Atenção !!")
                .setMessage("Você deseja confirmar que bebeu " + qtdIngerida + "ml de água?")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmar(qtdIngerida);
                    }
                });
        confirmaAcao.setNegativeButton("Não", null);
        confirmaAcao.create().show();
    }

    private void confirmar(double qtdIngerida){
        if(pessoa.QtdIngerida >= pessoa.MetaDiaria){
            Toast.makeText(this, "Parabéns você alcançou a meta!!!", Toast.LENGTH_LONG).show();
        } else {
            pessoaDAO.atualizaQtdIngerida(qtdIngerida);
            pessoa = pessoaDAO.getPessoaFromDb();
            txtResultado.setText(Double.valueOf(pessoa.QtdIngerida).intValue()+"/"+ Double.valueOf(pessoa.MetaDiaria).intValue()+"ml");
        }
    }

    public void btnAdiar(View view){
        AlertDialog.Builder confirmaAdiar = new AlertDialog.Builder(Resultado.this);
        confirmaAdiar.setTitle("Atenção !!")
                .setMessage("Tem certeza que deseja adiar a notificação em 30 minutos?")
                .setCancelable(false)
                .setPositiveButton("Sim", (dialog, which) -> adiar());
        confirmaAdiar.setNegativeButton("Não", null);
        confirmaAdiar.create().show();
    }

    private void adiar(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Notificacao notificacao = new Notificacao(this, alarmManager);
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean timerRunning = prefs.getBoolean("timerRunning", false);
        if(timerRunning){
            notificacao.cancelNotificationAlarm();
        } else {
            //setStartTimeInMillis(30 * 1000 * 60);
            setStartTimeInMillis(3000);
            notificacao.setNotificationAlarm();
        }
    }

    public void resetMeta(){
        AlertDialog.Builder confirmaReset = new AlertDialog.Builder(Resultado.this);
        confirmaReset.setTitle("Atenção !!")
                .setMessage("Você deseja zerar sua meta diária?")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pessoaDAO.resetQtdIngerida();
                        pessoa = pessoaDAO.getPessoaFromDb();
                        txtResultado.setText(Double.valueOf(pessoa.QtdIngerida).intValue()+"/"+ Double.valueOf(pessoa.MetaDiaria).intValue()+"ml");
                    }
                });
        confirmaReset.setNegativeButton("Não", null);
        confirmaReset.create().show();
    }

    /*
    public void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Lembrete para beber água";
            String description = "Canal para notificar quando beber água";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("drinkWaterApp", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    */

    public void btnAlarme (View view) {
        Intent it = new Intent(this, activity_alarmes.class);
        startActivity(it);
    }
}