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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.soomgodev.Fragment.ExpertProfileActivity;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    List<Photo> photoList;
    Context context;
//    ArrayList<Person> items = new ArrayList<Person>();

    public PhotoAdapter(List<Photo> photoList, Context context) {
        this.photoList = photoList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //각각의 아이템을 위한 레이아웃을 인플레이션 한 다음에, 뷰 홀드에 넣어서 리턴해준다
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        View itemView = inflater.inflate(R.layout.person_item, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        //우리가 정의한 listener 객체
//        return new ViewHolder(itemView, listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //위에서 만들었던 ViewHolder가 재사용된다.
        //데이터만 싹 바꿔준다
//        Person item = items.get(position);
//        holder.setItem(item);
        holder.iv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context.getApplicationContext(), "삭제 버튼을 눌렀습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        
        holder.iv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, ExpertProfileActivity.class);
//                intent.putExtra("id", presidentList.get(position).getId());

//                Log.i("personAdapter", tagIntentInput + "reviewAverage = " + photoList.get(position).getReviewAverage());
//                Log.i("personAdapter", tagIntentInput + "reviewCount = " + photoList.get(position).getReviewCount());
//
//                intent.putExtra("expertName", photoList.get(position).getExpertName());
//                intent.putExtra("expertIntro", photoList.get(position).getExpertIntro());
//                context.startActivity(intent);
            }
        });

        // 프로필 사진 표시 (세 가지 case)
        // case 1. 카카오아이디 최초 로그인 경우, https: 포함 여부 -> glide 사용 (단순 사용)

        if (photoList.get(position).getPhotoAddress().contains("https:")) {
            Glide.with(holder.itemView.getContext())
                    .load(photoList.get(position).getPhotoAddress())
                    .centerCrop()
                    .into(holder.iv_photo);

            // case 2. 앱 내부에서 사진 변경하여 서버에 jpeg 형식으로 저장한 경우, https: 포함 여부 -> glide 사용 (서버 IP 주소 포함)
        } else if (photoList.get(position).getPhotoAddress().contains("jpeg")) {
            Glide.with(holder.itemView.getContext())
                    .load("http://54.180.133.35/image/" + photoList.get(position).getPhotoAddress())
                    .centerCrop()
                    .into(holder.iv_photo);
            // case 3. 이메일 로그인 했으며, 최초 등록한 이미지가 없는 경우(sharedPreference null), 아무것도 작동하지 않도록 함 - 기본 이미지
        } else if (photoList.get(position).getPhotoAddress().equals(null)) {
        }


    }

    @Override
//    public int getItemCount() {
//        return items.size();
//    }
    public int getItemCount() {
        return photoList.size();
    }


    //viewHolder : 하나의 아아템이 보여질 때, 레이아웃(뷰)을 담아놓을 객체 -> 필요하면 재사용
    //하나의 아이템에 대한 조작

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //viewHolder의 view 안에 글자가 표시되도록 textView 2개를 추가되도록 할 것이다
        
        ImageView iv_remove, iv_photo;
        CardView parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            
//            iv_remove = itemView.findViewById(R.id.iv_remove);
            iv_photo = itemView.findViewById(R.id.iv_photo);
            parentLayout = itemView.findViewById(R.id.parentLayout);


        }

        }
    }

