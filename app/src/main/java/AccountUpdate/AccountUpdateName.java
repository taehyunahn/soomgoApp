package AccountUpdate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.soomgodev.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccountUpdateName extends AppCompatActivity {
    String userName;
    Button button;
    EditText et_name;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_update_name);
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
            String url = "http://54.180.133.35/updateName.php";

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("response : ", response);
                    editor.putString(userSeq + "userName", response);
                    editor.commit();
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
                    params.put("userName", et_name.getText().toString());
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