package com.example.attendance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.attendance.ui.main.SectionsPagerAdapter;
import com.example.attendance.ui.main.SectionsPagerAdapters;
import com.google.android.material.tabs.TabLayout;

public class Main3Activitys extends AppCompatActivity {
    public static Context contextOfApplication;
    ImageButton logout;
    String entryid;
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        contextOfApplication = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3s);
        Bundle bundle = getIntent().getExtras();
        entryid = bundle.getString("entryid");
        SectionsPagerAdapters sectionsPagerAdapter = new SectionsPagerAdapters(this, getSupportFragmentManager(),entryid);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        com.getbase.floatingactionbutton.FloatingActionButton fab1 = findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getApplicationContext(),"FAB1",Toast.LENGTH_SHORT);
                toast.show();
                setContentView(R.layout.fragment3add_layouts);
                getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new Fragment3adds ()).addToBackStack(null).commit();
            }
        });
        logout = (ImageButton) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Main3Activitys.this,Main2Activity.class);
                startActivity(i);
                Toast.makeText(getApplicationContext(),"Logged out Successfully!!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}