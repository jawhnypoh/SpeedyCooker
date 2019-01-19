package com.example.poj.speedycooker;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempText = (TextView)findViewById(R.id.temp_text);
        timeText = (TextView)findViewById(R.id.time_text);
        timeButton = (Button)findViewById(R.id.time_button);

        myFunction();
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
