package com.example.attendance;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;

public class Fragment3add extends Fragment {
    Context applicationContext = Main3Activity.getContextOfApplication();
    private ImageButton back;
    private Button click;
    private Button finger;
    private Button image;
    private EditText nm,no,c,sec;
    private ImageView imgview;
    SensorManager sensorManager;
    private FingerprintManager.AuthenticationCallback ac;
    String nm1,no1,c1,sec1;
    Uri forimage;
    int Image_Request_Code=1;
    ImageView imgview1;
    String entryid;
    DatabaseReference ref,checkmail;
    StorageReference storageReference;
    StorageTask mUploadTask;
    student s;
    //functions for uploading images are declared here
    private String getFileExtension(Uri uri) {
        ContentResolver cR = applicationContext.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    public Fragment3add(String entryid)
    {
        this.entryid = entryid;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment3add_layout,container,false);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        finger = v.findViewById(R.id.finger);
        back = v.findViewById(R.id.back);
        nm = v.findViewById(R.id.editText1);
        no = v.findViewById(R.id.editText2);
        c = v.findViewById(R.id.editText3);
        sec = v.findViewById(R.id.editText4);
        click = v.findViewById(R.id.click);
        image = v.findViewById(R.id.image);
        imgview = v.findViewById(R.id.imgview);
        ref = FirebaseDatabase.getInstance().getReference().child("student");
        checkmail = FirebaseDatabase.getInstance().getReference().child("student");
        storageReference = FirebaseStorage.getInstance().getReference().child("student");
        s=new student();
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"select picture"), Image_Request_Code);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("entryid",entryid);
                Intent i = new Intent(getActivity(), Main3Activity.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
        finger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Sorry...Pending", LENGTH_SHORT).show();
            }
        });
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), forimage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                nm1 = nm.getText().toString().trim();
                no1 = no.getText().toString().trim();
                c1 = c.getText().toString().trim();
                sec1 = sec.getText().toString().trim();
                //for checking whether the mail exists or not
                checkmail.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            student ss = ds.getValue(student.class);
                            if (ss.getNo().equals(no1)) {
                                Toast.makeText(getContext(), "Sorry! email is already registered!!", Toast.LENGTH_LONG).show();
                                Bundle bundle = new Bundle();
                                bundle.putString("entryid", entryid);
                                Intent i = new Intent(getActivity(), Main3Activity.class);
                                i.putExtras(bundle);
                                startActivity(i);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                s.setNm(nm1);
                s.setC(c1);
                s.setNo(no1);
                s.setSec(sec1);
                uploadImage(no1.split("@")[0]);
                ref.child("" + no1.split("@")[0]).setValue(s);
                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putString("entryid", entryid);
                Intent i = new Intent(getActivity(), Main3Activity.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try
        {
            if (requestCode == Image_Request_Code) {
                forimage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), forimage);
                    //imgview.setImageBitmap(bitmap);
                    Picasso.with(getContext()).load(forimage).into(imgview);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    //functions for uploading images are declared here
    private void uploadImage(String name)
    {
        if (forimage != null) {
            StorageReference fileReference = storageReference.child(name
                    + "." + "jpg");
            mUploadTask = fileReference.putFile(forimage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_LONG).show();
                            student upload = new student();
                            String uploadId = ref.push().getKey();
                            //ref.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed!!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}
