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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import static android.widget.Toast.LENGTH_SHORT;

public class Fragment3adds extends Fragment {
    Context applicationContext = Main3Activitys.getContextOfApplication();
    private ImageButton back;
    private Button click;
    private Button finger;
    private Button image;
    private EditText nm,no,dep,course,un,pw;
    private ImageView imgview;
    SensorManager sensorManager;
    private FingerprintManager.AuthenticationCallback ac;
    String nm1,no1,dep1,course1,un1,pw1;
    Uri forimage;
    int Image_Request_Code=1;
    DatabaseReference ref;
    StorageReference storageReference;
    StorageTask mUploadTask;
    staff s;
    //functions for uploading images are declared here
    private String getFileExtension(Uri uri) {
        ContentResolver cR = applicationContext.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    public Fragment3adds()
    {

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment3add_layouts,container,false);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        finger = v.findViewById(R.id.finger);
        back = v.findViewById(R.id.back);
        nm = v.findViewById(R.id.editText1);
        no = v.findViewById(R.id.editText2);
        dep = v.findViewById(R.id.editText3);
        course = v.findViewById(R.id.editText4);
        un = v.findViewById(R.id.un);
        pw = v.findViewById(R.id.un);
        click = v.findViewById(R.id.click);
        image = v.findViewById(R.id.image);
        imgview = v.findViewById(R.id.imgview);
        ref = FirebaseDatabase.getInstance().getReference().child("staff");
        storageReference = FirebaseStorage.getInstance().getReference().child("staff");
        s=new staff();
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
                bundle.putString("entryid","ms007");
                Intent i = new Intent(getActivity(), Main3Activitys.class);
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
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),forimage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                un1 = un.getText().toString().trim();
                pw1 = un.getText().toString().trim();
                nm1 = nm.getText().toString().trim();
                no1 = no.getText().toString().trim();
                dep1 = dep.getText().toString().trim();
                course1 = course.getText().toString().trim();
                s.setUn(un1);
                s.setPw(pw1);
                s.setNm(nm1);
                s.setNo(no1);
                s.setDep(dep1);
                s.setCourse(course1);
                uploadImage(no1.split("@")[0]);
                ref.child(""+no1.split("@")[0]).setValue(s);
                Bundle bundle = new Bundle();
                bundle.putString("entryid","ms007");
                try {
                    Intent intent = new Intent(getContext(), Main3Activitys.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
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
                            staff upload = new staff();
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
                            Toast.makeText(getContext(),"Please Wait!!",Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}
