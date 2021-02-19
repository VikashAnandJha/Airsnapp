package com.vaj.airsnapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class PreviewImageActivity extends AppCompatActivity {
Uri filePath;
String picpath;
    FirebaseStorage storage;
    StorageReference imagesRef;
    String TAG="airsnapp Preview image";
    TextView uploadPercentTv;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);
        getSupportActionBar().hide();
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Roboto-Bold.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        ImageView previewImage=findViewById(R.id.previewImage);
        storage = FirebaseStorage.getInstance();

        uploadPercentTv=findViewById(R.id.uploadPercent);

        imagesRef = storage.getReferenceFromUrl("gs://personalcloud-df385.appspot.com");

        picpath=getIntent().getStringExtra("picpath");
        if(picpath==null){
            picpath="/storage/emulated/0/Android/media/com.vaj.airsnapp/airsnap_1613690356626.jpg";
        }

if(picpath!=null){
    filePath= Uri.fromFile(new File(picpath));
  //  Config.showToast(this,"path"+picpath);
    Picasso.get()
            .load(filePath)

            .into(previewImage);

    findViewById(R.id.retakeBtn).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    }); findViewById(R.id.retakeBtn2).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    });

    findViewById(R.id.saveCoud).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            uploadPicToCloud();
        }
    });


 findViewById(R.id.viewInAirGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Config.showToast(PreviewImageActivity.this,"Coming Soon..");
            }
        });


    }

    }


    void showUploadingArea()
    {
        findViewById(R.id.uploadArea).setVisibility(View.VISIBLE);
        findViewById(R.id.bottomActions).setVisibility(View.GONE);
    }

    void showUploadCompleteArea()
    {
        findViewById(R.id.uploadArea).setVisibility(View.GONE);
        findViewById(R.id.successArea).setVisibility(View.VISIBLE);
    }

    void uploadPicToCloud()
    {
        showUploadingArea();

        filePath= Uri.fromFile(new File(picpath));

        Config.showToast(this,"Uploading to cloud..");
        Log.d(TAG,filePath+" --< file path");

        //if there is a file to upload

        //config.showToast(this,filePath.toString()+"kk");
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
          /*  final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.setCancelable(false);
            progressDialog.show();*/



            String pp=filePath.getPath();
            //   String ext=picpath.substring(picpath.lastIndexOf("."));
            String ext=pp.substring(pp.lastIndexOf("."));
            final StorageReference riversRef = imagesRef.child("airsnapp/"+Config.current_username+"/"+filePath.getLastPathSegment());
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog


                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                  //  progressDialog.dismiss();

                                    String durl = uri.toString();




                                    String fileURL1 = uri.getLastPathSegment();
                                    String token = uri.getEncodedQuery().replace("alt=media&token=", "");
                                    //config.showToast(UploadIDs.this,fileURL1);


                                    Log.d("url", durl + "");

                                    showUploadCompleteArea();

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                           // progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage() + "fil" + picpath, Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                           // progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                            uploadPercentTv.setText(((int) progress) + "%");
                        }
                    });
        }
        //if there is not any file
        else

        {

            Config.showToast(this, "error:" + picpath);
        }

    }
}