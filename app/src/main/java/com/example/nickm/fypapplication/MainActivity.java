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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // view object for the notification
        View v = new View(this);

        String little = "LITTLE Governor: " + getGovernorLittle();
        String big = "big Governor: " + getGovernorBig();
        final String internal = "sda";
        final String ext = "mmcblk0";

        TextView textView1 = findViewById(R.id.textView1);  //  big governor
        textView1.setText(big);

        TextView textView2 = findViewById(R.id.textView2);  //  little governor
        textView2.setText(little);

        TextView textView3 = findViewById(R.id.textView3);  //  int scheduler
        textView3.setText(getScheduler(internal));

        TextView textView4 = findViewById(R.id.textView4);  //  ext scheduler
        textView4.setText(getScheduler(ext));

        // show notification on startup
        showNotification(v);

        Button bigP = findViewById(R.id.button2);
        Button bigSA = findViewById(R.id.button3);
        Button bigSM = findViewById(R.id.button4);

        Button litP = findViewById(R.id.button5);
        Button litSA = findViewById(R.id.button6);
        Button litSM = findViewById(R.id.button7);
        

        bigP.setOnClickListener(new View.OnClickListener() {
            @Override
            //On click function
            public void onClick(View view) {
                ChangeGovernorBig("performance");
            }
        });

        bigSA.setOnClickListener(new View.OnClickListener() {
            @Override
            //On click function
            public void onClick(View view) {
                ChangeGovernorBig("smartassV2");
            }
        });

        bigSM.setOnClickListener(new View.OnClickListener() {
            @Override
            //On click function
            public void onClick(View view) {
                ChangeGovernorBig("smartmax");
            }
        });

        litP.setOnClickListener(new View.OnClickListener() {
            @Override
            //On click function
            public void onClick(View view) {
                ChangeGovernorLittle("performance");
            }
        });

        litSA.setOnClickListener(new View.OnClickListener() {
            @Override
            //On click function
            public void onClick(View view) {
                ChangeGovernorLittle("smartassV2");
            }
        });

        litSM.setOnClickListener(new View.OnClickListener() {
            @Override
            //On click function
            public void onClick(View view) {
                ChangeGovernorLittle("smartmax");
            }
        });


        Button noop = findViewById(R.id.button8);
        Button deadline = findViewById(R.id.button9);
        Button cfq = findViewById(R.id.button10);
        Button fiops = findViewById(R.id.button11);
        Button zen = findViewById(R.id.button12);

        noop.setOnClickListener(new View.OnClickListener() {
            @Override
            //On click function
            public void onClick(View view) {
                // set scheduler method
                ChangeScheduler("noop", internal);
            }
        });

        deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            //On click function
            public void onClick(View view) {
                // set scheduler method
                ChangeScheduler("deadline", internal);
            }
        });

        cfq.setOnClickListener(new View.OnClickListener() {
            @Override
            //On click function
            public void onClick(View view) {
                // set scheduler method
                ChangeScheduler("cfq", internal);
            }
        });

        fiops.setOnClickListener(new View.OnClickListener() {
            @Override
            //On click function
            public void onClick(View view) {
                // set scheduler method
                ChangeScheduler("fiops", internal);
            }
        });

        zen.setOnClickListener(new View.OnClickListener() {
            @Override
            //On click function
            public void onClick(View view) {
                // set scheduler method
                ChangeScheduler("zen", internal);
            }
        });

        Button noop2 = findViewById(R.id.button13);
        Button deadline2 = findViewById(R.id.button14);
        Button cfq2 = findViewById(R.id.button15);
        Button fiops2 = findViewById(R.id.button16);
        Button zen2 = findViewById(R.id.button17);

        noop2.setOnClickListener(new View.OnClickListener() {
            @Override
            //On click function
            public void onClick(View view) {
                // set scheduler method
                ChangeScheduler("noop",ext);
            }
        });

        deadline2.setOnClickListener(new View.OnClickListener() {
            @Override
            //On click function
            public void onClick(View view) {
                // set scheduler method
                ChangeScheduler("deadline",ext);
            }
        });

        cfq2.setOnClickListener(new View.OnClickListener() {
            @Override
            //On click function
            public void onClick(View view) {
                // set scheduler method
                ChangeScheduler("cfq",ext);
            }
        });

        fiops2.setOnClickListener(new View.OnClickListener() {
            @Override
            //On click function
            public void onClick(View view) {
                // set scheduler method
                ChangeScheduler("fiops",ext);
            }
        });

        zen2.setOnClickListener(new View.OnClickListener() {
            @Override
            //On click function
            public void onClick(View view) {
                // set scheduler method
                ChangeScheduler("zen",ext);
            }
        });
    }

    public void showNotification(View v) {
        //PendingIntent update = PendingIntent.getActivity(this,0,update,PendingIntent.FLAG_ONE_SHOT);
        // helps sets certain parameters of the notification, like icons etc
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("CPU Details");
        builder.setStyle(new NotificationCompat.InboxStyle()    // allows the notification to become bigger
                .addLine("big Governor:   " + getGovernorBig())
                .addLine("LITTLE Governor:   " + getGovernorLittle())
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

    private String getGovernorBig() {
        StringBuffer sb = new StringBuffer();
        String file = "/sys/devices/system/cpu/cpu4/cpufreq/scaling_governor";  // Gets governor for big cores

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

    private String getGovernorLittle() {
        StringBuffer sb = new StringBuffer();
        String file = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";  // Gets governor for LITTLE cores

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
