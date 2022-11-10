package com.example.appbebergua;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.AlarmClock;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class activity_alarmes extends AppCompatActivity {

    EditText edtHoraOuMinuto, edtMinutoOuSegundo;
    Button btnSetAlarm;
    RadioButton rdbAlarme, rdbTimer;
    RadioGroup rdgNotificacao;


    //variáveis que fazem o timer funcionar
    private CountDownTimer countDownTimer;

    private boolean timerRunning;

    private long startTimeInMillis;
    private long timeLeftInMillis;
    private long endTime;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmes);

        edtHoraOuMinuto = findViewById(R.id.edtHoraOuMinuto);
        edtMinutoOuSegundo = findViewById(R.id.edtMinutoOuSegundo);
        btnSetAlarm = findViewById(R.id.btnSetAlarm);
        rdbAlarme = findViewById(R.id.rdbAlarme);
        rdbTimer = findViewById(R.id.rdbTimer);
        rdgNotificacao = findViewById(R.id.rdgNotificacao);

        rdbAlarme.setOnClickListener(view -> {
            edtHoraOuMinuto.setHint("Digite a hora.");
            edtMinutoOuSegundo.setHint("Digite o minuto.");
        });

        rdbTimer.setOnClickListener(view -> {
            edtHoraOuMinuto.setHint("Digite os minutos.");
            edtMinutoOuSegundo.setHint("Digite os segundos.");

        });


        btnSetAlarm.setOnClickListener(view -> {
            closeKeyboard();
            if (rdbAlarme.isChecked()) {
                int hour = Integer.parseInt(edtHoraOuMinuto.getText().toString());
                int minute = Integer.parseInt(edtMinutoOuSegundo.getText().toString());

                Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
                intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
                intent.putExtra(AlarmClock.EXTRA_MESSAGE, "Tomar água");

                if (hour <= 24 && minute <= 60) {
                    startActivity(intent);
                }
            } else if (rdbTimer.isChecked()) {
                String inputMinutos = edtHoraOuMinuto.getText().toString();
                String inputSegundos = edtMinutoOuSegundo.getText().toString();
                correctInputTimer(inputMinutos, inputSegundos);
                if (timerRunning) {
                    Toast.makeText(this, "Timer já definido!", Toast.LENGTH_LONG).show();
                } else {
                    startTimer();
                }
            }
        });
    }

    public boolean correctInputAlarme(){
        return false;
    }

    //Métodos para o timer
    public boolean correctInputTimer(String minutos, String segundos) {
        long millisInputSegundos, millisInputMinutos;

        if (minutos.length() == 0 && segundos.length() == 0) {
            Toast.makeText(this, "Os campos não podem ser vazio!", Toast.LENGTH_LONG).show();
            return false;
        }

        if (segundos.length() == 0){
            millisInputSegundos = 0;
        } else {
            millisInputSegundos = Long.parseLong(segundos) * 1000;
        }

        if (minutos.length() == 0){
            millisInputMinutos = 0;
        } else {
            millisInputMinutos = Long.parseLong(minutos) * 60000;
        }

        if (millisInputMinutos < 0 || millisInputSegundos < 0) {
            Toast.makeText(this, "Digite um valor válido!", Toast.LENGTH_LONG).show();
            return false;
        }

        setTime(millisInputMinutos, millisInputSegundos);
        edtHoraOuMinuto.setText(null);
        edtMinutoOuSegundo.setText(null);
        return true;

    }

    public void setTime(long milliSecondsMinutes, long milliSeconds) {
        startTimeInMillis = milliSecondsMinutes + milliSeconds;
        timeLeftInMillis = startTimeInMillis;
    }

    private void startTimer() {
        endTime = System.currentTimeMillis() + timeLeftInMillis;
        setNotificationAlarm();

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                timerRunning = false;
            }
        }.start();

        timerRunning = true;
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        cancelNotificationAlarm();
    }

    //Métodos para a notificação
    public void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "drinkWaterAppReminderChannel";
            String description = "Channel to notify the time to drink water";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("drinkWaterApp", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setNotificationAlarm() {
        Intent it = new Intent(this, NotificacaoReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, it, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, endTime,
                pendingIntent);
        Toast.makeText(this, "Alarme definido.", Toast.LENGTH_SHORT).show();
    }

    private void cancelNotificationAlarm(){
        Intent it = new Intent(this, NotificacaoReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, it, 0);
        AlarmManager  alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Alarme cancelado.", Toast.LENGTH_SHORT).show();
    }

    //Método para fechar o teclado
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    //Método que "salva" os valores quando o app é fechado
    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("startTimeInMillis", startTimeInMillis);
        editor.putLong("millisLeft", timeLeftInMillis);
        editor.putLong("endTime", endTime);
        editor.putBoolean("timerRunning", timerRunning);
        editor.apply();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    //Método que "recupera" os valores quando o app é iniciado
    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        startTimeInMillis = prefs.getLong("startTimeInMillis", 10000);
        timeLeftInMillis = prefs.getLong("millisLeft", startTimeInMillis);
        timerRunning = prefs.getBoolean("timerRunning", false);

        if (timerRunning) {
            endTime = prefs.getLong("endTime", 0);
            timeLeftInMillis = endTime - System.currentTimeMillis();

            if (timeLeftInMillis < 0) {
                timeLeftInMillis = 0;
                timerRunning = false;
            } else {
                startTimer();
            }
        }
    }

}