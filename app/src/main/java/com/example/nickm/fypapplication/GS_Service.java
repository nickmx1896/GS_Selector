package com.example.nickm.fypapplication;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;


public class GS_Service extends IntentService {

    private static final String TAG = "com.example.nickm.fypapplication";
    private boolean serviceRun = false;


    //  IntentService Constructor
    public GS_Service() {
        super("GSIntentService");
    }


    //  handles intent, need to extend IntentService instead
    @Override
    protected void onHandleIntent(Intent intent) {
        int i = 0;
        while (true) {
            long futureTime = System.currentTimeMillis() + 1000;
            while (System.currentTimeMillis() < futureTime) {
                synchronized (this) {
/*                    ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

                    ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);


                    //  to get the current foreground app
                    String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
                    PackageManager pm = this.getPackageManager();*/
                    try {
                        wait(futureTime - System.currentTimeMillis());
/*                        PackageInfo foregroundAppPackageInfo = pm.getPackageInfo(foregroundTaskPackageName, 0);
                        String foregroundTaskAppName = foregroundAppPackageInfo.applicationInfo.loadLabel(pm).toString();*/
                        i += 1;
                        Log.i(TAG, i + "Intent Service started: "+printForegroundTask());
                        if (printForegroundTask().contains("Battleheart")){
                            View notif = new View(this);
                            Log.i(TAG,"battleheart running");
                            ChangeGovernors("interactive");
                            showNotification(notif);
                        }
                        else{
                            Log.i(TAG,"not running");
                            ChangeGovernors("smartmax");
                        }
//                        fore();

//                                sendMessage();
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    //  this is for the normal Service
/*    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"onStart called");


        //  IntentService makes its own thread
        //  we make our own thread now to handle this service
        Runnable r = new Runnable() {
            int i = 0;
            @Override
            public void run() {
                while(true){
                    long futureTime = System.currentTimeMillis() + 1000;
                    while (System.currentTimeMillis() < futureTime) {
                        synchronized (this){
                            try {
                                wait(futureTime-System.currentTimeMillis());
                                i += 1;
                                Log.i(TAG, i+"Runnable Service running, normal Service");
//                                sendMessage();
                            }catch (Exception e){}
                        }
                    }
                }
            }
        };

        Thread fypThread = new Thread(r);
        fypThread.start();
        //  start sticky means if service destroyed, it will be restarted
        return Service.START_NOT_STICKY;
    }*/


    private String printForegroundTask() {
        String currentApp = "NULL";
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }

       return currentApp;
    }

    private void fore(){
        ActivityManager activityManager = (ActivityManager) this.getSystemService( Context.ACTIVITY_SERVICE );
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo appProcess : appProcesses){
//            if(appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                Log.i(TAG, appProcess.processName);
//            }
        }
    }
    // Send an Intent with an action named "my-event".
    //  this is to send a message to the broadcast receiver
    private void sendMessage() {
        Intent intent = new Intent("my-event");
        // add data
        intent.putExtra("message", "change big gov to performance");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"onDestroy called");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void ChangeGovernors(String governor) {
        String[] newGovernor = {"echo " + governor + " > /sys/devices/system/cpu/cpu4/cpufreq/scaling_governor",
                "echo " + governor + " > /sys/devices/system/cpu/cpu5/cpufreq/scaling_governor",
                "echo " + governor + " > /sys/devices/system/cpu/cpu6/cpufreq/scaling_governor",
                "echo " + governor + " > /sys/devices/system/cpu/cpu7/cpufreq/scaling_governor",
                "echo " + governor + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor",
                "echo " + governor + " > /sys/devices/system/cpu/cpu1/cpufreq/scaling_governor",
                "echo " + governor + " > /sys/devices/system/cpu/cpu2/cpufreq/scaling_governor",
                "echo " + governor + " > /sys/devices/system/cpu/cpu3/cpufreq/scaling_governor"};
        RunCommand(newGovernor);
    }
    private String isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return null;
        }

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return appProcess.processName;
            }
        }
        return null;
    }
    String RunCommand(String[] cmd) {

        //  run any terminal command through this function, that doesn't return anything
        Process process;
        try {
            //  run as root
            process = Runtime.getRuntime().exec("su");

            DataOutputStream os = new DataOutputStream(process.getOutputStream());

            // the command will be written
            if (os != null) {
                //  iterate through the command string
                for (int i = 0; i < cmd.length; i++) {
                    os.writeBytes(cmd[i] + "\n");
                    os.flush();
                }
            }

            // command to exit the shell command
            os.writeBytes("exit" + "\n");
            os.flush();
            os.close();
            //////////////////////////////////////////////////////////////////////////
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();
            //////////////////////////////////////////////////////////////////////////

            process.waitFor();

            return output.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getGovernor(String g) {
        StringBuffer sb = new StringBuffer();
        String gov;
        if (g == "big") {
            gov = "4";
        }
        else {
            gov = "0";
        }
        String file = "/sys/devices/system/cpu/cpu"+gov+"/cpufreq/scaling_governor";  // Gets governor for big cores

        if (new File(file).exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File(file)));
                String aLine;
                while ((aLine = br.readLine()) != null)
                    sb.append(aLine + "\n");

                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public void showNotification(View v) {
        //PendingIntent update = PendingIntent.getActivity(this,0,update,PendingIntent.FLAG_ONE_SHOT);
        // helps sets certain parameters of the notification, like icons etc
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("CPU Details");
        builder.setStyle(new NotificationCompat.InboxStyle()    // allows the notification to become bigger
                        .addLine("big Governor:   " + getGovernor("big"))
                        .addLine("LITTLE Governor:   " + getGovernor("little"))
//                .addLine("Int IO Scheduler:    " + getScheduler("sda"))
//                .addLine("Ext IO Scheduler:    " + getScheduler("mmcblk0"))
//                .addLine(getNice())
        );
        NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NM.notify(0, builder.build());


    }
}
