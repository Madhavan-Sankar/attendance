package com.example.attendance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Fragment3del extends AppCompatDialogFragment {
    private ImageButton back;
    String number,name;
    Button yes,no;
    TextView deltext;
    FirebaseDatabase database;
    DatabaseReference userref;
    ArrayList<student> list;
    student p;
    @Nullable
    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment3delete_layout,null);
        yes = (Button) v.findViewById(R.id.yes);
        no = (Button) v.findViewById(R.id.no);
        deltext = (TextView) v.findViewById(R.id.deltext);
        Bundle bundle = this.getArguments();
        number = bundle.getString("number");
        name = bundle.getString("name");
        deltext.setText("Are you sure to delete "+name);
        database = FirebaseDatabase.getInstance();
        AlertDialog alertDialog = builder.create();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        width = (int) ((int) width*0.5f);
        height = (int) ((int) height*0.5f);
        layoutParams.width = width;
        layoutParams.height = height;
        alertDialog.getWindow().setAttributes(layoutParams);
        builder.show();
        userref = database.getReference("student");
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<student>();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    p = ds.getValue(student.class);
                    if(p.getNo().equals(number))
                    {
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        builder.setView(v).setTitle("Confirm");
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference delref = FirebaseDatabase.getInstance().getReference("student").child(number.split("@")[0]);
                delref.removeValue();
                Toast.makeText(getContext(),"Deleted successfully",Toast.LENGTH_SHORT).show();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Clicked No",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getActivity(), Main3Activity.class);
                startActivity(i);
            }
        });
        return builder.create();
    }
}
