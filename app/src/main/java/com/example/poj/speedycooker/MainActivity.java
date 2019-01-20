package com.example.poj.speedycooker;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.CountDownTimer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity: ";

    private TextView tempText, timeText;
    private Button timeButton, choice1button;
    private theCountDownTimer myCountDownTimer;
    private NotificationManagerCompat notificationManager;

    private Bluetooth bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //createNotificationChannel();

        notificationManager = NotificationManagerCompat.from(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempText = (TextView)findViewById(R.id.temp_text);
        timeText = (TextView)findViewById(R.id.time_text);
        timeButton = (Button)findViewById(R.id.time_button);
        choice1button = (Button)findViewById(R.id.choice1button);

        bt = new Bluetooth(getApplicationContext(), this);

        myButton();
//        connectBluetooth();
    }

//    public void connectBluetooth() {
//        // If could not find bluetooth device, error
//        if(bt.findBT() == -1) {
//            return;
//        }
//
//        // Try to open a Bluetooth connection
//        try {
//            bt.openBTConnection();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // Start to listen for data
//        bt.beginListenForData();
//    }
//
//    // Bluetooth instance calls this function to send new data to the BT device
//    public void sendBTData() {
//        try {
//            bt.sendData((byte)'0');
//            Log.d(TAG, "Data sending to BT device...");
//        } catch (IOException e) {
//            e.printStackTrace();
//            return;
//        }
//    }
//
//    // Bluetooth instance calls this function when new data is received
//    public void receiveData(byte[] data) {
//        // If the data exists and != 0
//        if(data.length > 0 && data[0] != 0) {
//            int dataInt = data[0];
//
//            Log.d(TAG, "Receiving data: " + data);
//
//            tempText.setText("" + dataInt);
//        }
//    }

    public void myButton(){
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "001")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("NOTIFICATION")
                .setContentText("This is a notification")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        final int PROGRESS_MAX = 100;
        int PROGRESS_CURRENT = 0;
        mBuilder.setProgress(PROGRESS_MAX,PROGRESS_CURRENT, false);

        timeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Log.d(TAG,"Button clicked");
                myCountDownTimer = new theCountDownTimer(70000, 1);
                myCountDownTimer.start();

            }
        });
        choice1button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                notificationManager.notify(001, mBuilder.build());
            }
        });
    }

    public NotificationCompat.Builder buildNot(){

        Log.d(TAG, "buildNot called");
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "001")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("NOTIFICATION")
                .setContentText("This is a notification")
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        //.setDefaults(NotificationCompat.DEFAULT_ALL);
        return mBuilder;
    }

    public class theCountDownTimer extends CountDownTimer {

        NotificationCompat.Builder bar = buildNot();
        long MAX_CONST;
        public theCountDownTimer (long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            MAX_CONST = millisInFuture;
        }

        @Override
        public void onTick(long millisUntilFinished) {

            int progress = (int) (millisUntilFinished / 10 % 100);
            int progress1 = (int) (millisUntilFinished / 1000);

            int progressPer = (int) ((double)millisUntilFinished/MAX_CONST*100);
            Log.d(TAG, Integer.toString(progressPer));
            if (progress1>59){
                timeText.setText(Integer.toString(progress1/60)+ " min " + Integer.toString(progress1%60) + " s");
            }
            else {
                timeText.setText(Integer.toString(progress1) + ":" + Integer.toString(progress) + " s");
            }
            notificationManager.notify(001, bar.build());
            bar.setProgress(100,progressPer, false);
            bar.setContentText(Integer.toString(progress1/60)+ " min " + Integer.toString(progress1%60) + " s remaining");
        }

        @Override
        public void onFinish() {
//            // Try to close the Bluetooth socket
//            try {
//                bt.closeBT();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            timeText.setText("Eyyyyy");
        }
    }
}
