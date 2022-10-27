package com.example.soomgodev.Fragment;

import static com.example.soomgodev.StaticVariable.serverAddress;
import static com.example.soomgodev.StaticVariable.tagClientToServer;
import static com.example.soomgodev.StaticVariable.tagServerToClient;
import static com.example.soomgodev.StaticVariable.tagSharedPreferenceOutput;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import AccountUpdate.AccountUpdateActivity;
import SurveyExpert.ExpertSurveyMainActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.soomgodev.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Fragment5 extends Fragment {


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq;
    Button btn_change, btn_expertRegister;
    ImageView iv_userPhoto;
    TextView tv_userName, tv_userEmail;
    private static final String TAG = "Fragment5";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_5, container, false);
        Log.i(TAG, "onCreateView 시작");


        sharedPreferences = this.getActivity().getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userSeq = sharedPreferences.getString("userSeq","");

        Log.i(TAG, tagSharedPreferenceOutput + "userSeq = " + userSeq);


        // 이름 보여줌
        tv_userName = rootView.findViewById(R.id.tv_userName);
        tv_userEmail = rootView.findViewById(R.id.tv_userEmail);
        iv_userPhoto = rootView.findViewById(R.id.iv_userPhoto);

//        Button button = (Button) rootView.findViewById(R.id.layout_account);
        ViewGroup layout_account = (ViewGroup) rootView.findViewById(R.id.layout_account);
        layout_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(), "클릭했습니다", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), AccountUpdateActivity.class);
                startActivity(intent);
            }
        });


        // 고객 -> 고수로 전환 (이때, s.f에 expertSeq를 담는다
        btn_change = rootView.findViewById(R.id.btn_change);
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 서버에 userSeq를 보내고, 그에 해당하는 expertSeq를 가져와서 sharedPreference에 저장하자
                editor.putString(userSeq + "clientOrExpert", "expert");
                editor.commit();
                Log.i(TAG, "sharedPreference에 저장된 값 : " + sharedPreferences.getString(userSeq + "clientOrExpert", "client"));
                requestExpertSeqSaveItSF();
            }
        });

        btn_expertRegister = rootView.findViewById(R.id.btn_expertRegister);
        btn_expertRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExpertSurveyMainActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Fragment5", "onDestory");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("Fragment5", "onDestroyView");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("Fragment5", "onResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("Fragment5", "onStop");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("Fragment5", "onStart");

        sharedPreferences = this.getActivity().getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);

        // 서버통신 : 회원 고유값(userSeq)을 보내서, 그에 해당되는 칼럼 정보(userName, userEmail)를 찾아서 배열로 받아온다.
        //           이름과 이메일 텍스트에 세팅해준다.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = serverAddress + "accountUpdate.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("AccountUpdateActivity", "서버에서 전달받은 값" + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    Log.i(TAG, tagServerToClient + "response = " + response);

                    // 서버에서 전달받은 JSON 객체를 분리하여, 각 변수에 담는다.
                    String userNameFromServer = jsonResponse.getString("userName");
                    String userEmailFromServer = jsonResponse.getString("userEmail");
                    String userProfileImage = jsonResponse.getString("userProfileImage");
                    String expertId = jsonResponse.getString("expertId") + "1";
                    String expertName = jsonResponse.getString("expertName");

                    // 고객 -> 고수로 화면 전환할 때, 고수의 이름을 s.f에 저장한다.
                    editor.putString(userSeq + "expertName", expertName);
                    editor.commit();

                    // 이름, 이메일 표시
                    tv_userName.setText(userNameFromServer + " 고객님");
                    tv_userEmail.setText(userEmailFromServer);

                    //고수로 등록된 ID가 null이라면,
                    if(expertId == null){
                        Log.i(TAG, "expertId == null입니다");
                        btn_change.setVisibility(View.GONE);
                        btn_expertRegister.setVisibility(View.VISIBLE);
                    } else {
                        Log.i(TAG, "expertId == null이 아닙니다.");
                        btn_change.setVisibility(View.VISIBLE);
                        btn_expertRegister.setVisibility(View.GONE);
                    }

                    Log.i(TAG, tagServerToClient + "userNameFromServer = " + userNameFromServer);
                    Log.i(TAG, tagServerToClient + "userEmailFromServer = " + userEmailFromServer);
                    Log.i(TAG, tagServerToClient + "userProfileImage = " + userProfileImage);
                    Log.i(TAG, tagServerToClient + "expertId = " + expertId);

                    if(userProfileImage != null && !userProfileImage.equals("0")){
                        Glide.with(getContext()).load(userProfileImage).circleCrop().into(iv_userPhoto);
                    }


//
//
//                    // 프로필 사진 표시 (세 가지 case)
//                    // case 1. 카카오아이디 최초 로그인 경우, https: 포함 여부 -> glide 사용 (단순 사용)
//                    if (userProfileImage.contains("https:")) {
//                        Glide.with(getContext()).load(userProfileImage)
//
//                                .circleCrop().into(iv_userPhoto);
//
//                        // case 2. 앱 내부에서 사진 변경하여 서버에 jpeg 형식으로 저장한 경우, https: 포함 여부 -> glide 사용 (서버 IP 주소 포함)
//                    } else if (userProfileImage.contains("jpeg")) {
//                        Glide.with(getContext()).load(serverAddress + "image/" + userProfileImage).circleCrop().into(iv_userPhoto);
//
//                        // case 3. 이메일 로그인 했으며, 최초 등록한 이미지가 없는 경우(sharedPreference null), 아무것도 작동하지 않도록 함 - 기본 이미지
//                    } else if (userProfileImage.equals(null)) {
//
//                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("AccountUpdateActivity", "JSON에서 전달받은 에러" + e);
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
                params.put("userSeq", userSeq);
                Log.i(TAG, tagClientToServer + "userSeq = " + userSeq);
                return params;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);





    }

    private void requestExpertSeqSaveItSF() {
        // 서버통신 : 회원 고유값(userSeq)을 보내서, 그에 해당되는 칼럼 정보(userName, userEmail)를 찾아서 배열로 받아온다.
        //           이름과 이메일 텍스트에 세팅해준다.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = serverAddress + "getExpertSeq.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("AccountUpdateActivity", "서버에서 전달받은 값" + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    Log.i(TAG, tagServerToClient + "response = " + response);
                    String expertSeq = jsonResponse.getString("expertSeq");

                    editor.putString("expertSeq", expertSeq);
                    editor.commit();

                    Intent intent = new Intent(getActivity(), ExpertMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("position", 4);
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("AccountUpdateActivity", "JSON에서 전달받은 에러" + e);
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
                params.put("userSeq", userSeq);
                Log.i(TAG, tagClientToServer + "userSeq = " + userSeq);
                return params;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


}