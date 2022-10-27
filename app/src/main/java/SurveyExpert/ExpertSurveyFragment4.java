package SurveyExpert;

import static com.example.soomgodev.StaticVariable.serverAddress;
import static com.example.soomgodev.StaticVariable.tagClientToServer;
import static com.example.soomgodev.StaticVariable.tagServerToClient;
import static com.example.soomgodev.StaticVariable.tagSharedPreferenceInput;
import static com.example.soomgodev.StaticVariable.tagSharedPreferenceOutput;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.soomgodev.Fragment.ExpertMainActivity;
import com.example.soomgodev.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

public class ExpertSurveyFragment4 extends Fragment {

    Button button;
    ExpertSurveyMainActivity expertSurveyMainActivity;
    RadioButton rb_male, rb_female;
    EditText et_phoneNumber;
    Boolean radioButtonIsClicked = false;
    TextInputLayout til_phoneNumber;
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferencesUser;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor editorUser;
    RadioGroup rb_group;

    private static final String TAG = "ExpertSurveyFragment4";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        expertSurveyMainActivity = (ExpertSurveyMainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        expertSurveyMainActivity = null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_expert_survey4, container, false);

        sharedPreferences = getActivity().getSharedPreferences("expertInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        sharedPreferencesUser = getActivity().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        editorUser = sharedPreferencesUser.edit();


        rb_group = rootView.findViewById(R.id.rb_group);

        button = rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RadioButton selectedButton = (RadioButton) rootView.findViewById(rb_group.getCheckedRadioButtonId());
                String selectedValue = selectedButton.getText().toString();
                String phoneNumberInput = et_phoneNumber.getText().toString();

                Log.i(TAG, tagSharedPreferenceInput + "gender = " + selectedValue);
                Log.i(TAG, tagSharedPreferenceInput + "phoneNumber = " + phoneNumberInput);

                editor.putString("gender", selectedValue);
                editor.putString("phoneNumber", phoneNumberInput);
                editor.commit();

                Log.i(TAG, tagSharedPreferenceOutput + "gender = " + sharedPreferences.getString("gender", ""));
                Log.i(TAG, tagSharedPreferenceOutput + "phoneNumber = " + sharedPreferences.getString("phoneNumber", ""));

                // 서버통신을 통해, 저장된 sharedPreference를 모두 전송하고, 값을 전달 받는 시점에서 다음 액티비티로 이동하도록 한다.
                // 그리고 저장되어있는 sharedPreference 값을 모두 삭제한다.

                String url = serverAddress + "expertRegister.php";

                // 회원가입 버튼 클릭 시, 입력값을 서버로 전송
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, tagServerToClient + response); //서버에서 받은 response 확인
                        editor.clear();
                        editor.commit();

                        String userSeq = sharedPreferencesUser.getString("userSeq", "");

                        editorUser.putString(userSeq+"expertRegistered", "done");
                        editorUser.commit();

                        Intent intent = new Intent(getContext(), ExpertMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        Toast.makeText(getContext(), "고수등록을 완료했습니다.", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();


                        params.put("cb_1", sharedPreferences.getString("cb_1",""));
                        params.put("cb_2", sharedPreferences.getString("cb_2",""));
                        params.put("cb_3", sharedPreferences.getString("cb_3",""));
                        params.put("cb_4", sharedPreferences.getString("cb_4",""));
                        params.put("address", sharedPreferences.getString("address",""));
                        params.put("gender", sharedPreferences.getString("gender",""));
                        params.put("phoneNumber", sharedPreferences.getString("phoneNumber",""));
                        params.put("userSeq", sharedPreferencesUser.getString("userSeq",""));
                        params.put("userName", sharedPreferencesUser.getString("userName",""));

                        Log.i(TAG, tagClientToServer + "cb_1 = " +  sharedPreferences.getString("cb_1",""));
                        Log.i(TAG, tagClientToServer + "cb_2 = " +  sharedPreferences.getString("cb_2",""));
                        Log.i(TAG, tagClientToServer + "cb_3 = " +  sharedPreferences.getString("cb_3",""));
                        Log.i(TAG, tagClientToServer + "cb_4 = " +  sharedPreferences.getString("cb_4",""));
                        Log.i(TAG, tagClientToServer + "address = " +  sharedPreferences.getString("address",""));
                        Log.i(TAG, tagClientToServer + "gender = " +  sharedPreferences.getString("gender",""));
                        Log.i(TAG, tagClientToServer + "phoneNumber = " +  sharedPreferences.getString("phoneNumber",""));
                        Log.i(TAG, tagClientToServer + "userSeq = " +  sharedPreferencesUser.getString("userSeq",""));
                        Log.i(TAG, tagClientToServer + "userName = " +  sharedPreferencesUser.getString("userName",""));

                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(stringRequest);


            }
        });


        rb_male = rootView.findViewById(R.id.rb_male);
        rb_male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                radioButtonIsClicked = true;

            }
        });

        rb_female = rootView.findViewById(R.id.rb_female);
        rb_female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                radioButtonIsClicked = true;
            }
        });



        til_phoneNumber = rootView.findViewById(R.id.til_phoneNumber);
        et_phoneNumber = til_phoneNumber.getEditText();
        et_phoneNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(radioButtonIsClicked == true) {
                    button.setEnabled(true);
                } else if(et_phoneNumber.equals(null)) {
                    button.setEnabled(false);
                }
            }
        });




        return rootView;

    }
}
