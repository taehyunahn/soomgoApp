package com.example.soomgodev;

import static com.example.soomgodev.StaticVariable.serverAddress;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.soomgodev.Fragment.UserMainActivity;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import AccountUpdate.AccountUpdatePassword;

public class FindPasswordCheck extends AppCompatActivity {

    // sharedPreference 기본설정 (변수 선언부)
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq, expertSeq;

    EditText et_oldPassword, et_newPassword, et_newPasswordCheck;
    Button btn_done;
    ImageButton ib_close;
    String passwordCode;
    private static final String TAG = "FindPasswordCheck";
    String userEmail = "";
    TextInputLayout til_newPasswordCheck, til_newPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password_check);
        getSupportActionBar().hide();


        et_newPassword = findViewById(R.id.et_newPassword);
        et_newPasswordCheck = findViewById(R.id.et_newPasswordCheck);
        btn_done = findViewById(R.id.btn_done);
        ib_close = findViewById(R.id.ib_close);
        til_newPasswordCheck =  findViewById(R.id.til_newPasswordCheck);
        til_newPassword =  findViewById(R.id.til_newPassword);

        // (onCreate 안에)
        sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        // 화면 닫기 버튼
        ib_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        if(intent.getStringExtra("email") != null){
            userEmail = intent.getStringExtra("email");
            Log.i(TAG, "userEmail = " + userEmail);
        }

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = et_newPassword.getText().toString();
                String newPasswordCheck = et_newPasswordCheck.getText().toString();

                if(!newPassword.equals(newPasswordCheck)){
                    Toast.makeText(FindPasswordCheck.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    updatePassword2(userEmail, newPassword);
                }


            }
        });

        // 1. 입력한 비밀번호와 비밀번호가 서로 일치하는지 확인한다
        // 2. 일치하다면, 아이디와 비밀번호를 서버에 보낸다
        // 3. 서버에서는 아이디를 기준으로 userInfo 테이블의 해당값을 update한다
        // 4. 서버통신이 완료되면 userSeq의 값을 가져오고, userMainActivity로 이동한다.


        //비밀번호 입력 형식이 잘못되면, 에러 띄움
        et_newPassword = til_newPassword.getEditText();
        et_newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

//                String a = et_password.getText().toString();
                // 대소문자 구분 숫자 특수문자  조합 9 ~ 12 자리
                String pwPattern = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,}$";
                Boolean tt = Pattern.matches(pwPattern,s);

                if(tt == false) {
                    til_newPassword.setError("비밀번호 형식이 잘못되었습니다. 영문+숫자 조합 8자리 이상 입력해주세요");
                } else {
                    til_newPassword.setError(null);
                }
            }
        });



        //위에 입력한 비밀번호와 '비밀번호 확인'에 입력한 값이 다를 경우 에러 메세지 띄움
        et_newPasswordCheck = til_newPasswordCheck.getEditText();
        et_newPasswordCheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!s.toString().equals(et_newPassword.getText().toString())) {
                    Log.e("'비밀번호'에 입력한 값 : ", et_newPassword.getText().toString());
                    Log.e("'비밀번호 확인'에 입력한 값 : ' ", s.toString());

                    til_newPasswordCheck.setError("비밀번호가 일치하지 않습니다.");
                } else {
                    til_newPasswordCheck.setError(null);
                }
            }
        });


    }


    private void updatePassword2(String userEmail, String newPassword) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = serverAddress + "updatePassword2.php";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response : ", response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String userSeq = jsonResponse.getString("userSeq");
                    String userName = jsonResponse.getString("userName");
                    String success = jsonResponse.getString("success");
                    if(success.equals("1")){
                        Toast.makeText(FindPasswordCheck.this, "비밀번호를 변경했습니다.", Toast.LENGTH_SHORT).show();
                        editor.putString("userSeq", userSeq);
                        editor.putString(userSeq + "userName", userName);
                        editor.commit();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), userName + "님, 환영합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FindPasswordCheck.this, "비밀번호 변경에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }

                    Log.i(TAG, "userSeq = " + userSeq);
                    Log.i(TAG, "userName = " + userName);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // textView.setText("That didn't work!");
                Log.e("error : ", String.valueOf(error));

            }
        }) {
            // 포스트 파라미터 넣기
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
//                        params.put("userSeq", userSeq);
                params.put("userEmail", userEmail);
                params.put("newPassword", newPassword);


                return params;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}