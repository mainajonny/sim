package com.aw.simitrac.pos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aw.simitrac.R;
import com.aw.simitrac.utils.Util;
import com.squareup.picasso.Picasso;

public class PaymentTrackingPos extends AppCompatActivity {

    ImageView image;
    TextView name, group,tvPrinter;


    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_tracking_pos);
        progress =(ProgressBar) findViewById(R.id.progress);


        image = (ImageView) findViewById(R.id.image);
        tvPrinter =(TextView) findViewById(R.id.tvPrinter);

        name =(TextView) findViewById(R.id.name);
        group =(TextView)  findViewById(R.id.group);

        name.setText(Util.MySharedPreference.getValue(getApplicationContext(),"name"));
        group.setText(Util.MySharedPreference.getValue(getApplicationContext(),"userTypeCode"));
        Picasso.get().load("https://school.kodinet.net/api/uploads/"+ Util.MySharedPreference.getValue(getApplicationContext(),"photo")).into(image);


    }
}
