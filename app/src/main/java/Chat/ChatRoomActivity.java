package Chat;

import static com.example.soomgodev.StaticVariable.serverAddress;
import static com.example.soomgodev.StaticVariable.tagServerToClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.soomgodev.ApiClient;
import com.example.soomgodev.Fragment.Fragment22;
import com.example.soomgodev.Fragment.Fragment4;
import com.example.soomgodev.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ConnectToServer.NetworkClient;
import SurveyQuote.QuoteAdapter;
import SurveyQuote.QuoteData;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoomActivity extends AppCompatActivity {

    EditText et_message;
    TextView tv_title;
    ImageView imageBack, iv_send;

    public static Context context;

    // sharedPreference 기본설정 (변수 선언부)
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq, expertSeq, userName, userImage;

    static SharedPreferences sharedPreferencesStatic;
    static SharedPreferences.Editor editorStatic;


    private static final String TAG = "ChatRoomActivity";

    public static Handler mHandler;
    Socket member_socket; // 서버와 연결되어있는 소켓 객체

    int page = 1;

    public static RecyclerView chat_ryc, recyclerView;
    static ChatAdapter chatAdapter;

    public static String NOW_CHAT = ""; // notification 띄울지 여부

    // 레트로핏 통신 공통
    NetworkClient networkClient;
    ChatInterface chatInterface;

    List<ChatMember> chatMemberList = new ArrayList<>();
    static List<ChatMember> chatMemberList2 = new ArrayList<>();
    static String chat_room_seq;
    static String staticUserSeq;
    String recipientId;

    private String clientOrExpert;
    private LinearLayout layoutForClient;

    Button btn_review, btn_deal, btn_dealCancel, btn_reviewCancel;
    ImageView iv_out;

    List<QuoteData> quoteDataList = new ArrayList<>();
    private RecyclerView recyclerViewForQuote;
    private RecyclerView.Adapter mAdapter2;
    private RecyclerView.LayoutManager layoutManager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        getSupportActionBar().hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Log.i(TAG, "생명주기 onCreate 실행됨");

        context = this;

        // (onCreate 안에)
        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userSeq = sharedPreferences.getString("userSeq","");
        expertSeq = sharedPreferences.getString("expertSeq", "");
        userName = sharedPreferences.getString("userName", "");

        // static Runnable에서 사용하기 위한 도구
        sharedPreferencesStatic = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editorStatic = sharedPreferencesStatic.edit();
        staticUserSeq = userSeq;

        // UI 연결
        et_message = findViewById(R.id.et_message); // 채팅 메세지 입력 내용
        tv_title = findViewById(R.id.tv_title); //채팅방 타이틀
        iv_send = findViewById(R.id.iv_send);
        imageBack = findViewById(R.id.imageBack); // 뒤로가기 버튼 (현재 액티비티 닫고, 이전 액티비티 -> 채팅방 목록)
        layoutForClient = findViewById(R.id.layoutForClient); // 고객에게만 보여주는 거래하기 또는 리뷰작성하기 버튼을 담은 레이아웃
        btn_deal = findViewById(R.id.btn_deal);
        btn_review = findViewById(R.id.btn_review);
        iv_out = findViewById(R.id.iv_out); // 채팅방 나가기 버튼
        recyclerViewForQuote = findViewById(R.id.recyclerViewForQuote);
        chat_ryc = findViewById(R.id.recyclerView);

        //리사이클러뷰 초기 세팅
        recyclerViewForQuote.setHasFixedSize(true);
        layoutManager2 = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recyclerViewForQuote.setLayoutManager(layoutManager2);

        // 핸들러 세팅
        mHandler = new Handler();

        // notifyDataSetChanged할때 리사이클러뷰가 깜빡이는 애니메이션을 없애줌.
        RecyclerView.ItemAnimator animator = chat_ryc.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        Intent intent = getIntent(); // post class or ChatFragment에서 전달됨
        chat_room_seq = intent.getExtras().getString("chat_room_seq"); // 해당 채팅방의 seq 번호
        clientOrExpert = intent.getExtras().getString("clientOrExpert"); // 채팅방을 보고 있는 사람이 고수인지 고객인지 구분


        //본인 계정과 채팅방 관련하여 안읽은 메세지 모두 삭제
        removeUnreadMsg(userSeq, chat_room_seq);


        // 채팅방 접속자가 고객/고수 여부에 따라, 상단 버튼(거래확정, 리뷰하기)을 포함한 레이아웃을 보유줌 또는 안보여줌
        if(clientOrExpert.equals("client")) {
            layoutForClient.setVisibility(View.VISIBLE);
        } else {
            layoutForClient.setVisibility(View.GONE);
        }

//        Toast.makeText(this, "현재 접속한 chat_room_seq = " + chat_room_seq, Toast.LENGTH_SHORT).show();
        NOW_CHAT = chat_room_seq; // 현재 채팅중인 사람의 notification을 띄우지 않기위한 변수

        // 현재 채팅방 관련 거래가 완료되었는지 확인
        String doneOrNot = sharedPreferences.getString(chat_room_seq + "dealDone", "");
        if(doneOrNot.equals("done")){
            btn_deal.setVisibility(View.VISIBLE); //보이게 한다
            btn_deal.setEnabled(false); // 클릭하지 못하게 한다.
            btn_review.setEnabled(true); // 리뷰작성 버튼을 클릭하도록 한다
        }

        // 현재 채팅방 리뷰 작성이 완료되었는지 확인
        String doneOrNot2 = sharedPreferences.getString(chat_room_seq + "reviewDone", "");
        if(doneOrNot2.equals("done")){
            btn_review.setVisibility(View.VISIBLE); // 보이게 한다
            btn_review.setEnabled(false); //클릭하지 못하게 한다
        }

        // 채팅방 나가기 버튼
        iv_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.remove(chat_room_seq + "dealDone");
                editor.remove(chat_room_seq + "reviewDone");
                editor.commit();
            }
        });

        // 거래하기 버튼
        btn_deal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DealActivity.class);
                intent.putExtra("chatRoomNumber", chat_room_seq);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // 리뷰작성 버튼
        btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);
                intent.putExtra("chatRoomNumber", chat_room_seq);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        Log.i(TAG, "chat_room_seq = " + chat_room_seq);
        Log.i(TAG, "clientOrExpert = " + clientOrExpert);

        Get_recipient_nickname(userSeq, chat_room_seq, clientOrExpert); // 채팅방 생성 된 이후 상대닉네임을 가져와 상단 액션바 타이틀 설정
        Get_recipient_seq(userSeq, chat_room_seq, clientOrExpert); // 상대방의 seq 번호 설정
        getUserProfileImage(userSeq, clientOrExpert);

        sharedPreferences = this.getSharedPreferences("loginInfo", ChatRoomActivity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(userSeq + "currentRoomNumber", chat_room_seq);
        editor.commit();

        Log.i(TAG, "생명주기 onCreate() 동작 후 sharedPreference에 저장된 currentRoomNumber = " + sharedPreferences.getString(userSeq+"currentRoomNumber",""));

        // 이전 채팅 메세지 기록을 불러오는 통신 -- 시작
        chatInterface = networkClient.getRetrofit().create(ChatInterface.class);
        // @POST("chat/getChattingMsgList.php")
        Call<List<ChatMember>> call = chatInterface.get_chatting_msg_list(userSeq, chat_room_seq, clientOrExpert);
        call.enqueue(new Callback<List<ChatMember>>() {
            @Override
            public void onResponse(@NonNull Call<List<ChatMember>> call, @NonNull Response<List<ChatMember>> response) {
                Log.e("기존 채팅메세지 목록", String.valueOf(response.body()));
                chatMemberList2 = new ArrayList<>(); // 어댑터에 담아줄 메세지 객체 리스트 선언
                for (int i = 0; i < response.body().size(); i++){
                    String now_time = response.body().get(i).getTime(); // 현재 메세지 객체의 시간
                        String now_year = now_time.substring(0, 4);
                        String now_month = now_time.substring(5, 7);
                        String now_date = now_time.substring(8, 10);
                        String showDate = now_year + "년 " + now_month + "월 " + now_date + "일 ";
                        String finalTime = "";
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    try {
                        Date date = formatter.parse(now_time);
                        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
                        String date2 = formatter2.format(date);
                        String a = date2.substring(20, 22);
                        String b = date2.substring(11, 16);
                        finalTime = a + " " + b;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                        // 첫 메세지가 아니라면
                        if(i != 0){
                            String before_time = response.body().get(i-1).getTime(); // 이전 메세지 객체의 시간
                            String before_date = before_time.substring(8, 10);
                            // 이전 메세지 객체의 시간과 일자가 다르다면
                            if(!now_date.equals(before_date)){
                                ChatMember date_chatmember = new ChatMember(); // 메세지 객체 리스트에 담을 메세지 객체 선언
                                date_chatmember.setTime(showDate); // 현제 메세지 객체의 년,월,일
                                chatMemberList2.add(date_chatmember); // 메세지 객체 리스트에 담아줌
                            }
                        }
                        // 첫 메세지 이전에 년,월,일 넣어줌
                        else if(response.body().size() < 40){ // 페이징할때 같은 날짜가 뜨는것을 방지함
                            ChatMember date_chatmember = new ChatMember(); // 메세지 객체 리스트에 담을 메세지 객체 선언
                            date_chatmember.setTime(showDate); // 현제 메세지 객체의 년,월,일
                            chatMemberList2.add(0, date_chatmember); // 메세지 객체 리스트에 담아줌
                    }
                    ChatMember updatedChatMember = new ChatMember();
                    updatedChatMember.setMessage(response.body().get(i).getMessage());

                    if(clientOrExpert.equals("client")){
                        updatedChatMember.setNickname(response.body().get(i).getNicknameExpert());
                    } else if (clientOrExpert.equals("expert")){
                        updatedChatMember.setNickname(response.body().get(i).getNicknameUser());
                    }
                    updatedChatMember.setUserProfileImage(response.body().get(i).getUserProfileImage());
                    updatedChatMember.setTime(finalTime); // 시간 변환해서 넣기
                    updatedChatMember.setChat_room_seq(response.body().get(i).getChat_room_seq());
                    updatedChatMember.setViewType(response.body().get(i).getViewType());

                    if(response.body().get(i).getUserIdWhoSent().equals(userSeq)){
                        if(response.body().get(i).getUnread() != null){
                            updatedChatMember.setViewType(3); // 메세지를 보낸사람 == 현재 로그인한 사람 → 오른쪽 위치, 안읽음 메세지를 기본으로 함.
                        } else {
                            updatedChatMember.setViewType(2);
                        }
                    } else {
                        updatedChatMember.setViewType(1); // 메세지를 보낸사람 != 현재 로그인한 사람 → 왼쪽 위치를 기본으로 함.
                    }
                    chatMemberList2.add(updatedChatMember);
                }


                LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false);
                chat_ryc.setLayoutManager(manager); // LayoutManager 등록
                chatAdapter = new ChatAdapter(getApplicationContext(), chatMemberList2); // 서버에서 받아온 메세지 목록을 리사이클러뷰에 담는다.
                chat_ryc.setAdapter(chatAdapter);
                chat_ryc.scrollToPosition(chatMemberList2.size() - 1);
            }

            // 통신 실패시(예외발생, 인터넷끊김 등의 이유)
            @Override
            public void onFailure(@NonNull Call<List<ChatMember>> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: "+t.getLocalizedMessage());
            }
        });
        // 이전 채팅 메세지 기록을 불러오는 통신 -- 끝


//페이징 시작
//        chat_ryc.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                // 리사이클러뷰 스크롤이 최상단 일때
//                if(!chat_ryc.canScrollVertically(-1)){
//                    page++;
//
//                    // 이전 채팅 메세지 기록을 불러오는 통신 -- 시작
//                    chatInterface = networkClient.getRetrofit().create(ChatInterface.class);
//                    // @POST("chat/getChattingMsgList.php")
//                    Call<List<ChatMember>> call = chatInterface.get_chatting_msg_list_paging(userSeq, chat_room_seq, page);
//                    call.enqueue(new Callback<List<ChatMember>>() {
//                        @Override
//                        public void onResponse(@NonNull Call<List<ChatMember>> call, @NonNull Response<List<ChatMember>> response) {
//
//                            List<ChatMember> responseResult = response.body();
//
//                            // 더이상 불러올 채팅 목록이 없을때
//                            if(responseResult.size() == 0){
//
//                            }
//                            else if(responseResult.size() < 40) {
//                                List<ChatMember> chatMemberList2 = new ArrayList<>();
//
//                                for (int i = 0; i < response.body().size(); i++){
//                                    String now_time = response.body().get(i).getTime(); // 현재 메세지 객체의 시간
//                                    String now_year = now_time.substring(0, 4);
//                                    String now_month = now_time.substring(5, 7);
//                                    String now_date = now_time.substring(8, 10);
//                                    String showDate = now_year + "년 " + now_month + "월 " + now_date + "일 ";
//                                    String finalTime = "";
//                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                                    try {
//                                        Date date = formatter.parse(now_time);
//                                        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
//                                        String date2 = formatter2.format(date);
//                                        String a = date2.substring(20, 22);
//                                        String b = date2.substring(11, 16);
//                                        finalTime = a + " " + b;
//                                    } catch (ParseException e) {
//                                        e.printStackTrace();
//                                    }
//                                    // 첫 메세지가 아니라면
//                                    if(i != 0){
//                                        String before_time = response.body().get(i-1).getTime(); // 이전 메세지 객체의 시간
//                                        String before_date = before_time.substring(8, 10);
//                                        // 이전 메세지 객체의 시간과 일자가 다르다면
//                                        if(!now_date.equals(before_date)){
//                                            ChatMember date_chatmember = new ChatMember(); // 메세지 객체 리스트에 담을 메세지 객체 선언
//                                            date_chatmember.setTime(showDate); // 현제 메세지 객체의 년,월,일
//                                            chatMemberList2.add(date_chatmember); // 메세지 객체 리스트에 담아줌
//                                        }
//                                    }
//                                    // 첫 메세지 이전에 년,월,일 넣어줌
//                                    else if(response.body().size() < 40){ // 페이징할때 같은 날짜가 뜨는것을 방지함
//                                        ChatMember date_chatmember = new ChatMember(); // 메세지 객체 리스트에 담을 메세지 객체 선언
//                                        date_chatmember.setTime(showDate); // 현제 메세지 객체의 년,월,일
//                                        chatMemberList2.add(0, date_chatmember); // 메세지 객체 리스트에 담아줌
//                                    }
//                                    ChatMember updatedChatMember = new ChatMember();
//                                    updatedChatMember.setMessage(response.body().get(i).getMessage());
//
//                                    if(clientOrExpert.equals("client")){
//                                        updatedChatMember.setNickname(response.body().get(i).getNicknameExpert());
//                                    } else if (clientOrExpert.equals("expert")){
//                                        updatedChatMember.setNickname(response.body().get(i).getNicknameUser());
//                                    }
//                                    updatedChatMember.setUserProfileImage(response.body().get(i).getUserProfileImage());
//                                    updatedChatMember.setTime(finalTime); // 시간 변환해서 넣기
//                                    updatedChatMember.setChat_room_seq(response.body().get(i).getChat_room_seq());
//                                    updatedChatMember.setViewType(response.body().get(i).getViewType());
//
//                                    if(response.body().get(i).getUserIdWhoSent().equals(userSeq)){
//                                        if(response.body().get(i).getUnread() != null){
//                                            updatedChatMember.setViewType(3); // 메세지를 보낸사람 == 현재 로그인한 사람 → 오른쪽 위치, 안읽음 메세지를 기본으로 함.
//                                        } else {
//                                            updatedChatMember.setViewType(2);
//                                        }
//                                    } else {
//                                        updatedChatMember.setViewType(1); // 메세지를 보낸사람 != 현재 로그인한 사람 → 왼쪽 위치를 기본으로 함.
//                                    }
//                                    chatMemberList2.add(updatedChatMember);
//                                }
//
//
//                                List<ChatMember> joined = new ArrayList<>();
//                                joined.addAll(chatMemberList2);
//                                joined.addAll(chatMemberList);
//
//                                chatMemberList = joined;
//                                chatAdapter.notifyItemRangeInserted(0, chatMemberList2.size());
//                                chat_ryc.scrollToPosition(chatMemberList2.size());
//                            }
//
//                            else {
//                                List<ChatMember> chatMemberList2 = new ArrayList<>();
//
//                                for (int i = 0; i < response.body().size(); i++){
//                                    String now_time = response.body().get(i).getTime(); // 현재 메세지 객체의 시간
//                                    String now_year = now_time.substring(0, 4);
//                                    String now_month = now_time.substring(5, 7);
//                                    String now_date = now_time.substring(8, 10);
//                                    String showDate = now_year + "년 " + now_month + "월 " + now_date + "일 ";
//                                    String finalTime = "";
//                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                                    try {
//                                        Date date = formatter.parse(now_time);
//                                        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
//                                        String date2 = formatter2.format(date);
//                                        String a = date2.substring(20, 22);
//                                        String b = date2.substring(11, 16);
//                                        finalTime = a + " " + b;
//                                    } catch (ParseException e) {
//                                        e.printStackTrace();
//                                    }
//                                    // 첫 메세지가 아니라면
//                                    if(i != 0){
//                                        String before_time = response.body().get(i-1).getTime(); // 이전 메세지 객체의 시간
//                                        String before_date = before_time.substring(8, 10);
//                                        // 이전 메세지 객체의 시간과 일자가 다르다면
//                                        if(!now_date.equals(before_date)){
//                                            ChatMember date_chatmember = new ChatMember(); // 메세지 객체 리스트에 담을 메세지 객체 선언
//                                            date_chatmember.setTime(showDate); // 현제 메세지 객체의 년,월,일
//                                            chatMemberList2.add(date_chatmember); // 메세지 객체 리스트에 담아줌
//                                        }
//                                    }
//                                    // 첫 메세지 이전에 년,월,일 넣어줌
//                                    else if(response.body().size() < 40){ // 페이징할때 같은 날짜가 뜨는것을 방지함
//                                        ChatMember date_chatmember = new ChatMember(); // 메세지 객체 리스트에 담을 메세지 객체 선언
//                                        date_chatmember.setTime(showDate); // 현제 메세지 객체의 년,월,일
//                                        chatMemberList2.add(0, date_chatmember); // 메세지 객체 리스트에 담아줌
//                                    }
//                                    ChatMember updatedChatMember = new ChatMember();
//                                    updatedChatMember.setMessage(response.body().get(i).getMessage());
//
//                                    if(clientOrExpert.equals("client")){
//                                        updatedChatMember.setNickname(response.body().get(i).getNicknameExpert());
//                                    } else if (clientOrExpert.equals("expert")){
//                                        updatedChatMember.setNickname(response.body().get(i).getNicknameUser());
//                                    }
//                                    updatedChatMember.setUserProfileImage(response.body().get(i).getUserProfileImage());
//                                    updatedChatMember.setTime(finalTime); // 시간 변환해서 넣기
//                                    updatedChatMember.setChat_room_seq(response.body().get(i).getChat_room_seq());
//                                    updatedChatMember.setViewType(response.body().get(i).getViewType());
//
//                                    if(response.body().get(i).getUserIdWhoSent().equals(userSeq)){
//                                        if(response.body().get(i).getUnread() != null){
//                                            updatedChatMember.setViewType(3); // 메세지를 보낸사람 == 현재 로그인한 사람 → 오른쪽 위치, 안읽음 메세지를 기본으로 함.
//                                        } else {
//                                            updatedChatMember.setViewType(2);
//                                        }
//                                    } else {
//                                        updatedChatMember.setViewType(1); // 메세지를 보낸사람 != 현재 로그인한 사람 → 왼쪽 위치를 기본으로 함.
//                                    }
//                                    chatMemberList2.add(updatedChatMember);
//                                }
//
//
//                                List<ChatMember> joined = new ArrayList<>();
//                                joined.addAll(chatMemberList2);
//                                joined.addAll(chatMemberList);
//
//                                chatMemberList = joined;
//                                chatAdapter.notifyItemRangeInserted(0, chatMemberList2.size());
//                                chat_ryc.scrollToPosition(chatMemberList2.size() - (chatMemberList2.size()*(page-1)));
//                            }
//                        }
//
//                        // 통신 실패시(예외발생, 인터넷끊김 등의 이유)
//                        @Override
//                        public void onFailure(@NonNull Call<List<ChatMember>> call, @NonNull Throwable t) {
//                            Log.e(TAG, "onFailure: "+t.getLocalizedMessage());
//                        }
//                    });
//                    // 이전 채팅 메세지 기록을 불러오는 통신 -- 끝
//
//
//                }
//            }
//        });
//
//
//
//















        ChatRoomEnterThread thread2 = new ChatRoomEnterThread(member_socket);
        thread2.start();

        // 채팅 메세지 작성 후 보내기 버튼
        iv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = et_message.getText().toString();
                if(msg.equals("")){
                    Toast.makeText(ChatRoomActivity.this, "메세지를 입력하세요", Toast.LENGTH_SHORT).show();
                } else {

                    joinCountCheck(chat_room_seq, msg, recipientId);


                    SendToServerThread thread = new SendToServerThread(member_socket);
                    thread.start();




//                    // 본인 채팅방에 입력하는 메세지 (리사이클러뷰 아이템 추가)
//                    ChatMember client_send_chatMember = new ChatMember();
//                    client_send_chatMember.setMessage(msg); // 메세지
//                    client_send_chatMember.setTime(getTime());
//                    client_send_chatMember.setViewType(ViewType.RIGHT_CHAT_UNREAD);
//
//                    chatMemberList2.add(client_send_chatMember);
//                    chat_ryc.scrollToPosition(chatMemberList2.size() - 1);

//                    client_send_chatMember.setCommand("SEND"); // 전송
//                    client_send_chatMember.setNickname(userName); // 닉네임
//                    client_send_chatMember.setUserSeq(userSeq); //
//                    client_send_chatMember.setExpertSeq(expertSeq); //
//                    client_send_chatMember.setChat_room_seq(chat_room_seq); // 채팅방 번호
//                    client_send_chatMember.setProfile_img(userImage); // 프로필 이미지

                    // 만약에 상대방이 채팅방에 접속한 상태라면? 읽은 ViewType으로 설정해야 하는데?
                    // 다른 사람이 채팅방에 접속했다는 상태를 어떻게 기억할 수 있을까?


                }
            }
        });
    }

    // 메세지를 보낼 때 동작하는 스레드
    class SendToServerThread extends Thread {
        Socket socket;
        public SendToServerThread(Socket socket) {
            try {
                this.socket = socket;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
                String userSeq = sharedPreferences.getString("userSeq", "");
                String userName = sharedPreferences.getString(userSeq + "userName", "");
                String expertName = sharedPreferences.getString(userSeq + "expertName", "");

                Log.i(TAG, "userSeq = " + userSeq);
                Log.i(TAG, "userName = " + userName);
                Log.i(TAG, "expertName = " + expertName);

                String msg = et_message.getText().toString();
                Intent intent = getIntent(); // post class에서 전달됨
                chat_room_seq = intent.getExtras().getString("chat_room_seq"); // 현재 채팅방의 seq 번호
                clientOrExpert = intent.getExtras().getString("clientOrExpert"); // 채팅방을 보고 있는 사람이 고수인지 고객인지 구분

                ChatService.chatMember.setChat_room_seq(chat_room_seq); // 채팅방 번호
                ChatService.chatMember.setCommand("SEND"); // 전송

                Log.i(TAG, "clientOrExpert = " + clientOrExpert);


                if(clientOrExpert.equals("expert")){
                    ChatService.chatMember.setNickname(expertName); // 닉네임
                } else {
                    ChatService.chatMember.setNickname(userName); // 닉네임
                }

                ChatService.chatMember.setMessage(msg); // 메세지
                ChatService.chatMember.setUserSeq(userSeq); // 채팅작성자의 seq번호
                ChatService.chatMember.setUserProfileImage(userImage); // 프로필 이미지
                ChatService.chatMember.setTime(getTime());
                ChatService.chatMember.setViewType(ViewType.RIGHT_CHAT_UNREAD);
                ChatService.chatMember.setRecipient(recipientId); // 객체에 채팅메세지 받는 사람의 seq 번호를 부여해줌
                ChatService.chatMember.setClientOrExpert(clientOrExpert);
                ChatService.chatMember.setOrderType("chat");

                Gson gson = new Gson();
                String _chatMemberJson = gson.toJson(ChatService.chatMember);

                Log.i(TAG, "서버로 보내는 값, _chatMemberJson = " + _chatMemberJson);

                // 서비스 클래스에 있는 PrintWriter 객체를 사용해서, 방금 전에 입력한 값을 담는다.
                ChatService.sendWriter.println(_chatMemberJson);
                ChatService.sendWriter.flush();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        et_message.setText("");
                    }
                });

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

    // 채팅방 입장할 때 동작하는 스레드
    class ChatRoomEnterThread extends Thread {
        Socket socket;
        public ChatRoomEnterThread(Socket socket) {
            try {
                this.socket = socket;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
                String userSeq = sharedPreferences.getString("userSeq", "");
                String userName = sharedPreferences.getString(userSeq + "userName", "");
                String expertName = sharedPreferences.getString(userSeq + "expertName", "");

                String msg = et_message.getText().toString();
                Intent intent = getIntent(); // post class에서 전달됨
                chat_room_seq = intent.getExtras().getString("chat_room_seq"); // 현재 채팅방의 seq 번호
                clientOrExpert = intent.getExtras().getString("clientOrExpert"); // 채팅방을 보고 있는 사람이 고수인지 고객인지 구분

                ChatService.chatMember = new ChatMember();
                ChatService.chatMember.setChat_room_seq(chat_room_seq); // 채팅방 번호
                ChatService.chatMember.setCommand("JOIN"); // 채팅방 접속

                if(clientOrExpert.equals("expert")){
                    ChatService.chatMember.setNickname(expertName); // 닉네임
                }else {
                    ChatService.chatMember.setNickname(userName); // 닉네임
                }

                ChatService.chatMember.setMessage(msg); // 메세지
                ChatService.chatMember.setUserSeq(userSeq); // 채팅작성자의 seq번호
                ChatService.chatMember.setUserProfileImage(userImage); // 프로필 이미지
                ChatService.chatMember.setTime(getTime());
                ChatService.chatMember.setViewType(ViewType.CENTER_JOIN);
                ChatService.chatMember.setRecipient(recipientId); // 객체에 채팅메세지 받는 사람의 seq 번호를 부여해줌
                ChatService.chatMember.setClientOrExpert(clientOrExpert);
                ChatService.chatMember.setOrderType("chat");

                Gson gson = new Gson();
                String _chatMemberJson = gson.toJson(ChatService.chatMember);

                ChatService.sendWriter.println(_chatMemberJson);
                ChatService.sendWriter.flush();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        et_message.setText("");
                    }
                });
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    // 현재시간을 원하는 형식으로 변경하는 메서드
    private String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
        String getTime = dateFormat.format(date);
        String a = getTime.substring(20, 22);
        String b = getTime.substring(11, 16);
        String getTime2 = a + " " + b;

        return getTime2;
    }

    // 채팅 상대 이름 얻어오는 서버통신
    public void Get_recipient_nickname(String userSeq, String chat_room_seq, String clientOrExpert) { // 채팅방 입장시 상대닉네임을 가져오는 메서드
        String url = serverAddress + "chat/getRecipientNickname.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //서버에 요청을 통해 받아온 데이터
                        Log.e(TAG, "Get_recipient_nickname()의 response = " + response);
                        if(response.equals("0")){ // DB와 통신 실패
                            Log.e("채팅 받는 사람 이름", response);
                        }else { // DB와 통신 성공
                            Log.e("채팅 받는 사람 이름", response);
                            tv_title.setText(response); // 채팅 상단 타이틀
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("채팅방 생성 후 상대닉네임 에러 " + error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //서버에 보낼 데이터를 해쉬맵을 사용해서 보낸다.
                Map<String, String> params = new HashMap<String, String>();
                params.put("userSeq", userSeq); // 로그인한 사용자의 seq 번호
                params.put("chat_room_seq", chat_room_seq); // 현재 채팅방의 seq 번호
                params.put("clientOrExpert", clientOrExpert); // 현재 채팅방의 seq 번호
                return params;
            }
        };

        //서버에 요청
        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);
    }

    // 채팅 상대 userSeq 얻어오는 서버통신
    public void Get_recipient_seq(String userSeq, String chat_room_seq, String clientOrExpert) { // 채팅을 받는사람의 seq 번호를 찾는 메서드
        String url = serverAddress + "chat/getRecipientSeq.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //서버에 요청을 통해 받아온 데이터
                        Log.i(TAG, "Get_recipient_seq() 2. 서버로 부터 받은 response = " + response);

                        if(response.equals("0")){ // DB와 통신 실패
                            Log.e("채팅 받는 사람 seq", response);
                        }else { // DB와 통신 성공
                            Log.e("채팅 받는 사람 seq", response);
                            response = response.substring(0, response.length() - 1);
                            recipientId = response; // 객체에 채팅메세지 받는 사람의 seq 번호를 부여해줌
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
                params.put("userSeq", userSeq); // 로그인한 사용자의 seq 번호
//                params.put("expertSeq", expertSeq); // 로그인한 고수의 seq 번호
                params.put("chat_room_seq", chat_room_seq); // 현재 채팅방의 seq 번호
                params.put("clientOrExpert", clientOrExpert); // 현재 채팅방의 seq 번호

                return params;
            }
        };
        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);
    }

    // 서버에서 메세지 수신하여, 채팅방에 메세지 표시
    public static class GetMsg implements Runnable {
        private final ChatMember chatMember;
        public GetMsg(ChatMember chatMember) {
            this.chatMember = chatMember;
        }
        @Override
        public void run() {
            Log.i(TAG, "GetMsg(Runnable) : 실행");

            // 채팅방에 누군가 접속했다면,
            // 상대방이 내 메세지를 읽었다는 정보를 받으면
            if(chatMember.getCommand().equals("JOIN")){
                //접속한 사람이 본인이라면,
                if(chatMember.getUserSeq().equals(staticUserSeq)){
                }
                //접속한 사람이 다른 사람이라면,
                else if(chatMember.getChat_room_seq().equals(chat_room_seq)) {
                    // 내가 보낸 메세지 중 안읽음 표시된 메세지를 읽음 표시로 바꾼다.
                    for (int i = 0; i < chatMemberList2.size(); i++){
                        if(chatMemberList2.get(i).getViewType() == ViewType.RIGHT_CHAT_UNREAD){
                            chatMemberList2.get(i).setViewType(ViewType.RIGHT_CHAT);
                        }
                    }
                    // 화면에 출력
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 어디까지 읽은 메세지 표시로 바꿔야할지 모르기 때문에 모든 리스트를 바꿔준다.
                            chatAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            // 메세지를 수신하는 경우, - Command로 구분한다 Send or Join
            else if (chatMember.getCommand().equals("SEND")){

                String receiverUserSeq = chatMember.getRecipient();
                Log.i(TAG, "receiverUserSeq = " + receiverUserSeq);
//                String currentRoomNumber = sharedPreferences9.getString(recipientForNoti + "currentRoomNumber", "no");

                //상대방이 현재 채팅방에 있는지 없는지를 확인하기 위함
                String receiverCurrentRoomNumber = sharedPreferencesStatic.getString(receiverUserSeq + "currentRoomNumber", "no");
                Log.i(TAG, "receiverCurrentRoomNumber = " + receiverCurrentRoomNumber);


                if(chatMember.getChat_room_seq().equals(chat_room_seq)) { // 메세지 보낸 사람의 채팅방 번호 == 로그인 정보의 채팅방 번호,
                    if(!chatMember.getUserSeq().equals(staticUserSeq)){ // 메세지 보낸 사람의 userSeq != 로그인 정보의 userSeq
                        chatMember.setViewType(1); // 1: 왼쪽
                        chatMemberList2.add(chatMember);
                        chat_ryc.scrollToPosition(chatMemberList2.size() - 1);



                    } else {
                        // 본인에게는 어떤 메세지를 보내줘야할까?
                        // 1. 상대방이 채팅방에 없다면, 보낼 필요 없다 (안읽음으로 표시되도록)
                        // 2. 상대방이 채팅방에 있다면, 무언가 보내서 읽음으로 갱신해줘야 한다. + DB에서도 삭제해줘야 한다.
                        // 상대방에 채팅방에 있는지 없는지를 어떻게 인지할 수 있을까?

                    }
                }
            }
        }
    }


    // userProfileImage를 받아서, 변수에 넣는다.
    public void getUserProfileImage(String userSeq, String clientOrExpert) { // 채팅을 받는사람의 seq 번호를 찾는 메서드
        String url = serverAddress + "chat/getUserProfile.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //서버에 요청을 통해 받아온 데이터
                if(response.equals("0")){ // DB와 통신 실패
                }else { // DB와 통신 성공
                    Log.i(TAG, "getUserProfile의 response = " + response);
                    String a = response;
                    userImage = a.substring(0, a.length() - 1);
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
                params.put("userSeq", userSeq); // 로그인한 사용자의 seq 번호
                params.put("clientOrExpert", clientOrExpert); // 로그인한 사용자의 seq 번호
                return params;
            }
        };

        //서버에 요청
        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "생명주기 onStop() 실행됨");


//       // sharedPreference 기본설정 (변수 선언부)
//        SharedPreferences sharedPreferences;
//
//        // (onCreate 안에)
//        sharedPreferences = this.getSharedPreferences("loginInfo", ChatRoomActivity.MODE_PRIVATE);
//        editor = sharedPreferences.edit();
//        editor.remove(userSeq + "currentRoomNumber");
//        editor.commit();

        Log.i(TAG, "생명주기 onStop() 동작 후 sharedPreference에 저장된 currentRoomNumber = " + sharedPreferences.getString(userSeq + "currentRoomNumber",""));

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "생명주기 onStart 실행됨");

        sharedPreferences = this.getSharedPreferences("loginInfo", ChatRoomActivity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(userSeq + "currentRoomNumber", chat_room_seq);
        editor.commit();
        Log.i(TAG, "생명주기 onStart() 동작 후 sharedPreference에 저장된 currentRoomNumber = " + sharedPreferences.getString(userSeq+"currentRoomNumber",""));


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "생명주기 onResume 실행됨");
        Log.i(TAG, "생명주기 onResume() 동작 후 sharedPreference에 저장된 currentRoomNumber = " + sharedPreferences.getString(userSeq+"currentRoomNumber",""));


        //채팅방에 참여한 인원 추가
        joinCountPlus(chat_room_seq);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "생명주기 onDestroy 실행됨");

        // sharedPreference 기본설정 (변수 선언부)
        SharedPreferences sharedPreferences;

        // (onCreate 안에)
        sharedPreferences = this.getSharedPreferences("loginInfo", ChatRoomActivity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.remove(userSeq + "currentRoomNumber");
        editor.commit();

        Log.i(TAG, "생명주기 onDestroy() 동작 후 sharedPreference에 저장된 currentRoomNumber = " + sharedPreferences.getString(userSeq+"currentRoomNumber",""));


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "생명주기 onPause() 동작함");

        // sharedPreference 기본설정 (변수 선언부)
        SharedPreferences sharedPreferences;

        // (onCreate 안에)
        sharedPreferences = this.getSharedPreferences("loginInfo", ChatRoomActivity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.remove(userSeq + "currentRoomNumber");
        editor.commit();

        Log.i(TAG, "생명주기 onPause() 동작 후 sharedPreference에 저장된 currentRoomNumber = " + sharedPreferences.getString(userSeq + "currentRoomNumber",""));

        // 채팅 참여자 수 빼기
        joinCountMinus(chat_room_seq);
        removeUnreadMsg(userSeq, chat_room_seq);


    }


    // 본인의 userSeq와 chatRoom에 해당되는 안읽은 메세지 목록을 삭제한다.
    public void removeUnreadMsg(String userSeq, String roomNumber) { // 채팅을 받는사람의 seq 번호를 찾는 메서드

        Log.i(TAG, "removeUnreadMsg 동작합니다");
        String url = serverAddress + "chat/removeUnreadMsg.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //서버에 요청을 통해 받아온 데이터
                Log.i(TAG, "removeUnreadMsg의 response = " + response);
                if(response.equals("0")){ // DB와 통신 실패
                }else { // DB와 통신 성공
                    Log.i(TAG, "removeUnreadMsg의 response = " + response);
                    String a = response;
                    userImage = a.substring(0, a.length() - 1);
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
                params.put("userSeq", userSeq); // 로그인한 사용자의 seq 번호
                params.put("roomNumber", roomNumber); // 로그인한 사용자의 seq 번호
                return params;
            }
        };

        //서버에 요청
        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);
    }


    // 본인의 userSeq와 chatRoom에 해당되는 안읽은 메세지 목록을 삭제한다.
    public void removeUnreadMsgOthers(String recipientId, String roomNumber) { // 채팅을 받는사람의 seq 번호를 찾는 메서드

        Log.i(TAG, "removeUnreadMsgOthers 동작합니다");
        String url = serverAddress + "chat/removeUnreadMsgOthers.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //서버에 요청을 통해 받아온 데이터
                Log.i(TAG, "removeUnreadMsgOthers의 response = " + response);
                if(response.equals("0")){ // DB와 통신 실패
                }else { // DB와 통신 성공
                    Log.i(TAG, "removeUnreadMsgOthers의 response = " + response);
                    String a = response;
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
                params.put("recipientId", recipientId); // 로그인한 사용자의 seq 번호
                params.put("roomNumber", roomNumber); // 로그인한 사용자의 seq 번호
                return params;
            }
        };

        //서버에 요청
        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);
    }




    // chat_rooms 테이블에서 보내는 roomNumber에 해당하는 joinCount 칼럼에 숫자 1을 더한다.
    public void joinCountPlus(String roomNumber) { // 채팅을 받는사람의 seq 번호를 찾는 메서드

        Log.i(TAG, "joinCountPlus 동작합니다");
        String url = serverAddress + "chat/joinCountPlus.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //서버에 요청을 통해 받아온 데이터
                Log.i(TAG, "joinCountPlus response = " + response);
                if(response.equals("0")){ // DB와 통신 실패
                }else { // DB와 통신 성공
                    Log.i(TAG, "joinCountPlus response = " + response);
//                    String a = response;
//                    userImage = a.substring(0, a.length() - 1);
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
                return params;
            }
        };

        //서버에 요청
        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);
    }



    // chat_rooms 테이블에서 보내는 roomNumber에 해당하는 joinCount 칼럼에 숫자 1을 뺀다.
    public void joinCountMinus(String roomNumber) { // 채팅을 받는사람의 seq 번호를 찾는 메서드

        Log.i(TAG, "joinCountMinus 동작합니다");
        String url = serverAddress + "chat/joinCountMinus.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //서버에 요청을 통해 받아온 데이터
                Log.i(TAG, "joinCountMinus response = " + response);
                if(response.equals("0")){ // DB와 통신 실패
                }else { // DB와 통신 성공
                    Log.i(TAG, "joinCountMinus response = " + response);
//                    String a = response;
//                    userImage = a.substring(0, a.length() - 1);
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
                return params;
            }
        };

        //서버에 요청
        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);
    }


    // chat_rooms 테이블에서 보내는 roomNumber에 해당하는 joinCount 칼럼에 숫자 1을 뺀다.
    public void joinCountCheck(String roomNumber, String msg, String recipientId) { // 채팅을 받는사람의 seq 번호를 찾는 메서드

        Log.i(TAG, "joinCountCheck 동작합니다");
        String url = serverAddress + "chat/joinCountCheck.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //서버에 요청을 통해 받아온 데이터
                Log.i(TAG, "joinCountCheck response = " + response);
                if(response.equals("0")){ // DB와 통신 실패
                }else { // DB와 통신 성공
                    Log.i(TAG, "joinCountCheck response = " + response);
                    String a = response;
                    String joinCount = a.substring(0, a.length() - 1);

                    // 본인 채팅방에 입력하는 메세지 (리사이클러뷰 아이템 추가)
                    Log.i(TAG, "joinCountCheck msg = " + msg);
                    ChatMember client_send_chatMember = new ChatMember();
                    client_send_chatMember.setMessage(msg); // 메세지
                    client_send_chatMember.setTime(getTime());
                    if(joinCount.equals("2")){
                        client_send_chatMember.setViewType(ViewType.RIGHT_CHAT);

                        removeUnreadMsgOthers(recipientId, roomNumber);

                    } else {
                        client_send_chatMember.setViewType(ViewType.RIGHT_CHAT_UNREAD);
                    }

                    chatMemberList2.add(client_send_chatMember);
                    chat_ryc.scrollToPosition(chatMemberList2.size() - 1);

                    et_message.setText("");


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
                return params;
            }
        };

        //서버에 요청
        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);
    }



}