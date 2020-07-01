package com.example.attendance;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static android.widget.Toast.LENGTH_SHORT;

public class Fragment33 extends Fragment {
    DatabaseReference reference;
    RecyclerView recyclerView;
    ArrayList<entry> list;
    MyAdapterentry adapter;
    EditText search;
    Button add,export;
    DatabaseReference ref,userref;
    static String entryid;
    entry e;
    student s;
    FirebaseDatabase database;
    String email;
    String prefix="";
    String date,time;
    public Fragment33(String entryid)
    {
        this.entryid = entryid;
    }

    public Fragment33() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment33_layout,container,false);
        entryid = entryid.split("@")[0];
        recyclerView = v.findViewById(R.id.myRecycler);
        recyclerView.setLayoutManager( new LinearLayoutManager(getContext()));
        search = v.findViewById(R.id.search);
        add = v.findViewById(R.id.add);
        export = v.findViewById(R.id.export);
        reference = FirebaseDatabase.getInstance().getReference().child("entry").child(entryid);
        database = FirebaseDatabase.getInstance();
        userref = database.getReference("student");
        e = new entry();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<entry>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    entry p = dataSnapshot1.getValue(entry.class);
                    list.add(p);
                }
                adapter = new MyAdapterentry(getActivity(),list);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Oops.... Error", Toast.LENGTH_SHORT).show();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addentry();
            }
        });
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("entryid",entryid);
                Intent i = new Intent(getContext(),Main4Activity.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String[] searchArray = s.toString().toLowerCase().split(" ");
                ArrayList<entry> filteredList = new ArrayList<>();
                for(String temp : searchArray){
                    for (entry item : list) {
                        if (item.getNm().toLowerCase().contains(temp) || item.getC().toLowerCase().contains(temp) || item.getSec().toLowerCase().contains(temp) || item.getDate().toLowerCase().contains(temp) || item.getTime().toLowerCase().contains(temp)) {
                            if(!filteredList.contains(item)){
                                filteredList.add(item);
                            }
                        }
                    }
                }
                adapter = new MyAdapterentry(getActivity(),filteredList);
                recyclerView.setAdapter(adapter);
            }
        });
        return v;
    }
    public void addentry()
    {
        int flag=0;
        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.addentry, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.email);
        userInput.setHint("Number");
        Button pref = (Button) promptsView.findViewById(R.id.button);
        pref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefix = userInput.getText().toString();
                Toast toast = Toast. makeText(getContext(), "Prefix set successfull!!", LENGTH_SHORT);
                toast. setGravity(Gravity. TOP ,0,0);
                toast. show();
            }
        });
        userInput.setText(prefix);
        alertDialogBuilder.setTitle("Enter Number")
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                email = userInput.getText().toString();
                                email = email.split("@")[0];
                                userref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            s = ds.getValue(student.class);
                                            if (s.getNo().split("@")[0].equals(email)) {
                                                //date and time initialization
                                                dateandtime();
                                                ref = FirebaseDatabase.getInstance().getReference().child("entry").child(entryid);
                                                e.setNm(s.getNm());
                                                e.setTime(time);
                                                e.setDate(date);
                                                e.setC(s.getC());
                                                e.setSec(s.getSec());
                                                String date1[] = date.split("/");
                                                String headdate = String.join(":",date1);
                                                ref.child(""+ headdate+" "+time).setValue(e);
                                                Toast.makeText(getContext(), "Thank You " + s.getNm(), Toast.LENGTH_LONG).show();
                                                break;
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                                Toast.makeText(getContext(),"No student found!", LENGTH_SHORT).show();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics = getActivity().getResources().getDisplayMetrics();
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        int dialogWindowWidth = (int) (displayWidth * 0.75f);
        int dialogWindowHeight = (int) (displayHeight * 0.35f);
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;
        alertDialog.getWindow().setAttributes(layoutParams);
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
