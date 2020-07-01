package com.example.attendance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.attendance.ui.main.SectionsPagerAdapter;

public class Main3Activity extends AppCompatActivity{
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
        setContentView(R.layout.activity_main3);
        Bundle bundle = getIntent().getExtras();
        entryid = bundle.getString("entryid");
        if(entryid.equals("ms007"))
        {
            Bundle bundle1 = new Bundle();
            bundle1.putString("entryid","ms007");
            Intent i = new Intent(Main3Activity.this,Main3Activitys.class);
            i.putExtras(bundle1);
            startActivity(i);
        }
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(),entryid);
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
                setContentView(R.layout.fragment3add_layout);
                getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new Fragment3add (entryid)).addToBackStack(null).commit();
            }
        });
        logout = (ImageButton) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Main3Activity.this,Main2Activity.class);
                startActivity(i);
                Toast.makeText(getApplicationContext(),"Logged out Successfully!!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}