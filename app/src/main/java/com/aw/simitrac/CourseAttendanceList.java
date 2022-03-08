package com.aw.simitrac;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aw.simitrac.adapter.AdapterCourseAttendance;
import com.aw.simitrac.api.API;
import com.aw.simitrac.model.courseAttendance.CourseAttendanceResponse;
import com.aw.simitrac.utils.Util;

import java.util.HashMap;

public class CourseAttendanceList extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    AdapterCourseAttendance adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_attandance);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

     /*   ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

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
                recyclerView.getAdapter().notifyItemRemoved(position);
                recyclerView.getAdapter().notifyItemRangeChanged(position, peopleListUser.size());

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);*/

        progressBar = (ProgressBar) findViewById(R.id.progress);

        getCourseAttendance();

    }

    private void getCourseAttendance() {

        progressBar.setVisibility(View.VISIBLE);
        HashMap<String,String> url_maps = new HashMap<String, String>();
        url_maps.put("function","getCourseAttendance");
        url_maps.put("courseCode",Util.MySharedPreference.getValue(getApplicationContext(),"courseCode"));
        url_maps.put("groupCode", Util.MySharedPreference.getValue(getApplicationContext(),"groupCode"));
        url_maps.put("schoolCode", Util.MySharedPreference.getValue(getApplicationContext(),"schoolCode"));

        API.POST(getApplicationContext(), url_maps, new API.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                progressBar.setVisibility(View.GONE);

                CourseAttendanceResponse response = API.mGson.fromJson(result, CourseAttendanceResponse.class);

                if(response.getSuccess()){

                    adapter = new AdapterCourseAttendance(getApplicationContext(), response.getData().getCourseAttendance());
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

}
