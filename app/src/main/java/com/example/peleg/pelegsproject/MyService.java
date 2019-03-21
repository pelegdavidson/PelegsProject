package com.example.peleg.pelegsproject;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;


public class MyService extends Service implements LocationListener {
    boolean isGPSEnable = false;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refMyEvents;
    FirebaseUser currentUser;
    String[] userEvents;
    public double latitude, longitude;
    LocationManager locationManager;
    int i = 0;



    public MyService() {

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try{
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
             refMyEvents= database.getReference().child("Users").child(currentUser.getUid()).child("MyEvents");
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
            EventsScanner es = new EventsScanner();
            es.start();

        }catch(SecurityException e){

        }


        return START_NOT_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {

        return new MyBinder();
    }

    public class MyBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }




public class EventsScanner extends Thread{
    @Override
    public void run() {
        listenToMyEvents();
        while(true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



    }

    private void listenToMyEvents(){
        refMyEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    database.getReference().child("Events").child(snapshot.getKey())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    GregorianCalendar currDate = new GregorianCalendar();
                                    long currdate1 = currDate.getTimeInMillis();
                                    Event ev = dataSnapshot.getValue(Event.class);
                                    //Log.i("ffff", ev.getDate());

                                    if(ev.date==currdate1){
                                        Toast.makeText(MyService.this,"the event started "+ev.eventName ,Toast.LENGTH_SHORT).show();
                                    }
                                    if(ev.date>=currdate1&&ev.date<(currdate1+ev.duration)){

                                        Location eventLocation = new Location("point A");
                                        eventLocation.setLatitude(ev.locLatitude);
                                        eventLocation.setLongitude(ev.locLongitude);

                                        Location myloc = new Location("point B");
                                        myloc.setLatitude(latitude);
                                        myloc.setLongitude(longitude);

                                        if(eventLocation.distanceTo(myloc)<30){
                                            Toast.makeText(MyService.this,"you arrived to the event" ,Toast.LENGTH_SHORT).show();
                                            database.getReference().child("Events").child(dataSnapshot.getKey()).child("arrivedMembers").child(currentUser.getUid()).setValue("");
                                            refMyEvents.child(dataSnapshot.getKey()).removeValue();
                                            database.getReference().child("Users").child(currentUser.getUid()).child("HistoryEvents").child(dataSnapshot.getKey()).setValue("");
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MyService.this,"cant get the user's events",Toast.LENGTH_SHORT).show();
            }
        });
    }
}



}