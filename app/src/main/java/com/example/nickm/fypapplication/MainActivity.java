package com.example.nickm.fypapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import static java.lang.System.in;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // view object for the notification
        View notification = new View(this);

        String little = "LITTLE Governor: " + getGovernor("little");
        String big = "big Governor: " + getGovernor("big");

        TextView textView1 = findViewById(R.id.textView1);  //  big governor
        textView1.setText(big);

        TextView textView2 = findViewById(R.id.textView2);  //  little governor
        textView2.setText(little);

        TextView textView3 = findViewById(R.id.textView3);  //  int scheduler
        textView3.setText(getScheduler("sda"));

        TextView textView4 = findViewById(R.id.textView4);  //  ext scheduler
        textView4.setText(getScheduler("mmcblk0"));

        // show notification on startup
        showNotification(notification);

        // big governor buttons
        Button bigP = findViewById(R.id.button2);
        bigP.setOnClickListener(this);
        Button bigSA = findViewById(R.id.button3);
        bigSA.setOnClickListener(this);
        Button bigSM = findViewById(R.id.button4);
        bigSM.setOnClickListener(this);

        // little governor buttons
        Button litP = findViewById(R.id.button5);
        litP.setOnClickListener(this);
        Button litSA = findViewById(R.id.button6);
        litSA.setOnClickListener(this);
        Button litSM = findViewById(R.id.button7);
        litSM.setOnClickListener(this);

        // int scheduler buttons
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
        zen2.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        // default method for handling onClick Events..
        //  doesn't include show notification button
        switch (v.getId()) {

            case R.id.button2:
                ChangeGovernorBig("performance");
                break;

            case R.id.button3:
                ChangeGovernorBig("smartassV2");
                break;

            case R.id.button4:
                ChangeGovernorBig("smartmax");
                break;

            case R.id.button5:
                ChangeGovernorLittle("performance");
                break;

            case R.id.button6:
                ChangeGovernorLittle("smartassV2");
                break;

            case R.id.button7:
                ChangeGovernorLittle("smartmax");
                break;
            case R.id.button8:
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
                break;
            default:
                finish();
                startActivity(getIntent());
                break;
        }
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
                .addLine("Int IO Scheduler:    " + getScheduler("sda"))
                .addLine("Ext IO Scheduler:    " + getScheduler("mmcblk0")));
        //builder.addAction(R.mipmap.ic_launcher, "refresh",update);
        NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NM.notify(0, builder.build());

    }


//    public void refresh(View v) {
//        finish();
//        startActivity(getIntent());
//    }

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


    public void ChangeGovernorBig(String governor) {
        String[] newGovernor = {"echo " + governor + " > /sys/devices/system/cpu/cpu4/cpufreq/scaling_governor",
                "echo " + governor + " > /sys/devices/system/cpu/cpu5/cpufreq/scaling_governor",
                "echo " + governor + " > /sys/devices/system/cpu/cpu6/cpufreq/scaling_governor",
                "echo " + governor + " > /sys/devices/system/cpu/cpu7/cpufreq/scaling_governor"};
        RunCommand(newGovernor);
        finish();
        startActivity(getIntent());
    }

    public void ChangeGovernorLittle(String governor) {
        String[] newGovernor = {"echo " + governor + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor",
                "echo " + governor + " > /sys/devices/system/cpu/cpu1/cpufreq/scaling_governor",
                "echo " + governor + " > /sys/devices/system/cpu/cpu2/cpufreq/scaling_governor",
                "echo " + governor + " > /sys/devices/system/cpu/cpu3/cpufreq/scaling_governor"};
        RunCommand(newGovernor);
        finish();
        startActivity(getIntent());
    }

    private String getScheduler(String s) {
        String[] cmd = {"cat /sys/block/"+s+"/queue/scheduler\n"};
        return RunCommand(cmd);
    }

    public void ChangeScheduler(String scheduler, String s){
        String[] newScheduler = {"echo "+scheduler+" > /sys/block/"+s+"/queue/scheduler"};
        RunCommand(newScheduler);
        finish();
        startActivity(getIntent());
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
