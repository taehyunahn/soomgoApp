package com.example.soomgodev;

import static com.example.soomgodev.StaticVariable.tagIntentInput;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.soomgodev.Fragment.ExpertProfileActivity;

import java.util.List;

public class ExpertAdapter2 extends RecyclerView.Adapter<ExpertAdapter2.ViewHolder> {

    List<ExpertData> expertDataList;
    Context context;

    public ExpertAdapter2(List<ExpertData> expertDataList, Context context) {
        this.expertDataList = expertDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_item2, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.tv_expertName.setText(expertDataList.get(position).getExpertName());
        holder.tv_expertMainService.setText(expertDataList.get(position).getExpertMainService());

//        holder.textView2.setText(expertDataList.get(position).getExpertIntro());
//        holder.tv_reviewAverage.setText(String.valueOf(expertDataList.get(position).getReviewAverage())+"점");
//        holder.tv_reviewCount.setText("("+String.valueOf(expertDataList.get(position).getReviewCount())+")");
//        holder.tv_hireCount.setText(String.valueOf(expertDataList.get(position).getHireCount())+"회 고용");

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ExpertProfileActivity.class);
//                intent.putExtra("id", presidentList.get(position).getId());

                Log.i("personAdapter", tagIntentInput + "reviewAverage = " + expertDataList.get(position).getReviewAverage());
                Log.i("personAdapter", tagIntentInput + "reviewCount = " + expertDataList.get(position).getReviewCount());
                Log.i("personAdapter", tagIntentInput + "hireCount = " + expertDataList.get(position).getHireCount());

                intent.putExtra("expertName", expertDataList.get(position).getExpertName());
                intent.putExtra("expertIntro", expertDataList.get(position).getExpertIntro());
                intent.putExtra("expertProfileImage", expertDataList.get(position).getExpertProfileImage());
                intent.putExtra("reviewAverage", expertDataList.get(position).getReviewAverage());
                intent.putExtra("reviewCount", expertDataList.get(position).getReviewCount());
                intent.putExtra("hireCount", expertDataList.get(position).getHireCount());
                intent.putExtra("selectedExpertId", expertDataList.get(position).getExpertId());
                context.startActivity(intent);

            }
        });

        if (expertDataList.get(position).getUserProfileImage().equals("0")){
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.ic_person)
                    .centerCrop()
                    .into(holder.imageView);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(expertDataList.get(position).getUserProfileImage())
                    .centerCrop()
                    .into(holder.imageView);
        }
    }

    @Override
//    public int getItemCount() {
//        return items.size();
//    }
    public int getItemCount() {
        return expertDataList.size();
    }


    //viewHolder : 하나의 아아템이 보여질 때, 레이아웃(뷰)을 담아놓을 객체 -> 필요하면 재사용
    //하나의 아이템에 대한 조작

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //viewHolder의 view 안에 글자가 표시되도록 textView 2개를 추가되도록 할 것이다
        TextView tv_expertName;
        TextView tv_expertMainService;
        ImageView imageView;
        CardView parentLayout;
        TextView tv_reviewAverage, tv_reviewCount, tv_hireCount;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_expertName = itemView.findViewById(R.id.tv_expertName);
            tv_expertMainService = itemView.findViewById(R.id.tv_expertMainService);
            imageView = itemView.findViewById(R.id.imageView);
            parentLayout = itemView.findViewById(R.id.parentLayout);

        }

        }
    }

