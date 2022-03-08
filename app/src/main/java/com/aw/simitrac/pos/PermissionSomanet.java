package com.aw.simitrac.pos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.serialport.DeviceControl;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.aw.simitrac.R;
import com.aw.simitrac.utils.Util;
import com.aw.simitrac.utils.ApplicationContext;
import com.speedata.libutils.ConfigUtils;
import com.speedata.libutils.ReadBean;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class PermissionSomanet extends AppCompatActivity {

    ImageView image;
    TextView name, group,tvPrinter;
    EditText edReason, edDestination, edObservation, edLeaving_date, edLeaving_time, edReturning_date, edReturning_time;
    Button send;
    ProgressBar progress;
    Intent intent;
    ImageView close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        tvPrinter =(TextView) findViewById(R.id.tvPrinter);

        //Printer
        initUi();
        connect();

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
        Picasso.get().load("https://school.kodinet.net/api/uploads/"+ Util.MySharedPreference.getValue(getApplicationContext(),"photo")).into(image);

        edReason = (EditText) findViewById(R.id.edReason);
        edDestination = (EditText) findViewById(R.id.edDestination);
        edObservation = (EditText) findViewById(R.id.edObservation);
        edLeaving_date = (EditText) findViewById(R.id.edLeaving_date);
        edLeaving_time = (EditText) findViewById(R.id.edLeaving_time);
        edReturning_date = (EditText) findViewById(R.id.edReturning_date);
        edReturning_time = (EditText) findViewById(R.id.edReturning_time);


        name =(TextView) findViewById(R.id.name);
        group =(TextView)  findViewById(R.id.group);

        name.setText(Util.MySharedPreference.getValue(getApplicationContext(),"name"));
        group.setText(Util.MySharedPreference.getValue(getApplicationContext(),"userTypeCode"));

        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.GONE);
                PrintData("");
              /*  if (edReason.getText().toString().matches("") ||
                        edDestination.getText().toString().matches("") || edObservation.getText().toString().matches("") ||
                        edLeaving_date.getText().toString().matches("") || edLeaving_time.getText().toString().matches("") ||
                        edReturning_date.getText().toString().matches("") || edReturning_date.getText().toString().matches("") ||
                        edReturning_time.getText().toString().matches("")) {
                    Toast.makeText(PermissionSomanet.this, "Empty", Toast.LENGTH_SHORT).show();

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
                            PrintData("");
                        }
                    });

                }*/
            }
        });

    }

    public void PrintData(String result){
        Picasso.get().load("https://www.embuni.ac.ke/images/Logo/University-of-Embu-logo.png").into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {


                sendToPinter(
                        "     ST ELIZABETH HIGH SCHOOL\n" +
                                "         P.O BOX 2030303\n");
                printPic(bitmap);
                sendToPinter("\n"+
                        "        PERMISSION SLIP\n\n" +
                                "Name: "+ Util.MySharedPreference.getValue(PermissionSomanet.this,"name")+"\n" +
                                "Reason: "+edObservation.getText().toString()+"\n" +
                                "Destination: "+edDestination.getText().toString()+"\n" +
                                "Observation: "+edObservation.getText().toString()+"\n\n" +
                                "Leaving: 2/16/2019 8:27AM\n"+
                                "Return: 2/19/2019 11:30AM\n\n" +
                                "Parent: 07184019272\n\n\n\n");


            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });

    }

    /*Printer*/
    public int state;
    public Button con;
    public Spinner com;
    public TextView version;
    public ApplicationContext context;
    public boolean mBconnect = false;
    private ReadBean mRead;
    private DeviceControl deviceControl;
    private void initUi() {
        //mActivity = this;
        // 多个页面之间数据共享
        context = new ApplicationContext();
        context.setObject();
       /* version = (TextView) findViewById(R.id.text_version);
        version.setText("V " + context.getObject().CON_QueryVersion());*/
    }
    public void connect() {

        // 读取配置文件
        modelJudgmen();
        if (mBconnect) {
            context.getObject().CON_CloseDevices(context.getState());

            mBconnect = false;
        } else {

            System.out.println("----RG---CON_ConnectDevices");

            if (state > 0) {
                Toast.makeText(getApplicationContext(), "success",
                        Toast.LENGTH_SHORT).show();
                tvPrinter.setText("Printer Connected");
                tvPrinter.setTextColor(Color.GREEN);

                mBconnect = true;
               /* Intent intent = new Intent(ConnectAvtivity.this,
                        PrintModeActivity.class);*/

                context.setState(state);
                context.setName("RG-E48");
                //startActivity(intent);



            } else {
                Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
                mBconnect = false;
            }
        }
    }
    private void modelJudgmen() {
        mRead = ConfigUtils.readConfig(getApplicationContext());
        ReadBean.PrintBean print = mRead.getPrint();
        String powerType = print.getPowerType();
        int braut = print.getBraut();
        List<Integer> gpio = print.getGpio();
        String serialPort = print.getSerialPort();
        state = context.getObject().CON_ConnectDevices("RG-E487",
                serialPort + ":" + braut + ":1:1", 200);
        int[] intArray = new int[gpio.size()];
        for (int i = 0; i < gpio.size(); i++) {
            intArray[i] = gpio.get(i);
        }
        try {
            deviceControl = new DeviceControl(powerType,intArray);
            deviceControl.PowerOnDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            deviceControl.PowerOffDevice();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void printPic(Bitmap bitmap){

        context.getObject().CON_PageStart(context.getState(), true,
                Integer.parseInt("300"),
                Integer.parseInt("215"));

        context.getObject().DRAW_PrintPicture(context.getState(),
                bitmap, 80, 50, 268, 176);

        context.getObject().CON_PageEnd(context.getState(),
                context.getPrintway());

    }
    public void sendToPinter(String resultTXT){


        byte[] gbks = null;
        try {
            gbks= resultTXT.getBytes("UTF-8");
            context.getObject().ASCII_PrintBuffer(context.getState(),gbks,gbks.length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }
    String amountWords ="N/A";

}
