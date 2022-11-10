package com.example.appbebergua;

import static android.content.Context.ALARM_SERVICE;
import static com.example.appbebergua.Timer.getEndTime;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Notificacao extends AppCompatActivity {

    private Context context;

    public Notificacao(Context _context){
        context = _context;
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

    public void setNotificationAlarm() {
        Intent it = new Intent(context, NotificacaoReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, it, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, getEndTime(),
                pendingIntent);
        Toast.makeText(context, "Alarme definido.", Toast.LENGTH_SHORT).show();
    }

    public void cancelNotificationAlarm(){
        Intent it = new Intent(context, NotificacaoReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, it, 0);
        AlarmManager  alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(context, "Alarme cancelado.", Toast.LENGTH_SHORT).show();
    }

}
