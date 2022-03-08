package com.aw.simitrac.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aw.simitrac.CourseAttendanceList;
import com.aw.simitrac.R;
import com.aw.simitrac.api.API;
import com.aw.simitrac.model.course.CoursesResponse;
import com.aw.simitrac.model.course.CoursesResponseData;
import com.aw.simitrac.model.courseAttendance.CourseAttendance;
import com.aw.simitrac.model.user.USER;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Alex Boey on 8/1/2016.
 */
public class AdapterCourseAttendance extends RecyclerView.Adapter<AdapterCourseAttendance.ViewHolder>{



    private Context context;

    private List<CourseAttendance> mList = new ArrayList<>();


    public AdapterCourseAttendance(Context context , List<CourseAttendance> mList){
        this.context = context;
        this.mList = mList;
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        TextView  name,type;
        RelativeLayout layout;
        CircleImageView imageView;
        CheckBox checkbox;

        public ViewHolder(View itemView) {
            super(itemView);

            type =(TextView)itemView.findViewById(R.id.userTypeCode);
            name = (TextView) itemView.findViewById(R.id.name);
            layout = (RelativeLayout) itemView.findViewById(R.id.layout);
            imageView =(CircleImageView) itemView.findViewById(R.id.image);
            checkbox =(CheckBox) itemView.findViewById(R.id.checkbox);

        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attendance,parent , false);

        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final CourseAttendance list = mList.get(position);

        if ((position % 2) == 0) {
            // number is even
            holder.layout.setBackgroundColor(Color.parseColor("#bfd9ff"));
        }

        Picasso.get().load("https://school.kodinet.net/api/uploads/"+list.getStudent().getPhoto()).into(holder.imageView);


        holder.name.setText(list.getStudent().getName());
        holder.type.setText(list.getStudent().getUserTypeCode());
        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                HashMap<String,String> url_maps = new HashMap<String, String>();
                url_maps.put("function","updateCourseAttendance");
                url_maps.put("userCode",list.getStudent().getUserCode());

                API.POST(context, url_maps, new API.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                    }
                });
                removeAt(position);
                return false;
            }
        });
        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,String> url_maps = new HashMap<String, String>();
                url_maps.put("function","updateCourseAttendance");
                url_maps.put("userCode",list.getStudent().getUserCode());

                API.POST(context, url_maps, new API.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                    }
                });
            }
        });

        if(list.getAttendance().getStatus().matches("1")){
            holder.checkbox.setChecked(true);

        } if(list.getAttendance().getStatus().matches("2")){
            holder.checkbox.setChecked(false);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Intent intent= new Intent(context, WriteTagActivity.class);
                intent.putExtra("userCode",list.getUserCode());
                intent.putExtra("userType",list.getUserTypeCode());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);*/
             // removeAt(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void removeAt(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mList.size());
    }


}
