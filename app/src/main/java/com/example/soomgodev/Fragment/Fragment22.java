package com.example.soomgodev.Fragment;

import static com.example.soomgodev.StaticVariable.serverAddress;
import static com.example.soomgodev.StaticVariable.tagServerToClient;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Chat.ChatInterface;
import ChatRoomList.ChatRoomAdapter;
import ChatRoomList.ChatRoomAdapterE;
import ChatRoomList.ChatRoomData;
import ConnectToServer.DataClass;
import ConnectToServer.NetworkClient;
import ConnectToServer.UploadApis;
import SurveyQuote.QuoteAdapter;
import SurveyQuote.QuoteData;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

// 고수의 채팅 구현
public class Fragment22 extends Fragment {

    private static final String TAG = "Fragment4. 고객의 채팅목록";
    // sharedPreference 기본설정 (변수 선언부)
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq, expertSeq;


    // 고수 목록 출력을 위한 RecyclerView 세팅
    ChatRoomAdapterE adapter;
    private List<ChatRoomData> chatRoomDataList = new ArrayList<>();
    private RecyclerViewEmptySupport recyclerView;
    public static RecyclerView.Adapter mAdapterInF22;
    private RecyclerView.LayoutManager layoutManager;

    // 레트로핏 통신 공통
    NetworkClient networkClient;
    ChatInterface chatInterface;

    public static Handler mHandlerInF22;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment2_2, container, false);

        // (onCreate 안에)
        sharedPreferences = this.getActivity().getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userSeq = sharedPreferences.getString("userSeq", "");
//        expertSeq = sharedPreferences.getString("expertSeq", "");

        recyclerView = rootView.findViewById(R.id.recyclerView);

        // 핸들러 세팅
        mHandlerInF22 = new Handler();


        //리사이클러뷰 초기 세팅
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        chatRoomDataList = new ArrayList<>();
        mAdapterInF22 = new ChatRoomAdapterE(chatRoomDataList, getContext());
        recyclerView.setAdapter(mAdapterInF22);


        showChatRoomList(userSeq);
        recyclerView.setEmptyView(rootView.findViewById(R.id.list_empty));


        return rootView;
    }

    // 서버에 채팅방 목록 요청
    public void showChatRoomList(String expertSeq) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = serverAddress + "chat/chatRoomListE.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, tagServerToClient +  response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("data"); //JSONArray로 배열객체를 받아서, 변수에 담는다.
                    Log.i(TAG, "jsonArray = " + String.valueOf(jsonArray));

//                    chatRoomDataList = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String selectedExpertId = object.getString("selectedExpertId");
                        String userIdWhoRequest = object.getString("userIdWhoRequest");
                        String chatRoomNumber = object.getString("chatRoomNumber");
                        chatRoomDataList.add(new ChatRoomData(chatRoomNumber, selectedExpertId, userIdWhoRequest));
                    }

                    mAdapterInF22.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "JSON에서 전달받은 에러" + e);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // textView.setText("That didn't work!");
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
    public static class RefreshChatRoomListExpert implements Runnable {

        @Override
        public void run() {
            Log.i(TAG, "refreshChatRoomListClient : 실행");
            mAdapterInF22.notifyDataSetChanged();
//            mAdapterInF22.notifyItemChanged(0);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "프래그먼트 생명주기 onStart");
        mAdapterInF22.notifyDataSetChanged();
    }

}
