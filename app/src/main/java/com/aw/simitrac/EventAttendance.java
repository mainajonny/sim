package com.aw.simitrac;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.aw.simitrac.api.API;
import com.aw.simitrac.dashboard.Main;
import com.aw.simitrac.model.course.CoursesResponse;
import com.aw.simitrac.model.course.CoursesResponseData;
import com.aw.simitrac.model.group.Group;
import com.aw.simitrac.model.group.GroupResponse;
import com.aw.simitrac.model.response.Response;
import com.aw.simitrac.model.units.Activity;
import com.aw.simitrac.model.units.UnitsResponse;
import com.aw.simitrac.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventAttendance extends AppCompatActivity {

    Spinner spinnerClass;
    ArrayList<String> arrayListClass = new ArrayList<>();
    ArrayAdapter<String> adaptersClass ;

    Spinner spinnerCourse;
    ArrayList<String> arrayListCourse = new ArrayList<>();
    ArrayAdapter<String> adaptersCourse ;

    ProgressBar progress;
    UnitsResponse unitsResponse;
    GroupResponse groupResponse;

    String activityCode="",groupCode="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_attendance);

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_course:
                if (checked)
                    course();
                    break;
            case R.id.signature:
                startActivity(new Intent(getApplicationContext(),Singature.class));
                    break;
            case R.id.radio_prep:
                if (checked)
                    // Ninjas rule
                break;

                case R.id.vehicles:
                if (checked)
                    configVehicle();
                break;

                case R.id.radio_users:
                if (checked)
                    startActivity(new Intent(getApplicationContext(),Users.class));
                    break;

                    case R.id.radio_logout:
                if (checked)
                    finish();
                        Util.MySharedPreference.save(getApplicationContext(),"login","false");
                    break;
        }
    }

    public void configVehicle(){
        final AlertDialog.Builder alertadd = new AlertDialog.Builder(
                EventAttendance.this);

        LayoutInflater factory = LayoutInflater.from(EventAttendance.this);
        final View view = factory.inflate(R.layout.config_vehicle, null);
        final EditText edNumberPlate = view.findViewById(R.id.edNumberPlate);
        final ProgressBar progress2 = view.findViewById(R.id.progress);

        alertadd.setView(view);
        alertadd.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {

                progress2.setVisibility(View.VISIBLE);

                TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                final Map<String,String> params = new HashMap<String, String>();
                params.put("function","addVehicles");
                params.put("schoolCode", Util.MySharedPreference.getValue(getApplicationContext(),"schoolCode"));
                params.put("IMEI",telephonyManager.getDeviceId());
                params.put("numberPlate",edNumberPlate.getText().toString().trim());

                API.POST(getApplicationContext(), params, new API.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        progress2.setVisibility(View.VISIBLE);

                        Response response = API.mGson.fromJson(result,Response.class);
                        if(response.getSuccess()){
                            edNumberPlate.setText("");
                            Toast.makeText(EventAttendance.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(EventAttendance.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        alertadd.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //   Toast.makeText(context, "Paying later", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        alertadd.show();

    }

    public void course(){
        AlertDialog.Builder alertadd = new AlertDialog.Builder(
                EventAttendance.this);

        LayoutInflater factory = LayoutInflater.from(EventAttendance.this);
        final View view = factory.inflate(R.layout.course, null);
        initUI(view);
        alertadd.setView(view);
        alertadd.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                generateCourseAttendance();

            }
        });
        alertadd.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //   Toast.makeText(context, "Paying later", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        alertadd.show();
        getCourses();
        getClasses();
    }

    public void initUI(View view){

        progress =(ProgressBar) view.findViewById(R.id.progress);
        //CLASS LIST
        spinnerClass = (Spinner) view.findViewById(R.id.spinnerClass);
        adaptersClass = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_dropdown_item, arrayListClass);
        adaptersClass.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                for (Group group: groupResponse.getData().getGroups()) {
                    if(group.getGroupName().matches(parent.getItemAtPosition(position).toString())){
                        groupCode = group.getGroupCode();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //Course LIST
        spinnerCourse = (Spinner) view.findViewById(R.id.spinnerCourse);
        adaptersCourse = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_dropdown_item, arrayListCourse);
        adaptersCourse.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinnerCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                for (Activity activity: unitsResponse.getData().getActivities()) {
                    if(activity.getActivityName().matches(parent.getItemAtPosition(position).toString())){
                       // activityCode = activity.getCourse().getCourseCode();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void getClasses(){
        progress.setVisibility(View.VISIBLE);
        final Map<String,String> params = new HashMap<String, String>();
        params.put("function","getGroup");
        params.put("schoolCode", Util.MySharedPreference.getValue(getApplicationContext(),"schoolCode"));

        API.POST(getApplicationContext(), params, new API.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                progress.setVisibility(View.GONE);

                groupResponse = API.mGson.fromJson(result,GroupResponse.class);
                if(groupResponse.getSuccess()){
                    for(Group group : groupResponse.getData().getGroups()){
                        arrayListClass.add(group.getGroupName());
                    }
                    spinnerClass.setAdapter(adaptersClass);

                }else {
                    Toast.makeText(EventAttendance.this, groupResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void getCourses(){
        progress.setVisibility(View.VISIBLE);
        final Map<String,String> params = new HashMap<String, String>();
        params.put("function","getActivity");
        params.put("schoolCode", Util.MySharedPreference.getValue(getApplicationContext(),"schoolCode"));

        API.POST(this, params, new API.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                progress.setVisibility(View.GONE);
                unitsResponse = API.mGson.fromJson(result, UnitsResponse.class);
                if(unitsResponse.getSuccess()){
                    for (Activity activity: unitsResponse.getData().getActivities()) {
                        arrayListCourse.add(activity.getActivityName());
                    }

                    spinnerCourse.setAdapter(adaptersCourse);
                }else {
                    Toast.makeText(getApplicationContext(),unitsResponse.getMessage(),Toast.LENGTH_LONG).show();
                }


            }
        });

    }

    public void generateCourseAttendance(){
        Util.MySharedPreference.save(getApplicationContext(),"activityCode",activityCode);
        Util.MySharedPreference.save(getApplicationContext(),"groupCode",groupCode);
        progress.setVisibility(View.VISIBLE);
        final Map<String,String> params = new HashMap<String, String>();
        params.put("function","generateCourseAttendance");
        params.put("groupCode",groupCode);
        params.put("activityCode",activityCode);
        params.put("schoolCode", Util.MySharedPreference.getValue(getApplicationContext(),"schoolCode"));

        API.POST(this, params, new API.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                Response response = API.mGson.fromJson(result,Response.class);
                if(response.getSuccess()){
                    startActivity(new Intent(getApplicationContext(),CourseAttendanceList.class));
                    Toast.makeText(EventAttendance.this, response.getMessage(), Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(EventAttendance.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(), Main.class));
    }
}
