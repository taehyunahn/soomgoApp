package AccountUpdate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.soomgodev.R;

import java.util.HashMap;
import java.util.Map;

public class AccountUpdatePhoneNumber extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq;

    Button btn_done;
    EditText et_phoneNumber;
    ImageButton ib_close;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_update_phone_number);
        getSupportActionBar().hide();

        btn_done = findViewById(R.id.btn_done);
        et_phoneNumber = findViewById(R.id.et_phoneNumber);
        ib_close = findViewById(R.id.ib_close);



        // sharedPreference로 회원의 고유값을 변수에 넣는다
        sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        userSeq = sharedPreferences.getString("userSeq", "");
        editor = sharedPreferences.edit();

        //화면 닫기 버튼
        ib_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //수정완료 버튼
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPhoneNumber = et_phoneNumber.getText().toString();
                updatePhoneNumber(newPhoneNumber, userSeq);
            }
        });



    }


    private void updatePhoneNumber(String newPhoneNumber, String userSeq) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://54.180.133.35/updatePhoneNumber.php";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response : ", response);
                Toast.makeText(AccountUpdatePhoneNumber.this, "휴대전화 번호를 수정했습니다.", Toast.LENGTH_SHORT).show();
                finish();

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
                params.put("newPhoneNumber", newPhoneNumber);
                params.put("userSeq", userSeq);

                return params;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
}