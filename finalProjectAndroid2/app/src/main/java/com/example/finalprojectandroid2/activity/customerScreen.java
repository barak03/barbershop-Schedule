package com.example.finalprojectandroid2.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalprojectandroid2.R;
import com.example.finalprojectandroid2.adapter.UserAdapter;
import com.example.finalprojectandroid2.model.day;
import com.example.finalprojectandroid2.model.event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class customerScreen extends AppCompatActivity {
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;
    public static ArrayList<com.example.finalprojectandroid2.model.day> dayArrayList = new ArrayList<day>();
    day tempDay;
    ImageButton dateBtn;
    Integer year = null;
    Integer month = null;
    Integer day = null;
    TextView dateTv;
    Spinner mySpinner;
    event tempEvent = new event();
    public static ArrayList<event> freeAppointmentsList = new ArrayList<event>();
    public static ArrayList<event> freeYesAppointmentsList = new ArrayList<event>();
    private static RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private static UserAdapter adapter = new UserAdapter(freeYesAppointmentsList);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_screen);
        dateTv = findViewById(R.id.show_date);
        recyclerView = (RecyclerView) findViewById(R.id.myRecyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        freeAppointmentsList = initializeBlankAppointmentsList();
        mySpinner = findViewById(R.id.spinner);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(customerScreen.this, android.R.layout.simple_expandable_list_item_1, getResources().getStringArray(R.array.haircuts));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);
        dateBtn = findViewById(R.id.date);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String text = mySpinner.getSelectedItem().toString();
                if (text.equals("man's haircut")) {
                    if (year == null || month == null || day == null) {
                        Toast.makeText(customerScreen.this, "please pick a date first.. ", Toast.LENGTH_SHORT).show();
                    } else {
                        freeYesAppointmentsList = new ArrayList<event>();
                        freeAppointmentsList = initializeBlankAppointmentsList();
                        getMansFreeAppointmentsList();
                        getYesEvents();
                        adapter = new UserAdapter(freeYesAppointmentsList);
                        recyclerView.setAdapter(adapter);
                    }
                } else if (text.equals("woman's haircut")) {
                    if (year == null || month == null || day == null) {
                        Toast.makeText(customerScreen.this, "please pick a date first.. ", Toast.LENGTH_SHORT).show();
                    } else {
                        freeYesAppointmentsList = new ArrayList<event>();
                        freeAppointmentsList = initializeBlankAppointmentsList();
                        getWomansFreeAppointmentsList();
                        getYesEvents();
                        adapter = new UserAdapter(freeYesAppointmentsList);
                        recyclerView.setAdapter(adapter);
                    }
                }

                adapter.setListener(new UserAdapter.UsersListener() {
                    @Override
                    public void onUserClicked(final int position, View view) {
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(customerScreen.this);
                        alertDialog.setMessage("Confirm appointment on\n" + dateTv.getText().toString() + "\nfrom " + freeYesAppointmentsList.get(position).getStartTime() + " to " + freeYesAppointmentsList.get(position).getEndTime() + " ?").setCancelable(false)
                                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        event tempEvent = freeYesAppointmentsList.get(position);
                                        if (tempEvent.getAvailability().equals("No")) {
                                            Toast.makeText(customerScreen.this, "can't schedule appointment..", Toast.LENGTH_SHORT).show();
                                        } else {
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            String userEmail = null;
                                            if (user != null) {
                                                userEmail = user.getEmail();
                                            }

                                            Calendar beginTime = Calendar.getInstance();
                                            beginTime.set(year, month, day, Integer.parseInt(tempEvent.getStartTime().split(":")[0]), Integer.parseInt(tempEvent.getStartTime().split(":")[1]));
                                            Calendar endTime = Calendar.getInstance();
                                            endTime.set(year, month, day, Integer.parseInt(tempEvent.getEndTime().split(":")[0]), Integer.parseInt(tempEvent.getEndTime().split(":")[1]));

                                            Intent intent = new Intent(Intent.ACTION_INSERT)
                                                    .setData(CalendarContract.Events.CONTENT_URI)
                                                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                                                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                                                    .putExtra(CalendarContract.Events.TITLE, mySpinner.getSelectedItem().toString())
                                                    .putExtra(CalendarContract.Events.DESCRIPTION, mySpinner.getSelectedItem().toString())
                                                    .putExtra(CalendarContract.Events.EVENT_LOCATION, "Beni barber shop, Ramla hanoch levin 22 st.")
                                                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                                                    .putExtra(Intent.EXTRA_EMAIL, "barbershopprojectp25@gmail.com");
                                            if (userEmail != null) {
                                                intent.putExtra(Intent.EXTRA_EMAIL, userEmail);
                                            }
                                            startActivity(intent);
                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            DatabaseReference myRef = database.getReference("calendar").child(dateTv.getText().toString());
                                            if (mySpinner.getSelectedItem().toString().equals("man's haircut")) {
                                                myRef.child("mansHaircut").setValue(tempEvent.getStartTime());
                                            } else if (mySpinner.getSelectedItem().toString().equals("woman's haircut")) {
                                                myRef.child("womansHaircut").setValue(tempEvent.getStartTime());
                                            }

                                            Toast.makeText(customerScreen.this, "Update successful..", Toast.LENGTH_SHORT).show();
                                            mySpinner.setSelection(0);
                                            dialog.dismiss();
                                        }
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        if (freeYesAppointmentsList.get(position).getAvailability().equals("No")) {
                            Toast.makeText(customerScreen.this, "please select other date..", Toast.LENGTH_SHORT).show();
                        } else {

                            AlertDialog alert = alertDialog.create();
                            alert.setTitle("Confirm appointment ?");
                            alert.show();
                        }
                    }
                });


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                freeYesAppointmentsList = new ArrayList<event>();
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                year = calendar.get(java.util.Calendar.YEAR);
                month = calendar.get(java.util.Calendar.MONTH);
                day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd = new DatePickerDialog(customerScreen.this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        freeYesAppointmentsList = new ArrayList<event>();
                        adapter = new UserAdapter(freeYesAppointmentsList);
                        recyclerView.setAdapter(adapter);
                        dateTv.setText(i2 + " - " + (i1 + 1) + " - " + i);
                        year = i;
                        month = i1;
                        day = i2;
                        findDay();
                    }
                }, year, month, day);
                dpd.getWindow().setLayout(1000, 1700);
                dpd.show();
                mySpinner.setSelection(0);
            }
        });
    }

    private void findDay()
    {
        databaseReference = FirebaseDatabase.getInstance().getReference("calendar");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    tempDay = new day();
                    String date = ds.getKey();
                    String[] dayParts = date.split(" - ");
                    tempDay.setDay(Integer.parseInt(dayParts[0]));
                    tempDay.setMonth(Integer.parseInt(dayParts[1]));
                    tempDay.setYear(Integer.parseInt(dayParts[2]));
                    //Toast.makeText(customerScreen.this, ds.getKey(), Toast.LENGTH_SHORT).show();
                    if (ds.getKey().equals(dateTv.getText().toString()))
                    {
                        //Toast.makeText(customerScreen.this, "inside if", Toast.LENGTH_SHORT).show();
                        databaseReference2 = FirebaseDatabase.getInstance().getReference("calendar").child(date);
                        databaseReference2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                for(DataSnapshot ds2 : dataSnapshot2.getChildren())
                                {

                                    event tempEvent = new event();
                                    if (ds2.getKey().equals("dayOff"))
                                    {
                                        tempEvent.setAvailability("No");
                                        tempEvent.setStartTime("00:01");
                                        tempEvent.setEndTime("23:59");
                                        tempEvent.setTitle("day off");
                                        tempDay.eventsList.add(tempEvent);
                                        break;
                                    }

                                    else if (ds2.getKey().equals("sickDay"))
                                    {
                                        tempEvent.setAvailability("No");
                                        tempEvent.setStartTime("00:01");
                                        tempEvent.setEndTime("23:59");
                                        tempEvent.setTitle("day off");
                                        tempDay.eventsList.add(tempEvent);
                                        break;
                                    }

                                    else if (ds2.getKey().equals("mansHaircut"))
                                    {
                                        String time = ds2.getValue(String.class);
                                        tempEvent.setAvailability("No");
                                        tempEvent.setStartTime(time);
                                        tempEvent.setEndTime(addTime(time, 30));
                                        tempEvent.setTitle("man's haircut");
                                        tempDay.eventsList.add(tempEvent);
                                    }

                                    else if (ds2.getKey().equals("womansHaircut"))
                                    {
                                        String time = ds2.getValue(String.class);
                                        tempEvent.setAvailability("No");
                                        tempEvent.setStartTime(time);
                                        tempEvent.setEndTime(addTime(time, 60));
                                        tempEvent.setTitle("woman's haircut");
                                        tempDay.eventsList.add(tempEvent);
                                    }

                                    else if (ds2.getKey().equals("Break"))
                                    {
                                        String time = ds2.getValue(String.class);
                                        String[] timeParts = time.split(" to ");
                                        tempEvent.setAvailability("No");
                                        tempEvent.setStartTime(timeParts[0]);
                                        tempEvent.setEndTime(timeParts[1]);
                                        tempEvent.setTitle("break");
                                        tempDay.eventsList.add(tempEvent);
                                    }

                                    else if (ds2.getKey().equals("startTime"))
                                    {
                                        String time = ds2.getValue(String.class);
                                        tempEvent.setAvailability("No");
                                        tempEvent.setStartTime(time);
                                        tempEvent.setEndTime(time);
                                        tempEvent.setTitle("start time");
                                        tempDay.eventsList.add(tempEvent);
                                    }

                                    else if (ds2.getKey().equals("endTime"))
                                    {
                                        String time = ds2.getValue(String.class);
                                        tempEvent.setAvailability("No");
                                        tempEvent.setStartTime(time);
                                        tempEvent.setEndTime(time);
                                        tempEvent.setTitle("end time");
                                        tempDay.eventsList.add(tempEvent);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(customerScreen.this, "Failed to read data from firebase.. ", Toast.LENGTH_SHORT).show();
                            }
                        });

                        dayArrayList = new ArrayList<day>();
                        dayArrayList.add(tempDay);
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return;

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(customerScreen.this, "Failed to read data from firebase.. ", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void getWomansFreeAppointmentsList()
    {
        String date = dateTv.getText().toString();
        String[] dateParts = date.split(" - ");
        Integer tempDay = Integer.parseInt(dateParts[0]);
        Integer tempMonth = Integer.parseInt(dateParts[1]);
        Integer tempYear = Integer.parseInt(dateParts[2]);
        day tempCalendarDay = null;
        for (day tempLoopDay : dayArrayList)
        {
            if (tempDay == tempLoopDay.day && tempMonth == tempLoopDay.month && tempYear == tempLoopDay.year)
            {
                tempCalendarDay = tempLoopDay;
                break;
            }
        }

        if (tempCalendarDay == null) // no day on database
        { // put default start time, end time and break time.
            freeAppointmentsList = new ArrayList<event>();
            freeAppointmentsList.add(new event("woman's haircut", "10:00", "11:00"));
            freeAppointmentsList.add(new event("woman's haircut", "11:00", "12:00"));
            freeAppointmentsList.add(new event("break", "12:00", "13:00"));
            freeAppointmentsList.get(2).setAvailability("No");
            freeAppointmentsList.add(new event("woman's haircut", "13:00", "14:00"));
            freeAppointmentsList.add(new event("woman's haircut", "14:00", "15:00"));
            freeAppointmentsList.add(new event("woman's haircut", "15:00", "16:00"));
            freeAppointmentsList.add(new event("woman's haircut", "16:00", "17:00"));
            freeAppointmentsList.add(new event("woman's haircut", "18:00", "19:00"));
            return;
        }
        else // there is date on the database
        {
            if (tempCalendarDay.eventsList.size() > 0)
            {
                if (tempCalendarDay.eventsList.get(0).getTitle().equals("day off") || tempCalendarDay.eventsList.get(0).getTitle().equals("sick day"))
                {
                    freeAppointmentsList = new ArrayList<event>();
                    freeAppointmentsList.add(new event("no free appointments for today", "00:01", "23:59"));
                    freeAppointmentsList.get(0).setAvailability("No");
                    return;
                }
            }
            event startTime = null;
            event endTime = null;

            for (event tempEvent : tempCalendarDay.eventsList)
            {
                if (tempEvent.getTitle().equals("start time"))
                {
                    startTime = tempEvent;
                }
                else if (tempEvent.getTitle().equals("end time"))
                {
                    endTime = tempEvent;
                }
            }

            if (startTime == null || endTime == null) // no specific start and end time -> use default
            {
                ArrayList<event> eventsToRemove = new ArrayList<event>();
                for (event tempEvent : tempCalendarDay.eventsList)
                {
                    for (event tempFreeListEvent : freeAppointmentsList)
                    {
                        if (tempFreeListEvent.getStartTime().equals(tempEvent.getStartTime()))
                        {
                            tempFreeListEvent.setTitle(tempEvent.getTitle());
                            tempFreeListEvent.setEndTime(tempEvent.getEndTime());
                            tempFreeListEvent.setAvailability("No");
                            String newTime = addTime(tempEvent.getStartTime(), 30);
                            if (tempEvent.getTitle().equals("break") || tempEvent.getTitle().equals("woman's haircut"))
                            {// if title is break or woman's haircut we need to delete the next event
                                for (event tempFreeListEvent2 : freeAppointmentsList)
                                {
                                    if (tempFreeListEvent2.getStartTime().equals(newTime))
                                    {
                                        eventsToRemove.add(tempFreeListEvent2);
                                    }
                                }
                            }
                        }
                    }
                }

                freeAppointmentsList.removeAll(eventsToRemove);
                eventsToRemove = new ArrayList<event>();
                for (int i = 0; i < freeAppointmentsList.size() - 1; i++)
                {
                    if (freeAppointmentsList.get(i).getAvailability().equals("Yes") && freeAppointmentsList.get(i + 1).getAvailability().equals("Yes") )
                    {
                        freeAppointmentsList.get(i + 1).setAvailability("No");
                        freeAppointmentsList.get(i).setEndTime(freeAppointmentsList.get(i + 1).getEndTime());
                        eventsToRemove.add(freeAppointmentsList.get(i + 1));
                        //i++;
                    }
                }

                freeAppointmentsList.removeAll(eventsToRemove);


                eventsToRemove = new ArrayList<event>();
                String tTime;
                for (event tempEvent : freeAppointmentsList)
                {
                    tTime = addTime(tempEvent.getStartTime(), 30);
                    if (tempEvent.getEndTime().equals(tTime) == true)
                    {
                        eventsToRemove.add(tempEvent);
                    }
                }

                freeAppointmentsList.removeAll(eventsToRemove);
                return;
            }
            else // there is specific start and end time -> build new schedule
            {
                ArrayList<event> newSchedule = new ArrayList<event>();
                ArrayList<event> eventsToRemove = new ArrayList<event>();
                String timeToStart = startTime.getStartTime();
                String timeToEnd = endTime.getStartTime();
                String newStartTime = timeToStart;
                while (!newStartTime.equals(timeToEnd)) // build the new schedule
                {
                    newSchedule.add(new event("", newStartTime, addTime(newStartTime, 30)));
                    newStartTime = addTime(newStartTime, 30);
                }


                for (event tempEvent : tempCalendarDay.eventsList)
                {
                    for (event tempFreeListEvent : newSchedule)
                    {
                        if (tempFreeListEvent.getStartTime().equals(tempEvent.getStartTime()) && !tempEvent.getTitle().equals("start time"))
                        {
                            tempFreeListEvent.setTitle(tempEvent.getTitle());
                            tempFreeListEvent.setEndTime(tempEvent.getEndTime());
                            tempFreeListEvent.setAvailability("No");
                            String newTime1 = addTime(tempEvent.getStartTime(), 30);
                            if (tempEvent.getTitle().equals("break") || tempEvent.getTitle().equals("woman's haircut"))
                            {// if title is break or woman's haircut we need to delete the next event
                                for (event tempFreeListEvent2 : newSchedule)
                                {
                                    if (tempFreeListEvent2.getStartTime().equals(newTime1))
                                    {
                                        eventsToRemove.add(tempFreeListEvent2);
                                    }
                                }
                            }
                        }
                    }
                }

                newSchedule.removeAll(eventsToRemove);
                freeAppointmentsList = newSchedule;
                eventsToRemove = new ArrayList<event>();
                for (int i = 0; i<freeAppointmentsList.size() - 1; i++)
                {
                    if (freeAppointmentsList.get(i).getAvailability().equals("Yes") && freeAppointmentsList.get(i + 1).getAvailability().equals("Yes") )
                    {
                        freeAppointmentsList.get(i).setEndTime(freeAppointmentsList.get(i + 1).getEndTime());
                        eventsToRemove.add(freeAppointmentsList.get(i + 1));
                        i++;
                    }
                }

                freeAppointmentsList.removeAll(eventsToRemove);
                return;
            }
        }
    }

    private void getMansFreeAppointmentsList()
    {
        String date = dateTv.getText().toString();
        String[] dateParts = date.split(" - ");
        Integer tempDay = Integer.parseInt(dateParts[0]);
        Integer tempMonth = Integer.parseInt(dateParts[1]);
        Integer tempYear = Integer.parseInt(dateParts[2]);
        day tempCalendarDay = null;
        for (day tempLoopDay : dayArrayList)
        {
            if (tempDay == tempLoopDay.day && tempMonth == tempLoopDay.month && tempYear == tempLoopDay.year)
            {
                tempCalendarDay = tempLoopDay;
                break;
            }
        }

        if (tempCalendarDay == null) // no day on database
        { // put default start time, end time and break time.
            freeAppointmentsList = new ArrayList<event>();
            freeAppointmentsList.add(new event("man's haircut", "10:00", "10:30"));
            freeAppointmentsList.add(new event("man's haircut", "10:30", "11:00"));
            freeAppointmentsList.add(new event("man's haircut", "11:00", "11:30"));
            freeAppointmentsList.add(new event("man's haircut", "11:30", "12:00"));
            freeAppointmentsList.add(new event("break", "12:00", "13:00"));
            freeAppointmentsList.get(4).setAvailability("No");
            freeAppointmentsList.add(new event("man's haircut", "13:00", "13:30"));
            freeAppointmentsList.add(new event("man's haircut", "13:30", "14:00"));
            freeAppointmentsList.add(new event("man's haircut", "14:00", "14:30"));
            freeAppointmentsList.add(new event("man's haircut", "14:30", "15:00"));
            freeAppointmentsList.add(new event("man's haircut", "15:00", "15:30"));
            freeAppointmentsList.add(new event("man's haircut", "15:30", "16:00"));
            freeAppointmentsList.add(new event("man's haircut", "16:00", "16:30"));
            freeAppointmentsList.add(new event("man's haircut", "16:30", "17:00"));
            freeAppointmentsList.add(new event("man's haircut", "17:30", "18:00"));
            freeAppointmentsList.add(new event("man's haircut", "18:00", "18:30"));
            freeAppointmentsList.add(new event("man's haircut", "18:30", "19:00"));
            return;
        }
        else // there is date on the database
        {
            if (tempCalendarDay.eventsList.size() > 0)
            {
                if (tempCalendarDay.eventsList.get(0).getTitle().equals("day off") || tempCalendarDay.eventsList.get(0).getTitle().equals("sick day"))
                {
                    freeAppointmentsList = new ArrayList<event>();
                    freeAppointmentsList.add(new event("no free appointments for today", "00:01", "23:59"));
                    freeAppointmentsList.get(0).setAvailability("No");
                    return;
                }
            }

            event startTime = null;
            event endTime = null;
            event Break = null;
            for (event tempEvent : tempCalendarDay.eventsList)
            {
                if (tempEvent.getTitle().equals("start time"))
                {
                    startTime = tempEvent;
                }
                else if (tempEvent.getTitle().equals("end time"))
                {
                    endTime = tempEvent;
                }
                else if (tempEvent.getTitle().equals("break"))
                {
                    Break = tempEvent;
                }
            }

            if (startTime == null || endTime == null) // no specific start and end time -> use default
            {
                ArrayList<event> eventsToRemove = new ArrayList<event>();
                for (event tempEvent : tempCalendarDay.eventsList)
                {
                    for (event tempFreeListEvent : freeAppointmentsList)
                    {
                        if (tempFreeListEvent.getStartTime().equals(tempEvent.getStartTime()) )
                        {
                            tempFreeListEvent.setTitle(tempEvent.getTitle());
                            tempFreeListEvent.setEndTime(tempEvent.getEndTime());
                            tempFreeListEvent.setAvailability("No");
                            String newTime = addTime(tempEvent.getStartTime(), 30);
                            if (tempEvent.getTitle().equals("break") || tempEvent.getTitle().equals("woman's haircut"))
                            {// if title is break or woman's haircut we need to delete the next event
                                for (event tempFreeListEvent2 : freeAppointmentsList)
                                {
                                    if (tempFreeListEvent2.getStartTime().equals(newTime))
                                    {
                                        eventsToRemove.add(tempFreeListEvent2);
                                    }
                                }
                            }
                        }
                    }
                }

                freeAppointmentsList.removeAll(eventsToRemove);
                return;
            }
            else // there is specific start and end time -> build new schedule
            {
                ArrayList<event> newSchedule = new ArrayList<event>();
                ArrayList<event> eventsToRemove = new ArrayList<event>();
                String timeToStart = startTime.getStartTime();
                String timeToEnd = endTime.getStartTime();
                String newStartTime = timeToStart;
                while (!newStartTime.equals(timeToEnd)) // build the new schedule
                {
                    newSchedule.add(new event("", newStartTime, addTime(newStartTime, 30)));
                    newStartTime = addTime(newStartTime, 30);
                }


                for (event tempEvent : tempCalendarDay.eventsList)
                {
                    for (event tempFreeListEvent : newSchedule)
                    {
                        if (tempFreeListEvent.getStartTime().equals(tempEvent.getStartTime()) &&  !tempEvent.getTitle().equals("start time"))
                        {
                            tempFreeListEvent.setTitle(tempEvent.getTitle());
                            tempFreeListEvent.setEndTime(tempEvent.getEndTime());
                            tempFreeListEvent.setAvailability("No");
                            String newTime1 = addTime(tempEvent.getStartTime(), 30);
                            if (tempEvent.getTitle().equals("break") || tempEvent.getTitle().equals("woman's haircut"))
                            {// if title is break or woman's haircut we need to delete the next event
                                for (event tempFreeListEvent2 : newSchedule)
                                {
                                    if (tempFreeListEvent2.getStartTime().equals(newTime1))
                                    {
                                        eventsToRemove.add(tempFreeListEvent2);
                                    }
                                }
                            }
                        }
                    }
                }

                newSchedule.removeAll(eventsToRemove);
                freeAppointmentsList = newSchedule;
                return;
            }
        }
    }

    public ArrayList<event> initializeBlankAppointmentsList()
    {
        ArrayList<event> tempEventList = new ArrayList<event>();
        tempEventList.add(new event("", "10:00", "10:30"));
        tempEventList.add(new event("", "10:30", "11:00"));
        tempEventList.add(new event("", "11:00", "11:30"));
        tempEventList.add(new event("", "11:30", "12:00"));
        tempEventList.add(new event("", "12:00", "12:30"));
        tempEventList.add(new event("", "12:30", "13:00"));
        tempEventList.add(new event("", "13:00", "13:30"));
        tempEventList.add(new event("", "13:30", "14:00"));
        tempEventList.add(new event("", "14:00", "14:30"));
        tempEventList.add(new event("", "14:30", "15:00"));
        tempEventList.add(new event("", "15:00", "15:30"));
        tempEventList.add(new event("", "15:30", "16:00"));
        tempEventList.add(new event("", "16:00", "16:30"));
        tempEventList.add(new event("", "16:30", "17:00"));
        tempEventList.add(new event("", "17:00", "17:30"));
        tempEventList.add(new event("", "17:30", "18:00"));
        tempEventList.add(new event("", "18:00", "18:30"));
        tempEventList.add(new event("", "18:30", "19:00"));
        return tempEventList;
    }

    public String addTime(String oldTime, int timeToAdd)
    {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        Date d = null;
        try {
            d = df.parse(oldTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(d);
        cal.add(java.util.Calendar.MINUTE, timeToAdd);
        String newTime = df.format(cal.getTime());
        return newTime;
    }

    public void getYesEvents()
    {
        for (event tempEvent : freeAppointmentsList)
        {
            if(tempEvent.getAvailability().equals("Yes"))
            {
                freeYesAppointmentsList.add(tempEvent);
            }
        }

        for (event tempEvemt : freeYesAppointmentsList)
        {
            if (tempEvemt.getStartTime().equals(tempEvemt.getEndTime()))
            {
                tempEvemt.setEndTime(addTime(tempEvemt.getStartTime(), 30));
            }
        }

        if (freeYesAppointmentsList.size() == 0)
        {
            freeYesAppointmentsList.add(new event("no free appointments for today", "00:01", "23:59"));
            freeYesAppointmentsList.get(0).setAvailability("No");
        }
    }
}
