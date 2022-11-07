package com.example.appbebergua;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.time.Clock;

public class activity_alarmes extends AppCompatActivity {

    EditText edtHour, edtMinute;
    Button btnSetAlarm;

    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmes);

        edtHour = findViewById(R.id.edtHour);
        edtMinute = findViewById(R.id.edtMinute);
        btnSetAlarm = findViewById(R.id.btnSetAlarm);

        btnSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    int hour = Integer.parseInt(edtHour.getText().toString());
                    int minute = Integer.parseInt(edtMinute.getText().toString());

                    Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                    intent.putExtra(AlarmClock.EXTRA_HOUR,hour);
                    intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
                    intent.putExtra(AlarmClock.EXTRA_MESSAGE, "Tomar água");

                    if(hour <= 24 && minute <=60) {
                        startActivity(intent);
                    }
            }


        });
    }


}