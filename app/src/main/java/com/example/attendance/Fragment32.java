package com.example.attendance;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Fragment32 extends Fragment{
    DatabaseReference reference;
    RecyclerView recyclerView;
    ArrayList<student> list;
    EditText search;
    String entryid;
    MyAdapter adapter;
    public Fragment32(String entryid)
    {
        this.entryid = entryid;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment32_layout, container, false);
        recyclerView = v.findViewById(R.id.myRecycler);
        recyclerView.setLayoutManager( new LinearLayoutManager(getContext()));
        search = v.findViewById(R.id.search);
        reference = FirebaseDatabase.getInstance().getReference().child("student");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<student>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    student p = dataSnapshot1.getValue(student.class);
                    list.add(p);
                }
                adapter = new MyAdapter(getActivity(),list,entryid);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Oops.... Error", Toast.LENGTH_SHORT).show();
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
                ArrayList<student> filteredList = new ArrayList<>();
                for(String temp : searchArray){
                    for (student item : list) {
                        if (item.getNm().toLowerCase().contains(temp) || item.getNo().toLowerCase().contains(temp) || item.getC().toLowerCase().contains(temp) || item.getSec().toLowerCase().contains(temp)) {
                            if(!filteredList.contains(item)){
                                filteredList.add(item);
                            }
                        }
                    }
                }
                adapter = new MyAdapter(getActivity(),filteredList,entryid){
                };
                recyclerView.setAdapter(adapter);
            }
        });
        return v;
    }
}


