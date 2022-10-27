package SurveyRequest;

import static com.example.soomgodev.StaticVariable.tagIntentInput;

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
import com.example.soomgodev.Fragment.ExpertProfileActivity;
import com.example.soomgodev.R;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    List<RequestData> requestDataList;
    Context context;

    public RequestAdapter(List<RequestData> requestDataList, Context context) {
        this.requestDataList = requestDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.tv_serviceNeed.setText(requestDataList.get(position).getServiceRequested()); // 요청한 서비스이름
        holder.tv_clientName.setText("(" + requestDataList.get(position).getClientName() + " 고객)"); // 요청한 고객 이름
        holder.tv_addressInfo.setText(requestDataList.get(position).getAddressInfo()); // 요청한 고객 주소
        holder.tv_requestDate.setText(requestDataList.get(position).getDate()); // 요청한 날짜

        holder.btn_detail.setOnClickListener(new View.OnClickListener() { //자세히 보기 버튼 클릭 시, 요청 상세 화면ㅇ르ㅗ 이동
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RequestDetail.class);
                intent.putExtra("expertId", requestDataList.get(position).getSelectedExpertId());
                context.startActivity(intent);
            }
        });

        // 프로필 사진 표시 (세 가지 case)
        // case 1. 카카오아이디 최초 로그인 경우, https: 포함 여부 -> glide 사용 (단순 사용)

    }

    @Override
//    public int getItemCount() {
//        return items.size();
//    }
    public int getItemCount() {
        return requestDataList.size();
    }


    //viewHolder : 하나의 아아템이 보여질 때, 레이아웃(뷰)을 담아놓을 객체 -> 필요하면 재사용
    //하나의 아이템에 대한 조작

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //viewHolder의 view 안에 글자가 표시되도록 textView 2개를 추가되도록 할 것이다
        TextView tv_serviceNeed, tv_addressInfo, tv_requestDate, tv_clientName;
        Button btn_remove, btn_detail;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_serviceNeed = itemView.findViewById(R.id.tv_serviceNeed);
            tv_addressInfo = itemView.findViewById(R.id.tv_addressInfo);
            tv_requestDate = itemView.findViewById(R.id.tv_requestDate);
            tv_clientName = itemView.findViewById(R.id.tv_clientName);

            btn_remove = itemView.findViewById(R.id.btn_remove);
            btn_detail = itemView.findViewById(R.id.btn_detail);


//            parentLayout = itemView.findViewById(R.id.parentLayout);



        }

        }
    }

