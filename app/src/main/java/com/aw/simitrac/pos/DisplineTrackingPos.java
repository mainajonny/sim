package com.aw.simitrac.pos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class DisplineTrackingPos extends AppCompatActivity {

    EditText    edFault,edMarks,edDescription;
    ImageView image;
    TextView name, group,tvPrinter;

    Button send;

    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displine_tracking_pos);

        progress =(ProgressBar) findViewById(R.id.progress);

        edFault =(EditText) findViewById(R.id.edFault);
        edMarks =(EditText) findViewById(R.id.edMarks);
        edDescription =(EditText) findViewById(R.id.edDescription);

        image = (ImageView) findViewById(R.id.image);
        tvPrinter =(TextView) findViewById(R.id.tvPrinter);

        name =(TextView) findViewById(R.id.name);
        group =(TextView)  findViewById(R.id.group);

        name.setText(Util.MySharedPreference.getValue(getApplicationContext(),"name"));
        group.setText(Util.MySharedPreference.getValue(getApplicationContext(),"userTypeCode"));
        Picasso.get().load("https://school.kodinet.net/api/uploads/"+ Util.MySharedPreference.getValue(getApplicationContext(),"photo")).into(image);

        send =(Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                edDescription.setText("");
                edMarks.setText("");
                edFault.setText("");
                Toast.makeText(DisplineTrackingPos.this, "Submitted", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
            }
        });


    }
}
