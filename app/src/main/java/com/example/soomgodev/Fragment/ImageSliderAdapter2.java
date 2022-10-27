package com.example.soomgodev.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.soomgodev.R;

import java.util.ArrayList;

public class ImageSliderAdapter2 extends RecyclerView.Adapter<ImageSliderAdapter2.MyViewHolder> {
    private Context context;
    private ArrayList<ImageData> imageArrayList;

    public ImageSliderAdapter2(Context context, ArrayList<ImageData> imageArrayList) {
        this.context = context;
        this.imageArrayList = imageArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slider, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bindSliderImage(imageArrayList.get(position).getImageAddress());
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("test" , "ImageSliderAdapter를 클릭했습니다");
                Log.i("test" , "sliderImage[position] = " + imageArrayList.get(position).getImageAddress());
                Log.i("test" , "sliderImage[position] = " + imageArrayList.get(position).getImageDetail());
                Intent intent  = new Intent(context, ImageSliderWebView.class);
                intent.putExtra("imageDetail", imageArrayList.get(position).getImageDetail());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return imageArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageSlider);
        }

        public void bindSliderImage(String imageURL) {
            Glide.with(context)
                    .load(imageURL)
                    .into(mImageView);
        }
    }
}