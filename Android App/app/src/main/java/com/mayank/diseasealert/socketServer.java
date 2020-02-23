package com.mayank.diseasealert;

import android.annotation.SuppressLint;

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
                    ps.println(MainActivity.id + "," + System.currentTimeMillis() + "," + MainActivity.lon + "," + MainActivity.lat);
                    sleep(5*1000);
                    if(MainActivity.r.nextBoolean()) {
                        MainActivity.lat = MainActivity.r.nextBoolean()?String.format("%.2f", Double.parseDouble(MainActivity.lat)+0.1):String.format("%.2f", Double.parseDouble(MainActivity.lat)-0.1);
                    } else {
                        MainActivity.lon = MainActivity.r.nextBoolean()?String.format("%.2f", Double.parseDouble(MainActivity.lon)+0.1):String.format("%.2f", Double.parseDouble(MainActivity.lon)-0.1);
                    }
                    MainActivity.setMessage();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
