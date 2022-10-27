package com.example.soomgodev;

import static com.example.soomgodev.StaticVariable.serverAddress;
import static com.example.soomgodev.StaticVariable.tagClientToServer;
import static com.example.soomgodev.StaticVariable.tagServerToClient;
import static com.example.soomgodev.StaticVariable.tagSharedPreferenceInput;
import static com.example.soomgodev.StaticVariable.tagSharedPreferenceOutput;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.soomgodev.Fragment.ExpertMainActivity;
import com.example.soomgodev.Fragment.UserMainActivity;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Chat.ChatRoomActivity;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

// 클래스 설명 : 로그인 절차

public class LoginActivity extends AppCompatActivity {
    private EditText et_email, et_password;
    private Button btn_login, btn_signUp, btn_test;
    private ImageButton btn_kakao;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static final String TAG = "LoginActivity";
    String userProfileImage;
    TextView tv_findPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        et_email = (EditText) findViewById(R.id.et_email); // 이메일 입력창
        et_password = (EditText) findViewById(R.id.et_password); // 비밀번호 입력
        btn_login = (Button) findViewById(R.id.btn_login); // 이메일로그인 버튼
        btn_signUp = (Button) findViewById(R.id.btn_signUp); // 회원가입 버튼
        btn_kakao = (ImageButton) findViewById(R.id.btn_kakao); // 카카오로그인 버튼
        tv_findPassword = findViewById(R.id.tv_findPassword); // 비밀번호 찾기
        btn_test = findViewById(R.id.btn_test); // 테스트용



        tv_findPassword.setPaintFlags(tv_findPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tv_findPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityC(FindPasswordActivity.class); // 비밀번호 찾기 화면으로 이동
            }
        });

        // sharedPreference 세팅
        sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // sharedPreference에 저장된 회원고유값(UserSeq)이 있는지 확인
        String userSeq = sharedPreferences.getString("userSeq", "");
        String userName = sharedPreferences.getString(userSeq + "userName", "");
        String expertName = sharedPreferences.getString(userSeq + "expertName", "");
        String clientOrExpert = sharedPreferences.getString(userSeq+"clientOrExpert", "client");

        Log.i(TAG, "로그인할 때 userSeq + " + userSeq);
        Log.i(TAG, "로그인할 때 userName + " + userName);
        Log.i(TAG, "로그인할 때 expertName + " + expertName);

        Log.i(TAG, "로그인할 때 clientOrExpert + " + clientOrExpert);

        Log.i(TAG, tagSharedPreferenceOutput + "userSeq = " + userSeq);
        // sharedPreference가 null이 아니면, 로그아웃하지 않고 앱을 종료했다는 의미.
        if(!userSeq.equals("")) {
            // 채팅 소켓 서비스 가동 시키는 타이밍
            Intent intentService = new Intent(getApplicationContext(), Chat.ChatService.class);
            intentService.putExtra("userSeq", userSeq);
            intentService.putExtra("userName", userName);
            intentService.putExtra("expertName", expertName);
            startService(intentService);

            if(clientOrExpert.equals("expert")){
                // 고수의 'MainActivity로 이동
                startActivityNewTask(ExpertMainActivity.class);
            } else {
                // 고객의 '홈' 액티비티로 이동
                startActivityNewTask(UserMainActivity.class);
            }
            Toast.makeText(getApplicationContext(), "자동로그인됐습니다.", Toast.LENGTH_SHORT).show();

        }

        // 이메일 로그인 버튼 활성여부는 최초 비활성 -> 이메일 입력칸에 값을 넣으면 활성화 되도록 설정
        btn_login.setEnabled(false);

        // 이메일 입력창 안에 값이 입력된지 확인하는 리스너
        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                // 이메일 입력칸에 값이 입력된 경우, '이메일 로그인' 버튼 활성화
                btn_login.setEnabled(true);
                } else {
                // 이메일 입력칸에 값이 입력되지 않은 경우, '이메일 로그인' 버튼 비활성화
                btn_login.setEnabled(false);
                }
                }
             });



        // '회원가입' 버튼 클릭 -> '회원가입' 액티비티로 이동
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityC(SignUpActivity.class);
            }
        });

        // 이메일 로그인 버튼 클릭시
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이메일, 비밀번호에 입력된 값을 변수(userEmail, userPassword)에 담는다
                String userEmail = et_email.getText().toString();
                String userPassword = et_password.getText().toString();
                Log.e("버튼 클릭 여부", "Yes");

                // 서버 통신 : 로그인 정보 조회하는 쿼리문 동작 (2가지 쿼리문 존재)
                // 1) 입력한 이메일과 비밀번호 세트를 서버DB에 회원정보와 대조하여 진행 여부 결정
                // 2) 이메일을 기준으로 이름, 이메일, 회원고유번호, 이미지값을 전달한다
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url = serverAddress + "loginNew.php";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("0")) {
                            Log.i(TAG, tagServerToClient + response);
                            Toast.makeText(getApplicationContext(), "로그인 정보를 확인하세요", Toast.LENGTH_SHORT).show();
                        } else {
//                            Log.i(TAG, tagServerToClient + response);
//                            Log.i(TAG, tagSharedPreferenceInput + "userSeq = " + response);

                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            String userSeq = jsonResponse.getString("userSeq");
                            String userName = jsonResponse.getString("userName");
                            String resign = jsonResponse.getString("resign"); // 회원탈퇴 여부 확인
                            Log.i(TAG, "userSeq = " + userSeq);
                            Log.i(TAG, "userName = " + userName);
                            Log.i(TAG, "resign = " + resign);



                            if(resign.equals("resign")){
                                Toast.makeText(LoginActivity.this, "회원탈퇴한 계정입니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                editor.putString("userSeq", userSeq);
                                editor.putString("userName", userName);
                                editor.commit();

                                // 채팅 소켓 서비스 가동 시키는 타이밍
                                Intent intentService = new Intent(getApplicationContext(), Chat.ChatService.class);
                                intentService.putExtra("userSeq", userSeq);
                                intentService.putExtra("userName", userName);
                                startService(intentService);


                                if (jsonResponse.getString("userStatus").equals("expert")) {
                                    startActivityNewTask(ExpertMainActivity.class);
                                    Toast.makeText(getApplicationContext(), userName + "님, 환영합니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivityNewTask(UserMainActivity.class);
                                }
                            }




                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i(TAG, tagServerToClient + e);
                        }
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "에러 : " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    // 포스트 파라미터 넣기
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();

                        // 로그인 입력창에 넣은 이메일, 비밀번호를 서버에 전송
                        params.put("userEmail", userEmail);
                        params.put("userPassword", userPassword);

                        Log.i(TAG, tagClientToServer + "userEmail = " + userEmail);
                        Log.i(TAG, tagClientToServer + "userPassword = " + userPassword);

                        return params;
                    }
                };
                queue.add(stringRequest);
            }
        });


        // 카카오톡 로그인 버튼 클릭 (카카오톡 설치 여부에 따라 로그인 방식이 달라짐)
        btn_kakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 로그인 버튼 클릭 시, 결과에 대한 처리를 콜백 객체로 만듬
                Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
                    @Override
                    public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                        if (oAuthToken != null) {
                            // 토큰이 전달되어, 로그인 성공 시 -> 연결된 창에서 로그인 정보 모두 입력한 경우
                            Log.e("토큰이 전달되어 성공시 :", "yes");
                            updatedKakaoLoginUi();
                        }
                        if (throwable != null) {
                            // 토큰이 전달되어, 로그인 실패 시 -> 연결된 창을 닫아버린 경우
                            Log.e("토큰이 전달되어 실패시 :", "no");
                        }
                        return null;
                    }
                };

                // 기기에 카카오톡이 설치 되어있는지 확인 : true -> 설치된 경우
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this)) {
                    //카카오가 설치된 경우는 loginWithKakaoTalk
                    UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this, callback);
                } else {
                    //카카오가 설치되지 않은 경우는 loginWithKakaoAccount
                    UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, callback);

                }
            }
        });
    }

    // 사용자가 카카오톡 로그인이 되었있는지를 확인
    private void updatedKakaoLoginUi () {
        // 로그인 여부 확인
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {

            // invoke라는 메서드를 콜백으로 호출
            @Override
            public Unit invoke(User user, Throwable throwable) {
                // 로그인이 되어있다면,
                if (user != null) {
                    String userKakaoId = String.valueOf(user.getId());
                    Log.e("LoginActivity", "카카오 아이디 확인"+userKakaoId);

                    String userEmail = user.getKakaoAccount().getEmail();
                    String userNameFromKakao = user.getKakaoAccount().getProfile().getNickname();
                    String userProfileImage = user.getKakaoAccount().getProfile().getThumbnailImageUrl();

                    //만약에 현재 카카오 로그인한 아이디값이 DB에 있다면, 카카오아이디값에 해당하는 seq를 받아와서 sharedPreference에 저장한다.
                    //만약에 현재 카카오 로그인한 아이디값이 DB에 없다면, DB에 카카오아이디값, 이름, 프로필URL, 이메일을 보낸다

                    //카카오로 로그인이 성공했을 , DB에 아이디를 생성한다
                    //조건문으로 id 값에 해당되는 회원정보가 없을 때에만 생성하도록 한다.
                    //id 값에 해당되는 회원정보가 있다면, 그 id에 해당되는 seq 값을 가져와서 sharedPreference로 저장한다.

                    // 서버통신 : 이미 회원정보가 등록되어있는지를 확인하여, -> kakaoId를 보내서 db 데이터 조회 (모든 정보 같이 보냄)
                    //           1) 있으면, userSeq만 전달받아서 sharedPreference에 저장한다
                    //           2) 없으면, 이메일/이름/프로필이미지/카카오아이디로 계정을 등록한다. 그리고 userSeq 전달받아 s.p 저장
                   RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    String url = "http://54.180.133.35/loginKakao.php";

                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.i(TAG, tagServerToClient + response);

                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                Log.i(TAG, tagServerToClient + jsonResponse.getString("userSeq"));
//                                Log.i(TAG, tagServerToClient + jsonResponse.getString("userStatus"));
                                Log.i(TAG, tagServerToClient + jsonResponse.getString("userName"));

                                String userSeq = jsonResponse.getString("userSeq");
                                String userName = jsonResponse.getString("userName");

                                editor.putString("userSeq", userSeq);
                                editor.putString("userName", userName);
                                editor.commit();
//                                if (jsonResponse.getString("userStatus").equals("expert")) {
//                                    startActivityNewTask(ExpertMainActivity.class);
//                                } else {
//                                    startActivityNewTask(UserMainActivity.class);
//                                }

                                // 채팅 소켓 서비스 가동 시키는 타이밍
                                Intent intentService = new Intent(getApplicationContext(), Chat.ChatService.class);
                                intentService.putExtra("userSeq", userSeq);
                                intentService.putExtra("userName", userName);
                                startService(intentService);


                                startActivityNewTask(UserMainActivity.class);
                                Toast.makeText(getApplicationContext(), userName + "님, 환영합니다.", Toast.LENGTH_SHORT).show();



                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.i(TAG, tagServerToClient + e);
                                editor.putString("userSeq", response);
                                editor.commit();
                                startActivityNewTask(UserMainActivity.class);
                                Toast.makeText(getApplicationContext(), "로그인하셨습니다.", Toast.LENGTH_SHORT).show();


                            }


                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "에러 : " + error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        // 포스트 파라미터 넣기
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("userEmail", userEmail);
                            params.put("userKakaoId", userKakaoId);
                            params.put("userName", userNameFromKakao);
                            params.put("userProfileImage", userProfileImage);

                            return params;
                        }

                    };

                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);



                } else {
//                    name.setText(null);
//                    profileImage.setImageURI(null);
                }
                return null;
            }
        });

    }

    // 인텐트 액티비티 전환함수
    public void startActivityC(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }
    // 인텐트 화면전환 하는 함수
    // FLAG_ACTIVITY_CLEAR_TOP = 불러올 액티비티 위에 쌓인 액티비티 지운다.
    public void startActivityflag(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

    // 문자열 인텐트 전달 함수
    public void startActivityString(Class c, String name , String sendString) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.putExtra(name, sendString);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

    // 백스택 지우고 새로 만들어 전달
    public void startActivityNewTask(Class c){
        Intent intent = new Intent(getApplicationContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // sharedPreference 기본설정 (변수 선언부)
        SharedPreferences sharedPreferences;

        // (onCreate 안에)
        sharedPreferences = this.getSharedPreferences("loginInfo", ChatRoomActivity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.remove("currentRoomNumber");
        editor.commit();

        Log.i(TAG, "onStop() 동작 후 sharedPreference에 저장된 currentRoomNumber = " + sharedPreferences.getString("currentRoomNumber",""));

    }



}