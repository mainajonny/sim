package com.aw.simitrac.dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aw.simitrac.Clocking;
import com.aw.simitrac.EventAttendance;
import com.aw.simitrac.pos.DisplineTackingPosRFID;
import com.aw.simitrac.pos.PaymentTackingPosRFID;
import com.aw.simitrac.pos.PermissionRFIDSomanet;
import com.aw.simitrac.R;
import com.aw.simitrac.pos.VisitTackingPosRFID;
import com.aw.simitrac.pos.VisitTrackingPos;
import com.aw.simitrac.utils.Util;
import com.aw.simitrac.api.API;
import com.aw.simitrac.model.login.LoginResponse;

import java.util.HashMap;

public class Main extends AppCompatActivity {

    Button buttonStaff, event,permission,despline,pay;

    ProgressBar progressBar;

    Button visit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar =(ProgressBar) findViewById(R.id.progress);

        pay =(Button) findViewById(R.id.pay);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Util.MySharedPreference.getValue(getApplicationContext(),"login").matches("true")){
                    login();
                }else {
                    startActivity(new Intent(getApplicationContext(), PaymentTackingPosRFID.class));

                }
            }
        });


        visit =(Button) findViewById(R.id.visit);
        visit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Util.MySharedPreference.getValue(getApplicationContext(),"login").matches("true")){
                    login();
                }else {
                    startActivity(new Intent(getApplicationContext(), VisitTackingPosRFID.class));

                }
            }
        });

        permission =(Button) findViewById(R.id.permission);
        permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Util.MySharedPreference.getValue(getApplicationContext(),"login").matches("true")){
                    login();
                }else {
                    startActivity(new Intent(getApplicationContext(), PermissionRFIDSomanet.class));

                }
            }
        });

        buttonStaff = (Button) findViewById(R.id.buttonStaff);
        buttonStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Util.MySharedPreference.getValue(getApplicationContext(),"login").matches("true")){
                    login();
                }else {
                    startActivity(new Intent(getApplicationContext(), Clocking.class));//FAMOCO
                  //startActivity(new Intent(getApplicationContext(), ClockingsSomaNet.class));//POS

                }
            }
        });

        event =(Button) findViewById(R.id.event);
        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Util.MySharedPreference.getValue(getApplicationContext(),"login").matches("true")){
                    login();
                }else {
                    startActivity(new Intent(getApplicationContext(),EventAttendance.class));
                }
            }
        });

        despline =(Button) findViewById(R.id.despline);
        despline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Util.MySharedPreference.getValue(getApplicationContext(),"login").matches("true")){
                    login();
                }else {
                    startActivity(new Intent(getApplicationContext(), DisplineTackingPosRFID.class));
                }
            }
        });


        if(!Util.MySharedPreference.getValue(getApplicationContext(),"login").matches("true")){
            login();
        }
    }

    public void login(){
        AlertDialog.Builder alertadd = new AlertDialog.Builder(
                Main.this);

        LayoutInflater factory = LayoutInflater.from(Main.this);
        final View view = factory.inflate(R.layout.alert, null);
        final EditText username = (EditText) view.findViewById(R.id.userName);
        final EditText password = (EditText) view.findViewById(R.id.pin);

        alertadd.setView(view);
        alertadd.setNegativeButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if(password.getText().toString().matches("") || username.getText().toString().matches("")){
                    Toast.makeText(Main.this, "Empty", Toast.LENGTH_SHORT).show();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    HashMap<String,String> url_maps = new HashMap<String, String>();
                    url_maps.put("function","login");
                    url_maps.put("username",username.getText().toString());
                    url_maps.put("password",password.getText().toString());
                    API.POST(getApplicationContext(), url_maps, new API.VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            LoginResponse loginResponse = API.mGson.fromJson(result, LoginResponse.class);
                            if(loginResponse.getSuccess()){
                                progressBar.setVisibility(View.GONE);
                                dialog.dismiss();

                                Util.MySharedPreference.save(getApplicationContext(),"schoolCode",loginResponse.data.getSchoolCode());
                                Util.MySharedPreference.save(getApplicationContext(),"login","true");

                            }else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(Main.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.dismiss();
                }


            }
        });
        alertadd.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
