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
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


public class GS_Service extends IntentService{

    private static final String TAG = "com.example.nickm.fypapplication";
    public String prevApp;
    static boolean firstGovChangeEntry = true;
    //  IntentService Constructor
    public GS_Service() {
        super("GSIntentService");
    }


    //  handles intent, need to extend IntentService instead
    @Override
    protected void onHandleIntent(Intent intent) {
        HashMap<String, String> map = new HashMap<>();
        Integer time = 1000;
        Integer time_getFreq = 0;
        map.put("com.ea.games.r3_row","game1");
        map.put("com.gameloft.android.ANMP.GloftNOHM","game2");
        map.put("com.MikaMobile.Battleheart","game3");
        map.put("com.cyanogenmod.trebuchet","launcher");
        int cnt = 0;

        //  following two lists if fore retrieving the frequency levels
        ArrayList<String> littleList = new ArrayList<>();
        ArrayList<String> bigList = new ArrayList<>();
        while (true) {
            long futureTime = System.currentTimeMillis() + time;

            /*  the following code will be run every time milliseconds

                Games and apps that we going to test
                Games:
                    com.ea.games.r3_row
                    com.MikaMobile.Battleheart
                    com.gameloft.android.ANMP.GloftNOHM

                Apps:
                    com.facebook.katana
                    com.futuremark.pcmark.android.benchmark
                    com.instagram.android

            */
            while (System.currentTimeMillis() < futureTime) {   //  maybe activate if foreground app change?
                synchronized (this) {
                    try {
                        //  getPID will retrieve the PID of the current foreground app
                        String currentPID = getPID();
                        wait(futureTime - System.currentTimeMillis());

                        Log.i(TAG, Integer.toString(cnt) + " " + printForegroundTask() + " nice: " + getNice(currentPID,true)+" PID:"+currentPID
                +"\n"+"little: "+getGovernor("little")+"big: "+getGovernor("big"));
/*                        ################################################################################################################################
                        ################################################################################################################################
                        ################################################################################################################################
                        ################################################################################################################################
                        ################################################################################################################################*/
                        //  check the value for each key
                        //  the key in the map is the package name which will be checked against the current running foreground app
                        //  the following should also change nice values etc


                        //  check if the foreground app has changed
                        if (!printForegroundTask().equals(prevApp) || firstGovChangeEntry) {
                            firstGovChangeEntry = false;
                            //  if the current task has a genre present, we check what governor to swap to
                            if (map.get(printForegroundTask()) != null) {
                                Log.i(TAG,"entered switch, prevApp is:"+prevApp+" current is:"+printForegroundTask());
                                switch (map.get(printForegroundTask())) {
                                    case "game1":   //  require the best performance
                                        //  row
                                        ChangeGovernors("performance");
                                        break;
                                    case "game2":   //  require not so much
                                        //  nova
                                        ChangeGovernors("interactive");
                                        break;
                                    case "game3":   //  more for older games that don't need much power
                                        //  battleheart
                                        ChangeBigGovernors("smartassV2");
                                        ChangeLittleGovernors("interactive");
                                        break;
                                    case "launcher":    //  do nothing if the foreground is the fyp app
//                                    setLowest();
                                        ChangeGovernors("smartassV2");
                                        break;
                                    default:
                                        ChangeGovernors("smartassV2");
                                        break;
                                }
                            }
                            //  for any other app that don't have a genre yet
                            else{
                                ChangeGovernors("smartassV2");
                            }
                        }

                        prevApp = printForegroundTask();
/*                        ################################################################################################################################
                        ################################################################################################################################
                        ################################################################################################################################
                        ################################################################################################################################
                        ################################################################################################################################*/

                        //  does debugging or make changes depending on the app name
/*                        if (printForegroundTask().equals("com.ea.games.r3_row")) {
*//*                      ################################################################################################################################
                        this part changes nice depending on the app, will be implemented in the switch case above
                        ################################################################################################################################*//*
//                            if (!(getNice(currentPID, true).equals("-20"))) {
//                                ChangeNice(currentPID, -20);
//                                Log.i(TAG, "changed");
//                            }
//                            else {
//                                Log.i(TAG, "no change");
//                            }
*//*//*                       ################################################################################################################################
                          end of change of nice value
                          ################################################################################################################################*//*
//                          get clock frequency info for 60 counts
                            littleList.add(getFreq("little"));
                            bigList.add(getFreq("big"));
//                            counter to check if the app running
                            cnt = cnt + 1;
                            time_getFreq = time_getFreq + time;
                        }*/

                        //  following is to print the frequency after 90 seconds passed in that game/app
//                        if(time_getFreq == 60000){
//                            Log.i(TAG,"CPUFreq List\n"+"little\n"+print(littleList)+"\nbig\n"+print(bigList));
//                        }

                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    private String print(ArrayList<String> file){
        StringBuilder newfile = new StringBuilder();
        for (int i = 0; i<file.size();i++){
//            newfile.append("\n");
            newfile.append(file.get(i));
        }
        return newfile.toString();
    }

    public String getCPUUsage(int pid) {
        String[] cmd = {
                "sh",
                "-c",
                "top -m 1000 -d 1 -n 1 | grep \""+pid+"\" "};
        return RunCommand(cmd);
    }

    public void governorAlgo(){
        //  built on the premise that my service decides what app should use what governor
            //  based on internal testings --> the report table

        //  check if genre is game or what not
            //  maybe can split game into 3 different performance based sub sections
            //  game1 --> highest performance, game3 --> lowest performance

        //  this can be done by calling a statically defined dictionary or table, probably a Map<String><String>
            //  this map should be defined statically somewhere in the code, then constantly updated as more apps come
            //  check the package name and see its label

        //  while label exists, switch label, each label, change the governor accordingly
            //  service class should run this algo


        //  can include the changing of nice values or cgroups in the future
            //  based on testing as well to determine what apps require what cgroup or nice configs
    }

    //  this is for the normal Service with running threads
    //  to implement this, extend Service instead of IntentService

/*    public void onCreate() {
        HandlerThread handlerthread = new HandlerThread("MyHandlerThread", Process.THREAD_PRIORITY_BACKGROUND);
        handlerthread.start();
        looper = handlerthread.getLooper();
        myServiceHandler = new MyServiceHandler(looper);
        isRunning = true;
        createNotification();
    }*/

/*    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //  IntentService makes its own thread
        //  we make our own thread now to handle this service
*//*        Message msg = myServiceHandler.obtainMessage();

        msg.arg1 = startId;
        myServiceHandler.sendMessage(msg);
        Toast.makeText(this, "MyService Started.", Toast.LENGTH_SHORT).show();
        //If service is killed while starting, it restarts.
        return START_STICKY;*//*
*//*        Runnable r = new Runnable() {

            @Override
            public void run() {
                while(true){
                    long futureTime = System.currentTimeMillis() + 1000;
                    while (System.currentTimeMillis() < futureTime) {
                        synchronized (this){
                            try {
                                wait(futureTime-System.currentTimeMillis());
                                Log.i(TAG,"\n"+getFreq("little")+"\n"+getFreq("big"));   //  get little and big cpu freqs

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
        return Service.START_NOT_STICKY;*//*
    }*/
/*
    @Override
    public void onDestroy() {
        Toast.makeText(this, "MyService Completed or Stopped.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }*/

/*    private final class MyServiceHandler extends Handler {
        public MyServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            while(true){
                long futureTime = System.currentTimeMillis() + 1000;
                while (System.currentTimeMillis() < futureTime) {
                    synchronized (this){
                        try {
                            wait(futureTime-System.currentTimeMillis());
//                            createNotification();
//                            Log.i(TAG,"\n"+getFreq("little")+"\n"+getFreq("big"));   //  get little and big cpu freqs
                            Log.i(TAG, printForegroundTask());
                        }catch (Exception e){}
                    }
                }
            }
        }
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


    private NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    private boolean firstTime = true;


    public void showNotification() {  //  need call this method to show notification when required

        // helps sets certain parameters of the notification, like icons etc
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        if (firstTime) {
            builder.setSmallIcon(R.drawable.ic_launcher)
                    //  title of the notification
                    .setContentTitle("CPU Details")
            .setOnlyAlertOnce(true);
            builder.setStyle(new NotificationCompat.InboxStyle()

                    //  addLine each line is an info displayed inside the Inbox Style text box
                    .addLine("big Governor:   " + getGovernor("big"))
                    .addLine("LITTLE Governor:   " + getGovernor("little"))
                    .addLine("Current App: "+printForegroundTask())
                    .addLine(getNice(getPID(),false))
            );
            firstTime = false;
        }
        builder.setStyle(new NotificationCompat.InboxStyle()

                //  addLine each line is an info displayed inside the Inbox Style text box
                .addLine("big Governor:   " + getGovernor("big"))
                .addLine("LITTLE Governor:   " + getGovernor("little"))
                .addLine("Current App: "+printForegroundTask())
                .addLine(getNice(getPID(),false))
        );
        NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NM.notify(notificationID, builder.build());
    }

    private int notificationID = 1;
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
                        .addLine("big Governor:   " + getGovernor("big"))
                        .addLine("LITTLE Governor:   " + getGovernor("little"))
                        .addLine("Current App: "+printForegroundTask()))
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

    }

    String RunCommand(String[] cmd) {

        //  run any terminal command through this function, that doesn't return anything
        java.lang.Process process;
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


    //###################################################################################
    //  everything here onwards is for getting cpu related stuff

    public void setLowest(){
        String[] cmd= {"echo 442000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_setspeed",
                "echo 442000 > /sys/devices/system/cpu/cpu1/cpufreq/scaling_setspeed",
                "echo 442000 > /sys/devices/system/cpu/cpu2/cpufreq/scaling_setspeed",
                "echo 442000 > /sys/devices/system/cpu/cpu3/cpufreq/scaling_setspeed",
                "echo 520000 > /sys/devices/system/cpu/cpu4/cpufreq/scaling_setspeed",
                "echo 520000 > /sys/devices/system/cpu/cpu5/cpufreq/scaling_setspeed",
                "echo 520000 > /sys/devices/system/cpu/cpu6/cpufreq/scaling_setspeed",
                "echo 520000 > /sys/devices/system/cpu/cpu7/cpufreq/scaling_setspeed",};
        RunCommand(cmd);
        ChangeGovernors("userspace");
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
    public void ChangeBigGovernors(String governor) {
        String[] newGovernor = {"echo " + governor + " > /sys/devices/system/cpu/cpu4/cpufreq/scaling_governor",
                "echo " + governor + " > /sys/devices/system/cpu/cpu5/cpufreq/scaling_governor",
                "echo " + governor + " > /sys/devices/system/cpu/cpu6/cpufreq/scaling_governor",
                "echo " + governor + " > /sys/devices/system/cpu/cpu7/cpufreq/scaling_governor"};
        RunCommand(newGovernor);
    }
    public void ChangeLittleGovernors(String governor) {
        String[] newGovernor = {"echo " + governor + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor",
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

    private String getAllFreq(){
        String[] cmd = {"cat /sys/devices/system/cpu/cpu*/cpufreq/cpuinfo_cur_freq"};
        return RunCommand(cmd);
    }

    private String getFreq(String cpuType) {
        if (cpuType.equals("little")) {
            String[] file = {"cat /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_cur_freq"};
//                String[] file = {"cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq"};
            return RunCommand(file);
        } else {
            String[] file = {"cat /sys/devices/system/cpu/cpu4/cpufreq/cpuinfo_cur_freq"};
//                String[] file = {"cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq"};
            return RunCommand(file);
        }


    }

    //  print all the frequencies
/*    private String getFreq(String cpuType) {
        int i;
        String[] file = new String[8];
        if (cpuType.equals("little")){
            for (i = 0; i < 4; i++){
                file[i] = "cat /sys/devices/system/cpu/cpu"+Integer.toString(i)+"/cpufreq/cpuinfo_cur_freq";
            }
            return RunCommand(file);
        }
        else {
            for (i = 4; i < 8; i++){
                file[i] = "cat /sys/devices/system/cpu/cpu"+Integer.toString(i)+"/cpufreq/cpuinfo_cur_freq";
            }
            return RunCommand(file);
        }

    }*/

    public String getCgroup(String PID){
        String[] cmd = {"cat /proc/"+PID+"/cgroup"};
        return RunCommand(cmd);
    }

    public String getSpecificPID(String process){
        String cmd[] = {"pidof "+process};
        String pid = RunCommand(cmd);
        String returnPID = pid.replace("\n", "").replace("\r", "");
        return returnPID;
    }

    public String getPID(){
        String cmd[] = {"pidof "+printForegroundTask()};
        String pid = RunCommand(cmd);
        String returnPID = pid.replace("\n", "").replace("\r", "");
        return returnPID;
    }

    public String getSpecificNice(String process){
        String[] cmd = {"top -n 1 | grep "+process};
        return RunCommand(cmd);
    }

    public String getNice (String PID, Boolean onlyNice){
        String niceValue = "";
        //  if want to show other information like name, pid, priority
        if (onlyNice==false){
//            String[] nice = {"toybox ps -o PID,NI,NAME,PRI,GROUP " + "-p " + PID};

            //  this code gets a specific nice for 1 iteration
            String[] nice = {"top -n 1 | grep row"};
            return RunCommand(nice);

        }
        //  else just show the nice value
        else{
            String[] nice = {"toybox ps -o NI " + "-p " + PID};
            niceValue =  RunCommand(nice);
            String output[] = niceValue.split("\\n");
            return output[1];
        }
    }




    public String getCPU (String PID, Boolean onlyCPU){
        String cpuValue = "";
        if (onlyCPU==false){
//            String[] nice = {"toybox ps -o PID,PCPU,NAME " + "-p " + PID};
            String[] nice = {"top -n 1 | grep Battle"};
            cpuValue =  RunCommand(nice);
            return cpuValue;
        }
        //  else just show the nice value
        else{
//            String[] nice = {"toybox ps -o NI " + "-p " + PID};
            String[] nice = {"top -n 1 | grep Battle"};
            cpuValue =  RunCommand(nice);
            String output[] = cpuValue.split("\\n");
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
//        String inc = Integer.toString(newNice);

//        String cmd[] = {"toybox renice -p -n "+inc+" "+pid};
        String cmd[] = {"toybox renice -n "+inc+" "+pid};
        RunCommand(cmd);
    }


}
