package SurveyRequest;

import static com.example.soomgodev.StaticVariable.serverAddress;
import static com.example.soomgodev.StaticVariable.tagServerToClient;


import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.soomgodev.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Dialog.CustomDialog;

public class RequestDetail extends AppCompatActivity {

    TextView tv_age, tv_how, tv_day, tv_schedule, tv_gender, tv_address, tv_question;
    private static final String TAG = "RequestDetail";

    // sharedPreference 기본설정
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq, expertSeq;
    Button btn_sendQuote, btn_cancelQuote;
    String requestId, quoteId, chatroomId, userIdWhoRequest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);
        getSupportActionBar().hide();

        tv_age = findViewById(R.id.tv_age);
        tv_how = findViewById(R.id.tv_how);
        tv_day = findViewById(R.id.tv_day);
        tv_schedule = findViewById(R.id.tv_schedule);
        tv_gender = findViewById(R.id.tv_gender);
        tv_address = findViewById(R.id.tv_address);
        tv_question = findViewById(R.id.tv_question);
        btn_sendQuote = findViewById(R.id.btn_sendQuote);
        btn_cancelQuote = findViewById(R.id.btn_cancelQuote);

        // (onCreate 안에) 현재 로그인 정보를 얻는 용도
        sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userSeq = sharedPreferences.getString("userSeq","");
        expertSeq = sharedPreferences.getString("expertSeq", "");

        // 견적서를 보내고나면, 견적서 버튼을 enable(false)하기 위해 s.f로 저장하려고함.
        SharedPreferences sf = getSharedPreferences("quote", Activity.MODE_PRIVATE);
        Boolean sent = sf.getBoolean("sent", false);

        Intent intent = getIntent();
        String expertId = intent.getStringExtra("expertId");

        // 내가 얻으려는 요청서를 받을 expertId를 보내고, 해당 요청서의 세부내용을 전달 받는다.
        requestDetail(expertId);

        // 견적 보내기 취소 버튼
        btn_cancelQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 견적 취소. DB에서 Delete한다.
                btn_cancelQuote.setVisibility(View.GONE);
                deleteQuote(quoteId, chatroomId);

                Log.i(TAG, "quoteId = " + quoteId);
                Log.i(TAG, "chatroomId = " + chatroomId);
                btn_sendQuote.setVisibility(View.VISIBLE);

            }
        });

        // sharedF에 보냈다고 저장했으면, enable(false)를 해라
//        if(sent) {
//            btn_sendQuote.setEnabled(false);
//            btn_sendQuote.setText("이미 견적을 보냈습니다.");
//        }
    }

    //activity_main.xml 에서 show_dialog_btn 의 onclick 메소드
    // 견적 금액을 입력하는 다이얼로그를 띄우고, 입력하면 견적서를 서버제 저장하도록 하는 함수가 동작한다.
    public void show_quote_dialog(View v){
        //클릭시 defaultDialog 를 띄워준다
        CustomDialog.getInstance(this).showQuoteDialog(requestId, userIdWhoRequest, chatroomId, getApplicationContext());
        Log.i("test", "여기까지 오는지 확인");

    }

    // 서버에 요청서 목록 요청
    public void requestDetail(String expertId) {
        Log.i(TAG, "서버에 요청서 목록 요청");
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = serverAddress + "request/requestDetail.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "서버에 요청서 목록 요청" +  response);

                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(response);
                    String age = jsonResponse.getString("age");
                    String how = jsonResponse.getString("how");
                    String day = jsonResponse.getString("day");
                    String schedule = jsonResponse.getString("schedule");
                    String gender = jsonResponse.getString("gender");
                    String addressInfo = jsonResponse.getString("addressInfo");
                    String question = jsonResponse.getString("question");
                    requestId = jsonResponse.getString("requestId"); // request 테이블의 행 고유값
                    quoteId = jsonResponse.getString("quoteId"); // quote 테이블의 행 고유값
                    chatroomId = jsonResponse.getString("chatroomId"); //
                    userIdWhoRequest = jsonResponse.getString("userIdWhoRequest"); //


                    tv_age.setText(age);
                    tv_how.setText(how);
                    tv_day.setText(day);
                    tv_schedule.setText(schedule);
                    tv_gender.setText(gender);
                    tv_address.setText(addressInfo);
                    tv_question.setText(question);


                    String quoteSent = sharedPreferences.getString(requestId, "");
                    Log.i(TAG, "quoteSent = "+quoteSent);

                    if (quoteSent.equals("sent")){
                        btn_sendQuote.setVisibility(View.GONE);
                        btn_cancelQuote.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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
                params.put("expertId", expertId);
                Log.i(TAG, "(클라이언트→서버)expertId = " + expertId);
                return params;
            }

        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    // 서버에 요청서 목록 요청
    public void deleteQuote(String quoteId, String chatroomId) {
        Log.i(TAG, "서버에 요청서 목록 요청");
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = serverAddress + "request/deleteQuote.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "response = "  + response);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // textView.setText("That didn't work!");
                Log.i(TAG, "error = " + error);
            }
        }) {
            // 포스트 파라미터 넣기
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("quoteId", quoteId);
                params.put("chatroomId", chatroomId);
                Log.i(TAG, "(클라이언트→서버)quoteId = " + quoteId);
                Log.i(TAG, "(클라이언트→서버)chatroomId = " + chatroomId);
                Toast.makeText(RequestDetail.this, "받은 요청을 삭제했습니다.", Toast.LENGTH_SHORT).show();
                        

                return params;
            }

        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}