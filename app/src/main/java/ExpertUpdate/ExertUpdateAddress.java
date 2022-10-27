package ExpertUpdate;

import static com.example.soomgodev.StaticVariable.serverAddress;
import static com.example.soomgodev.StaticVariable.tagClientToServer;
import static com.example.soomgodev.StaticVariable.tagServerToClient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.soomgodev.NetworkStatus;
import com.example.soomgodev.R;

import java.util.HashMap;
import java.util.Map;

import SurveyExpert.ExpertSurveyMainActivity;

public class ExertUpdateAddress extends AppCompatActivity {


    Button button;
    ExpertSurveyMainActivity expertSurveyMainActivity;
    EditText et_address;
    private static final String TAG = "ExpertSurveyFragment2";
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;
    private String addressResult, userSeq;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exert_update_address);
        getSupportActionBar().hide();

        // loginInfo 정보를 담은 sharedPreference
        sharedPreferences = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userSeq = sharedPreferences.getString("userSeq", "");

        // 수정 완료 버튼
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 수정된 주소를 서버에 전송 후, 저장
                serverAddressUpdate();

//                Log.i(TAG, tagSharedPreferenceInput + "address = " + et_address.getText().toString());
//                editor.putString("address", et_address.getText().toString());
//                editor.commit();
//                Log.i(TAG, tagSharedPreferenceOutput + "address = " + sharedPreferences.getString("address", ""));

            }
        });

        // 주소 출력 및 주소 검색 링크 기능
        et_address = findViewById(R.id.et_address);
        et_address.setFocusable(false);
        et_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "주소설정페이지 - 주소입력창 클릭");
                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if(status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

                    // WebView가있는 페이지로 넘어갈 Intent를 설정
                    Log.i("주소설정페이지", "주소입력창 클릭");
                    Intent intent = new Intent(getApplicationContext(), AddressSearch.class);
                    // 주소결과
                    startActivityForResult(intent, SEARCH_ADDRESS_ACTIVITY);

                }else {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // 이전 activity에서 받아온 intent가 null이 아니라면,
        // 지도 API에서 출력해온 값을 가져온 직후,
        // data 값에는 최종 주소가 담겨있음.
        Intent intent = getIntent();
        if(intent != null) {
            addressResult = intent.getStringExtra("data");
            et_address.setText(addressResult);
            button.setEnabled(true);
        }
    }

    private void serverAddressUpdate() {
        String url = serverAddress + "updateAddress.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, tagServerToClient + response); //서버에서 받은 response 확인
                finish();
                Toast.makeText(getApplicationContext(), "활동지역을 수정했습니다.", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("addressResult", addressResult);
                params.put("userSeq", userSeq);

                Log.i(TAG, tagClientToServer + "cb_1 = " +  sharedPreferences.getString("cb_1",""));
                Log.i(TAG, tagClientToServer + "userSeq = " +  userSeq);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }



}