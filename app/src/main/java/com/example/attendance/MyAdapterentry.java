package com.example.attendance;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class MyAdapterentry extends RecyclerView.Adapter<MyAdapterentry.MyViewHolder>{
    Context context;
    ArrayList<entry> profiles;
    ArrayList<entry> fprofiles;
    ImageButton edit,delete;
    Dialog mydialog;
    EditText name,course,section,dat;
    TextView number;
    private FragmentActivity myContext;

    public MyAdapterentry(Context c , ArrayList<entry> p)
    {
        context = c;
        profiles = p;
        fprofiles = p;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(context).inflate(R.layout.entry,parent,false);
        return new MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.nm.setText(profiles.get(position).getNm());
        holder.c.setText(profiles.get(position).getC());
        holder.sec.setText(profiles.get(position).getSec());
        holder.date.setText(profiles.get(position).getDate());
        holder.time.setText(profiles.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView nm,c,sec,date,time;
        ImageView imgview;
        LinearLayout main;
        @SuppressLint("WrongViewCast")
        public MyViewHolder(View itemView) {
            super(itemView);
            nm = (TextView) itemView.findViewById(R.id.nm);
            c = (TextView) itemView.findViewById(R.id.c);
            sec = (TextView) itemView.findViewById(R.id.sec);
            date = (TextView) itemView.findViewById(R.id.datef);
            time = (TextView) itemView.findViewById(R.id.time);
            main = (LinearLayout) itemView.findViewById(R.id.main);
            edit = (ImageButton) itemView.findViewById(R.id.edit);
            delete = (ImageButton) itemView.findViewById(R.id.delete);
        }
    }
}
