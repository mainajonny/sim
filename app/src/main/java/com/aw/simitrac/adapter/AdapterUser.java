package com.aw.simitrac.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aw.simitrac.R;
import com.aw.simitrac.WriteTagActivity;
import com.aw.simitrac.api.API;
import com.aw.simitrac.model.user.USER;
import com.aw.simitrac.pos.PhotoFingerPrint;
import com.aw.simitrac.pos.WriteTagActivitySomanet;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Alex Boey on 8/1/2016.
 */
public class AdapterUser extends RecyclerView.Adapter<AdapterUser.ViewHolder> {



    private Context context;

    private List<USER> mList = new ArrayList<>();


    public AdapterUser(Context context , List<USER> mList){
        this.context = context;
        this.mList = mList;
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        TextView  name,type;
        LinearLayout layout;
        CircleImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            type =(TextView)itemView.findViewById(R.id.userTypeCode);
            name = (TextView) itemView.findViewById(R.id.name);
            layout = (LinearLayout) itemView.findViewById(R.id.layout);
            imageView =(CircleImageView) itemView.findViewById(R.id.image);

        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user,parent , false);

        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final USER list = mList.get(position);

        if ((position % 2) == 0) {
            // number is even
            holder.layout.setBackgroundColor(Color.parseColor("#bfd9ff"));
        }

        Picasso.get().load("https://shuleh.com/api/uploads/"+list.getPhoto()).into(holder.imageView);


        holder.name.setText(list.getName());
        holder.type.setText(list.getUserTypeCode());

        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent= new Intent(context, PhotoFingerPrint.class);/*POS*/
                intent.putExtra("userCode",list.getUserCode());
                intent.putExtra("userType",list.getUserTypeCode());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                return false;
            }
        });

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(context, WriteTagActivity.class);//FAMOCO
                //Intent intent= new Intent(context, WriteTagActivitySomanet.class);/*POS*/
                intent.putExtra("userCode",list.getUserCode());
                intent.putExtra("userType",list.getUserTypeCode());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
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
