package com.example.soomgodev.Fragment;

import static com.example.soomgodev.StaticVariable.serverAddress;
import static com.example.soomgodev.StaticVariable.tagClientToServer;
import static com.example.soomgodev.StaticVariable.tagIntentOutput;
import static com.example.soomgodev.StaticVariable.tagServerToClient;
import static com.example.soomgodev.StaticVariable.tagSharedPreferenceOutput;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.soomgodev.BackKeyHandler;
import com.example.soomgodev.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Chat.ChatService;
import ExpertUpdate.ExertUpdateAddress;

public class UserMainActivity extends AppCompatActivity{

    Fragment fragment1;
    Fragment fragment2;
    Fragment fragment3;
    Fragment fragment4;
    Fragment fragment5;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq;

    int mHour;
    int mMin;

    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    private static final String TAG = "UserMainActivity";

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();



        //fragment 객체 생성, 5개
        fragment1 = new Fragment1(); // user 홈
        fragment2 = new Fragment2(); // user 고수찾기
        fragment3 = new Fragment3(); // user 채팅
        fragment4 = new Fragment4(); // user 받은견적
        fragment5 = new Fragment5(); // user 마이페이지

        sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // sharedPreference에 저장된 계정고유번호(userSeq)를 변수에 담음 → 서버에서 계정 정보 얻는 기준으로 사용
        userSeq = sharedPreferences.getString("userSeq", "");
        Log.i(TAG, tagSharedPreferenceOutput + "userSeq = " + userSeq);

        // 이전 fragment가 몇 번째 fragment였는지 전달 받음
        Intent intent = getIntent();
        int position = intent.getIntExtra("position",0);
        Log.i(TAG, tagIntentOutput + "position = " + position);
        String serviceFromSearch = intent.getStringExtra("service");
        Boolean moveToFragment2 = intent.getBooleanExtra("moveToFragment2", false);
        Log.i(TAG, tagIntentOutput + "serviceFromSearch = " + serviceFromSearch);
        Log.i(TAG, tagIntentOutput + "moveToFragment2 = " + moveToFragment2);

        Log.i(TAG, "moveToFragment 직전?");


        // 끝


        // 하단바
        bottomNavigationView = findViewById(R.id.bottom_navigation);



        Log.i(TAG, "SearchAdapter에서 두 가지 값을 가져온다 boolean, service이름");
        Log.i(TAG, "SearchAdapter에서 두 가지 값을 가져온다 service이름 = " + serviceFromSearch);
        Log.i(TAG, "SearchAdapter에서 두 가지 값을 가져온다 boolean = " + moveToFragment2);

        if(moveToFragment2){
            Log.i(TAG, "SearchAdapter에서 값을 받고, moveToFragment2는 일단 동작한다");
//            Bundle bundle = new Bundle();
//            bundle.putString("service", serviceFromSearch);
//            fragment2.setArguments(bundle);

            editor.putString(userSeq+"searchService", serviceFromSearch);
            editor.commit();

            getSupportFragmentManager().beginTransaction().replace(R.id.scrollView, fragment2).commit();
            bottomNavigationView.setSelectedItemId(R.id.tab2);
//


        } else {


            // 임시로 작성
            if (position == 0) {
                getSupportFragmentManager().beginTransaction().replace(R.id.scrollView, fragment1).commit();

                // 이전 fragment에서 본인의 위치 5를 보냈다면,
            } else if (position == 5) {
                getSupportFragmentManager().beginTransaction().replace(R.id.scrollView, fragment5).commit();
                bottomNavigationView.setSelectedItemId(R.id.tab5);
            }

        }



        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tab1: // user 홈
                        getSupportFragmentManager().beginTransaction().replace(R.id.scrollView, fragment1).commit();
                        return true;
                    case R.id.tab2: // user 고수찾기
                        getSupportFragmentManager().beginTransaction().replace(R.id.scrollView, fragment2).commit();
                        return true;
                    case R.id.tab3: // user 채팅
                        getSupportFragmentManager().beginTransaction().replace(R.id.scrollView, fragment3).commit();
                        return true;
                    case R.id.tab4: // user 받은견적
                        getSupportFragmentManager().beginTransaction().replace(R.id.scrollView, fragment4).commit();
                        return true;
                    case R.id.tab5: // user 마이페이지
                        getSupportFragmentManager().beginTransaction().replace(R.id.scrollView, fragment5).commit();
                        return true;
                }
                return false;
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "onResume 동작");

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = serverAddress + "mainEnter.php";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, tagServerToClient + response);


                try {
                    // 2번 쿼리문을 통해 전달받은 JSON 객체를 통해, 이름, 이메일, 회원고유번호, 이미지값을 각 변수에 넣는다
                    JSONObject jsonResponse = new JSONObject(response);
                    String userNameFromServer = jsonResponse.getString("userName");
                    String userEmailFromServer = jsonResponse.getString("userEmail");
                    String userProfileImageFromServer = jsonResponse.getString("userProfileImage");
                    String expertId = jsonResponse.getString("expertId");

                    Bundle b = new Bundle();
                    b.putString("userNameFromServer", userNameFromServer);
                    b.putString("userEmailFromServer", userEmailFromServer);
                    b.putString("userProfileImageFromServer", userProfileImageFromServer);
                    fragment5.setArguments(b);



                    Log.i(TAG, tagServerToClient + "userNameFromServer = " + userNameFromServer);
                    Log.i(TAG, tagServerToClient + "userEmailFromServer = " + userEmailFromServer);
                    Log.i(TAG, tagServerToClient + "userProfileImageFromServer = " + userProfileImageFromServer);
                    Log.i(TAG, tagServerToClient + "expertId = " + expertId);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "JSONException : " + e);

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
            protected Map getParams()
            {
                Map params = new HashMap();
                Log.i(TAG, tagClientToServer + "userSeq = " + userSeq);
                params.put("userSeq", userSeq);

                return params;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);


    }

    @Override
    public void onBackPressed() {
        backKeyHandler.onBackPressed();
    }
}
