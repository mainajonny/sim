package com.aw.simitrac;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aw.simitrac.api.API;
import com.aw.simitrac.utils.Util;

import java.util.HashMap;

public class Permission extends AppCompatActivity {

    ImageView image;
    TextView name, group;
    EditText edReason, edDestination, edObservation, edLeaving_date, edLeaving_time, edReturning_date, edReturning_time;
    Button send;
    ProgressBar progress;
    Intent intent;
    ImageView close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        close =(ImageView) findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        intent = getIntent();
        progress = (ProgressBar) findViewById(R.id.progress);
        progress.setVisibility(View.GONE);

        image = (ImageView) findViewById(R.id.image);

        edReason = (EditText) findViewById(R.id.edReason);
        edDestination = (EditText) findViewById(R.id.edDestination);
        edObservation = (EditText) findViewById(R.id.edObservation);
        edLeaving_date = (EditText) findViewById(R.id.edLeaving_date);
        edLeaving_time = (EditText) findViewById(R.id.edLeaving_time);
        edReturning_date = (EditText) findViewById(R.id.edReturning_date);
        edReturning_time = (EditText) findViewById(R.id.edReturning_time);


        edLeaving_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Permission.this);
                DatePicker picker = new DatePicker(Permission.this);
                picker.setCalendarViewShown(false);

                builder.setTitle("Create Year");
                builder.setView(picker);
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("Set", null);
                builder.show();
            }
        });

        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edReason.getText().toString().matches("") ||
                        edDestination.getText().toString().matches("") || edObservation.getText().toString().matches("") ||
                        edLeaving_date.getText().toString().matches("") || edLeaving_time.getText().toString().matches("") ||
                        edReturning_date.getText().toString().matches("") || edReturning_date.getText().toString().matches("") ||
                        edReturning_time.getText().toString().matches("")) {
                    Toast.makeText(Permission.this, "Empty", Toast.LENGTH_SHORT).show();

                } else {
                    progress.setVisibility(View.VISIBLE);

                    HashMap<String, String> url_maps = new HashMap<String, String>();
                    url_maps.put("function", "addPermissionRequest");
                    url_maps.put("schoolCode", Util.MySharedPreference.getValue(getApplicationContext(), "schoolCode"));
                    url_maps.put("userCode", intent.getStringExtra("userCode"));
                    url_maps.put("destination", edDestination.getText().toString());
                    url_maps.put("observation", edObservation.getText().toString());
                    url_maps.put("leavingDate", edLeaving_date.getText().toString());
                    url_maps.put("leavingTime", edLeaving_time.getText().toString());
                    url_maps.put("returningDate", edReturning_date.getText().toString());
                    url_maps.put("returningTime", edReturning_time.getText().toString());

                    API.POST(getApplicationContext(), url_maps, new API.VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            progress.setVisibility(View.GONE);
                            Print();

                        }
                    });

                }
            }
        });
    }

    public  void Print(){

    }
}
