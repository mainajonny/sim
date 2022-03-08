package com.aw.simitrac;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aw.simitrac.api.API;
import com.aw.simitrac.model.checking.CheckingResponse;
import com.aw.simitrac.model.checking.User;
import com.aw.simitrac.model.response.Response;
import com.aw.simitrac.pos.WriteTagActivitySomanet;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class WriteTagActivity extends Activity {


    //NFC
    private NfcAdapter nfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;

    ProgressBar progress;


    public String CardSN = "";

    Intent intent;

    ImageView close;

    public  void validate(String value){
        progress.setVisibility(View.VISIBLE);
        final Map<String,String> params = new HashMap<String, String>();
        params.put("function","encodeCard");
        params.put("RFID",value);
        params.put("userCode", intent.getStringExtra("userCode"));
        params.put("userType", intent.getStringExtra("userType"));
        API.POST(getApplicationContext(), params, new API.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                progress.setVisibility(View.GONE);



                Response response = API.mGson.fromJson(result, Response.class);

                if(response.getSuccess()){
                    Toast.makeText(WriteTagActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                  //  finish();

                }else {
                    Toast.makeText(WriteTagActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
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
            Intent intent= new Intent(this, WriteTagActivitySomanet.class);
            startActivity(intent);
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

      //  Toast.makeText(this, cardstr, Toast.LENGTH_SHORT).show();
        validate(cardstr);

        CardSN = cardstr;

    }

    @Override
    public void onPause() {
        if (nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null)
            nfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), Users.class));
        finish();
    }
}
