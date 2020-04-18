package com.mayank.diseasealert;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public static Random r;
    @SuppressLint("StaticFieldLeak")
    public static TextView points;
    @SuppressLint("StaticFieldLeak")
    public static TextView intelligence;
    public static int ServerPort;
    public static double lat;
    public static double lon;
    private static String ip;
    public static Handler UIHandler;
    public static String id;
    public static String count = "";
    public NotificationChannel notificationChannel;
    private static NotificationManager notificationManager;
    Notification updateNotify;


    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        id = "mobilePhoneGPS";
        r = new Random();
        lon = r.nextDouble() * (180 - (-180)) + (-180);
        lat = r.nextDouble() * (90 - (-90)) + (-90);

        intelligence = findViewById(R.id.intelligence);
        points = findViewById(R.id.point);

        Button exit = findViewById(R.id.exit);
        exit.setOnClickListener(view -> {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        });

        notificationChannel = new NotificationChannel("alert", "diseaseAlert", NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.setShowBadge(true);
        notificationChannel.enableVibration(false);
        notificationChannel.canBypassDnd();
        notificationChannel.setSound(null,null);
        notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        notificationManager = getSystemService(NotificationManager.class);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(notificationChannel);

        Button serverLaunch = findViewById(R.id.button);
        serverLaunch.setOnClickListener(view -> {
            EditText port = findViewById(R.id.portEditText);

            if(port.getText().toString().isEmpty()) {
                Snackbar error = Snackbar.make(view, "Spark port cannot be empty!",Snackbar.LENGTH_INDEFINITE);
                error.setAction("OK", view1 -> error.dismiss());
                error.show();
            } else {
                ServerPort = Integer.parseInt(port.getText().toString());
                WifiManager wifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                assert wifiMan != null;
                WifiInfo wifiInf = wifiMan.getConnectionInfo();
                int ipAddress = wifiInf.getIpAddress();
                ip = String.format("%d.%d.%d.%d:%s", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff), port.getText().toString());

                UIHandler = new Handler(Looper.getMainLooper());

                setMessage();

                EditText serverip = findViewById(R.id.serverIPEditText);
                if (serverip.getText().toString().isEmpty()) {
                    Snackbar error = Snackbar.make(view, "Server IP cannot be empty!", Snackbar.LENGTH_INDEFINITE);
                    error.setAction("OK", view1 -> error.dismiss());
                    error.show();
                } else {
                    try {
                        final URL url = new URL("http://" + serverip.getText().toString().replaceAll("\n","") + ":8080");
                        Thread socketThread = new Thread(new socketServer());
                        socketThread.start();
                        final Handler pushNotify = new Handler();
                        Timer timer = new Timer();
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                            pushNotify.post(() -> new checkUpdate().execute(url));

                            if (!count.isEmpty() && !count.equals("0"))
                                runOnUI(() -> {
                                    updateNotify = new Notification.Builder(getApplicationContext(), "alert")
                                            .setContentTitle("Disease Outbreak")
                                            .setSmallIcon(R.mipmap.ic_launcher_round)
                                            .setContentText(count + " disease cases nearby!")
                                            .setAutoCancel(true)
                                            .build();
                                    notificationManager.notify(0, updateNotify);
                                });
                            else
                                notificationManager.cancel(0);

                            int c = count.isEmpty()?0:Integer.parseInt(count);
                            if (c == 0) {
                                runOnUI(() -> intelligence.setText("Have a safe day!"));
                            } else if (c <= 10 && c > 0) {
                                runOnUI(() -> intelligence.setText("Wash hands regularly!"));
                            } else if (c <= 50 && c > 10) {
                                runOnUI(() -> intelligence.setText("Wash hands regularly!\nMaintain social distance!"));
                            } else if (c <= 100 && c > 50) {
                                runOnUI(() -> intelligence.setText("Wash hands regularly!\nMaintain social distance!\nStay at home and avoid travelling unless necessary!"));
                            } else {
                                runOnUI(() -> intelligence.setText("Wash hands regularly!\nMaintain social distance!\nWear face mask!\nStay at home and avoid travelling unless necessary!"));
                            }
                            }
                        };
                        timer.schedule(task, 0, 5000);
                    } catch(MalformedURLException e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public static void setMessage() {
        runOnUI(() -> points.setText("IP: " + ip + "\n\nLon: " + String.format("%.2f", lon) + "\nLat: " + String.format("%.2f", lat)));
    }

    public static void runOnUI(Runnable runnable) {
        UIHandler.post(runnable);
    }

    private class checkUpdate extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            String str = "";
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(urls[0].openStream()));
                str = in.readLine();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return str;
        }

        @Override
        protected void onPostExecute(String result) {
            count = result;
        }
    }

}
