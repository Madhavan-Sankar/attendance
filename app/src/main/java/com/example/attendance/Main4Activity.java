package com.example.attendance;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Main4Activity extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    String FILE_NAME;
    int d=0;
    DatabaseReference reference;
    Button save,button;
    String entryid;
    ArrayList<entry> list;
    TextView datef,datet,timef,timet;
    entry e;
    ImageButton logout,back;
    EditText c,sec;
    String datecf,datect,timecf,timect;//ex:- time compare from,time compare to
    Button dfrom,dto,tfrom,tto;
    String date,time,course,section;
    Date from,to,fromt,tot,retriveddate,retrivedtime;
    private static final int wesc=1;
    //for date setting
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
        String formatted = format1.format(c.getTime());
        if(d==1)
        {
            datef.setText(formatted);
        }
        else if(d==2) {
            datet.setText(formatted);
        }
    }
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(d==1) {
            timef.setText(hourOfDay + ":" + minute + ":00");
        }
        else if(d==2) {
            timet.setText(hourOfDay + ":" + minute + ":00");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        Button save = findViewById(R.id.button_save);
        final Bundle bundle = getIntent().getExtras();
        entryid = bundle.getString("entryid");
        dfrom = findViewById(R.id.calendarf);
        dto = findViewById(R.id.calendart);
        tfrom = findViewById(R.id.timef1);
        tto = findViewById(R.id.timet1);
        datef = findViewById(R.id.datef);
        datet = findViewById(R.id.datet);
        timef = (TextView) findViewById(R.id.timef);
        timet = (TextView) findViewById(R.id.timet);
        c = findViewById(R.id.course);
        sec = findViewById(R.id.section);
        logout = (ImageButton) findViewById(R.id.logout);
        back = (ImageButton) findViewById(R.id.back);
        dateandtime();
        FILE_NAME = entryid+".csv";
        dfrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
                d=1;
            }
        });
        dto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
                d=2;
            }
        });
        tfrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new timepickerfragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
                d=1;
            }
        });
        tto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new timepickerfragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
                d=2;
            }
        });
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(permissions,wesc);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Main4Activity.this,Main2Activity.class);
                startActivity(i);
                Toast.makeText(getApplicationContext(),"Logged out Successfully!!",Toast.LENGTH_SHORT).show();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle1 = new Bundle();
                bundle1.putString("entryid", entryid);
                if(entryid.equals("ms007"))
                {
                    Intent i = new Intent(Main4Activity.this, Main3Activitys.class);
                    i.putExtras(bundle1);
                    startActivity(i);
                }
                else {
                    Intent i = new Intent(Main4Activity.this, Main3Activity.class);
                    i.putExtras(bundle1);
                    startActivity(i);
                }
            }
        });
    }
    @SuppressLint("WorldReadableFiles")
    public void save(View v){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED)
            {
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions,wesc);
            }
            else
            {
                savetofile();
            }
        }
        else
        {
            savetofile();
        }

    }

    public void savetofile()
    {
        try {
            course = c.getText().toString();
            section = sec.getText().toString();
            datecf = datef.getText().toString();
            datect = datet.getText().toString();
            //conversion to date formats for comparision
            from = new SimpleDateFormat("dd/MM/yyyy").parse(datecf);
            to = new SimpleDateFormat("dd/MM/yyyy").parse(datect);
            if(datecf.equals(datect)) {
                timecf = timef.getText().toString();
                timect = timet.getText().toString();
                fromt = new SimpleDateFormat("HH:mm:ss").parse(timecf);
                tot = new SimpleDateFormat("HH:mm:ss").parse(timect);
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"please fill all the fields",Toast.LENGTH_LONG).show();
        }
        reference = FirebaseDatabase.getInstance().getReference().child("entry").child(entryid);
        try{
            StringBuilder build = new StringBuilder();
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    list = new ArrayList<entry>();
                    for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                    {
                        entry e = dataSnapshot1.getValue(entry.class);
                        try {
                            DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                            retriveddate = format.parse(e.getDate().trim());
                            DateFormat time = new SimpleDateFormat("HH:mm:ss");
                            retrivedtime = time.parse(e.getTime().trim());
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                        if(e.getC().equals(course) && e.getSec().equals(section) ) // (retriveddate.compareTo(from)>0 && retriveddate.compareTo(to)<0) ||
                        {
                            //If both dates are equal any day it may be compare any one date(from or to)
                            if(from.compareTo(to)==0) {
                                if(retriveddate.compareTo(from) == 0 && retrivedtime.compareTo(fromt)>0 && retrivedtime.compareTo(tot)<0)
                                {
                                    list.add(e);
                                }
                                else if(retriveddate.compareTo(from) == 0 && retrivedtime.compareTo(fromt)==0 || retrivedtime.compareTo(to)==0)
                                {
                                    list.add(e);
                                }
                            }
                            else {
                                if (retriveddate.compareTo(from) > 0 && retriveddate.compareTo(to) < 0) {
                                    list.add(e);
                                } else if (retriveddate.compareTo(from) == 0 || retriveddate.compareTo(to) == 0) {
                                    list.add(e);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Oops.... Error", Toast.LENGTH_SHORT).show();
                }
            });
            build.append("Name,Course,Section,Date,Time");
            for(entry l:list)
            {
                Toast.makeText(getApplicationContext(),""+l.getDate()+" "+l.getTime(),Toast.LENGTH_LONG).show();
                build.append("\n"+l.getNm()+","+l.getC()+","+l.getSec()+","+l.getDate()+","+l.getTime());
            }
            File path = Environment.getExternalStorageDirectory();
            File dir = new File(path+"/Attendance Records/");
            dir.mkdirs();
            File file = new File(dir,FILE_NAME);
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_SHORT).show();
            bw.write(String.valueOf(build));
            bw.close();
        } catch (Exception e)
        {
            //Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(),"Press again!!",Toast.LENGTH_LONG).show();
        }
    }
    public void dateandtime()
    {
        Date full = Calendar.getInstance().getTime();
        String full1 = full.toString();
        time = full1.split(" ")[3];
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");
        date = mdformat.format(calendar.getTime());
    }
}