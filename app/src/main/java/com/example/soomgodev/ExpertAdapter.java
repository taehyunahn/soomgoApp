package com.example.soomgodev;

import static com.example.soomgodev.StaticVariable.serverAddress;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.soomgodev.Fragment.ExpertProfileActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpertAdapter extends RecyclerView.Adapter<ExpertAdapter.ViewHolder> {

    List<ExpertData> expertDataList;
    Context context;

    public ExpertAdapter(List<ExpertData> expertDataList, Context context) {
        this.expertDataList = expertDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.textView.setText(expertDataList.get(position).getExpertName());

        if(expertDataList.get(position).getExpertIntro() == null) {
            holder.textView2.setText("한줄소개 미작성");
        } else {
            holder.textView2.setText(expertDataList.get(position).getExpertIntro());
        }

        String reviewAverage = expertDataList.get(position).getReviewAverage();
        if(reviewAverage == null) {
            holder.tv_reviewAverage.setText("리뷰 없음");
        } else {
            holder.tv_reviewAverage.setText(reviewAverage+"점");
        }

        holder.tv_reviewCount.setText("("+String.valueOf(expertDataList.get(position).getReviewCount())+"건)");
        holder.tv_hireCount.setText(String.valueOf(expertDataList.get(position).getHireCount())+"회 고용");
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
//                intent.putExtra("userProfileImage", expertDataList.get(position).getUserProfileImage());


                intent.putExtra("reviewAverage", expertDataList.get(position).getReviewAverage());
                intent.putExtra("reviewCount", expertDataList.get(position).getReviewCount());
                intent.putExtra("hireCount", expertDataList.get(position).getHireCount());
                intent.putExtra("selectedExpertId", expertDataList.get(position).getExpertId());
                context.startActivity(intent);
            }
        });

        // 프로필 사진 표시 (세 가지 case)
        // case 1. 카카오아이디 최초 로그인 경우, https: 포함 여부 -> glide 사용 (단순 사용)

        Log.i("ExpertAdapter", "expertDataList.get(position).getExperProfileImage() = " + expertDataList.get(position).getExpertProfileImage());

        if (expertDataList.get(position).getExpertProfileImage().equals("0")){
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.ic_person)
                    .centerCrop()
                    .into(holder.imageView);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(expertDataList.get(position).getExpertProfileImage())
                    .centerCrop()
                    .into(holder.imageView);
        }

//        if (expertDataList.get(position).getUserProfileImage().equals("0")){
//            Glide.with(holder.itemView.getContext())
//                    .load(R.drawable.ic_person)
//                    .centerCrop()
//                    .into(holder.imageView);
//        } else {
//            Glide.with(holder.itemView.getContext())
//                    .load(expertDataList.get(position).getUserProfileImage())
//                    .centerCrop()
//                    .into(holder.imageView);
//        }

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
        TextView textView;
        TextView textView2;
        ImageView imageView;
        CardView parentLayout;
        TextView tv_reviewAverage, tv_reviewCount, tv_hireCount;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            textView2 = itemView.findViewById(R.id.textView2);
            imageView = itemView.findViewById(R.id.imageView);
            parentLayout = itemView.findViewById(R.id.parentLayout);

            tv_reviewAverage = itemView.findViewById(R.id.tv_reviewAverage);
            tv_reviewCount = itemView.findViewById(R.id.tv_reviewCount);
            tv_hireCount = itemView.findViewById(R.id.tv_hireCount);


        }

    }




}

