package com.example.appbebergua;

import static com.example.appbebergua.Timer.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class activity_alarmes extends AppCompatActivity {

    private EditText edtHoraOuMinuto, edtMinutoOuSegundo;
    private Button btnSetAlarm;
    private RadioButton rdbAlarme, rdbTimer;
    private RadioGroup rdgNotificacao;

    private Notificacao notificacao;

    private String escolhaUsuario;


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

                if (correctInputAlarme(hour, minute)) {
                    escolhaUsuario = "alarme";
                    startActivity(intent);
                }
            } else if (rdbTimer.isChecked()) {
                String inputMinutos = edtHoraOuMinuto.getText().toString();
                String inputSegundos = edtMinutoOuSegundo.getText().toString();
                if(correctInputTimer(inputMinutos, inputSegundos)) {
                    escolhaUsuario = "timer";
                    if (isTimerRunning()) {
                        Toast.makeText(this, "Timer já definido!", Toast.LENGTH_LONG).show();
                    } else {
                        startTimer();
                    }
                }
            }
        });
    }

    public boolean correctInputAlarme(int hour, int minute){
        if(hour <= 24 && hour > 0 &&  minute <= 60)
            return true;
        else
            return false;
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

        setTime(millisInputMinutos, millisInputSegundos);
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
        setTimeLeftInMillis(prefs.getLong("millisLeft", getStartTimeInMillis()));
        setTimerRunning(prefs.getBoolean("timerRunning", false));

        if (isTimerRunning()) {
            setEndTime(prefs.getLong("endTime", 0));
            setTimeLeftInMillis(getEndTime() - System.currentTimeMillis());

            if (getTimeLeftInMillis() < 0) {
                setTimeLeftInMillis(0);
                setTimerRunning(false);
            } else {
                startTimer();
            }
        }
    }

    //Método que "salva" os valores quando o app é fechado
    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("startTimeInMillis", getStartTimeInMillis());
        editor.putLong("millisLeft", getTimeLeftInMillis());
        editor.putLong("endTime", getEndTime());
        editor.putBoolean("timerRunning", isTimerRunning());
        editor.apply();

        if (getCountDownTimer() != null) {
            cancelCountDownTimer();
        }
    }
}