package com.example.soomgodev.Fragment;

import static com.example.soomgodev.StaticVariable.serverAddress;
import static com.example.soomgodev.StaticVariable.tagClientToServer;
import static com.example.soomgodev.StaticVariable.tagServerToClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import AccountUpdate.AccountUpdateActivity;
import ConnectToServer.DataClass;
import ConnectToServer.NetworkClient;
import ConnectToServer.UploadApis;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.soomgodev.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Fragment24 extends Fragment {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq, expertSeq;
    Button btn_change;
    Switch switch_search;
    ImageView iv_userPhoto;
    TextView tv_userName, tv_userEmail;
    private static final String TAG = "Fragment24";
    Boolean switchChecked;
    String checkedOrNot = null;
    NetworkClient networkClient; // ????????? ????????? ??????
    UploadApis uploadApis; // ????????? ????????? ??????, interface


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment2_4, container, false);


        sharedPreferences = this.getActivity().getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userSeq = sharedPreferences.getString("userSeq","");
        expertSeq = sharedPreferences.getString("expertSeq", "");
        switchChecked = sharedPreferences.getBoolean(expertSeq+"switch", false);

        // ?????? ?????????
        tv_userName = rootView.findViewById(R.id.tv_userName);
        tv_userEmail = rootView.findViewById(R.id.tv_userEmail);
        iv_userPhoto = rootView.findViewById(R.id.iv_userPhoto);
        switch_search = rootView.findViewById(R.id.switch_search);

        // ?????????????????? ????????? ???????????????(expertSeq)??? ??????, ???????????? ?????? ?????? ????????? ?????????.
        // 1. ????????? ????????????,
        if(switchChecked) {
            switch_search.setChecked(true);
        } else {
            // 2. ??????????????? ????????????, sharedPreference??? ????????? ?????? ?????????,
            switch_search.setChecked(false);
        }

        // ???????????? ?????? ?????? ?????????
        switch_search.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // ????????? expertSeq??? ????????????.
                // expertInfo ???????????? exposeOnSearch ?????? yes??? ????????????
                // Fragment2 - ?????? ?????? ??????????????? exposeOnSearch??? yes??? ????????? ??????????????? ????????? ????????????.
                // ???????????? ????????? ???????????? off??????, exposeOnSearch ?????? no??? ????????????. (???????????? no??? ??????)
                if (isChecked) {
                    Log.i(TAG, "???????????? ??????????????????");
                    editor.putBoolean(expertSeq+"switch", true);
                    editor.commit();
                    checkedOrNot = "yes";

                } else {
                    Log.i(TAG, "????????? ????????? ??????????????????");
                    checkedOrNot = "no";
                    editor.remove(expertSeq+"switch");
                    editor.commit();
                }


                // ????????? ????????? ????????? ????????? ???????????? ???????????? ??????.
                UpdateExposeOnSearch(expertSeq, checkedOrNot);


            }
        });

//        Button button = (Button) rootView.findViewById(R.id.layout_account);
        ViewGroup layout_account = (ViewGroup) rootView.findViewById(R.id.layout_account);
        layout_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(), "??????????????????", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), AccountUpdateActivity.class);
                startActivity(intent);
            }
        });

        // ???????????? ?????? ??????
        btn_change = rootView.findViewById(R.id.btn_change);
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //?????? ?????????????????? ??????, ?????? MainActivity?????? ?????? Main2Activity??? ???????????? ??????.
                //??????????????? ????????? ?????? acitivity??? ??????????????? ????????? ????????? ?????? ???????

                editor.putString(userSeq + "clientOrExpert", "client");
                editor.commit();
                Log.i(TAG, "sharedPreference??? ????????? ??? : " + sharedPreferences.getString(userSeq + "clientOrExpert", "client"));

                Intent intent = new Intent(getActivity(), UserMainActivity.class);
                intent.putExtra("position", 5);
                startActivity(intent);

//                Intent intent = new Intent(getActivity(), Main2Activity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        sharedPreferences = this.getActivity().getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);



        // ???????????? : ?????? ?????????(userSeq)??? ?????????, ?????? ???????????? ?????? ??????(userName, userEmail)??? ????????? ????????? ????????????.
        //           ????????? ????????? ???????????? ???????????????.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = serverAddress + "accountUpdate.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("AccountUpdateActivity", "???????????? ???????????? ???" + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    Log.i(TAG, tagServerToClient + "response = " + response);

                    // ???????????? ???????????? JSON ????????? ????????????, ??? ????????? ?????????.
                    String userNameFromServer = jsonResponse.getString("userName");
                    String userEmailFromServer = jsonResponse.getString("userEmail");
                    String userProfileImage = jsonResponse.getString("userProfileImage");
                    String expertSeq = jsonResponse.getString("expertId");

                    editor.putString("expertSeq", expertSeq);
                    editor.commit();

                    // ??????, ????????? ??????
                    tv_userName.setText(userNameFromServer + " ?????????");
                    tv_userEmail.setText(userEmailFromServer);


                    Log.i(TAG, tagServerToClient + "userNameFromServer = " + userNameFromServer);
                    Log.i(TAG, tagServerToClient + "userEmailFromServer = " + userEmailFromServer);
                    Log.i(TAG, tagServerToClient + "userProfileImage = " + userProfileImage);
                    Log.i(TAG, tagServerToClient + "expertId = " + expertSeq);

                    if(userProfileImage != null && !userProfileImage.equals("0")){
                        Glide.with(getContext()).load(userProfileImage).circleCrop().into(iv_userPhoto);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("AccountUpdateActivity", "JSON?????? ???????????? ??????" + e);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // textView.setText("That didn't work!");
            }
        }) {
            // ????????? ???????????? ??????
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

    private void UpdateExposeOnSearch(String expertSeq, String checkedOrNot){
        
        uploadApis = networkClient.getRetrofit().create(UploadApis.class);

        HashMap<String, RequestBody> requestMap = new HashMap<>();
        requestMap.put("expertSeq", RequestBody.create(MediaType.parse("text/plain"), expertSeq));
        requestMap.put("isChecked", RequestBody.create(MediaType.parse("text/plain"), checkedOrNot));

        Call<DataClass> call = uploadApis.switchSearchCheck(requestMap);
        call.enqueue(new Callback<DataClass>() {
            @Override
            public void onResponse(Call<DataClass> call, retrofit2.Response<DataClass> response) {
                Log.i(TAG, "onResponse = " + response);
            }

            @Override
            public void onFailure(Call<DataClass> call, Throwable t) {
                Log.i(TAG, "t = " + t);
            }
        });

    }
}