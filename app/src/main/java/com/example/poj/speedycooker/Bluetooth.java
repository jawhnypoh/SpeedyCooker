package com.example.poj.speedycooker;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.provider.SyncStateContract;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.util.Set;
import java.util.UUID;

public class Bluetooth {

    private final String TAG = "Blueooth: ";

    private String DEVICE_NAME = "HC-05"; // Figure out bluetooth device name
    private String DEVICE_UUID;

    private static final UUID deviceUUID = UUID.fromString("DEVICE_UUID");

    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;

    private InputStream mInputStream;
    private OutputStream mOutputStream;

    private Thread workerThread;
    private byte[] readBuffer;
    private int readBufferPosition;

    private Context context;
    private MainActivity mainActivity;

    private volatile boolean stopWorker;

    // Defines several constants used when transmitting messages between the
    // service and the UI.
    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }


    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use tmp object that's later assigned to mmSocket since it's declared as final
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                tmp = device.createInsecureRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery since it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Try to connect to the remote device through the socket
                mmSocket.connect();
            } catch (IOException connectException) {
                // Can't connect, close the socket and return
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close client socket ", closeException);
                }
                return;
            }
        }

        // Closes client thread socket and causes the thread to finish
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close socket. ", closeException);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get Input/Output streams, use tmp because member streams are final
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Could not create input stream. ", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch(IOException e) {
                Log.e(TAG, "Could not create output stream. ", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "Begin mConnectedThread");

            mmBuffer = new byte[1024];
            int numBytes; // Bytes returned from read

            // Keep listening to input stream until exception occurs
            while (true) {
                try {
                    // Read from InputStream
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();

                } catch (IOException e) {
                    Log.e(TAG, "Disconnected. ", e);

                }
            }
        }

        // Call from MainActivity to send data to remote device
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();

            } catch(IOException e) {
                Log.e(TAG, "Error sending data to device. ", e);

                // Send failure message back to the activity
                Message writeErrorMsg =
                        mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                mHandler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }

    }

    public Bluetooth(Context context, MainActivity mainActivity) {
        // When this object is created, it needs the context of it's instantiating class, as well as
        // reference to the class itself
        this.context = context;
        this.mainActivity = mainActivity;
        stopWorker = false; // Flag to start/stop the background thread checking for data
    }

    // Search for bluetooth with the DEVICE_NAME
    public int findBT() {

        // Retrieve a BluetoothAdapter object
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If BluetoothAdapter returns nothing
        if (mBluetoothAdapter == null) {
            Toast toast = Toast.makeText(context, "No Bluetooth adapter available", Toast.LENGTH_LONG);
            toast.show();

            return -1;
        }

        // Check to make sure Bluetooth is enabled
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity) context).startActivityForResult(enableBluetooth, 0);
        }

        // Loop through all paired devices looking for match to DEVICE_NAME
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(DEVICE_NAME)) {
                    mDevice = device;
                    Method method = null;
                    try {
                        // If and when device is found, retrieved it's UUID
                        method = mDevice.getClass().getMethod("getUuids", null);
                        ParcelUuid[] deviceUuids = (ParcelUuid[]) method.invoke(mDevice, null);
                        DEVICE_UUID = deviceUuids[0].getUuid().toString(); // Will only be one UUID.
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    break;
                }
            }
        }

        // If device was not found
        if(DEVICE_UUID == null) {
            // Couldn't find the device, so user must pair it themselves with Bluetooth settings
            Intent intentBluetooth = new Intent();
            intentBluetooth.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentBluetooth.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            context.startActivity(intentBluetooth);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(context, "Pair with " + DEVICE_NAME + " in Bluetooth Settings with key 1234. Then return to app and try again.", Toast.LENGTH_LONG);
                    toast.show();
                }
            }, 1000);

            return -1;
        }
        // Otherwise, device was found!
        else {
            Toast toast = Toast.makeText(context, "Bluetooth device found: " + DEVICE_UUID, Toast.LENGTH_LONG);
            toast.show();
        }
        return 0;
    }

    // Open a Bluetooth connection with DEVICE_NAME
    public void openBTConnection() throws IOException {
        // Create socket, connect to device, grab I/O data streams
        UUID uuid = UUID.fromString(DEVICE_UUID); // Standard SerialPortService ID

        mSocket = mDevice.createRfcommSocketToServiceRecord(uuid);
        mSocket.connect();

        mOutputStream = mSocket.getOutputStream();
        mInputStream = mSocket.getInputStream();

        Toast toast = Toast.makeText(context, "Bluetooth connection opened", Toast.LENGTH_LONG);
        toast.show();
    }

//    public void beginListenForData()
//    {
//        Toast toast = Toast.makeText(context, "Listening for incoming Bluetooth data in background", Toast.LENGTH_LONG);
//        toast.show();
//
//        final Handler handler = new Handler();
//        final byte delimiter = 10; //This is the ASCII code for a newline character '\n'
//
//        stopWorker = false;
//        readBufferPosition = 0;
//        readBuffer = new byte[1024];
//        workerThread = new Thread(new Runnable()
//        {
//            public void run()
//            {
//                while(!Thread.currentThread().isInterrupted() && !stopWorker) // While stopWorker is false
//                {
//                    try
//                    {
//                        // If there is no input data stream then bail
//                        if(mInputStream == null) {
//                            stopWorker = true;
//                            break;
//                        }
//
//
//                        int bytesAvailable = mInputStream.available();
//                        if(bytesAvailable > 0) // If there is data available to be read in
//                        {
//                            // Read in the data and loop through each byte
//                            byte[] packetBytes = new byte[bytesAvailable];
//                            mInputStream.read(packetBytes);
//                            for(int i=0;i<bytesAvailable;i++)
//                            {
//                                byte b = packetBytes[i];
//                                if(b == delimiter) // If the new line character is found
//                                {
//                                    // Grab the read data and pass it to the MainActivity
//                                    final byte[] encodedBytes = new byte[readBufferPosition];
//                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
//                                    readBufferPosition = 0;
//                                    handler.post(new Runnable()
//                                    {
//                                        public void run()
//                                        {
//                                            mainActivity.receiveData(encodedBytes); // MAKE SURE MAINACTIVITY IMPLEMENTS THIS METHOD
//                                        }
//                                    });
//                                }
//                                else // While the delimeter (new line char) is not found, keep looping through bytes
//                                {
//                                    readBuffer[readBufferPosition++] = b;
//                                }
//                            }
//                        }
//                    }
//                    catch (IOException ex)
//                    {
//                        stopWorker = true;
//                    }
//                }
//            }
//        });
//
//        workerThread.start(); // Start the background thread
//    }

    // Send a byte of data over Bluetooth
    public void sendData(byte data) throws IOException {
        mOutputStream.write(data);

        Log.d(TAG, "Data is: " + data);
    }

    // Close the Bluetooth connection and clean stuff up
    public void closeBT() throws IOException {
        stopWorker = true;

        mOutputStream.close();
        mInputStream.close();
        mSocket.close();
        Toast toast = Toast.makeText(context, "Bluetooth closed", Toast.LENGTH_LONG);
        toast.show();
    }
}
