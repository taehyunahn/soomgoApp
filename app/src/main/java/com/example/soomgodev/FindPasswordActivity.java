package com.example.soomgodev;

import static com.example.soomgodev.StaticVariable.serverAddress;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class FindPasswordActivity extends AppCompatActivity {

    ImageButton ib_close;
    EditText et_userEmail, et_passwordCode;
    Button btn_send, btn_next, btn_resend;
    TextView tv_test, tv_timer;
    LinearLayout codeContainer;
    Handler handler = new Handler();
    int i;

    private static final String TAG = "FindPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        getSupportActionBar().hide();


        ib_close = findViewById(R.id.ib_close); //현재 화면 닫기
        et_userEmail = findViewById(R.id.et_userEmail); // 이메일 입력
        btn_send = findViewById(R.id.btn_send); // 이메일 전송 버튼
        btn_next = findViewById(R.id.btn_next); // 테스트용 텍스트뷰
        codeContainer = findViewById(R.id.codeContainer); // 테스트용 텍스트뷰
        et_passwordCode = findViewById(R.id.et_passwordCode);
        tv_timer = findViewById(R.id.tv_timer);
        btn_resend = findViewById(R.id.btn_resend);

        ib_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // 이메일 전송 버튼 클릭 시, 해당 주소로 난수 생성해서 이메일 발송
        btn_send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String inputEmail = et_userEmail.getText().toString(); // 입력한 이메일 String값
                // HTTP통신으로 input Email값 전달하기
                sendEmailAddress(inputEmail);


            }
        });

        btn_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_passwordCode.setEnabled(true);
                et_passwordCode.setText("");
                String inputEmail = et_userEmail.getText().toString(); // 입력한 이메일 String값
                // HTTP통신으로 input Email값 전달하기
                sendEmailAddress(inputEmail);
            }
        });

        //인증번호를 입력하고, 다음으로 넘어가는 버튼
        // 1. 서버에 이메일 주소와 인증번호를 보낸다
        // 2. 서버의 passwordCode 테이블에서 이메일과 날짜 기준으로 가장 최근에 만들어진 인증번호 값을 변수에 넣는다
        // 3. 클라이언트에서 받은 인증번호와 해당 값이 동일한지 비교한다. 맞다면, 0, 틀리다면 1.
        // 4. 맞다면(0) 다음 버튼으로 이동할 수 있다. 현재 입력한 이메일 주소를 인텐트로 보낸다
        // 5. 틀리다면(1) 다음 버튼으로 이동되지 않고, 인증번호가 틀렸다는 토스트 메세지를 띄운다.
        // 6. 인텐트로 이메일 주소를 보내서, 이메일 주소를 기준으로 비밀번호를 update한다.
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codeInput = et_passwordCode.getText().toString(); // 입력한 인증코드
                String inputEmail = et_userEmail.getText().toString(); // 입력한 이메일
                checkPasswordCode(codeInput, inputEmail);


            }
        });
    }

    private void sendEmailAddress(String emailAddress){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = serverAddress + "index.php";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i(TAG, "response1 = " + response);
                if(response.equals("0")){ // 인증서 발송 실패
                    Log.i(TAG, "response2 = " + response);
                    Toast.makeText(FindPasswordActivity.this, "가입되지 않은 이메일입니다.", Toast.LENGTH_SHORT).show();

                } else { // 인증서 발송 성공
                    Log.i(TAG, "response3 = " + response);
                    Toast.makeText(FindPasswordActivity.this, "이메일로 인증코드를 발송했습니다.", Toast.LENGTH_SHORT).show();
                    codeContainer.setVisibility(View.VISIBLE);
                    et_userEmail.setEnabled(false);
                    btn_resend.setVisibility(View.INVISIBLE);
                    btn_next.setVisibility(View.VISIBLE);
                    btn_send.setVisibility(View.INVISIBLE);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (i = 60; i >= 0; i--){
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv_timer.setText("남은 시간 : "+i + "초");
                                        if(tv_timer.getText().toString().equals("남은 시간 : 0초")){
                                            btn_next.setVisibility(View.INVISIBLE);
                                            btn_resend.setVisibility(View.VISIBLE);
                                            // 새로운 인증코드를 서버에 추가하는 로직을 넣는다.
                                            // 버튼을 인증번호 재전송으로 변경한다.
                                            et_passwordCode.setText("인증번호를 다시 요청해주세요");
                                            et_passwordCode.setEnabled(false);
                                            tv_timer.setText("인증번호 만료");
                                            expirePasswordCode(emailAddress);
                                        }
                                    }
                                });

                                try{
                                    Thread.sleep(1000);
                                }catch (InterruptedException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();


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
                params.put("emailAddress", emailAddress);

                return params;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    private void checkPasswordCode(String passwordCode, String email){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = serverAddress + "index2.php";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i(TAG, "response = " + response);
                String receivedCode = response;
//                String receivedCode = response.substring(0, response.length() - 1);
                String inputEmail = et_userEmail.getText().toString(); // 입력한 이메일
                String codeInput = et_passwordCode.getText().toString(); // 입력한 인증코드

                Log.i(TAG, "receivedCode = " + receivedCode);
                Log.i(TAG, "codeInput = " + codeInput);

                if(!receivedCode.equals(codeInput)){
                    Toast.makeText(FindPasswordActivity.this, "인증코드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), FindPasswordCheck.class);
                    intent.putExtra("email", inputEmail);
                    startActivity(intent);
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
                params.put("passwordCode", passwordCode);
                params.put("email", email);

                return params;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void expirePasswordCode(String email){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = serverAddress + "expirePasswordCode.php";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "response = " + response);
                String receivedCode = response;

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
                params.put("email", email);
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    @Override
    protected void onStop() {
        super.onStop();

        codeContainer.setVisibility(View.INVISIBLE);
        et_userEmail.setEnabled(true);
        btn_next.setVisibility(View.INVISIBLE);
        btn_send.setVisibility(View.VISIBLE);

    }

}