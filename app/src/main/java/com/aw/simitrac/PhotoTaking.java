package com.aw.simitrac;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.aw.simitrac.api.API;
import com.aw.simitrac.utils.Util;
import com.mindorks.paracamera.Camera;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

class PhotoFingerPrint extends AppCompatActivity {
    //UI
    ImageView photo;
    Camera camera;
    Button button;
    ProgressBar progressBar;
    String sPhoto = "", sFingerPrint = "";
    int CAMERA_REQUEST = 447, MY_CAMERA_PERMISSION_CODE = 20;
    EditText admin;



    public void addPhoto(){
        progressBar.setVisibility(View.VISIBLE);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> url_maps = new HashMap<String, String>();
                url_maps.put("function", "addPhoto");
                url_maps.put("photo", sPhoto);
                url_maps.put("id_number", admin.getText().toString());
                url_maps.put("schoolCode", Util.MySharedPreference.getValue(getApplicationContext(),"schoolCode"));
                API.POST(getApplicationContext(), url_maps, new API.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        progressBar.setVisibility(View.GONE);
                        admin.setText("");
                        Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_LONG).show();

                    }
                });
            }
        });
    }

    public void initializeUI() {
        admin =(EditText) findViewById(R.id.admin);
        camera = new Camera.Builder()
                .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                .setTakePhotoRequestCode(2)
                .setDirectory("pics")
                .setName("ali_" + System.currentTimeMillis())
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                .build(this);

        photo = (ImageView) findViewById(R.id.photo);
        photo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);

              /*  if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                } else {

                }
*/

            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sPhoto.matches("") || admin.getText().toString().matches("")){
                    Toast.makeText(PhotoFingerPrint.this, "Take photo and Enter AdmNo or ID", Toast.LENGTH_SHORT).show();

                }else {

                    addPhoto();
                }

            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.setProperty("DPTRACE_ON", "1");
        //System.setProperty("DPTRACE_VERBOSITY", "10");

        setContentView(R.layout.activity_photo_finger_print);
        //Check permission
        initializeUI();


    }

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        Log.i("# imgString = ", imgString);

        return imgString;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

         if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
             Bitmap bitmap = (Bitmap) data.getExtras().get("data");
             Bitmap croppedBmp = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(),bitmap.getHeight()-42);
             photo.setImageBitmap(croppedBmp);
             sPhoto = getEncoded64ImageStringFromBitmap(croppedBmp);
         }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }
    }
}
