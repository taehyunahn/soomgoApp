package AccountUpdate;

import static com.example.soomgodev.StaticVariable.serverAddress;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AccountUpdatePassword extends AppCompatActivity {

    Button button;
    EditText et_name;
    TextInputLayout til_oldPassword, til_newPassword, til_newPasswordCheck;
    EditText et_oldPassword, et_newPassword, et_newPasswordCheck;


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_update_password);
        getSupportActionBar().hide();

        // sharedPreference로 회원의 고유값을 변수에 넣는다
        sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        userSeq = sharedPreferences.getString("userSeq", "");
        editor = sharedPreferences.edit();

        et_newPassword = findViewById(R.id.et_newPassword);
        et_oldPassword = findViewById(R.id.et_oldPassword);
        til_newPassword = findViewById(R.id.til_newPassword);
        til_newPasswordCheck = findViewById(R.id.til_newPasswordCheck);
        button = findViewById(R.id.button);


        //새로운 비밀번호 입력 형식이 잘못되면, 에러 띄움
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

        // 변경완료 버튼 클릭 시,
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = et_newPassword.getText().toString();
                String oldPassword = et_oldPassword.getText().toString();
                updatePassword(userSeq, oldPassword, newPassword);
            }
        });
    }
    private void updatePassword(String userSeq, String oldPassword, String newPassword) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = serverAddress + "updatePassword.php";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response : ", response);
                if(response.equals("0")){
                    Toast.makeText(AccountUpdatePassword.this, "비밀번호를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AccountUpdatePassword.this, "비밀번호를 변경했습니다.", Toast.LENGTH_SHORT).show();
                    finish();
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
                params.put("userSeq", userSeq);
                params.put("oldPassword", oldPassword);
                params.put("newPassword", newPassword);


                return params;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}