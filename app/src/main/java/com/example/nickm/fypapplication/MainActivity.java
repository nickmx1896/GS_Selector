package com.example.nickm.fypapplication;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "com.example.nickm.fypapplication";
    private static boolean onService = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // view object for the notification
        View notification = new View(this);






        //  text to show governor information
        TextView textView1 = findViewById(R.id.bigGovernor);  //  big governor
        String big = "big Governor: " + getGovernor("big");
        textView1.setText(big);

        TextView textView2 = findViewById(R.id.littleGovernor);  //  little governor
        String little = "LITTLE Governor: " + getGovernor("little");
        textView2.setText(little);

        //  text to show IO scheduler information
/*        TextView textView3 = findViewById(R.id.textView3);  //  int scheduler
        textView3.setText("Internal Scheduler: "+getScheduler("sda"));

        TextView textView4 = findViewById(R.id.textView4);  //  ext scheduler
        textView4.setText("External Scheduler: "+getScheduler("mmcblk0"));*/

        // show notification on startup
        Button notificationButton = findViewById(R.id.notificationButton);
        notificationButton.setOnClickListener(this);
//        showNotification(notification);

        Button onService = findViewById(R.id.onService);
        onService.setOnClickListener(this);
        Button offService = findViewById(R.id.offService);
        offService.setOnClickListener(this);

        // big governor buttons
        Button bigI = findViewById(R.id.bigInteractive);
        bigI.setOnClickListener(this);
        Button bigP = findViewById(R.id.bigPerformance);
        bigP.setOnClickListener(this);
        Button bigSA = findViewById(R.id.bigSmartass);
        bigSA.setOnClickListener(this);
        Button bigSM = findViewById(R.id.bigSmartmax);
        bigSM.setOnClickListener(this);

        Button bigC = findViewById(R.id.bigConservative);
        bigC.setOnClickListener(this);
        Button bigO = findViewById(R.id.bigOndemand);
        bigO.setOnClickListener(this);
        Button bigU = findViewById(R.id.bigUserspace);
        bigU.setOnClickListener(this);
        Button bigPo = findViewById(R.id.bigPowersave);
        bigPo.setOnClickListener(this);

        // little governor buttons
        Button litI = findViewById(R.id.littleInteractive);
        litI.setOnClickListener(this);
        Button litP = findViewById(R.id.littlePerformance);
        litP.setOnClickListener(this);
        Button litSA = findViewById(R.id.littleSmartass);
        litSA.setOnClickListener(this);
        Button litSM = findViewById(R.id.littleSmartmax);
        litSM.setOnClickListener(this);

        Button litC = findViewById(R.id.littleConservative);
        litC.setOnClickListener(this);
        Button litO = findViewById(R.id.littleOndemand);
        litO.setOnClickListener(this);
        Button litU = findViewById(R.id.littleUserspace);
        litU.setOnClickListener(this);
        Button litPo = findViewById(R.id.littlePowersave);
        litPo.setOnClickListener(this);
/*        // int scheduler buttons
        Button noop = findViewById(R.id.button8);
        noop.setOnClickListener(this);
        Button deadline = findViewById(R.id.button9);
        deadline.setOnClickListener(this);
        Button cfq = findViewById(R.id.button10);
        cfq.setOnClickListener(this);
        Button fiops = findViewById(R.id.button11);
        fiops.setOnClickListener(this);
        Button zen = findViewById(R.id.button12);
        zen.setOnClickListener(this);

        // ext scheduler buttons
        Button noop2 = findViewById(R.id.button13);
        noop2.setOnClickListener(this);
        Button deadline2 = findViewById(R.id.button14);
        deadline2.setOnClickListener(this);
        Button cfq2 = findViewById(R.id.button15);
        cfq2.setOnClickListener(this);
        Button fiops2 = findViewById(R.id.button16);
        fiops2.setOnClickListener(this);
        Button zen2 = findViewById(R.id.button17);
        zen2.setOnClickListener(this);*/

        //  check process/thread info
        Button currentNice = findViewById(R.id.currentNice);
        currentNice.setOnClickListener(this);
        Button changeNice = findViewById(R.id.changeNice);
        changeNice.setOnClickListener(this);


    }


    //*********************************************************************************************
    //  this section is to implement a receiver that handles message sent by the service
/*    @Override
    public void onResume() {
        super.onResume();

        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("my-event"));
    }

    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            Log.i(TAG, "Got message: " + message);
//            ChangeGovernorBig("performance");
//            Log.i(TAG,"hello there");
        }
    };

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }*/
    //  end of section
    //*********************************************************************************************
    @Override
    public void onClick(View v) {
        // default method for handling onClick Events..
        //  doesn't include show notification button --> done by OnClick of the button
        Intent serviceIntent = new Intent(this,GS_Service.class);
        switch (v.getId()) {
            case R.id.notificationButton:
                createNotification();
//                executePeriodicTask();
                break;

            case R.id.onService:
                //  check if service already started
                if (onService==false){
//                    createNotification();
                    startService(serviceIntent);
                    onService = true;
                }

                else{
                    View p = new View(this);
                    showAlertServiceAlreadyOn(p);
                }
                break;

            case R.id.offService:
                stopService(serviceIntent);
                break;

            //################################################################################
            //  big Governor Buttons
            //################################################################################

            case R.id.bigInteractive:
                ChangeGovernorBig("interactive");
                break;

            case R.id.bigPerformance:
                ChangeGovernorBig("performance");
                break;

            case R.id.bigSmartass:
                ChangeGovernorBig("smartassV2");
                break;

            case R.id.bigSmartmax:
                ChangeGovernorBig("smartmax");
                break;

            case R.id.bigConservative:
                ChangeGovernorBig("conservative");
                break;

            case R.id.bigOndemand:
                ChangeGovernorBig("ondemand");
                break;

            case R.id.bigUserspace:
                ChangeGovernorBig("userspace");
                break;

            case R.id.bigPowersave:
                ChangeGovernorBig("powersave");
                break;

            //################################################################################
            //  little Governor Buttons
            //################################################################################

            case R.id.littleInteractive:
                ChangeGovernorLittle("interactive");
                break;

            case R.id.littlePerformance:
                ChangeGovernorLittle("performance");
                break;

            case R.id.littleSmartass:
                ChangeGovernorLittle("smartassV2");
                break;

            case R.id.littleSmartmax:
                ChangeGovernorLittle("smartmax");
                break;

            case R.id.littleConservative:
                ChangeGovernorLittle("conservative");
                break;

            case R.id.littleOndemand:
                ChangeGovernorLittle("ondemand");
                break;

            case R.id.littleUserspace:
                ChangeGovernorLittle("userspace");
                break;

            case R.id.littlePowersave:
                ChangeGovernorLittle("powersave");
                break;
            //################################################################################
            //  I/O Scheduler Buttons
            //################################################################################


           /* case R.id.button8:
                ChangeScheduler("noop", "sda");
                break;

            case R.id.button9:
                ChangeScheduler("deadline", "sda");
                break;

            case R.id.button10:
                ChangeScheduler("cfq", "sda");
                break;

            case R.id.button11:
                ChangeScheduler("fiops", "sda");
                break;

            case R.id.button12:
                ChangeScheduler("zen", "sda");
                break;

            case R.id.button13:
                ChangeScheduler("noop", "mmcblk0");
                break;

            case R.id.button14:
                ChangeScheduler("deadline", "mmcblk0");
                break;

            case R.id.button15:
                ChangeScheduler("cfq", "mmcblk0");
                break;

            case R.id.button16:
                ChangeScheduler("fiops", "mmcblk0");
                break;

            case R.id.button17:
                ChangeScheduler("zen", "mmcblk0");
                break;*/

            case R.id.currentNice:
                View p = new View(this);
                showAlertCurrentNice(p);
                break;

            case R.id.changeNice:
                //  change the niceness of current foreground to -20
//                ChangeNice(getPID(),-15);
                String[] randomCommand = new String[8];
                for (int i = 0; i<4;i++){
                    randomCommand[i]="echo 650000 > /sys/devices/system/cpu/cpu"+i+"/cpufreq/scaling_setspeed";
                }
                for (int j = 4; j<8;j++){
                    randomCommand[j]="echo 728000 > /sys/devices/system/cpu/cpu"+j+"/cpufreq/scaling_setspeed";
                }
//                View p2 = new View(this);
//                showAlert(p2);
                RunCommand(randomCommand);
                break;

            default:
                finish();
                startActivity(getIntent());
                break;
        }
    }

    //  this method allows us to run periodic tasks
    public void executePeriodicTask() {
        final android.os.Handler handler = new android.os.Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask;
        doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {

                            try {

                                createNotification();

                            } catch (Exception e) {
                                e.printStackTrace();

                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 1000); //execute in every 50000 ms
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
                        .addLine("big Governor:   " + getGovernor("big"))
                        .addLine("LITTLE Governor:   " + getGovernor("little"))
                        .addLine("Current App: "+printForegroundTask())
                        .addLine(getNice(getPID(),false)))
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

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
                .addLine("PID is: "+getPID())
        );

        //  start the notification
        NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NM.notify(0, builder.build());
    }

    public void showAlertCurrentNice (View w){
        AlertDialog.Builder a_builder = new AlertDialog.Builder(this);
        a_builder.setTitle("Foreground App Nice Value");
        a_builder.setMessage(getNice(getPID(),false));
        AlertDialog alert = a_builder.create();
        alert.show();
    }

    public void showAlertServiceAlreadyOn (View w){
        AlertDialog.Builder a_builder = new AlertDialog.Builder(this);
        a_builder.setTitle("Alert");
        a_builder.setMessage("Service already running!");
        AlertDialog alert = a_builder.create();
        alert.show();
    }

    public void refresh() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        this.overridePendingTransition(0, 0);
        startActivity(intent);
        this.overridePendingTransition(0,0);
    }

    private String getGovernor(String g) {
//        StringBuffer sb = new StringBuffer();
        String gov;
        if (g == "big") {
            gov = "4";
        }
        else {
            gov = "0";
        }
        String[] file = {"cat /sys/devices/system/cpu/cpu"+gov+"/cpufreq/scaling_governor"};  // Gets governor for big cores
        return RunCommand(file);

        //  below required to read the file output after querying the above
/*        if (new File(file).exists()) {
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

        return sb.toString();*/
    }


    public void ChangeGovernorBig(String governor) {
        String[] newGovernor = {"echo " + governor + " > /sys/devices/system/cpu/cpu4/cpufreq/scaling_governor",
                "echo " + governor + " > /sys/devices/system/cpu/cpu5/cpufreq/scaling_governor",
                "echo " + governor + " > /sys/devices/system/cpu/cpu6/cpufreq/scaling_governor",
                "echo " + governor + " > /sys/devices/system/cpu/cpu7/cpufreq/scaling_governor"};
        RunCommand(newGovernor);
        refresh();
    }

    public void ChangeGovernorLittle(String governor) {
        String[] newGovernor = {"echo " + governor + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor",
                "echo " + governor + " > /sys/devices/system/cpu/cpu1/cpufreq/scaling_governor",
                "echo " + governor + " > /sys/devices/system/cpu/cpu2/cpufreq/scaling_governor",
                "echo " + governor + " > /sys/devices/system/cpu/cpu3/cpufreq/scaling_governor"};
        RunCommand(newGovernor);
        refresh();
    }

/*    private String getScheduler(String s) {
        String[] cmd = {"cat /sys/block/"+s+"/queue/scheduler\n"};
        return RunCommand(cmd);
    }

    public void ChangeScheduler(String scheduler, String s){
        String[] newScheduler = {"echo "+scheduler+" > /sys/block/"+s+"/queue/scheduler"};
        RunCommand(newScheduler);
        refresh();
    }*/

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

    public String getPID(){
        String cmd[] = {"pidof "+printForegroundTask()};
        return RunCommand(cmd);
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
