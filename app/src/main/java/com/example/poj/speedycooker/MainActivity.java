package com.example.poj.speedycooker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
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

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity: ";

    private TextView tempText, timeText;
    private Button timeButton, choice1button;
    private theCountDownTimer myCountDownTimer;
    private NotificationManagerCompat notificationManager;

    private BluetoothAdapter mBluetoothAdapter;
    private Bluetooth bt = null;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        createNotificationChannel();

        notificationManager = NotificationManagerCompat.from(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempText = (TextView)findViewById(R.id.temp_text);
        timeText = (TextView)findViewById(R.id.time_text);
        timeButton = (Button)findViewById(R.id.time_button);
        choice1button = (Button)findViewById(R.id.choice1button);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bt = new Bluetooth(getApplicationContext(), mHandler);

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }

        myButton();
    }

    @Override
    public void onStart() {
        super.onStart();

        // If Bluetooth isn't on, ask for it to be turned on
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the our application
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    tempText.setText("Received Data: " + readMessage);
                    break;

                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    break;
            }
        }
    };

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

                sendMessage("Test");

                myCountDownTimer = new theCountDownTimer(5000, 1);
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

    private void sendMessage(String message) {

        Toast.makeText(getApplicationContext(), "Sending data to bluetooth device...", Toast.LENGTH_LONG).show();


        Log.d(TAG, "Message is: " + message);
        if(message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            bt.write(send);
        }
    }

    private void receiveMessage() {

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

            int progress = (int) (millisUntilFinished / 10000);
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
