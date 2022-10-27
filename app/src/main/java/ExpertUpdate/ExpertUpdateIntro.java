package ExpertUpdate;

import static com.example.soomgodev.StaticVariable.serverAddress;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import com.example.soomgodev.R;

import java.util.HashMap;
import java.util.Map;

public class ExpertUpdateIntro extends AppCompatActivity {



    String userName;
    Button button;
    EditText et_name;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exert_update_intro);
        getSupportActionBar().hide();


        // sharedPreference로 회원의 고유값을 변수에 넣는다
        sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        userSeq = sharedPreferences.getString("userSeq", "");
        editor = sharedPreferences.edit();

        //
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");


        // 이름변경을 위해 입력한 값
        et_name = findViewById(R.id.et_name);
        et_name.setText(userName);
        et_name.setSelection(et_name.getText().length());

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url = serverAddress + "updateExpertIntro.php";

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response : ", response);
                        editor.putString("expertName", response);
                        editor.commit();
                        finish();
                        Toast.makeText(getApplicationContext(), "한줄소개를 수정했습니다.", Toast.LENGTH_SHORT).show();

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
                        params.put("expertIntro", et_name.getText().toString());
                        params.put("userSeq", userSeq);

                        return params;
                    }

                };

                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        });


    }
}