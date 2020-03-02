package com.mayank.diseasealert;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class socketServer extends Thread {

    public static PrintStream ps;

    @SuppressLint("DefaultLocale")
    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(MainActivity.ServerPort);
            while(true) {
                Socket s = ss.accept();
                ps = new PrintStream(s.getOutputStream());
                for(;;) {
                    MainActivity.minlat = MainActivity.lat-0.25;
                    MainActivity.maxlat = MainActivity.lat+0.25;
                    MainActivity.minlon = MainActivity.lon-0.25;
                    MainActivity.maxlon = MainActivity.lon+0.25;
                    Log.e("points",""+MainActivity.minlat+","+MainActivity.maxlat+","+MainActivity.minlon+","+MainActivity.maxlon);
                    ps.println(MainActivity.id + "," + System.currentTimeMillis() + "," + String.format("%.2f", MainActivity.minlon) + "," + String.format("%.2f", MainActivity.maxlon) + "," + String.format("%.2f", MainActivity.minlat) + "," + String.format("%.2f", MainActivity.maxlat));
                    sleep(5*1000);
                    if(MainActivity.r.nextBoolean()) {
                        MainActivity.lat = MainActivity.r.nextBoolean()? MainActivity.lat+0.1: MainActivity.lat-0.1;
                    } else {
                        MainActivity.lon = MainActivity.r.nextBoolean()? MainActivity.lon+0.1: MainActivity.lon-0.1;
                    }
                    MainActivity.setMessage();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
