package com.aw.simitrac.pos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aw.simitrac.R;
import com.aw.simitrac.Users;
import com.aw.simitrac.api.API;
import com.aw.simitrac.model.student.StudentReposne;
import com.aw.simitrac.utils.Util;
import com.speedata.r6lib.IMifareManager;
import com.speedata.r6lib.R6Manager;

import java.util.HashMap;
import java.util.Map;

import static com.speedata.r6lib.R6Manager.CardType.MIFARE;

public class VisitTackingPosRFID extends Activity {


    //NFC
    private NfcAdapter nfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;

    ProgressBar progress;


    public String CardSN = "";

    Intent intent;

    ImageView close;

    public  void validate(String value){
        goToServer = false;
        progress.setVisibility(View.VISIBLE);
        final Map<String,String> params = new HashMap<String, String>();
        params.put("function","getUserRFID");
        params.put("RFID",value);
        API.POST(getApplicationContext(), params, new API.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                progress.setVisibility(View.GONE);
                StudentReposne response = API.mGson.fromJson(result, StudentReposne.class);

                if(response.getSuccess()){
                   // Toast.makeText(PermissionRFIDSomanet.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                    Util.MySharedPreference.save(getApplicationContext(),"userCode",response.getData().getStudent().getUserCode());
                    Util.MySharedPreference.save(getApplicationContext(),"name",response.getData().getStudent().getName());
                    Util.MySharedPreference.save(getApplicationContext(),"userTypeCode",response.getData().getStudent().getUserTypeCode());
                    Util.MySharedPreference.save(getApplicationContext(),"photo",response.getData().getStudent().getPhoto());
                    startActivity(new Intent(getApplicationContext(),VisitTrackingPos.class));
                    goToServer = true;
                  //  finish();

                }else {
                    Toast.makeText(VisitTackingPosRFID.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                    //  Toast.makeText(getActivity(), noteDePerceptionResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                    final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            r.stop();
                            goToServer = true;
                        }
                    }, 2000);
                }


            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writetag);
        intent = getIntent();

        progress = (ProgressBar) findViewById(R.id.progress);

        close =(ImageView) findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Users.class));
                finish();
            }
        });



        //RFID
        InitializeRFID();

    }

    /*RFID */
    private IMifareManager dev;
    boolean goToServer = true;
    public void InitializeRFID(){
        dev = R6Manager.getMifareInstance(MIFARE);
        if (dev.InitDev() != 0) {
            Toast.makeText(getApplicationContext(), "Failed to init dev", Toast.LENGTH_SHORT).show();
            return;
        }

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                byte[] ID = dev.SearchCard();
                if (ID == null) {
                    // Toast.makeText(DLVerification.this, "Card not found", Toast.LENGTH_SHORT).show();
                } else {
                    String IDString = new String();
                    for (byte a : ID) {
                        IDString += String.format("%02X", a);
                    }
                    //  Toast.makeText(DLVerification.this, IDString, Toast.LENGTH_SHORT).show();
                    //   Toast.makeText(DLVerification.this, "RFID Detected", Toast.LENGTH_SHORT).show();
                    if (goToServer) {
                        validate(IDString);
                    }


                }
                handler.postDelayed(this, 0);
            }
        };

        handler.postDelayed(runnable, 1000);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        dev.ReleaseDev();
        super.onBackPressed();
        android.os.Process.killProcess(android.os.Process.myPid());
        finish();
        finishAffinity();
        finishAndRemoveTask ();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDestroy() {
        dev.ReleaseDev();
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
        finish();
        finishAffinity();
        finishAndRemoveTask ();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onPause() {
        dev.ReleaseDev();
        super.onPause();
        android.os.Process.killProcess(android.os.Process.myPid());
        finish();
        finishAffinity();
        finishAndRemoveTask ();
    }
}
