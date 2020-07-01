package com.example.attendance;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
    Context context;
    ArrayList<student> profiles;
    ArrayList<student> fprofiles;
    ImageButton edit,delete;
    Dialog mydialog;
    String entryid;
    EditText name,course,section;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference;
    TextView number;
    private FragmentActivity myContext;

    public MyAdapter(Context c , ArrayList<student> p,String entryid)
    {
        context = c;
        profiles = p;
        fprofiles = p;
        this.entryid = entryid;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(context).inflate(R.layout.cardview,parent,false);
        return new MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.nm.setText(profiles.get(position).getNm());
        holder.no.setText(profiles.get(position).getNo());
        holder.c.setText(profiles.get(position).getC());
        holder.sec.setText(profiles.get(position).getSec());
        storageReference = storage.getReferenceFromUrl("gs://attendance-b1b8f.appspot.com/student").child(profiles.get(position).getNo().split("@")[0]+".jpg");
        try {
            final File file = File.createTempFile("image",".jpg");
            storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    holder.imgview.setImageBitmap(bitmap);
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(context,"Please Wait",Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        mydialog = new Dialog(context);
        mydialog.setContentView(R.layout.fragment3edit_layout);
        name = (EditText) mydialog.findViewById(R.id.name);
        number = (TextView) mydialog.findViewById(R.id.number);
        course = (EditText) mydialog.findViewById(R.id.course);
        section = (EditText) mydialog.findViewById(R.id.section);


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("name",profiles.get(holder.getAdapterPosition()).getNm());
                bundle.putString("number",profiles.get(holder.getAdapterPosition()).getNo());
                bundle.putString("course",profiles.get(holder.getAdapterPosition()).getC());
                bundle.putString("section",profiles.get(holder.getAdapterPosition()).getSec());
                bundle.putString("entryid",entryid);
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment3edit fragment3edit = new Fragment3edit();
                Fragment32 fragment32 = new Fragment32(entryid);
                fragment3edit.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().remove(fragment32).addToBackStack(null).commit();
                activity.getSupportFragmentManager().beginTransaction().add(fragment3edit,"ok").addToBackStack(null).commit();
                //Toast.makeText(context,"Edit",Toast.LENGTH_SHORT).show();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm");
                builder.setMessage("Sure to delete "+profiles.get(holder.getAdapterPosition()).getNm());
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference delref = FirebaseDatabase.getInstance().getReference("student").child(profiles.get(holder.getAdapterPosition()).getNo().split("@")[0]);
                        StorageReference del = storage.getReferenceFromUrl("gs://attendance-b1b8f.appspot.com/student").child(profiles.get(holder.getAdapterPosition()).getNo().split("@")[0]+".jpg");
                        delref.removeValue();
                        try {
                            del.delete();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        Toast.makeText(context,"Deleted Successfully!!",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No",null);
                AlertDialog dialog = builder.create();
                dialog.show();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                displayMetrics = context.getResources().getDisplayMetrics();
                int displayWidth = displayMetrics.widthPixels;
                int displayHeight = displayMetrics.heightPixels;
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                int dialogWindowWidth = (int) (displayWidth * 0.75f);
                int dialogWindowHeight = (int) (displayHeight * 0.3f);
                layoutParams.width = dialogWindowWidth;
                layoutParams.height = dialogWindowHeight;
                dialog.getWindow().setAttributes(layoutParams);
            }
        });
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView nm,no,c,sec;
        ImageView imgview;
        LinearLayout main;
        @SuppressLint("WrongViewCast")
        public MyViewHolder(View itemView) {
            super(itemView);
            nm = (TextView) itemView.findViewById(R.id.nm);
            no = (TextView) itemView.findViewById(R.id.no);
            c = (TextView) itemView.findViewById(R.id.c);
            sec = (TextView) itemView.findViewById(R.id.sec);
            imgview = (ImageView) itemView.findViewById(R.id.imgview);
            main = (LinearLayout) itemView.findViewById(R.id.main);
            edit = (ImageButton) itemView.findViewById(R.id.edit);
            delete = (ImageButton) itemView.findViewById(R.id.delete);
        }
    }
    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}
