package com.example.peleg.pelegsproject;

import android.Manifest;
import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener, ServiceConnection {
    private MyService myService;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final int REQUEST_PERMISSIONS = 100;
    boolean boolean_permission;

    private TextView mTextMessage;

    TextView txtDate, txtTime,txtDuration;
    EditText txtitle;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private CheckBox cb;
    Intent intent;
    private GregorianCalendar eventDate;
    private long duration;
    ArrayList<String> members = new ArrayList<>();
    ArrayList<String> membersIds = new ArrayList<>();
    ArrayList<String> chosenMembers = new ArrayList<>();
    ArrayList<String> chosenMembersids = new ArrayList<>();
    ListView membersLV;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        txtDate=(TextView) findViewById(R.id.in_date);
        txtTime=(TextView) findViewById(R.id.in_time);
        txtDuration=(TextView) findViewById(R.id.in_duration);
        txtitle=(EditText)findViewById(R.id.title);
        cb=(CheckBox)findViewById(R.id.cb1);

        eventDate = new GregorianCalendar();

        intent= new Intent(MainActivity.this, MyService.class);
        fn_permission();
        if (boolean_permission)
            startService();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        collectMembers((Map<String,Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });



    }
    private void initSearchWorkLV() {

        membersLV = (ListView) findViewById(R.id.members_lv);

        ArrayAdapter<String> adapterS = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_multiple_choice,
                members);

        membersLV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        membersLV.setAdapter(adapterS);


    }

    private void collectMembers(Map<String,Object> users) {



        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            members.add((String) singleUser.get("displayName"));
            membersIds.add((String) singleUser.get("uid"));
        }
        if(members.size()>0)
            initSearchWorkLV();

    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        if(isMyServiceRunning(MyService.class))
            bindService(intent, MainActivity.this,Context.BIND_AUTO_CREATE);
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(isMyServiceRunning(MyService.class))
            unbindService(MainActivity.this);
    }

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {


            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean_permission = true;
                    startService();
                    bindService(intent, MainActivity.this,Context.BIND_AUTO_CREATE);
                } else {
                    Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

                }
            }
        }
    }



    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch(i){
            case R.id.btn_date:
                pickDate();
                break;
            case R.id.btn_time:
                pickTime();
                break;
            case R.id.btn_duration:
                pickDuration();
                break;
            case R.id.btn_create:
                createEvent();
                break;
        }
    }

    private void startService(){
        if (boolean_permission) {

            if (!isMyServiceRunning(MyService.class)) {
                startService(intent);


            } else {
                Toast.makeText(getApplicationContext(), "Service is already running", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please enable the gps", Toast.LENGTH_SHORT).show();
        }
    }

    private void pickDate(){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        eventDate.set(Calendar.YEAR, year);
                        eventDate.set(Calendar.MONTH, monthOfYear+1);
                        eventDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }



    private void pickTime(){
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        if(minute<10&&hourOfDay<10)
                            txtTime.setText("0"+hourOfDay + ":0" + minute);
                        else if(minute<10)
                            txtTime.setText(hourOfDay + ":0" + minute);
                        else  if(hourOfDay<10)
                            txtTime.setText("0"+hourOfDay + ":" + minute);
                        else
                            txtTime.setText(hourOfDay + ":" + minute);

                        eventDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        eventDate.set(Calendar.MINUTE, minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void pickDuration(){
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        if(minute<10&&hourOfDay<10)
                            txtDuration.setText("0"+hourOfDay + ":0" + minute);
                        else if(minute<10)
                            txtDuration.setText(hourOfDay + ":0" + minute);
                        else  if(hourOfDay<10)
                            txtDuration.setText("0"+hourOfDay + ":" + minute);
                        else
                            txtDuration.setText(hourOfDay + ":" + minute);

                        duration=(hourOfDay*60 + minute)*60000;
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void createEvent(){

        SparseBooleanArray sp=membersLV.getCheckedItemPositions();

        for(int j=0;j<sp.size();j++)
        {
            chosenMembers.add(members.get(sp.keyAt(j)));
            chosenMembersids.add(membersIds.get(sp.keyAt(j)));


        }
        long date1 = eventDate.getTimeInMillis();
        // DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        //String date = df.format(eventDate.getTime());


        double latitude,longitude;
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        latitude = myService.latitude;
        longitude = myService.longitude;
        final Event e1 = new Event(user.getUid(),txtitle.getText().toString(),latitude,longitude,duration,chosenMembers,cb.isChecked(),date1);
        final DatabaseReference ref = database.getReference().child("Events").push();
        ref.setValue(e1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    final String uniqueKey = ref.getKey();
                    for(int t=0 ; t<chosenMembers.size() ; t++){
                        final DatabaseReference ref2 = database.getReference().child("Users").child(chosenMembersids.get(t)).child("MyEvents");
                        ref2.child(uniqueKey).setValue("");

                    }

                    Toast.makeText(getApplicationContext(), "Event created", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "creation failed", Toast.LENGTH_LONG).show();
            }
        });



    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

        MyService.MyBinder mBinder = (MyService.MyBinder) iBinder;
        myService =mBinder.getService();
        findViewById(R.id.btn_create).setEnabled(true);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }
}



