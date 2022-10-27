package ExpertUpdate;

import static com.example.soomgodev.StaticVariable.serverAddress;
import static com.example.soomgodev.StaticVariable.tagClientToServer;
import static com.example.soomgodev.StaticVariable.tagServerToClient;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

public class ExertUpdateTime extends AppCompatActivity {

    Spinner spinner1, spinner2;
    private static final String TAG = "ExertUpdateTime";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq;
    Button button;
    String updatedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exert_update_time);
        getSupportActionBar().hide();

        sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userSeq = sharedPreferences.getString("userSeq","");

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.my_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner1.setAdapter(adapter);

        Log.i(TAG, "spinner1.toString() = "+spinner1.toString());
//
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        // Create an ArrayAdapter using the string array and a default spinner layout
        spinner2.setAdapter(adapter);

        //수정 완료 버튼
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "spinner1.getSelectedItem().toString() = "+ spinner1.getSelectedItem().toString());
                Log.i(TAG, "spinner2.getSelectedItem().toString() = "+ spinner2.getSelectedItem().toString());


                updatedTime = spinner1.getSelectedItem().toString() + "-" + spinner2.getSelectedItem().toString();

                editor.putString(userSeq+"expertTime", updatedTime);
                editor.commit();

                requestTimeUpdate(updatedTime);
            }
        });

    }

    private void requestTimeUpdate (String updatedService) {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = serverAddress + "expertUpdateTime.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, tagServerToClient + response);

                // 3)update된 '제공 서비스' 값을 클라이언트에서 받는다 -> expertService를 갱신한다.
                updatedTime = response;
                finish();
                Toast.makeText(getApplicationContext(), "연락 가능 시간을 수정했습니다.", Toast.LENGTH_SHORT).show();


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
                params.put("updatedTime", updatedTime);
                params.put("userSeq", userSeq);
                Log.i(TAG, tagClientToServer + "updatedTime = " + updatedTime);
                Log.i(TAG, tagClientToServer + "userSeq = " + userSeq);

                return params;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

}