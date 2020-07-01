package com.example.attendance;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

public class Fragment32s extends Fragment{
    DatabaseReference reference;
    RecyclerView recyclerView;
    ArrayList<staff> list;
    EditText search;
    String entryid;
    MyAdapters adapter;
    public Fragment32s(String entryid)
    {
        this.entryid = entryid;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment32_layouts, container, false);
        recyclerView = v.findViewById(R.id.myRecyclers);
        recyclerView.setLayoutManager( new LinearLayoutManager(getContext()));
        search = v.findViewById(R.id.search);
        reference = FirebaseDatabase.getInstance().getReference().child("staff");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<staff>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    staff p = dataSnapshot1.getValue(staff.class);
                    list.add(p);
                }
                adapter = new MyAdapters(getActivity(),list,entryid);
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
                ArrayList<staff> filteredList = new ArrayList<>();
                for(String temp : searchArray){
                    for (staff item : list) {
                        if (item.getNm().toLowerCase().contains(temp) || item.getNo().toLowerCase().contains(temp) || item.getDep().toLowerCase().contains(temp) || item.getCourse().toLowerCase().contains(temp)) {
                            if(!filteredList.contains(item)){
                                filteredList.add(item);
                            }
                        }
                    }
                }
                adapter = new MyAdapters(getActivity(),filteredList,entryid);
                recyclerView.setAdapter(adapter);
            }
        });
        return v;
    }
}


