package com.example.finalprojectandroid2.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.example.finalprojectandroid2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.Calendar;


public class managementScreen extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    TextView dateTv;
    TextView startTv;
    TextView endTv;
    Spinner mySpinner;
    int year;
    int month;
    int day;
    int hourEnd;
    int minuteEnd;
    int hourStart;
    int minuteStart;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_management_screen);
        dateTv = findViewById(R.id.show_date);
        startTv = findViewById(R.id.show_start_time);
        endTv = findViewById(R.id.show_end_time);
        mySpinner = findViewById(R.id.spinner);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(managementScreen.this, android.R.layout.simple_expandable_list_item_1, getResources().getStringArray(R.array.names));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);
        Button startBtn = findViewById(R.id.shiftstartTime);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                hourStart = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                minuteStart = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(managementScreen.this, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if (selectedMinute >= 0 || selectedMinute <= 9)
                        {
                            startTv.setText(selectedHour + ":" + "0"+ selectedMinute);
                        }
                        else
                        {
                            startTv.setText(selectedHour + ":" + selectedMinute);
                        }

                    }
                }, hourStart, minuteStart, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        Button endBtn = findViewById(R.id.shiftEndTime);
        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar mcurrentTime = Calendar.getInstance();
                hourEnd = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                minuteEnd = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(managementScreen.this, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if (selectedMinute >= 0 || selectedMinute <= 9)
                        {
                            endTv.setText(selectedHour + ":" + "0"+ selectedMinute);
                        }
                        else
                        {
                            endTv.setText(selectedHour + ":" + selectedMinute);
                        }
                        hourEnd = selectedHour;
                        minuteEnd = selectedMinute;
                    }
                }, hourEnd, minuteEnd, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();


            }
        });
        ImageButton dateBtn = findViewById(R.id.date);
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(managementScreen.this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        dateTv.setText(i2 + " - " + (i1 + 1) + " - " + i);
                        year = i;
                        month = i1;
                        day = i2;
                    }
                }, year, month, day);
                dpd.getWindow().setLayout(1000, 1500);
                dpd.show();
            }
        });

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String text = mySpinner.getSelectedItem().toString();
                if (text.equals("Day off") || text.equals("Sick day")) {
                    startTv.setText("00:01");
                    endTv.setText("23:59");
                } else {
                    startTv.setText("");
                    endTv.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    public void SaveUpdate(View view) throws GeneralSecurityException, IOException {
        String startTimeString = startTv.getText().toString();
        String endTimeString = endTv.getText().toString();

        String[] parts = startTimeString.split(":");
        Integer startTimeHour = Integer.parseInt(parts[0]);
        Integer startTimeMinuter = Integer.parseInt(parts[1]);

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(year, month, day, startTimeHour, startTimeMinuter);
        Calendar endTime = Calendar.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("calendar").child(dateTv.getText().toString());


        String text = mySpinner.getSelectedItem().toString();
        if (text.equals("Day off") || text.equals("Sick day"))
        {
            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);
            endTime.set(year, month, tomorrow.getDayOfMonth(), 0, 0);
            if (text.equals("Day off"))
            {
                myRef.child("dayOff").setValue(startTimeString + " to " + endTimeString);
            }
            else
            {
                myRef.child("sickDay").setValue(startTimeString + "to" + endTimeString);
            }
        }
        else
        {
            endTime.set(year, month, day, hourEnd, minuteEnd);
            if (text.equals("Break"))
            {
                myRef.child("Break").setValue(startTimeString + " to " + endTimeString);
            }
            else if(text.equals("Start time and end time"))
            {
                myRef.child("startTime").setValue(startTimeString);
                myRef.child("endTime").setValue(endTimeString);
            }
        }

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, mySpinner.getSelectedItem().toString())
                .putExtra(CalendarContract.Events.DESCRIPTION,  mySpinner.getSelectedItem().toString())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "Beni barber shop, Ramla hanoch levin 22 st.")
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                .putExtra(Intent.EXTRA_EMAIL, "barbershopprojectp25@gmail.com");
        startActivity(intent);
        Toast.makeText(managementScreen.this, "Update successful..", Toast.LENGTH_SHORT).show();
        dateTv.setText("");
        startTv.setText("");
        endTv.setText("");
        mySpinner.setSelection(0);
    }
}