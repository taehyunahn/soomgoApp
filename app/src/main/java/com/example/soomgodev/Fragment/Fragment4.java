package com.example.soomgodev.Fragment;

import static com.example.soomgodev.StaticVariable.serverAddress;
import static com.example.soomgodev.StaticVariable.tagServerToClient;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.soomgodev.R;
import com.example.soomgodev.RecyclerViewEmptySupport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Chat.ChatMember;
import ChatRoomList.ChatRoomAdapter;
import ChatRoomList.ChatRoomData;
import SurveyQuote.QuoteAdapter;
import SurveyQuote.QuoteData;

// 고객의 채팅 구현
public class Fragment4 extends Fragment {


    private static final String TAG = "Fragment4. 고객의 채팅목록";
    // sharedPreference 기본설정 (변수 선언부)
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq, expertSeq;


    // 고수 목록 출력을 위한 RecyclerView 세팅
    ChatRoomAdapter adapter;
    List<ChatRoomData> chatRoomDataList = new ArrayList<>();
    private RecyclerViewEmptySupport recyclerView;
    public static RecyclerView.Adapter mAdapterInF4;
    private RecyclerView.LayoutManager layoutManager;
    TextView tv_test;

    public static Handler mHandlerInF4;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_4, container, false);
        Log.i(TAG, "프래그먼트 생명주기 onCreateView");

        // (onCreate 안에)
        sharedPreferences = this.getActivity().getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userSeq = sharedPreferences.getString("userSeq","");
        expertSeq = sharedPreferences.getString("expertSeq", "");

        recyclerView = rootView.findViewById(R.id.recyclerView);
        tv_test = rootView.findViewById(R.id.tv_test);


        // 핸들러 세팅
        mHandlerInF4 = new Handler();



        //리사이클러뷰 초기 세팅
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);


        chatRoomDataList = new ArrayList<>();
        mAdapterInF4 = new ChatRoomAdapter(chatRoomDataList, getContext());
        recyclerView.setAdapter(mAdapterInF4);


        // 현재 로그인 정보 고유값을 DB로 보낸다
        // DB의 chat_rooms + request + expertInfo 테이블에서 userIdWhoRequest 칼럼이 userSeq와 동일한 목록을 출력한다.
        showChatRoomList(userSeq);

        //리사이클러뷰 아이템이 없으면 없다고 표시한다.
        recyclerView.setEmptyView(rootView.findViewById(R.id.list_empty));

        return rootView;
    }

    //
    public void showChatRoomList(String userSeq) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = serverAddress + "chat/chatRoomList1.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i(TAG, "response = " + response);

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("data"); //JSONArray로 배열객체를 받아서, 변수에 담는다.
                    Log.i(TAG, "jsonArray = " + String.valueOf(jsonArray));


                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String selectedExpertId = object.getString("selectedExpertId"); // 채팅방 관련 요청서의 고수 정보
                        String userIdWhoRequest = object.getString("userIdWhoRequest"); // 채팅방 관련 요청서의 고객 정보
                        String chatRoomNumber = object.getString("chatRoomNumber"); // 채팅방 번호

                        chatRoomDataList.add(new ChatRoomData(chatRoomNumber, selectedExpertId, userIdWhoRequest));
                    }

                    mAdapterInF4.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "JSON에서 전달받은 에러" + e);
                }



            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // textView.setText("That didn't work!");
                Log.e(TAG, "VolleyError = " + error);

            }
        }) {
            // 포스트 파라미터 넣기
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userSeq", userSeq);
                Log.i(TAG, "(클라이언트→서버)userSeq = " + userSeq);
                return params;
            }

        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }



    // 메세지가 오면 채팅 목록을 자동으로 갱신하는 Runnable
    public static class RefreshChatRoomListClient implements Runnable {

        @Override
        public void run() {
            Log.i(TAG, "refreshChatRoomListClient : 실행");
            mAdapterInF4.notifyDataSetChanged();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "프래그먼트 생명주기 onStart");
        mAdapterInF4.notifyDataSetChanged();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i(TAG, "프래그먼트 생명주기 onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "프래그먼트 생명주기 onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "프래그먼트 생명주기 onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "프래그먼트 생명주기 onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "프래그먼트 생명주기 onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "프래그먼트 생명주기 onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "프래그먼트 생명주기 onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "프래그먼트 생명주기 onDetach");
    }
}