package com.aw.simitrac.dashboard;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aw.simitrac.R;
import com.aw.simitrac.utils.Util;
import com.aw.simitrac.api.API;
import com.aw.simitrac.model.checking.CheckingResponse;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.speedata.r6lib.IMifareManager;
import com.speedata.r6lib.R6Manager;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.speedata.r6lib.R6Manager.CardType.MIFARE;

public class ClockingsSomaNet extends AppCompatActivity {


    TextView tvTime, tvDate,tvStatus,tvName, type,tvStatusDenied;
    LinearLayout layoutProfile,layoutHome,layoutDenied;

    ProgressBar progress;
    Boolean search =true;
    CircleImageView image;
    Bitmap decodedByte;
    Gson mGson;

    public String CardSN = "";

    public  void validate(String value){
        goToServer =false;
        search = false;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                search = true;
            }
        }, 10000);
        progress.setVisibility(View.VISIBLE);
        tvStatus.setText("Verifying...");
        final Map<String,String> params = new HashMap<String, String>();
        params.put("function","RFIDClocking");
        params.put("schoolCode", Util.MySharedPreference.getValue(getApplicationContext(),"schoolCode"));
        params.put("activityCode", Util.MySharedPreference.getValue(getApplicationContext(),"activityCode"));
        params.put("RFID", value);
        API.POST(getApplicationContext(), params, new API.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                progress.setVisibility(View.GONE);
                mGson = new GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                        .setPrettyPrinting()
                        .setLenient()
                        .serializeNulls()
                        .create();


                CheckingResponse checkingResponse = mGson.fromJson(result, CheckingResponse.class);

                if(checkingResponse.getSuccess()){
                    layoutProfile.setVisibility(View.VISIBLE);
                    layoutHome.setVisibility(View.GONE);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            layoutProfile.setVisibility(View.GONE);
                            layoutHome.setVisibility(View.VISIBLE);
                            goToServer = true;
                        }
                    }, 2500);


                    Picasso.get().load(checkingResponse.getData().getURL()+checkingResponse.getData().getUser().getPhoto()).into(image);
                    tvName.setText(checkingResponse.getData().getUser().getName());
                    type.setText(checkingResponse.getData().getUser().getUserTypeCode());

                    playThankYou();



                }else {
                    tvName.setText(checkingResponse.getMessage());
                    //  Toast.makeText(getActivity(), noteDePerceptionResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    playDenied();
                }


            }
        });
    }

    public  void playThankYou(){

        final MediaPlayer player = MediaPlayer.create(this,  R.raw.thank);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);
        player.start();
        tvStatus.setText("Thank You");

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                player.stop();
                tvStatus.setText("Welcome");
            }
        }, 900);


    }
    public  void playDenied(){

        final MediaPlayer  player = MediaPlayer.create(this,  R.raw.access);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);
        player.start();
        layoutDenied.setVisibility(View.VISIBLE);
        layoutHome.setVisibility(View.GONE);
        tvStatus.setText("Access Denied");

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                player.stop();
                layoutDenied.setVisibility(View.GONE);
                layoutHome.setVisibility(View.VISIBLE);
                tvStatus.setText("Welcome");
                goToServer = true;

            }
        }, 1200);


    }

    String strDate;

    public static String getCurrentMonth () {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM", Locale.US);

        return month_date.format(cal.getTime());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_clockings);

        progress =(ProgressBar) findViewById(R.id.progress);
        image =(CircleImageView) findViewById(R.id.image);

        layoutDenied =(LinearLayout) findViewById(R.id.layoutDenied);
        tvDate =(TextView) findViewById(R.id.tvDate);
        tvTime =(TextView) findViewById(R.id.tvTime);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 20 seconds
                handler.postDelayed(this, 1000);
                // Toast.makeText(Clockings.this, "s", Toast.LENGTH_SHORT).show();
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                strDate = sdf.format(c.getTime());
                tvTime.setText(strDate);
            }
        }, 0);  //the time is in miliseconds

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);



        String day = new SimpleDateFormat("d", Locale.getDefault()).format(new Date());

        tvDate.setText(dayOfTheWeek+", "+getCurrentMonth()+" "+day);



        tvName =(TextView) findViewById(R.id.tvName);
        type =(TextView) findViewById(R.id.type);

        tvStatus =(TextView) findViewById(R.id.tvStatus);

        layoutHome =(LinearLayout) findViewById(R.id.layoutHome);
        layoutProfile =(LinearLayout) findViewById(R.id.layoutProfile);

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
