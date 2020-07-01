package com.example.attendance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Fragment3edit extends AppCompatDialogFragment {
    Context applicationContext = Main3Activity.getContextOfApplication();
    EditText name,course,section;
    TextView number;
    Button image,up;
    ImageView imageView;
    Uri forimage;
    ArrayList<student> list;
    StorageTask mUploadTask;
    student p;
    FirebaseDatabase database;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference;
    DatabaseReference userref;
    String entryid;
    int Image_Request_Code = 1;
    //functions for uploading images are declared here
    private String getFileExtension(Uri uri) {
        ContentResolver cR = applicationContext.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    @Nullable
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment3edit_layout,null);
        name = (EditText) v.findViewById(R.id.name);
        number = (TextView) v.findViewById(R.id.number);
        course = (EditText) v.findViewById(R.id.course);
        section = (EditText) v.findViewById(R.id.section);
        Bundle bundle = this.getArguments();
        number.setText(bundle.getString("number"));
        entryid = bundle.getString("entryid");
        image = (Button) v.findViewById(R.id.imageedit);
        imageView = (ImageView) v.findViewById(R.id.imgviewedit);
        up = (Button) v.findViewById(R.id.update);
        builder.setView(v);
        database = FirebaseDatabase.getInstance();
        userref = database.getReference("student");
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<student>();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    p = ds.getValue(student.class);
                    if(p.getNo().equals(number.getText()))
                    {
                        name.setText(p.getNm());
                        course.setText(p.getC());
                        section.setText(p.getSec());
                        storageReference = FirebaseStorage.getInstance().getReference().child("student");
                        try {
                            final File file = File.createTempFile("image",".jpg");
                            storageReference.child(p.getNo().split("@")[0]+".jpg").getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                    imageView.setImageBitmap(bitmap);
                                }
                            });
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"select picture"), Image_Request_Code);
            }
        });
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),forimage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                p.setNm(name.getText().toString().trim());
                p.setC(course.getText().toString().trim());
                p.setSec(section.getText().toString().trim());
                uploadImage(p.getNo().split("@")[0]);
                //add url
                userref.child(""+number.getText().toString().split("@")[0]).setValue(p);
                Toast.makeText(getContext(),"Success",Toast.LENGTH_SHORT).show();
                Bundle bundle1 = new Bundle();
                bundle1.putString("entryid",entryid);
                Intent i = new Intent(getActivity(), Main3Activity.class);
                i.putExtras(bundle1);
                startActivity(i);
            }
        });
        return builder.create();
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
                    imageView.setImageBitmap(bitmap);
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
    private void uploadImage(String name)
    {
        if (forimage != null) {
            StorageReference fileReference = storageReference.child(name
                    + "." + getFileExtension(forimage));
            mUploadTask = fileReference.putFile(forimage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_LONG).show();
                            student upload = new student();
                            String uploadId = userref.push().getKey();
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
                        }
                    });
        } else {
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}
