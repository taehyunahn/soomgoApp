package ExpertUpdate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.soomgodev.R;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import Chat.ChatInterface;
import Chat.ChatRoomActivity;
import ChatRoomList.ChatRoomData;
import ConnectToServer.NetworkClient;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

// 고객 화면용
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    List<ChatRoomData> reviewList;
    Context context;


    public ReviewAdapter(List<ChatRoomData> reviewList, Context context) {
        this.reviewList = reviewList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.clientName.setText(reviewList.get(position).getReviewWriterName());
        holder.tv_reviewContents.setText(reviewList.get(position).getReviewContents());


//        holder.tv_reviewDate.setText(reviewList.get(position).getReviewDate());
        if(reviewList.get(position).getReviewDate() != null){
            String now_date = reviewList.get(position).getReviewDate();
            String now_date_short = now_date.substring(0, 10);  //날짜 변환 -> 시,분,초 제거
            holder.tv_reviewDate.setText(now_date_short);
        }


        holder.ratingBar.setRating(Float.parseFloat(reviewList.get(position).getReviewGrade()));

    }

    @Override
//    public int getItemCount() {
//        return items.size();
//    }
    public int getItemCount() {
        return reviewList.size();
    }


    //viewHolder : 하나의 아아템이 보여질 때, 레이아웃(뷰)을 담아놓을 객체 -> 필요하면 재사용
    //하나의 아이템에 대한 조작

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //viewHolder의 view 안에 글자가 표시되도록 textView 2개를 추가되도록 할 것이다

        TextView clientName, tv_reviewContents, tv_reviewDate;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clientName = itemView.findViewById(R.id.clientName);
            tv_reviewContents = itemView.findViewById(R.id.tv_reviewContents);
            tv_reviewDate = itemView.findViewById(R.id.tv_reviewDate);
            ratingBar = itemView.findViewById(R.id.ratingBar);


        }

    }

}



