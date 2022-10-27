package com.example.soomgodev.Fragment;

import static com.example.soomgodev.StaticVariable.serverAddress;
import static com.example.soomgodev.StaticVariable.tagClientToServer;
import static com.example.soomgodev.StaticVariable.tagServerToClient;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.soomgodev.ExpertAdapter;
import com.example.soomgodev.ExpertAdapter2;
import com.example.soomgodev.ExpertData;
import com.example.soomgodev.R;
import com.example.soomgodev.RecyclerViewEmptySupport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ConnectToServer.NetworkClient;
import ConnectToServer.UploadApis;
import SurveyRequest.RequestAdapter;
import SurveyRequest.RequestData;


// (고수가) 받은요청 → 고객이 요청서를 보내면(HTTP통신), 요청서가 여기에서 출력된다.
public class Fragment21 extends Fragment {

    private static final String TAG = "Fragment21";

    // sharedPreference 기본설정
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq;

    // 레트로핏 통신 공통
    NetworkClient networkClient;
    private UploadApis uploadApis;

    // 고수 목록 출력을 위한 RecyclerView 세팅
    ExpertAdapter adapter;
    List<RequestData> requestDataList = new ArrayList<>();
    private RecyclerViewEmptySupport recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment2_1, container, false);

        // (프래그먼트에서) sharedPreference 기본설정
        sharedPreferences = this.getActivity().getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userSeq = sharedPreferences.getString("userSeq","");

        recyclerView = rootView.findViewById(R.id.recyclerView);

        //리사이클러뷰 초기 세팅
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        requestList(userSeq);

        recyclerView.setEmptyView(rootView.findViewById(R.id.list_empty));



        return rootView;
    }


    // 서버에 요청서 목록 요청
    public void requestList(String userSeq) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = serverAddress + "request/requestList.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, tagServerToClient +  response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("data"); //JSONArray로 배열객체를 받아서, 변수에 담는다.
                    Log.i(TAG, tagServerToClient + "jsonArray : " + jsonArray);
                    Log.i(TAG, tagServerToClient + "jsonArray.length() : " + jsonArray.length());

                    // ArrayList를 선언한다.
                    requestDataList = new ArrayList<>();
                    String expertListCount = String.valueOf(jsonArray.length());

                    // 전달받은 배열 객체의 길이만큼 반복문을 실행한다
                    // 1. 고수이름, 고수사진, 한줄소개를 Person 객체에 담아서 ArrayList에 추가한다

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String addressInfo = object.getString("addressInfo");
                        String selectedExpertId = object.getString("selectedExpertId"); // 요청 받은 고수의 ID
                        String userIdWhoRequest = object.getString("userIdWhoRequest"); // 요청한 고객의 ID
                        String serviceRequested = object.getString("serviceRequested");
                        String requestDate = object.getString("requestDate"); // DB 테이블에서 자동 생성된 값,
                        String clientName = object.getString("clientName"); // DB 테이블에서 자동 생성된 값,

                        long curTime = System.currentTimeMillis();
                        Date curDate = new Date(curTime);
                        Log.i(TAG, "curTime = " + String.valueOf(curTime));
                        Log.i(TAG, "curDate = " + String.valueOf(curDate));

                        requestDataList.add(new RequestData(serviceRequested, addressInfo, requestDate, selectedExpertId, userIdWhoRequest, clientName));
                    }

                    Log.i(TAG, "requestDataList.size() = "+String.valueOf(requestDataList.size()));
                    Log.i(TAG, "requestDataList = " + String.valueOf(requestDataList));
                    mAdapter = new RequestAdapter(requestDataList, getContext());
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();

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


}