package com.aw.simitrac.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.aw.simitrac.R;

public class Util {
    public static class MySharedPreference {

        public static void save(Context context , String key , String value){
            SharedPreferences prefs= context.getSharedPreferences("com.aw.pref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor= prefs.edit();
            editor.putString(key,value);
            editor.apply();
        }
        public static String getValue(Context context , String key){
            //if 1st time , register the user
            SharedPreferences prefs = context.getSharedPreferences("com.aw.pref", Context.MODE_PRIVATE);
            return prefs.getString(key , "");
        }
    }


    public static void alert(Context context,String message){
        AlertDialog.Builder alertadd = new AlertDialog.Builder(
                context);

        LayoutInflater factory = LayoutInflater.from(context);
        final View view = factory.inflate(R.layout.alert, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(message);
        alertadd.setView(view);
        alertadd.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //   Toast.makeText(context, "Paying later", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        alertadd.show();
    }

}
