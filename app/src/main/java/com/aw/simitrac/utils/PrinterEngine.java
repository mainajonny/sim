package com.aw.simitrac.utils;

import android.annotation.SuppressLint;
import android.serialport.DeviceControl;
import android.util.Log;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.aw.simitrac.R;
import com.speedata.libutils.ConfigUtils;
import com.speedata.libutils.ReadBean;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PrinterEngine {

    public static void sendToPinter(String resultTXT){
        connect();
        //  mContext.printSettings();
      /*  byte[] gbks = null;
        try {
            gbks= resultTXT.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        context.getObject().ASCII_PrintBuffer(context.getState(),gbks,gbks.length);*/
    }



    public static int state;
    public Button con;
    public Spinner com;
    public TextView version;
    public static ApplicationContext context;
    public static boolean mBconnect = false;


    private static ReadBean mRead;
    private static DeviceControl deviceControl;


    public static void connect() {
        context = new ApplicationContext();
        context.setObject();

        modelJudgmen();
        if (mBconnect) {
            context.getObject().CON_CloseDevices(context.getState());
            mBconnect = false;
        } else {

            System.out.println("----RG---CON_ConnectDevices");

            if (state > 0) {
                Log.i("# connect","success");
               /* Toast.makeText(getApplicationContext(),"success",
                        Toast.LENGTH_SHORT).show();
*/
                mBconnect = true;
              /*  Intent intent = new Intent(ConnectAvtivity.this,
                        PrintModeActivity.class);
                startActivity(intent);*/
                context.setState(state);
                //  context.setName("RG-E48");
            } else {
                /*Toast.makeText(getApplicationContext(),"Failed",
                        Toast.LENGTH_SHORT).show();*/
                Log.i("# connect","Failed");
                mBconnect = false;
            }
        }
    }
    private static void modelJudgmen() {
        mRead = ConfigUtils.readConfig(context);
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
    public void printPic(){

        @SuppressLint("ResourceType") final InputStream bitmap = context.getResources().openRawResource(
                R.mipmap.ic_launcher);

        context.getObject().CON_PageStart(context.getState(), true,
                Integer.parseInt("300"),
                Integer.parseInt("155"));
       /* context.getObject().ASCII_CtrlReset(context.getState());
        context.getObject().DRAW_SetFillMode(false);
        context.getObject().DRAW_SetLineWidth(4);*/


        context.getObject().DRAW_PrintPicture(context.getState(),
                bitmap, 1, 5, 268, 176);


        context.getObject().CON_PageEnd(context.getState(),
                context.getPrintway());

    }
}
