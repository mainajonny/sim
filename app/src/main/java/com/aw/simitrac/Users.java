package com.aw.simitrac;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.aw.simitrac.adapter.AdapterUser;
import com.aw.simitrac.api.API;
import com.aw.simitrac.dashboard.Main;
import com.aw.simitrac.model.user.UserResponse;
import com.aw.simitrac.utils.Util;

import java.util.HashMap;

public class Users extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    String userType="STUDENT";
    EditText editSearch;
    AdapterUser adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(getApplicationContext(), "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                Toast.makeText(getApplicationContext(), "on Swiped ", Toast.LENGTH_SHORT).show();
                //Remove swiped item from list and notify the RecyclerView
                int position = viewHolder.getAdapterPosition();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        progressBar = (ProgressBar) findViewById(R.id.progress);
        editSearch =(EditText) findViewById(R.id.editSearch);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getStaff(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void getStaff(String keyword) {

        progressBar.setVisibility(View.VISIBLE);
        HashMap<String,String> url_maps = new HashMap<String, String>();
        url_maps.put("function","getAllUsers");
        url_maps.put("keyword",keyword);
        url_maps.put("userType",userType);
        url_maps.put("schoolCode", Util.MySharedPreference.getValue(getApplicationContext(),"schoolCode"));

        API.POST(getApplicationContext(), url_maps, new API.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                progressBar.setVisibility(View.GONE);

                UserResponse response = API.mGson.fromJson(result, UserResponse.class);

                if(response.getSuccess()){

                    adapter = new AdapterUser(getApplicationContext(), response.getData().getUSERS());
                    adapter.notifyDataSetChanged();
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerView.setAdapter(adapter);
                    recyclerView.setHasFixedSize(false);
                    recyclerView.setVisibility(View.VISIBLE);


                }else {
                    Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                    recyclerView.setAdapter(null);
                }
            }
        });
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_staff:
                if (checked)
                    userType="STAFF";
                    getStaff("");
                    break;
            case R.id.radio_student:
                if (checked)
                    userType="STUDENT";
                    getStaff("");
                    break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(), EventAttendance.class));
    }
}
