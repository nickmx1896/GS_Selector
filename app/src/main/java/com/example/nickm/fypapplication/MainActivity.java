package com.example.nickm.fypapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        TextView textView3 = (TextView)findViewById(R.id.textView3);
        TextView textView6 = (TextView)findViewById(R.id.textView6);
        textView3.setText("LITTLE Governor: " + getGovernorLittle());
        textView6.setText("big Governor: " + getGovernorBig());

    }


    private String getGovernorLittle() {
        StringBuffer sb = new StringBuffer();

        //String file = "/proc/cpuinfo";  // Gets most cpu info (but not the governor)
        String file = "/sys/devices/system/cpu/cpu4/cpufreq/scaling_governor";  // Gets governor for big cores

        if (new File(file).exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File(file)));
                String aLine;
                while ((aLine = br.readLine()) != null)
                    sb.append(aLine + "\n");

                if (br != null)
                    br.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
    private String getGovernorBig() {
        StringBuffer sb = new StringBuffer();

        //String file = "/proc/cpuinfo";  // Gets most cpu info (but not the governor)
        String file = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";  // Gets governor for LITTLE cores

        if (new File(file).exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File(file)));
                String aLine;
                while ((aLine = br.readLine()) != null)
                    sb.append(aLine + "\n");

                if (br != null)
                    br.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
