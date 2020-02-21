package com.mayank.diseasealert;

import android.annotation.SuppressLint;

import java.io.IOException;
import java.io.OutputStream;
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
                while(true) {
                    ps.println(MainActivity.lon + "," + MainActivity.lat);
                    sleep(1000);
                    if(MainActivity.r.nextBoolean()) {
                        MainActivity.lat = String.format("%.2f", Double.parseDouble(MainActivity.lat)+0.1);
                    } else {
                        MainActivity.lon = String.format("%.2f", Double.parseDouble(MainActivity.lon)+0.1);
                    }
                    MainActivity.setMessage();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
