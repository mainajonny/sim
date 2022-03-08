import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.aw.simitrac.R;
import com.aw.simitrac.api.API;
import com.aw.simitrac.pos.GetReaderActivity;
import com.aw.simitrac.pos.Globals;
import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.dpfpddusbhost.DPFPDDUsbException;
import com.digitalpersona.uareu.dpfpddusbhost.DPFPDDUsbHost;
import com.mindorks.paracamera.Camera;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class FingerPrint extends AppCompatActivity {
    //UI
    ImageView photo;
    Camera camera;
    Button button;
    ProgressBar progressBar;
    String sPhoto = "", sFingerPrint = "";
    int CAMERA_REQUEST = 447, MY_CAMERA_PERMISSION_CODE = 20;

    public void addFingerPrint(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> url_maps = new HashMap<String, String>();
                url_maps.put("function", "addFingerPrint");
                url_maps.put("finger_print", sFingerPrint);
                API.POST(getApplicationContext(), url_maps, new API.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                    }
                });
            }
        });
    }

  
    
    public void initializeUI() {
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

                if(sFingerPrint.matches("")){
                    Toast.makeText(FingerPrint.this, "Prendre une photo", Toast.LENGTH_SHORT).show();

                }else {

                    addFingerPrint();
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

           // Finger Print
        switchUsb("2");
        SystemClock.sleep(500);
        launchGetReader();
    }

    /*Finger Print Capture*/
    private final int GENERAL_ACTIVITY_RESULT = 1;
    private static final String ACTION_USB_PERMISSION = "com.digitalpersona.uareu.dpfpddusbhost.USB_PERMISSION";
    private String m_deviceName = "";
    Reader m_reader;
    private int m_DPI = 0;
    private Bitmap m_bitmap = null;
    private ImageView fingerPrint;
    private boolean m_reset = false;
    private String m_text_conclusionString;
    private Reader.CaptureResult cap_result = null;

    protected void CheckDevice() {
        try {
            m_reader.Open(Reader.Priority.EXCLUSIVE);
            Reader.Capabilities cap = m_reader.GetCapabilities();
            m_reader.Close();
            startFingerPrintLisener();
        } catch (UareUException e1) {
            displayReaderNotFound();
        }
    }

    protected void launchGetReader() {
        Intent i = new Intent(FingerPrint.this, GetReaderActivity.class);
        i.putExtra("device_name", m_deviceName);
        startActivityForResult(i, 1);
    }

    private void initializeActivity() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        fingerPrint = (ImageView) findViewById(R.id.fingerPrint);
        fingerPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), FingerPrint.class));
            }
        });
        m_bitmap = Globals.GetLastBitmap();
        if (m_bitmap == null)
            m_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.black);
        fingerPrint.setImageBitmap(m_bitmap);


    }

    public void UpdateGUI() {
        //  Toast.makeText(this, "hehe", Toast.LENGTH_SHORT).show();
        fingerPrint.setImageBitmap(m_bitmap);
        sFingerPrint = getEncoded64ImageStringFromBitmap(m_bitmap);
        fingerPrint.invalidate();

    }

    public void startFingerPrintLisener() {
        initializeActivity();
        // initiliaze dp sdk
        try {
            Context applContext = getApplicationContext();
            m_reader = Globals.getInstance().getReader(m_deviceName, applContext);
            m_reader.Open(Reader.Priority.EXCLUSIVE);
            m_DPI = Globals.GetFirstDPI(m_reader);
        } catch (Exception e) {
            Log.w("DLVerification", "error during init of reader");
            m_deviceName = "";
            onBackPressed();
            return;
        }

        // loop capture on a separate thread to avoid freezing the UI
        //循环捕获在一个单独的线程来避免冻结UI
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    m_reset = false;
                    while (!m_reset) {
                        cap_result = m_reader.Capture(Fid.Format.ANSI_381_2004, Globals.DefaultImageProcessing, m_DPI, -1);
                        // an error occurred
                        if (cap_result == null || cap_result.image == null) continue;
                        // save bitmap image locally
                        m_bitmap = Globals.GetBitmapFromRaw(cap_result.image.getViews()[0].getImageData(), cap_result.image.getViews()[0].getWidth(), cap_result.image.getViews()[0].getHeight());
                        m_text_conclusionString = Globals.QualityToString(cap_result);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UpdateGUI();
                            }
                        });
                    }
                } catch (Exception e) {
                    if (!m_reset) {
                        Log.w("DLVerification", "error during capture: " + e.toString());
                        m_deviceName = "";
                        onBackPressed();
                    }
                }
            }
        }).start();
    }

    private void switchUsb(String s) {
        File file = new File("sys/class/misc/hwoper/usb_route");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            writer.write(s);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void displayReaderNotFound() {
        if (!isFinishing()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FingerPrint.this);
            alertDialogBuilder.setTitle("Reader Not Found");
            alertDialogBuilder.setMessage("Plug in a reader and try again.").setCancelable(false).setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //call method to set up device communication
                            CheckDevice();
                        }
                    } else {
                        //Reader not started
                    }
                }
            }
        }
    };

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        Log.i("# imgString = ", imgString);

        return imgString;
    }

    public void stopFingerPrint() {
        try {
            m_reset = true;
            try {
                //停止获取
                m_reader.CancelCapture();
            } catch (Exception e) {
            }
            m_reader.Close();

        } catch (Exception e) {
            Log.w("UareUSampleJava", "error during reader shutdown");
        }

        Intent i = new Intent();
        i.putExtra("device_name", m_deviceName);
        setResult(Activity.RESULT_OK, i);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        stopFingerPrint();
    }

    @Override
    public void onStop() {
        // stopFingerPrint();
        // reset you to initial state when activity stops
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     try {
         if (data == null) {
             displayReaderNotFound();
             return;
         }

         Globals.ClearLastBitmap();
         m_deviceName = (String) data.getExtras().get("device_name");

         switch (requestCode) {
             case GENERAL_ACTIVITY_RESULT:

                 if ((m_deviceName != null) && !m_deviceName.isEmpty()) {

                     try {
                         Context applContext = getApplicationContext();
                         m_reader = Globals.getInstance().getReader(m_deviceName, applContext);

                         {
                             PendingIntent mPermissionIntent;
                             mPermissionIntent = PendingIntent.getBroadcast(applContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
                             IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                             applContext.registerReceiver(mUsbReceiver, filter);

                             if (DPFPDDUsbHost.DPFPDDUsbCheckAndRequestPermissions(applContext, mPermissionIntent, m_deviceName)) {
                                 CheckDevice();
                             }
                         }
                     } catch (UareUException e1) {
                         displayReaderNotFound();
                     } catch (DPFPDDUsbException e) {
                         displayReaderNotFound();
                     }

                 } else {
                     displayReaderNotFound();
                 }
                 break;
         }

         if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
             Bitmap bitmap = (Bitmap) data.getExtras().get("data");
             Bitmap croppedBmp = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(),bitmap.getHeight()-42);
             photo.setImageBitmap(croppedBmp);
             sPhoto = getEncoded64ImageStringFromBitmap(croppedBmp);
         }

     }catch (NullPointerException ex){

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
