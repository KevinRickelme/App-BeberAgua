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

        btnSetAlarm.setOnClickListener(view -> {
            closeKeyboard();
            if (rdbAlarme.isChecked()) {
                setAlarme();
            } else if (rdbTimer.isChecked()) {
                setTimer();
            }
        });
    }

    public void setAlarme() {
        String hourInput = edtHoraOuMinuto.getText().toString();
        String minuteInput = edtMinutoOuSegundo.getText().toString();
        if (hourInput.length() > 0 && minuteInput.length() > 0) {
            int hour = Integer.parseInt(hourInput);
            int minute = Integer.parseInt(minuteInput);

            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
            intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
            intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
            intent.putExtra(AlarmClock.EXTRA_MESSAGE, "Tomar ??gua");

            if (correctInputAlarme(hour, minute)) {
                escolhaUsuario = "alarme";
                startActivity(intent);
            } else {
                Toast.makeText(this, "Digite um valor v??lido!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Os campos n??o podem ser vazios!", Toast.LENGTH_LONG).show();
        }
    }

    public void setTimer() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Notificacao notificacao = new Notificacao(this, alarmManager);
        String inputMinutos = edtHoraOuMinuto.getText().toString();
        String inputSegundos = edtMinutoOuSegundo.getText().toString();
        if (correctInputTimer(inputMinutos, inputSegundos)) {
            escolhaUsuario = "timer";
            if (isTimerRunning()) {
                perguntarReprogramarTimer();
            } else {
                notificar();
            }
        }
    }

    private void perguntarReprogramarTimer(){

        AlertDialog.Builder confirmaReprog = new AlertDialog.Builder(Alarmes.this);
        confirmaReprog.setTitle("Aten????o !!")
                .setMessage("J?? h?? um timer rodando, deseja redefini-lo?")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notificar();
                    }
                });
        confirmaReprog.setNegativeButton("N??o", null);
        confirmaReprog.create().show();
    }

    private void notificar() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        notificacao = new Notificacao(this, alarmManager);
        setEndTime();
        notificacao.setNotificationAlarm();
        finish();
    }

    public boolean correctInputAlarme(int hour, int minute) {
        return hour <= 24 && hour > 0 && minute <= 60 && minute >= 0;
    }

    //M??todos para o timer
    public boolean correctInputTimer(String minutos, String segundos) {
        long millisInputSegundos, millisInputMinutos;

        if (minutos.length() == 0 && segundos.length() == 0) {
            Toast.makeText(this, "Os campos n??o podem ser vazios!", Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "Digite um valor v??lido!", Toast.LENGTH_LONG).show();
            return false;
        }

        setStartTimeInMillis(millisInputMinutos + millisInputSegundos);
        edtHoraOuMinuto.setText(null);
        edtMinutoOuSegundo.setText(null);
        return true;
    }

    //M??todo para fechar o teclado
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //M??todo que "recupera" os valores quando o app ?? iniciado
    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        setStartTimeInMillis(prefs.getLong("startTimeInMillis", 10000));
        setTimerRunning(prefs.getBoolean("timerRunning", false));
        if (System.currentTimeMillis() > getEndTime())
            setTimerRunning(false);

    }

    //M??todo que "salva" os valores quando o app ?? fechado
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