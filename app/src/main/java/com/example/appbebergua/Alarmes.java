package com.example.appbebergua;

import static com.example.appbebergua.Notificacao.*;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.AlarmClock;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.util.Calendar;

public class Alarmes extends AppCompatActivity {

    private EditText edtHoraOuMinuto, edtMinutoOuSegundo;
    private RadioButton rdbAlarme, rdbTimer;
    private String escolhaUsuario;
    //private boolean alarmeProgramado;
    private Notificacao notificacao;
    private Button btnSetAlarm;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmes);

        edtHoraOuMinuto = findViewById(R.id.edtHoraOuMinuto);
        edtMinutoOuSegundo = findViewById(R.id.edtMinutoOuSegundo);
        btnSetAlarm = findViewById(R.id.btnSetAlarm);
        rdbAlarme = findViewById(R.id.rdbAlarme);
        rdbTimer = findViewById(R.id.rdbTimer);


        //Métodos que observam o comportamente de click dos radio buttons
        rdbAlarme.setOnClickListener(view -> {
            btnSetAlarm.setText("Programar alarme");
            edtHoraOuMinuto.setHint("Digite a hora.");
            edtMinutoOuSegundo.setHint("Digite o minuto.");
        });

        rdbTimer.setOnClickListener(view -> {
                btnSetAlarm.setText("Programar timer");
                edtHoraOuMinuto.setHint("Digite os minutos.");
                edtMinutoOuSegundo.setHint("Digite os segundos.");
        });

        //Ação de click do botão alarme
        btnSetAlarm.setOnClickListener(view -> {
            closeKeyboard();
            if (rdbAlarme.isChecked()) {
                setAlarme();
            } else if (rdbTimer.isChecked()) {
                setTimer();
            }
        });
    }

    //Método que pega o texto dos campos, chama o método de verificação dos inputs
    //e caso esteja tudo correto ele abre a tela de alarmes do celular e define o
    //alarme com a mensagem "Tomar água"
    public void setAlarme() {
        String hourInput = edtHoraOuMinuto.getText().toString();
        String minuteInput = edtMinutoOuSegundo.getText().toString();
        if (hourInput.length() > 0 && minuteInput.length() > 0) {
            int hour = Integer.parseInt(hourInput);
            int minute = Integer.parseInt(minuteInput);

            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
            intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
            intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
            intent.putExtra(AlarmClock.EXTRA_MESSAGE, "Tomar água");

            if (correctInputAlarme(hour, minute)) {
                escolhaUsuario = "alarme";
                startActivity(intent);
            } else {
                Toast.makeText(this, "Digite um valor válido!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Os campos não podem ser vazios!", Toast.LENGTH_LONG).show();
        }
    }

    //Método que pega o texto dos campos, chama o método de verificação dos inputs
    //e caso esteja tudo correto ele define o horário em que a notificação irá
    //aparecer para o usuário, se já houver uma notificação definida será exibida
    //uma mensagem para o usuário confirmar se quer redefini-la ou não
    public void setTimer() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Notificacao notificacao = new Notificacao(this, alarmManager);
        String inputMinutos = edtHoraOuMinuto.getText().toString();
        String inputSegundos = edtMinutoOuSegundo.getText().toString();
        if (correctInputTimer(inputMinutos, inputSegundos)) {
            escolhaUsuario = "timer";
            verificaAlarmeNotificacao();
            if (isTimerRunning()) {
                perguntarReprogramarTimer();
            } else {
                notificar();
            }
        }
    }

    //Método que mostra a mensagem para o usuário confirmar se deseja redefinir em quanto
    //ele será notificado
    private void perguntarReprogramarTimer(){

        AlertDialog.Builder confirmaReprog = new AlertDialog.Builder(Alarmes.this);
        confirmaReprog.setTitle("Atenção !!")
                .setMessage("Já há um timer rodando, deseja redefini-lo?")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notificar();
                    }
                });
        confirmaReprog.setNegativeButton("Não", null);
        confirmaReprog.create().show();
    }

    //Método responsável por definir o horário em que o usuário será notificado e salvar
    //a informação de que há uma notificação definida
    private void notificar() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        notificacao = new Notificacao(this, alarmManager);
        setEndTime();
        setTimerRunning(true);
        notificacao.setNotificationAlarm();
        finish();
    }

    //Verifica o input para o alarme
    public boolean correctInputAlarme(int hour, int minute) {
        return hour <= 24 && hour > 0 && minute <= 60 && minute >= 0;
    }

    //Verifica o input para o timer
    //Métodos para o timer
    public boolean correctInputTimer(String minutos, String segundos) {
        long millisInputSegundos, millisInputMinutos;

        if (minutos.length() == 0 && segundos.length() == 0) {
            Toast.makeText(this, "Os campos não podem ser vazios!", Toast.LENGTH_LONG).show();
            return false;
        }

        if (segundos.length() == 0) {
            millisInputSegundos = 0;
        } else {
            millisInputSegundos = Long.parseLong(segundos) * 1000;
        }

        if (minutos.length() == 0) {
            millisInputMinutos = 0;
        } else {
            millisInputMinutos = Long.parseLong(minutos) * 60000;
        }

        if (millisInputMinutos < 0 || millisInputSegundos < 0) {
            Toast.makeText(this, "Digite um valor válido!", Toast.LENGTH_LONG).show();
            return false;
        }

        setStartTimeInMillis(millisInputMinutos + millisInputSegundos);
        edtHoraOuMinuto.setText(null);
        edtMinutoOuSegundo.setText(null);
        return true;
    }

    //Verifica se já passou o tempo da notificação
    //caso já tenha passado o app irá salvar a informação de que
    //não há mais nenhuma notificação definida
    private void verificaAlarmeNotificacao(){
        if (System.currentTimeMillis() > getEndTime())
            setTimerRunning(false);
    }

    //Método para fechar o teclado
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //Método que "recupera" os valores quando o app é iniciado
    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        setStartTimeInMillis(prefs.getLong("startTimeInMillis", 10000));
        setTimerRunning(prefs.getBoolean("timerRunning", false));
        setEndTime(prefs.getLong("endTime", 0));

        verificaAlarmeNotificacao();

    }

    //Método que "salva" os valores quando o app é fechado
    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("startTimeInMillis", getStartTimeInMillis());
        editor.putLong("endTime", getEndTime());
        editor.putBoolean("timerRunning", isTimerRunning());

        editor.apply();

    }
}