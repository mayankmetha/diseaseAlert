package com.mayank.diseasealert;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static Random r;
    @SuppressLint("StaticFieldLeak")
    public static TextView points;
    public static int ServerPort;
    public static String lat;
    public static String lon;
    private static String ip;

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        r = new Random();

        points = findViewById(R.id.point);

        Button sertverLaunch = findViewById(R.id.button);
        sertverLaunch.setOnClickListener(view -> {
            EditText port = findViewById(R.id.portEditText);

            if(port.getText().toString().isEmpty()) {
                Snackbar error = Snackbar.make(view, "Port cannot be empty!",Snackbar.LENGTH_INDEFINITE);
                error.setAction("OK", view1 -> error.dismiss());
                error.show();
            } else {
                ServerPort = Integer.parseInt(port.getText().toString());
                WifiManager wifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                assert wifiMan != null;
                WifiInfo wifiInf = wifiMan.getConnectionInfo();
                int ipAddress = wifiInf.getIpAddress();
                ip = String.format("%d.%d.%d.%d:%s", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff), port.getText().toString());

                lon = String.format("%.2f", r.nextDouble() * (180 - (-180)) + (-180));
                lat = String.format("%.2f", r.nextDouble() * (90 - (-90)) + (-90));
                setMessage();

                Thread socketThread = new Thread(new socketServer());
                socketThread.start();
            }
        });
    }

    public static void setMessage() {
        points.setText("IP: " + ip + "\n\nLat: " + lat + "\nLon: " + lon);
    }
}
