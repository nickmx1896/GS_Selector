package com.example.nickm.fypapplication;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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
        while (true) {
            long futureTime = System.currentTimeMillis() + 1000;
            while (System.currentTimeMillis() < futureTime) {
                synchronized (this) {
                    try {
                        wait(futureTime - System.currentTimeMillis());
                        Log.i(TAG, printForegroundTask()+ " is running. PID: "+ getPID()+"\n " +
                                "nice value: "+getNice(getPID(),true));
//                        createNotification();
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    //  this is for the normal Service with running threads
    //  to implement this, extend Service instead of IntentService
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

    //  method to retrieve current task running in foreground
    private String printForegroundTask() {
        String currentApp = "NULL";

        //  check the SDK version of android
        //  different version requires different way of implementation
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
        } else {    //  older method for older Android versions
            ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }
       return currentApp;
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

    private String getGovernor(String g) {
        String gov;
        if (g == "big") {
            gov = "4";
        }
        else {
            gov = "0";
        }

        // cat command to retrieve current governor information
        String[] file = {"cat /sys/devices/system/cpu/cpu"+gov+"/cpufreq/scaling_governor"};
        return RunCommand(file);
    }

    public String getPID(){
        String cmd[] = {"pidof "+printForegroundTask()};
        return RunCommand(cmd);
    }

    public String getNice (String PID, Boolean onlyNice){
        String niceValue = "";
        //  if want to show other information like name, pid, priority
        if (onlyNice==false){
            String[] nice = {"toybox ps -o PID,NI,NAME,PRI " + "-p " + PID};
             niceValue =  RunCommand(nice);
             return niceValue;
        }
        //  else just show the nice value
        else{
            String[] nice = {"toybox ps -o NI " + "-p " + PID};
            niceValue =  RunCommand(nice);
            String output[] = niceValue.split("\\n");
            return output[1];
        }
    }

    public void ChangeNice(String pid, int newNice){

        //  command to change nice value of pid by incrementing
        int increment = 0;

        //  need to calculate increment by getting current nice value
        increment = newNice - Integer.parseInt(getNice(getPID(),true));

        //  convert the integer to string
        String inc = Integer.toString(increment);

//        String cmd[] = {"toybox renice -p -n "+inc+" "+pid};
        String cmd[] = {"renice -n "+inc+" "+pid};
        RunCommand(cmd);
    }

    public void showNotification(View v) {  //  need call this method to show notification when required

        // helps sets certain parameters of the notification, like icons etc
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher)
                //  title of the notification
                .setContentTitle("CPU Details");

        //  set the contents of the notification
        //  Inbox style --> allows notification to look bigger
        builder.setStyle(new NotificationCompat.InboxStyle()

                //  addLine each line is an info displayed inside the Inbox Style text box
                .addLine("big Governor:   " + getGovernor("big"))
                .addLine("LITTLE Governor:   " + getGovernor("little"))
                .addLine("Current App: "+printForegroundTask())
                .addLine(getNice(getPID(),false))
        );

        //  start the notification
        NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NM.notify(0, builder.build());
    }

    public void createNotification(){

        Intent intent = new Intent(this, MainActivity.class);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = taskStackBuilder.
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Sample Notification")
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setStyle(new Notification.InboxStyle()

                        //  addLine each line is an info displayed inside the Inbox Style text box
//                        .addLine("big Governor:   " + getGovernor("big"))
//                        .addLine("LITTLE Governor:   " + getGovernor("little"))
                        .addLine("Current App: "+printForegroundTask())
                        .addLine(getNice(getPID(),false)))
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

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
}
