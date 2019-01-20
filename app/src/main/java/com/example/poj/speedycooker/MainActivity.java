package com.example.poj.speedycooker;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.CountDownTimer;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends Activity {

    private static String TAG = "MainActivity: ";

    private TextView tempText, timeText;
    private Button timeButton;
    private theCountDownTimer myCountDownTimer;

    Bluetooth bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempText = (TextView)findViewById(R.id.temp_text);
        timeText = (TextView)findViewById(R.id.time_text);
        timeButton = (Button)findViewById(R.id.time_button);

        // Create an instance of the Bluetooth class defined in Bluetooth.java
        bt = new Bluetooth(getApplicationContext(), this);

//        connectBluetooth();

        myFunction();
    }

    public void connectBluetooth(View view) {
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
        timeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Log.d(TAG,"Button clicked");
                myCountDownTimer = new theCountDownTimer(10000, 1);
                myCountDownTimer.start();
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
}
