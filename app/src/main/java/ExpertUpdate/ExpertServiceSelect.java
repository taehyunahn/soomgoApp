package ExpertUpdate;

import static android.content.ContentValues.TAG;
import static com.example.soomgodev.StaticVariable.serverAddress;
import static com.example.soomgodev.StaticVariable.tagClientToServer;
import static com.example.soomgodev.StaticVariable.tagIntentOutput;
import static com.example.soomgodev.StaticVariable.tagServerToClient;
import static com.example.soomgodev.StaticVariable.tagSharedPreferenceInput;
import static com.example.soomgodev.StaticVariable.tagSharedPreferenceOutput;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.soomgodev.Fragment.ExpertMainActivity;
import com.example.soomgodev.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExpertServiceSelect extends AppCompatActivity {
    CheckBox cb_1, cb_2, cb_3, cb_4;
    Button button;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String expertService, userSeq;
    String service1, service2, service3, service4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_service_select);
        getSupportActionBar().hide();

        sharedPreferences = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userSeq = sharedPreferences.getString("userSeq", "");

        cb_1 = findViewById(R.id.cb_1);
        cb_2 = findViewById(R.id.cb_2);
        cb_3 = findViewById(R.id.cb_3);
        cb_4 = findViewById(R.id.cb_4);
        button = findViewById(R.id.button);

        Intent intent = getIntent();
        ArrayList<String> currentServiceList = intent.getStringArrayListExtra("currentServiceList");

        Log.i(TAG, tagIntentOutput + "currentServiceList = " + currentServiceList);


        // 배열에 담겨있는 서비스 이름을 모두 합친 String을 하나 만든다.
        // 각 체크박스의 setText하고, 위 String값에 contain 되어있는지 여부를 판단하여,
        // 포함되어있다면 체크되도록 설정

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < currentServiceList.size(); i++) { //array와 arrayList의 길이 재고, 값 찾는 방식이 약간 다르다. length vs size
                sb.append(currentServiceList.get(i));
        }
        String allServiceName = sb.toString();

        Log.i(TAG, "allServiceName = " + allServiceName);


        if(allServiceName.contains(cb_1.getText())){
            cb_1.setChecked(true);
            button.setEnabled(true);
        }

        if(allServiceName.contains(cb_2.getText())){
            cb_2.setChecked(true);
            button.setEnabled(true);
        }
        if(allServiceName.contains(cb_3.getText())){
            cb_3.setChecked(true);
            button.setEnabled(true);
        }

        if(allServiceName.contains(cb_4.getText())){
            cb_4.setChecked(true);
            button.setEnabled(true);
        }




        // 영어 과외 선택
        cb_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_1.isChecked()) {
                    button.setEnabled(true);
                } else if (!cb_1.isChecked() && !cb_2.isChecked() && !cb_3.isChecked() && !cb_4.isChecked()){
                    button.setEnabled(false);

                }
            }
        });

        // 비즈니스 영어 선택
        cb_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_2.isChecked()) {
                    button.setEnabled(true);
                } else if (!cb_1.isChecked() && !cb_2.isChecked() && !cb_3.isChecked() && !cb_4.isChecked()){
                    button.setEnabled(false);

                }

            }
        });

        // 화상영어/전화영어 회화 선택
        cb_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_3.isChecked()) {
                    button.setEnabled(true);
                } else if (!cb_1.isChecked() && !cb_2.isChecked() && !cb_3.isChecked() && !cb_4.isChecked()){
                    button.setEnabled(false);

                }
            }
        });

        // TOEIC/Speaking/writing 과외 선택
        cb_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_4.isChecked()) {
                    button.setEnabled(true);
                } else if (!cb_1.isChecked() && !cb_2.isChecked() && !cb_3.isChecked() && !cb_4.isChecked()){
                    button.setEnabled(false);

                }
            }
        });

        // 수정완료 버튼
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cb_1.isChecked()) {
                    service1 = (String) cb_1.getText() + "-";
                } else {
                    service1 = "";
                }
                if(cb_2.isChecked()) {
                    service2 = (String) cb_2.getText() + "-";
                } else {
                    service2 = "";
                }
                if(cb_3.isChecked()) {
                    service3 = (String) cb_3.getText() + "-";
                } else {
                    service3 = "";
                }
                if(cb_4.isChecked()) {
                    service4 = (String) cb_4.getText() + "-";

                } else {
                    service4 = "";
                }


                String test = service1 + service2 + service3 + service4;
                String expertService = test.substring(0, test.length() - 1);

                //위 작업까지 마치면, expertService 안에는 '영어과외-비즈니스 영어'와 같은 형태로 저장됨.
                // 저장한 서비스 full name을 ExpertUpdateService Activity로 보내자

                Log.i(TAG, "expertService : " + expertService);
                serverChipUpdate(expertService);

                Intent intent = new Intent(ExpertServiceSelect.this, ExpertUpdateService.class);
                intent.putExtra("selectedService", expertService);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);



            }
        });



    }


    public void serverChipUpdate (String updatedService) {
        // 서버통신 : 회원 고유값(userSeq)을 보내서, 그에 해당되는 칼럼 정보(userName, userEmail)를 찾아서 배열로 받아온다.
        //           이름과 이메일 텍스트에 세팅해준다.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = serverAddress + "expertUpdateService.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, tagServerToClient + response);

                // 3)update된 '제공 서비스' 값을 클라이언트에서 받는다 -> expertService를 갱신한다.
                expertService = response;

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
                // 1)Chip Close가 반영된, '제공 서비스' String 값을 서버에 보낸다.
                params.put("updatedService", updatedService);
                params.put("userSeq", userSeq);


                Log.i(TAG, tagClientToServer + "updatedService = " + updatedService);
                Log.i(TAG, tagClientToServer + "userSeq = " + userSeq);

                return params;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

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