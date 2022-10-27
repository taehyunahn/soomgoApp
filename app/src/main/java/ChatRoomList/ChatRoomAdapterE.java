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
import com.example.soomgodev.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Chat.AppHelper;
import Chat.ChatInterface;
import Chat.ChatRoomActivity;
import ConnectToServer.NetworkClient;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;


//고수의 채팅방 목록
public class ChatRoomAdapterE extends RecyclerView.Adapter<ChatRoomAdapterE.ViewHolder> {

    List<ChatRoomData> chatRoomDataList;
    Context context;
    NetworkClient networkClient;
    ChatInterface chatInterface;
    String userProfileImage;


    // sharedPreference 기본설정 (변수 선언부)
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq, expertSeq;


    public ChatRoomAdapterE(List<ChatRoomData> chatRoomDataList, Context context) {
        this.chatRoomDataList = chatRoomDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatroom_item_e, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatRoomActivity.class);
                intent.putExtra("chat_room_seq", chatRoomDataList.get(position).getChatRoomNumber());
                intent.putExtra("clientOrExpert", "expert");
                intent.putExtra("userProfileImage", userProfileImage);
                context.startActivity(intent);
                Log.i("ChatRoomAdapterE(고수)", "Fragment22->ChatRoomActivity 갈 때, 채팅방번호 = " + chatRoomDataList.get(position).getChatRoomNumber());
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


        Call<ChatRoomData> call = chatInterface.getInfoRelatedChatRoomTitle(requestMap);
        call.enqueue(new Callback<ChatRoomData>() {
            @Override
            public void onResponse(Call<ChatRoomData> call, retrofit2.Response<ChatRoomData> response) {
                Log.i("ChatRoomAdapterE", "onResponse = " + response);
                Log.i("ChatRoomAdapterE", "onResponse.body() = " + response.body());
                ChatRoomData responseBody = response.body();
                Log.i("ChatRoomAdapterE", "responseBody.getClientName() = " + responseBody.getClientName());
                Log.i("ChatRoomAdapterE", "responseBody.getExpertImage() = " + responseBody.getExpertImage());
                holder.tv_clientName.setText(responseBody.getClientName());
                holder.tv_serviceName.setText(responseBody.getServiceRequested());
                holder.tv_clientAddress.setText(responseBody.getAddressInfo());

                if(responseBody.getLastDate() != null){
                    String now_date = responseBody.getLastDate();
                    String now_date_short = now_date.substring(0, 10);  //날짜 변환 -> 시,분,초 제거
                    holder.tv_lastDate.setText(now_date_short);
                }

                holder.tv_lastMsg.setText(responseBody.getLastMsg());


                userProfileImage = responseBody.getExpertImage();


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
    public int getItemCount() {
        return chatRoomDataList.size();
    }


    //viewHolder : 하나의 아아템이 보여질 때, 레이아웃(뷰)을 담아놓을 객체 -> 필요하면 재사용
    //하나의 아이템에 대한 조작

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //viewHolder의 view 안에 글자가 표시되도록 textView 2개를 추가되도록 할 것이다
        ImageView iv_expertImage;
        CardView parentLayout;
        TextView tv_clientName, tv_serviceName, tv_clientAddress, tv_requestDate, tv_lastMsg, tv_lastDate, tv_unread;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            tv_clientName = itemView.findViewById(R.id.tv_clientName);
            tv_serviceName = itemView.findViewById(R.id.tv_serviceName);
            tv_clientAddress = itemView.findViewById(R.id.tv_clientAddress);
            tv_requestDate = itemView.findViewById(R.id.tv_requestDate);

            tv_lastMsg = itemView.findViewById(R.id.tv_lastMsg);
            tv_lastDate = itemView.findViewById(R.id.tv_lastDate);
            tv_unread = itemView.findViewById(R.id.tv_unread);

        }
    }


    // (설명) 레트로핏을 사용하여, 현재 리사이클러뷰에 띄워진 아이템 목록을 서버로 전송한다.
    // (동작) 고수고유값, 회원고유값을 보낸다. 파일을 생성한다. DB의 photo 테이블에 이미지주소를 insert하고, 고수고유값도 함께 저장한다.
    private void photoUploadWithRetrofit(String selectedExpertId, String userIdWhoRequest) {


    }






}

