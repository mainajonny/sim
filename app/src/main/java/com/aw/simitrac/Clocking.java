package com.aw.simitrac;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aw.simitrac.api.API;
import com.aw.simitrac.dashboard.ClockingsSomaNet;
import com.aw.simitrac.dashboard.Main;
import com.aw.simitrac.model.checking.CheckingResponse;
import com.aw.simitrac.utils.Util;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.aw.simitrac.dashboard.ClockingsSomaNet.getCurrentMonth;

public class Clocking extends AppCompatActivity {

    private static final String TAG = Clocking.class.getSimpleName();
    private static final long MIN_UPDATE_INTERVAL_IN_MS = 5 * 1000;
    private static final float MIN_UPDATE_DISTANCE_IN_M = 1;

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;


    TextView tvTime, tvDate,tvStatus,tvName, type,tvStatusDenied,tvIMEI;
    LinearLayout layoutProfile,layoutHome,layoutDenied;

    ProgressBar progress;
    Boolean search =true, post=true;
    CircleImageView image;
    Bitmap decodedByte;
    Gson mGson;

    //NFC
    private NfcAdapter nfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;

    FrameLayout frm;


    public String CardSN = "",latLong="0,0",imei="";

    public  void validate(String value){
       Toast.makeText(this, latLong, Toast.LENGTH_SHORT).show();
       Toast.makeText(this, imei, Toast.LENGTH_SHORT).show();
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
        params.put("IMEI",imei);
        params.put("latLong",latLong);
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

            }
        }, 1200);


    }

    String strDate;

    public void updateLiveLocation(){

        post = false;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                post = true;
            }
        }, 10000);

        //send location to server
        final Map<String,String> params = new HashMap<String, String>();
        params.put("function","updateLive");
        params.put("schoolCode", Util.MySharedPreference.getValue(getApplicationContext(),"schoolCode"));
        params.put("IMEI",imei);
        params.put("latLong",latLong);

        API.POST(getApplicationContext(), params, new API.VolleyCallback() {
            @Override
            public void onSuccess(String result) {


            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_clockings);

        frm = (FrameLayout) findViewById(R.id.frm);
        frm.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startActivity(new Intent(getApplicationContext(),Main.class));
                return false;
            }
        });

        latLong = API.getValue(getApplicationContext(),"latLong");

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged: " + location);
                // Update location on a map, etc.
               Toast.makeText(Clocking.this, String.valueOf(location.getLatitude()+","+location.getLongitude()), Toast.LENGTH_SHORT).show();
                latLong =  String.valueOf(location.getLatitude()+","+location.getLongitude());
                API.save(getApplicationContext(),"latLong",latLong);

                if(post)
                    updateLiveLocation();

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };



        progress =(ProgressBar) findViewById(R.id.progress);
        image =(CircleImageView) findViewById(R.id.image);

        layoutDenied =(LinearLayout) findViewById(R.id.layoutDenied);
        tvDate =(TextView) findViewById(R.id.tvDate);
        tvTime =(TextView) findViewById(R.id.tvTime);
        tvIMEI =(TextView) findViewById(R.id.tvIMEI);


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


        //Card
        InitReadCard();

    }





    public void InitReadCard() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "Device does not support NFC!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!nfcAdapter.isEnabled()) {
            startActivity(new Intent(getApplicationContext(), ClockingsSomaNet.class));
            Toast.makeText(this, "Enable the NFC function in the system settings!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mFilters = new IntentFilter[]{
                new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)};
    }



    //NFC

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }

    private void processIntent(Intent intent) {
        byte[] sn = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        String cardstr =/*Integer.toString(count)+":"+*/
                Integer.toHexString(sn[0] & 0xFF).toUpperCase() +
                        Integer.toHexString(sn[1] & 0xFF).toUpperCase() +
                        Integer.toHexString(sn[2] & 0xFF).toUpperCase() +
                        Integer.toHexString(sn[3] & 0xFF).toUpperCase();


        //editText10.setText(cardstr);
        validate(cardstr);
        CardSN = cardstr;

    }

    @Override
    public void onPause() {
        if (nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this);
        // Stop location updates while the app is in background
        mLocationManager.removeUpdates(mLocationListener);
        super.onPause();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        // Retrieve last known location
        @SuppressLint("MissingPermission") Location lastKnownLocation = mLocationManager.getLastKnownLocation(
                LocationManager.GPS_PROVIDER);

        // Retrieve location every 10 seconds if the device moved more than 10
        // meters and notify the LocationListener
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                MIN_UPDATE_INTERVAL_IN_MS, MIN_UPDATE_DISTANCE_IN_M, mLocationListener);

        if (nfcAdapter != null)
            nfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, null);

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
         imei= telephonyManager.getDeviceId();
         tvIMEI.setText(imei);

           if(!Util.MySharedPreference.getValue(getApplicationContext(),"login").matches("true")){
               startActivity(new Intent(getApplicationContext(), Main.class));
               finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.encode:
                // do your code
                Toast.makeText(this, "Encode", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
