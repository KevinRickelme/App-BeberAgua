package com.example.appbebergua;

import static com.example.appbebergua.Notificacao.*;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

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

        btnSetAlarm.setOnClickListener(view ->{
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            notificacao = new Notificacao(this, alarmManager);
            notificacao.setNotificationAlarm();
            closeKeyboard();
            if (rdbAlarme.isChecked()) {
                setAlarme();
            } else if (rdbTimer.isChecked()) {
                setTimer();
            }
        });
    }

    public void setAlarme(){
        int hour = Integer.parseInt(edtHoraOuMinuto.getText().toString());
        int minute = Integer.parseInt(edtMinutoOuSegundo.getText().toString());

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, "Tomar água");

        if (correctInputAlarme(hour, minute)) {
            escolhaUsuario = "alarme";
            startActivity(intent);
        }
    }

    public void setTimer(){
        String inputMinutos = edtHoraOuMinuto.getText().toString();
        String inputSegundos = edtMinutoOuSegundo.getText().toString();
        if(correctInputTimer(inputMinutos, inputSegundos)) {
            escolhaUsuario = "timer";
            if (isTimerRunning()) {
                Toast.makeText(this, "Timer já definido!", Toast.LENGTH_LONG).show();
            } else {
                setEndTime();
                notificacao.setNotificationAlarm();
            }
            finish();
        }
    }

    public boolean correctInputAlarme(int hour, int minute){
        return hour <= 24 && hour > 0 && minute <= 60;
    }

    //Métodos para o timer
    public boolean correctInputTimer(String minutos, String segundos) {
        long millisInputSegundos, millisInputMinutos;

        if (minutos.length() == 0 && segundos.length() == 0) {
            Toast.makeText(this, "Os campos não podem ser vazio!", Toast.LENGTH_LONG).show();
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
    }

    //Método que "salva" os valores quando o app é fechado
    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("startTimeInMillis", getStartTimeInMillis());
        //editor.putLong("millisLeft", getTimeLeftInMillis());
        editor.putLong("endTime", getEndTime());
        editor.putBoolean("timerRunning", isTimerRunning());
        editor.apply();

    }
}