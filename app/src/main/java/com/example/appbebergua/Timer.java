package com.example.appbebergua;

import android.content.Context;
import android.os.CountDownTimer;

public class Timer {

    private static long startTimeInMillis;
    private static long timeLeftInMillis;
    private static long endTime;
    private static boolean timerRunning;
    private static CountDownTimer countDownTimer;
    private static Context context;
    private static Notificacao notificacao;

    //Getters
    public static long getStartTimeInMillis() {
        return startTimeInMillis;
    }

    public static long getTimeLeftInMillis() {
        return timeLeftInMillis;
    }

    public static long getEndTime() {
        return endTime;
    }

    public static boolean isTimerRunning() {
        return timerRunning;
    }

    public static CountDownTimer getCountDownTimer(){
        if(countDownTimer != null)
            return countDownTimer;
        else
            return null;
    }

    public static void cancelCountDownTimer(){
        countDownTimer.cancel();
    }

    //Setters
    public static void setStartTimeInMillis(long _startTimeInMillis) {
        startTimeInMillis = _startTimeInMillis;
    }

    public static void setTimeLeftInMillis(long _timeLeftInMillis) {
        timeLeftInMillis = _timeLeftInMillis;
    }

    public static void setEndTime(long _endTime) {
        endTime = _endTime;
    }

    public static void setTimerRunning(boolean _timerRunning) {
        timerRunning = _timerRunning;
    }

    public static void setContext(Context _context){
        context = _context;
        notificacao = new Notificacao(context);
        notificacao.createNotificationChannel();
    }

    //Métodos para o timer
    public static void setTime(long milliSecondsMinutes, long milliSeconds) {
        startTimeInMillis = milliSecondsMinutes + milliSeconds;
        timeLeftInMillis = startTimeInMillis;
    }

    public static void startTimer() {
        endTime = System.currentTimeMillis() + timeLeftInMillis;
        notificacao.setNotificationAlarm();

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

    private static void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        notificacao.cancelNotificationAlarm();
    }
}
