package com.example.poj.speedycooker;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity: ";

    private TextView tempText, timeText;
    private Button timeButton, choice1button;
    private theCountDownTimer myCountDownTimer;
    private NotificationManagerCompat notificationManager;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC Address
    private static String address;

    Handler bluetoothIn;
    final int handlerState = 0;     // Use to identify handler message

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mBluetoothSocket;

    private ConnectedThread mConnectedThread;

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

        bluetoothIn = new Handler() {
            public void handleMessage(Message msg) {
                if(msg.what == handlerState) {
                    Log.d(TAG, "Content in msg ");
                    String readMessage = (String) msg.obj;
                    tempText.setText("Received data: " + readMessage);
                }
            }
        };

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        myButton();
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        //creates secure outgoing connection with BT device using UUID
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        try {
            mBluetoothSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            mBluetoothSocket.connect();
        } catch (IOException e) {
            try
            {
                mBluetoothSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(mBluetoothSocket);
        mConnectedThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            // Don't leave Bluetooth sockets open when we leave the activity
            mBluetoothSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Couldn't close bluetooth socket. ", e);
        }
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(mBluetoothAdapter == null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (mBluetoothAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    // Create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Could not set tmpIn and tmpOut. ", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);        	//read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            Log.d(TAG, "input string is: " + input);
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes

            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

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

                Toast.makeText(getApplicationContext(), "Sending data to bluetooth device...", Toast.LENGTH_LONG).show();

                String input = "Test";

                Log.d(TAG,"Button clicked");

                mConnectedThread.write(input);

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

            timeText.setText("Eyyyyy");
        }
    }
}
