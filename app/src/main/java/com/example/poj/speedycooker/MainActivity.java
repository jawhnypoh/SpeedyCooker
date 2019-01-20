package com.example.poj.speedycooker;

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
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity: ";

    private TextView tempText, timeText;
    private Button timeButton;
    private theCountDownTimer myCountDownTimer;
    private NotificationManagerCompat notificationManager;

    Bluetooth bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        createNotificationChannel();

        notificationManager = NotificationManagerCompat.from(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempText = (TextView)findViewById(R.id.temp_text);
        timeText = (TextView)findViewById(R.id.time_text);
        timeButton = (Button)findViewById(R.id.time_button);

<<<<<<< HEAD
        myButton();
    }

    public void myButton(){
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "001")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("NOTIFICATION")
                .setContentText("This is a notification")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

=======
        // Create an instance of the Bluetooth class defined in Bluetooth.java
        bt = new Bluetooth(getApplicationContext(), this);

        connectBluetooth();

        myFunction();
    }

    public void connectBluetooth() {
        // If could not find bluetooth device, error
        if(bt.findBT() == -1) {
            return;
        }

        // Try to open a Bluetooth connection
        try {
            bt.openBTConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Start to listen for data
        bt.beginListenForData();
    }

    // Bluetooth instance calls this function when new data is received
    public void receiveData(byte[] data) {
        // If the data exists and != 0
        if(data.length > 0 && data[0] != 0) {
            int dataInt = data[0];
        }
    }

    public void myFunction(){
>>>>>>> 936fc95fd6e4a2e84f1af28a570a79caa28015ac
        timeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Log.d(TAG,"Button clicked");
<<<<<<< HEAD
                    myCountDownTimer = new theCountDownTimer(10000, 1);
                    myCountDownTimer.start();


                    notificationManager.notify(001, mBuilder.build());

=======
                myCountDownTimer = new theCountDownTimer(10000, 1);
                myCountDownTimer.start();
>>>>>>> 936fc95fd6e4a2e84f1af28a570a79caa28015ac
            }
        });

    }

    public class theCountDownTimer extends CountDownTimer {

        public theCountDownTimer (long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int progress = (int) (millisUntilFinished / 10 % 100);
            int progress1 = (int) (millisUntilFinished / 1000);

            timeText.setText(Integer.toString(progress1)+ ":" + Integer.toString(progress));
        }

        @Override
        public void onFinish() {
            timeText.setText("Eyyyyy");
        }
    }
//    public void sendNotification(View view){
//        NotificationCompat.Builder myBuilder = new NotificationCompat.Builder(this)
//                //.setSmallIcon(R.drawable.notification_icon)
//                .setContentTitle("NOTIFICATION")
//                .setContentText("This is a notification")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//        NotificationManager myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        //NotificationManager.notify();
//        myNotificationManager.notify(001, myBuilder.build());
//    }

    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.0){
//            CharSequence name = getString(R.string.channel_name);
//            String description = getString(R.string.channel_description);
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel("001", name, importance);
//            channel.setDescription(description);
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
    }
}
