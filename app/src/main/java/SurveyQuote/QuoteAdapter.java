package SurveyQuote;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.soomgodev.R;

import java.text.DecimalFormat;
import java.util.List;

import Chat.ChatRoomActivity;
import SurveyRequest.RequestData;
import SurveyRequest.RequestDetail;

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.ViewHolder> {

    List<QuoteData> quoteDataList;
    Context context;

    public QuoteAdapter(List<QuoteData> quoteDataList, Context context) {
        this.quoteDataList = quoteDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quote_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.tv_expertName.setText(quoteDataList.get(position).getExpertName());



        // 별점 평균
        String reviewAverage = quoteDataList.get(position).getReviewAverage();
        if(reviewAverage.equals("0")) {
            holder.tv_reviewAverage.setText("리뷰 없음");
        } else {
            holder.tv_reviewAverage.setText(reviewAverage+"점");
        }

        holder.tv_reviewCount.setText("("+quoteDataList.get(position).getReviewCount()+"건)");
        holder.tv_hireCount.setText(quoteDataList.get(position).getHireCount() + "회 고용");


        // 천단위 마다 콤마를 넣는 방법
        String money = quoteDataList.get(position).getPrice();
        long value = Long.parseLong(money);
        DecimalFormat format = new DecimalFormat("###,###"); //콤마
        format.format(value);
        String result = format.format(value);
        holder.tv_price.setText("시간당 " + result + "원 부터~");


        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatRoomActivity.class);
                intent.putExtra("chat_room_seq", quoteDataList.get(position).getRoomNumber());
                intent.putExtra("clientOrExpert", "client");
                context.startActivity(intent);
                Log.i("ChatRoomAdapter(고객)", "Fragment3(받은견적)->ChatRoomActivity 갈 때, 채팅방번호 = " + quoteDataList.get(position).getRoomNumber());
            }
        });


        // 프로필 사진 표시 (세 가지 case)
        // case 1. 카카오아이디 최초 로그인 경우, https: 포함 여부 -> glide 사용 (단순 사용)

        if(quoteDataList.get(position).getExpertImage() != null) {
                Glide.with(holder.itemView.getContext())
                        .load(quoteDataList.get(position).getExpertImage())
                        .into(holder.iv_expertImage);

        }

    }

    @Override
//    public int getItemCount() {
//        return items.size();
//    }
    public int getItemCount() {
        return quoteDataList.size();
    }


    //viewHolder : 하나의 아아템이 보여질 때, 레이아웃(뷰)을 담아놓을 객체 -> 필요하면 재사용
    //하나의 아이템에 대한 조작

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //viewHolder의 view 안에 글자가 표시되도록 textView 2개를 추가되도록 할 것이다
        TextView tv_expertName, tv_reviewAverage, tv_reviewCount, tv_hireCount, tv_price;
        ImageView iv_expertImage;
        CardView parentLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_expertName = itemView.findViewById(R.id.tv_expertName);
            tv_reviewAverage = itemView.findViewById(R.id.tv_reviewAverage);
            tv_reviewCount = itemView.findViewById(R.id.tv_reviewCount);
            tv_hireCount = itemView.findViewById(R.id.tv_hireCount);

            tv_price = itemView.findViewById(R.id.tv_price);
            iv_expertImage = itemView.findViewById(R.id.iv_expertImage);
            parentLayout = itemView.findViewById(R.id.parentLayout);




//            parentLayout = itemView.findViewById(R.id.parentLayout);



        }

        }
    }

