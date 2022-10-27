package com.example.soomgodev;

import static com.example.soomgodev.StaticVariable.tagServerToClient;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import SurveyExpert.ExpertSurveyMainActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText et_name, et_email, et_password, et_passwordCheck;
    private Button btn_signUp;
    private CheckBox cb_agree1, cb_agree2;
    private TextInputLayout text_input_layout_name, text_input_layout_password, text_input_layout_passwordCheck, text_input_layout_email;
    boolean agree1, agree2;
    private String userOption = "";
    private RadioButton rb_user, rb_expert;
    private boolean validate;
    private String userEmail;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private static final String TAG = "SignUpActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();


        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_passwordCheck = findViewById(R.id.et_passwordCheck);
        cb_agree1 = findViewById(R.id.cb_agree1);
        cb_agree2 = findViewById(R.id.cb_agree2);
        btn_signUp = findViewById(R.id.btn_signUp);

        // sharedPreference 세팅
        sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        btn_signUp.setEnabled(false);

        rb_user = findViewById(R.id.rb_user);
        rb_user.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userOption = "고객";
                Log.e("resultText : ", userOption);
            }
        });

        rb_expert = findViewById(R.id.rb_expert);
        rb_expert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userOption = "고수";
                Log.e("resultText : ", userOption);
            }
        });


        //이용약관 동의 체크박스
        cb_agree1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cb_agree1.isChecked() && cb_agree2.isChecked()) {
                    btn_signUp.setEnabled(true);

                } else if(!cb_agree1.isChecked() || !cb_agree2.isChecked()){
                    btn_signUp.setEnabled(false);
                }
            }
        });

        //만 14세 이상 체크박스
        cb_agree2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cb_agree1.isChecked() && cb_agree2.isChecked()) {
                    btn_signUp.setEnabled(true);

                } else if(!cb_agree1.isChecked() || !cb_agree2.isChecked()){
                    btn_signUp.setEnabled(false);
                }
            }
        });


        //이메일 정규식을 따라 입력하지 않으면, 에러 메세지 띄움
        text_input_layout_email = findViewById(R.id.text_input_layout_email);
        et_email = text_input_layout_email.getEditText();
        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Pattern pattern = Patterns.EMAIL_ADDRESS;
                Matcher matcher = pattern.matcher(s.toString());
                if(matcher.find()){
                    text_input_layout_email.setError(null);
                }else{
                    text_input_layout_email.setError("올바른 이메일 주소를 입력해주세요");
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse=new JSONObject(response);
                            boolean success=jsonResponse.getBoolean("success");
                            if(success){
//                                text_input_layout_email.setError("사용할 수 있는 이메일입니다");
                                validate = true;
                            }
                            else{
                                text_input_layout_email.setError("중복된 이메일입니다");
                                validate = false;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                userEmail = et_email.getText().toString();

                ValidateRequest validateRequest = new ValidateRequest(userEmail,responseListener);
                RequestQueue queue= Volley.newRequestQueue(SignUpActivity.this);
                queue.add(validateRequest);
            }
        });

        //비밀번호 입력 형식이 잘못되면, 에러 띄움
        text_input_layout_password = findViewById(R.id.text_input_layout_password);
        et_password = text_input_layout_password.getEditText();
        et_password.addTextChangedListener(new TextWatcher() {
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
                    text_input_layout_password.setError("비밀번호 형식이 잘못되었습니다. 영문+숫자 조합 8자리 이상 입력해주세요");
                } else {
                    text_input_layout_password.setError(null);
                }
            }
        });

        //위에 입력한 비밀번호와 '비밀번호 확인'에 입력한 값이 다를 경우 에러 메세지 띄움
        text_input_layout_passwordCheck = findViewById(R.id.text_input_layout_passwordCheck);
        et_passwordCheck = text_input_layout_passwordCheck.getEditText();
        et_passwordCheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!s.toString().equals(et_password.getText().toString())) {
                    Log.e("'비밀번호'에 입력한 값 : ", et_password.getText().toString());
                    Log.e("'비밀번호 확인'에 입력한 값 : ' ", s.toString());

                    text_input_layout_passwordCheck.setError("비밀번호가 일치하지 않습니다.");
                } else {
                    text_input_layout_passwordCheck.setError(null);
                }
            }
        });




        // 회원가입 버튼 클릭 시 수행
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(TAG, "회원가입 버튼을 클릭했습니다.");

                String userName = et_name.getText().toString();
                userEmail = et_email.getText().toString();
                String userPassword = et_password.getText().toString();
                String userPasswordCheck = et_passwordCheck.getText().toString();



                String a = et_password.getText().toString();
                // 대소문자 구분 숫자 특수문자  조합 9 ~ 12 자리
                String pwPattern = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,}$";
                Boolean tt = Pattern.matches(pwPattern,a);

                if(userName.length() != 0 && userEmail.length() != 0 && userPassword.length() != 0 && userPasswordCheck.length() != 0) {


                    if(!userEmail.contains("@")) {
                        Toast.makeText(getApplicationContext(), "올바른 이메일 주소를 입력해주세요", Toast.LENGTH_SHORT).show();
                        et_email.requestFocus();
                        return;
                    } else if(!userPassword.equals(userPasswordCheck)) {
                        Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        et_passwordCheck.requestFocus();
                        return;
                    } else if(tt == false) {
                        Toast.makeText(getApplicationContext(), "비밀번호 형식이 잘못되었습니다. 영문+숫자 조합 8자리 이상 입력해주세요", Toast.LENGTH_SHORT).show();
                        et_password.requestFocus();
                        return;
                    }
//                    else if (validate == false) {
//                        Toast.makeText(getApplicationContext(), "중복된 이메일입니다.", Toast.LENGTH_SHORT).show();
//                        et_email.requestFocus();
//                        return;
//                    }

                } else {
                    if(userName.length() == 0) {
                        Toast.makeText(getApplicationContext(), "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                        et_name.requestFocus();
                        return;
                    }

                    if(userEmail.length() == 0) {
                        Toast.makeText(getApplicationContext(), "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
                        et_email.requestFocus();
                        return;
                    }

                    if(userPassword.length() == 0) {
                        Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                        et_password.requestFocus();
                        return;
                    }

                    if(userPasswordCheck.length() == 0) {
                        Toast.makeText(getApplicationContext(), "비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
                        et_passwordCheck.requestFocus();
                        return;
                    }
                }


//
//                if(tt == false) {
//                    text_input_layout_password.setError("비밀번호 형식이 잘못되었습니다. 영문+숫자 조합 8자리 이상 입력해주세요");
//                } else {
//                    text_input_layout_password.setError(null);
//                }




                Log.i(TAG, "서버로 무언가 보내기 직전입니다.");
                Log.i(TAG, "userName = " + userName);
                Log.i(TAG, "userEmail = " + userEmail);
                Log.i(TAG, "userPassword = " + userPassword);


                // 회원가입 버튼 클릭 시, 입력값을 서버로 전송

                String url = "http://54.180.133.35/signUp.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //고수로 회원가입한 경우, 로그인 후 메인 expertSurveyMain으로 이동 (sharedF로 seq 보내줄 것)

                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            String userSeq = jsonResponse.getString("userSeq");
                            String userName = jsonResponse.getString("userName");

                            Log.i(TAG, tagServerToClient + userSeq);
                            Log.i(TAG, tagServerToClient + userName);

                            if(rb_expert.isChecked()) {
                                editor.putString("userSeq", userSeq);
                                editor.putString(userSeq + "userName", userName);
                                editor.commit();
                                Log.e("SignUpActivity", "회원가입 완료 후 전달받은 response : " + response);


                                startActivityNewTask(ExpertSurveyMainActivity.class);
                            }
                            //고객으로 회원가입한 경우, 로그인 후 메인 activity로 이동 (sharedF로 seq 보내줄 것)
                            if(rb_user.isChecked()) {
                                editor.putString("userSeq", userSeq);
                                editor.putString(userSeq + "userName", userName);
                                editor.commit();
                                Log.e("SignUpActivity", "회원가입 완료 후 전달받은 response : " + response);
                                startActivityNewTask(UserMainActivity.class);
                                Toast.makeText(getApplicationContext(), userName + "님, 환영합니다.", Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i(TAG, tagServerToClient + e);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "에러 : " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("userName", userName);
                        params.put("userEmail", userEmail);
                        params.put("userPassword", userPassword);
                        params.put("userOption", userOption);

                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
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

}


