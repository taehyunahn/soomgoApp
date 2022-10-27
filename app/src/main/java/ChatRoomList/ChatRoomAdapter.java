package ChatRoomList;

import static com.example.soomgodev.StaticVariable.serverAddress;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.soomgodev.R;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Chat.AppHelper;
import Chat.ChatInterface;
import Chat.ChatMember;
import Chat.ChatRoomActivity;
import Chat.ViewType;
import ConnectToServer.NetworkClient;
import SurveyQuote.QuoteData;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

// 고객 화면용
public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> {

    List<ChatRoomData> chatRoomDataList;
    Context context;
    NetworkClient networkClient;
    ChatInterface chatInterface;
    String expertProfileImage;

    // sharedPreference 기본설정 (변수 선언부)
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq, expertSeq;



    public ChatRoomAdapter(List<ChatRoomData> chatRoomDataList, Context context) {
        this.chatRoomDataList = chatRoomDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatroom_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.tv_expertName.setText(chatRoomDataList.get(position).getExpertName());
//        holder.tv_serviceName.setText(chatRoomDataList.get(position).getServiceName());
//        holder.tv_expertAddress.setText(chatRoomDataList.get(position).getExpertAddress());
//        holder.tv_price.setText("시간당 " + chatRoomDataList.get(position).getPrice() + "원 부터~");
//        holder.tv_lastMsg.setText(chatRoomDataList.get(position).getLastMsg());
//        holder.tv_lastDate.setText(chatRoomDataList.get(position).getLastDate());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatRoomActivity.class);
                intent.putExtra("chat_room_seq", chatRoomDataList.get(position).getChatRoomNumber());
                intent.putExtra("clientOrExpert", "client");
                context.startActivity(intent);
                Log.i("ChatRoomAdapter(고객)", "Fragment4->ChatRoomActivity 갈 때, 채팅방번호 = " + chatRoomDataList.get(position).getChatRoomNumber());
            }
        });


        Log.i("ChatRoomAdapterE", "photoUploadWithRetrofit는 시작됨");

        chatInterface = networkClient.getRetrofit().create(ChatInterface.class);

        // 1. 회원고유 번호와, 2. 고수 고유번호를 보낸다.
        RequestBody selectedExpertIdBody = RequestBody.create(MediaType.parse("text/plain"), chatRoomDataList.get(position).getSelectedExpertId());
        RequestBody userIdWhoRequestBody = RequestBody.create(MediaType.parse("text/plain"), chatRoomDataList.get(position).getUserIdWhoRequest());
        RequestBody chatRoomNumberRequestBody = RequestBody.create(MediaType.parse("text/plain"), chatRoomDataList.get(position).getChatRoomNumber());


        // 3. 서버에서 받았던 서버 이미지 경로를 다시 보낸다 + 이미지 경로 총 개수도 보낸다.
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        requestMap.put("selectedExpertId", selectedExpertIdBody);
        requestMap.put("userIdWhoRequest", userIdWhoRequestBody);
        requestMap.put("chatRoomNumber", chatRoomNumberRequestBody);


        //    @POST("chat/getInfoRelatedChatRoomTitleClient.php")
        Call<ChatRoomData> call = chatInterface.getInfoRelatedChatRoomTitleClient(requestMap);
        call.enqueue(new Callback<ChatRoomData>() {
            @Override
            public void onResponse(Call<ChatRoomData> call, retrofit2.Response<ChatRoomData> response) {
                Log.i("ChatRoomAdapter", "onResponse = " + response);
                Log.i("ChatRoomAdapter", "onResponse.body() = " + response.body());
                ChatRoomData responseBody = response.body();


                expertProfileImage = responseBody.getExpertImage();

                Glide.with(holder.itemView.getContext())
                .load(expertProfileImage)
                .centerCrop()
                .into(holder.iv_expertImage);


                holder.tv_expertName.setText(responseBody.getExpertName());
                holder.tv_serviceName.setText(responseBody.getServiceRequested());
                holder.tv_expertAddress.setText(responseBody.getExpertAddress());


                if(responseBody.getLastDate() != null){
                    String now_date = responseBody.getLastDate();
                    String now_date_short = now_date.substring(0, 10);  //날짜 변환 -> 시,분,초 제거
                    holder.tv_lastDate.setText(now_date_short);
                }
                holder.tv_lastMsg.setText(responseBody.getLastMsg());

                if(responseBody.getPrice() != null) {
                    // 천단위 마다 콤마를 넣는 방법
                    String money = responseBody.getPrice();
                    long value = Long.parseLong(money);
                    DecimalFormat format = new DecimalFormat("###,###"); //콤마
                    format.format(value);
                    String result = format.format(value);

                    holder.tv_price.setText("시간당 " + result + "원 부터~");
                }


            }

            @Override
            public void onFailure(Call<ChatRoomData> call, Throwable t) {
                Log.i("ChatRoomAdapterE", "t = " + t);
            }
        });

        String url = serverAddress + "chat/countUnreadMsg.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("ChatRoomAdapter", "response = " + response);
                //서버에 요청을 통해 받아온 데이터
                if(response.equals("0")){ // DB와 통신 실패
                }else { // DB와 통신 성공
                    String a = response;
                    String unreadCount = a.substring(0, a.length() - 1);
                    Log.i("ChatRoomAdapter", "unreadCount = " + unreadCount);

                    holder.tv_unread.setText(a);
                    if(unreadCount.equals("0")){
                        holder.tv_unread.setVisibility(View.INVISIBLE);

                    } else {
                        holder.tv_unread.setVisibility(View.VISIBLE);
                    }


                }
            }
        },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("채팅방 생성 메서드 에러 " + error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //서버에 보낼 데이터를 해쉬맵을 사용해서 보낸다.


                // (onCreate 안에)
                sharedPreferences = context.getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                userSeq = sharedPreferences.getString("userSeq","");


                Map<String, String> params = new HashMap<String, String>();
                params.put("roomNumber", chatRoomDataList.get(position).getChatRoomNumber()); // 로그인한 사용자의 seq 번호
                params.put("userSeq", userSeq); // 로그인한 사용자의 seq 번호

                return params;
            }
        };

        //서버에 요청
        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(context.getApplicationContext()); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);






    }

    @Override
//    public int getItemCount() {
//        return items.size();
//    }
    public int getItemCount() {
        return chatRoomDataList.size();
    }


    //viewHolder : 하나의 아아템이 보여질 때, 레이아웃(뷰)을 담아놓을 객체 -> 필요하면 재사용
    //하나의 아이템에 대한 조작

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //viewHolder의 view 안에 글자가 표시되도록 textView 2개를 추가되도록 할 것이다
        ImageView iv_expertImage;
        CardView parentLayout;
        TextView tv_expertName, tv_serviceName, tv_expertAddress, tv_price, tv_lastMsg, tv_lastDate, tv_unread;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            iv_expertImage = itemView.findViewById(R.id.iv_expertImage);
            tv_expertName = itemView.findViewById(R.id.tv_expertName);
            tv_serviceName = itemView.findViewById(R.id.tv_serviceName);
            tv_expertAddress = itemView.findViewById(R.id.tv_expertAddress);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_lastMsg = itemView.findViewById(R.id.tv_lastMsg);
            tv_lastDate = itemView.findViewById(R.id.tv_lastDate);
            tv_unread = itemView.findViewById(R.id.tv_unread);

        }

    }


    // chat_rooms 테이블에서 보내는 roomNumber에 해당하는 joinCount 칼럼에 숫자 1을 뺀다.
    public void countUnreadMsg(String roomNumber, String userSeq) { // 채팅을 받는사람의 seq 번호를 찾는 메서드

        String url = serverAddress + "chat/countUnreadMsg.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //서버에 요청을 통해 받아온 데이터
                if(response.equals("0")){ // DB와 통신 실패
                }else { // DB와 통신 성공
                    String a = response;
                    String joinCount = a.substring(0, a.length() - 1);

                }
            }
        },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("채팅방 생성 메서드 에러 " + error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //서버에 보낼 데이터를 해쉬맵을 사용해서 보낸다.
                Map<String, String> params = new HashMap<String, String>();
                params.put("roomNumber", roomNumber); // 로그인한 사용자의 seq 번호
                params.put("userSeq", userSeq); // 로그인한 사용자의 seq 번호

                return params;
            }
        };

        //서버에 요청
        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(context.getApplicationContext()); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);
    }


}

