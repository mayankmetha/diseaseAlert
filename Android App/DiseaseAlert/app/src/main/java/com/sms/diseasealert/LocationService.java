package com.sms.diseasealert;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationService extends Service {

    public static final String CHANNEL_ID = "LocationService";
    LocationRequest locReq;
    Notification mNotification;
    NotificationChannel mNotificationChannel;
    public static final int NOTIFICATION_ID = 0x01;
    NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent mPendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

        mNotificationChannel = new NotificationChannel(CHANNEL_ID,"Disease Alert", NotificationManager.IMPORTANCE_DEFAULT);
        mNotificationChannel.enableLights(false);
        mNotificationChannel.setShowBadge(false);
        mNotificationChannel.enableVibration(false);
        mNotificationChannel.canBypassDnd();
        mNotificationChannel.setSound(null,null);
        mNotificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        mNotificationManager = getSystemService(NotificationManager.class);
        mNotificationManager.createNotificationChannel(mNotificationChannel);

        mNotification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Disease Alert")
                .setContentText("Check your current location")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(mPendingIntent)
                .build();

        startForeground(NOTIFICATION_ID, mNotification);

        getLastLocation();

        locReq = new LocationRequest();
        locReq.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locReq.setInterval(60*1000);
        locReq.setFastestInterval(5000);
        locReq.setSmallestDisplacement(1000.0f);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locReq);
        LocationSettingsRequest locSettingReq = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locSettingReq);

        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locReq, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        }, Looper.myLooper());

        return START_NOT_STICKY;
    }

    public void onLocationChanged(Location location) {
        mNotification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Disease Alert")
                .setContentText("Latitude: "+location.getLatitude()+"\nLongtitude: "+location.getLongitude())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .build();
        mNotificationManager.notify(NOTIFICATION_ID,mNotification);
    }

    public void getLastLocation() {
        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);

        locationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mNotification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setContentTitle("Disease Alert")
                        .setContentText("Latitude: "+location.getLatitude()+"\nLongtitude: "+location.getLongitude())
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .build();
                mNotificationManager.notify(NOTIFICATION_ID,mNotification);
            }
        }).addOnFailureListener(e -> e.printStackTrace());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
