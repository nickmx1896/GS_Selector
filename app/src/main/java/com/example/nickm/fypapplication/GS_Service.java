package com.example.nickm.fypapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class GS_Service extends Service {

    private static final String TAG = "com.example.nickm.fypapplication";

    // the constructor
    public GS_Service() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"onStart called");

        //  IntentService makes its own thread
        //  we make our own thread now to handle this service
        Runnable r = new Runnable() {
            @Override
            public void run() {
                while(true){
                    long futureTime = System.currentTimeMillis() + 1000;
                    while (System.currentTimeMillis() < futureTime) {
                        synchronized (this){
                            try {
                                wait(futureTime-System.currentTimeMillis());
                                Log.i(TAG, "Service is doing something");
                            }catch (Exception e){}
                        }
                    }
                }
            }
        };

        Thread fypThread = new Thread(r);
        fypThread.start();
        //  start sticky means if service destroyed, it will be restarted
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"onDestroy called");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
