package com.example.appbebergua;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Notificacao extends AppCompatActivity {

    private Context context;

    private static long startTimeInMillis;
    private static long endTime;
    private static boolean timerRunning;
    public AlarmManager alarmManager;

    public Notificacao(Context context, AlarmManager alarmManager){
        this.context = context;
        this.alarmManager = alarmManager;
    }

    public void setNotificationAlarm() {
        Intent it = new Intent(context, NotificacaoReceiver.class);
        it.putExtra("primeiroAcesso", false);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, it, 0);

        //AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, endTime,
                pendingIntent);

        Toast.makeText(context, "Alarme definido.", Toast.LENGTH_SHORT).show();
    }

    /*
    public void cancelNotificationAlarm(){
        Intent it = new Intent(context, NotificacaoReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, it, 0);
        AlarmManager  alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(context, "Alarme cancelado.", Toast.LENGTH_SHORT).show();
    }
    */

    //Getters
    public static long getStartTimeInMillis() {
        return startTimeInMillis;
    }

    public static long getEndTime() {
        return endTime;
    }

    public static boolean isTimerRunning() {
        return timerRunning;
    }


    //Setters
    public static void setStartTimeInMillis(long _startTimeInMillis) {
        startTimeInMillis = _startTimeInMillis;
    }

    public static void setEndTime() {
        endTime = System.currentTimeMillis() + startTimeInMillis;
    }

    public static void setTimerRunning(boolean _timerRunning) {
        timerRunning = _timerRunning;
    }

}
