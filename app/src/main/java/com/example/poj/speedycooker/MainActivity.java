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

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity: ";

    private TextView tempText, timeText;
    private Button timeButton;
    private theCountDownTimer myCountDownTimer;
    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        createNotificationChannel();

        notificationManager = NotificationManagerCompat.from(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempText = (TextView)findViewById(R.id.temp_text);
        timeText = (TextView)findViewById(R.id.time_text);
        timeButton = (Button)findViewById(R.id.time_button);

        myButton();
    }

    public void myButton(){
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "001")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("NOTIFICATION")
                .setContentText("This is a notification")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        timeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Log.d(TAG,"Button clicked");
                    myCountDownTimer = new theCountDownTimer(10000, 1);
                    myCountDownTimer.start();


                    notificationManager.notify(001, mBuilder.build());

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
