package com.example.soomgodev.Fragment;

import static com.example.soomgodev.StaticVariable.serverAddress;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
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

public class ExpertMainActivity extends AppCompatActivity{

    Fragment fragment21;
    Fragment fragment22;
    Fragment fragment23;
    Fragment fragment24;

    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq;

    private static final String TAG = "ExpertMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);
        getSupportActionBar().hide();

        Log.e("Main2Activity", "onCreate");

        fragment21 = new Fragment21();
        fragment22 = new Fragment22();
        fragment23 = new Fragment23();
        fragment24 = new Fragment24();

        sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userSeq = sharedPreferences.getString("userSeq", "");


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation2);



        Intent intent = getIntent();
        int position = intent.getIntExtra("position",0);
        Log.i(TAG, "ExpertMainActivity에서 보내고 Fragment5에 도착해서 받은 position = " + position);

        if (position == 0) {
            getSupportFragmentManager().beginTransaction().replace(R.id.scrollView, fragment21).commit();
        } else if (position == 4) {
            Log.i(TAG, "ExpertMainActivity에서 보내고 Fragment5에 도착해서 받은 position으로 Fragment24 띄움 ");
            getSupportFragmentManager().beginTransaction().replace(R.id.scrollView, fragment24).commit();
            bottomNavigationView.setSelectedItemId(R.id.tab24);
        }


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tab21:
                        getSupportFragmentManager().beginTransaction().replace(R.id.scrollView, fragment21).commit();
                        return true;
                    case R.id.tab22:
                        getSupportFragmentManager().beginTransaction().replace(R.id.scrollView, fragment22).commit();
                        return true;
                    case R.id.tab23:
                        getSupportFragmentManager().beginTransaction().replace(R.id.scrollView, fragment23).commit();
                        return true;
                    case R.id.tab24:
                        getSupportFragmentManager().beginTransaction().replace(R.id.scrollView, fragment24).commit();
                        return true;

                }
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // 서버통신 : 회원 고유값(userSeq)을 보내서, 그에 해당되는 칼럼 정보(userName, userEmail)를 찾아서 배열로 받아온다.
        //           이름과 이메일 텍스트에 세팅해준다.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = serverAddress + "expertUpdate.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("AccountUpdateActivity", "서버에서 전달받은 값" + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    String expertName = jsonResponse.getString("expertName");
                    String updateMainService = jsonResponse.getString("expertMainService");
                    String userProfileImage = jsonResponse.getString("userProfileImage");
                    String expertIntro = jsonResponse.getString("expertIntro");
                    String expertAddress = jsonResponse.getString("expertAddress");
                    String expertYear = jsonResponse.getString("expertYear");
                    String expertIntroDetail = jsonResponse.getString("expertIntroDetail");

                    String userName = jsonResponse.getString("userName");
                    String userEmail = jsonResponse.getString("userEmail");

                    Bundle b = new Bundle();
                    b.putString("expertName", expertName);
                    b.putString("updateMainService", updateMainService);
                    b.putString("userProfileImage", userProfileImage);
                    b.putString("expertIntro", expertIntro);
                    b.putString("expertAddress", expertAddress);
                    b.putString("expertYear", expertYear);
                    b.putString("expertIntroDetail", expertIntroDetail);
                    fragment23.setArguments(b);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("AccountUpdateActivity", "JSON에서 전달받은 에러" + e);
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




//        tv_name = findViewById(R.id.tv_name);
//        button6 = findViewById(R.id.button6);
//        button10 = findViewById(R.id.button10);
//
//
//        button6.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intent);            }
//        });
//
//        button10.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
//                startActivity(intent);            }
//        });
//
//        Intent intent = getIntent();
//        String userEmail = intent.getStringExtra("userEmail");
//
//        tv_name.setText(userEmail);


