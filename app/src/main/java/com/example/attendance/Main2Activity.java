package com.example.attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;

public class Main2Activity extends AppCompatActivity {

    private Button b;//finget
    private EditText e1;
    private EditText e2;
    staff p;
    ArrayList<staff> list;
    DatabaseReference userref;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        b = (Button) findViewById(R.id.login);
        e1 = (EditText) findViewById(R.id.editText2);
        e2 = (EditText) findViewById(R.id.editText);
        //finger = (Button) findViewById(R.id.finger);
        database = FirebaseDatabase.getInstance();
        userref = database.getReference("staff");
        b.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if("admin".equals(e1.getText().toString()) && "admin".equals(e2.getText().toString()))
                 {
                     Bundle bundle = new Bundle();
                     bundle.putString("entryid","ms007");
                     Intent i = new Intent(Main2Activity.this,Main3Activitys.class);
                     i.putExtras(bundle);
                     startActivity(i);
                     Toast.makeText(getApplicationContext(),"Welcome admin",Toast.LENGTH_SHORT).show();
                 }
                 else {
                     userref.addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                             list = new ArrayList<staff>();
                             for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                 p = ds.getValue(staff.class);
                                 if (p.getUn().equals(e1.getText().toString()) && p.getPw().equals(e2.getText().toString())) {
                                     Bundle bundle = new Bundle();
                                     bundle.putString("entryid", p.getNo());
                                     Intent i = new Intent(Main2Activity.this, Main3Activity.class);
                                     i.putExtras(bundle);
                                     startActivity(i);
                                     Toast.makeText(getApplicationContext(), "Welcome " + p.getNm(), LENGTH_SHORT).show();
                                     break;
                                 }
                             }
                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError databaseError) {
                         }
                     });
                 }
                 //Toast.makeText(getApplicationContext(),"Login failed!", LENGTH_SHORT).show();
             }
         });
        /*finger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Sorry...Pending", LENGTH_SHORT).show();
            }
        });*/
    }
}
