package com.example.soomgodev.Fragment;

import static com.example.soomgodev.StaticVariable.serverAddress;
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
import com.example.soomgodev.R;
import com.example.soomgodev.RecyclerViewEmptySupport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SurveyQuote.QuoteAdapter;
import SurveyQuote.QuoteData;
import SurveyRequest.RequestAdapter;
import SurveyRequest.RequestData;

// 받은 견적
public class Fragment3 extends Fragment {

    private static final String TAG = "Fragment3";
    // sharedPreference 기본설정 (변수 선언부)
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq, expertSeq;


    // 고수 목록 출력을 위한 RecyclerView 세팅
    QuoteAdapter adapter;
    List<QuoteData> quoteDataList = new ArrayList<>();
    private RecyclerViewEmptySupport recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_3, container, false);

        // (onCreate 안에)
        sharedPreferences = this.getActivity().getSharedPreferences("loginInfo",Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userSeq = sharedPreferences.getString("userSeq","");
        expertSeq = sharedPreferences.getString("expertSeq", "");


        recyclerView = rootView.findViewById(R.id.recyclerView);

        //리사이클러뷰 초기 세팅
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setEmptyView(rootView.findViewById(R.id.list_empty));

        quoteList(userSeq);

        return rootView;
    }

    // 서버에 견적서 목록 요청
    public void quoteList(String userSeq) {
        Log.i(TAG, "quoteList 1. 동작 시작");
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = serverAddress + "request/quoteList.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, tagServerToClient +  response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("data"); //JSONArray로 배열객체를 받아서, 변수에 담는다.
                    Log.i(TAG, "jsonArray = " + String.valueOf(jsonArray));

                    quoteDataList = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String expertImage = object.getString("expertImage");
                        String expertName = object.getString("expertName");
                        String reviewAverage = object.getString("reviewAverage");
                        String reviewCount = object.getString("reviewCount");
                        String hireCount = object.getString("hireCount");
                        String price = object.getString("price");
                        String roomNumber = object.getString("roomNumber");

                        Log.i(TAG, "expertName = " + expertName);
                        Log.i(TAG, "reviewAverage = " + reviewAverage);
                        Log.i(TAG, "reviewCount = " + reviewCount);
                        Log.i(TAG, "hireCount = " + hireCount);
                        Log.i(TAG, "price = " + price);

                        if(reviewAverage.equals("null")){
                            reviewAverage = "0";
                        }
                        quoteDataList.add(new QuoteData(expertImage, expertName, reviewAverage, reviewCount, hireCount, price, roomNumber));
                    }


                    mAdapter = new QuoteAdapter(quoteDataList, getContext());
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