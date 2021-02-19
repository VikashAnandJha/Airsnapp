package com.vaj.airsnapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class CameraActivity extends AppCompatActivity {
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    private Camera mCamera;
    private CameraPreview mPreview;
    private String TAG="Camera activity";
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    String picpath;
    FirebaseStorage storage;
    StorageReference imagesRef;
    private Uri filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        getSupportActionBar().hide();
       if(checkAndRequestPermissions(this)){
// Create an instance of Camera
            mCamera = getCameraInstance();

            // get Camera parameters

            // Create our Preview view and set it as the content of our activity.
            mPreview = new CameraPreview(this, mCamera);




            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);
        }


        // Add a listener to the Capture button
        ImageView captureButton = (ImageView) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.setDisplayOrientation(90);

                        mCamera.takePicture(null, null, mPicture);
                       // showPreview();
                    }
                }
        );


    }

    void showPreview()
    {
        Intent intent=new Intent(this,PreviewImageActivity.class);
        intent.putExtra("picpath",picpath);
        startActivity(intent);
    }

    void showCameraArea()
    {

    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
boolean cameraFront=false;
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getExternalMediaDirs()[0];



            pictureFile = new File(directory, "airsnap_"+Config.getradomNumber() + ".jpg");

            if (pictureFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions");
                return;
            }

            try {
              /*  FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();



                Toast.makeText(CameraActivity.this,"Stored at:"+picpath,Toast.LENGTH_LONG).show();
                Log.d("airsnapp",picpath);*/

                picpath=pictureFile.getAbsolutePath();
                FileOutputStream fos = new FileOutputStream(pictureFile);
                Bitmap bm = null;

                // COnverting ByteArray to Bitmap - >Rotate and Convert back to Data
                if (data != null) {
                    bm = BitmapFactory.decodeByteArray(data, 0, (data != null) ? data.length : 0);
                    int screenWidth = bm.getHeight();
                    int screenHeight = bm.getWidth();

                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        // Notice that width and height are reversed
                        Bitmap scaled = Bitmap.createScaledBitmap(bm, screenHeight, screenWidth, true);
                        int w = scaled.getWidth();
                        int h = scaled.getHeight();
                        // Setting post rotate to 90
                        Matrix mtx = new Matrix();

                        int CameraEyeValue = setPhotoOrientation(CameraActivity.this, cameraFront == true ? 1 : 0); // CameraID = 1 : front 0:back
                        if (cameraFront) { // As Front camera is Mirrored so Fliping the Orientation
                            if (CameraEyeValue == 270) {
                                mtx.postRotate(90);
                            } else if (CameraEyeValue == 90) {
                                mtx.postRotate(270);
                            }
                        } else {
                            mtx.postRotate(CameraEyeValue); // CameraEyeValue is default to Display Rotation
                        }

                        bm = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
                    } else {// LANDSCAPE MODE
                        //No need to reverse width and height
                        Bitmap scaled = Bitmap.createScaledBitmap(bm, screenWidth, screenHeight, true);
                        bm = scaled;
                    }
                }
                // COnverting the Die photo to Bitmap

                bm=addWatermark(getResources(),bm);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);




                byte[] byteArray = stream.toByteArray();
                fos.write(byteArray);
                //fos.write(data);
                fos.close();






                 Toast.makeText(CameraActivity.this, "Picture saved: " + pictureFile.getName(), Toast.LENGTH_LONG).show();
Log.d(TAG,pictureFile.getAbsolutePath());
                // uploadPicToCloud();
                showPreview();
                refreshGallery();
            } catch (FileNotFoundException e) {
                Log.d(TAG,e.getMessage());
            } catch (IOException e) {

                Log.d(TAG,e.getMessage());
            }


        }

    };

    public static Bitmap addWatermark(Resources res, Bitmap source) {
        int w, h;
        Canvas c;
        Paint paint;
        Bitmap bmp, watermark;
        Matrix matrix;
        float scale;
        RectF r;
        w = source.getWidth();
        h = source.getHeight();
        // Create the new bitmap
        bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
        // Copy the original bitmap into the new one
        c = new Canvas(bmp);
        c.drawBitmap(source, 0, 0, paint);
        // Load the watermark
        watermark = BitmapFactory.decodeResource(res, R.drawable.watermark);
        // Scale the watermark to be approximately 40% of the source image height
        scale = (float) (((float) h * 0.04) / (float) watermark.getHeight());
        // Create the matrix
        matrix = new Matrix();
        matrix.postScale(scale, scale);
        // Determine the post-scaled size of the watermark
        r = new RectF(0, 0, watermark.getWidth(), watermark.getHeight());
        matrix.mapRect(r);
        // Move the watermark to the bottom right corner
        matrix.postTranslate(w - r.width(), h - r.height());
        // Draw the watermark
        c.drawBitmap(watermark, matrix, paint);
        // Free up the bitmap memory
        watermark.recycle();
        return bmp;
    }

    public int setPhotoOrientation(Activity activity, int cameraId) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        // do something for phones running an SDK before lollipop
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        return result;
    }







    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }
    void refreshGallery()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f1 = new File("file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
            Uri contentUri = Uri.fromFile(f1);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);
        } else {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(picpath))));
    }



    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "airsnapp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
    public static boolean checkAndRequestPermissions(final Activity context) {
        int ExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (ExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
}