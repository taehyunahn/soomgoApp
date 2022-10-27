package SurveyRequest;

import static android.content.ContentValues.TAG;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.soomgodev.Fragment.ExpertMainActivity;
import com.example.soomgodev.Fragment.UserMainActivity;
import com.example.soomgodev.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import Chat.ChatService;
import Chat.ViewType;

public class SurveyRequestF7 extends Fragment {
    SurveyRequestMainActivity surveyRequestMainActivity;
    Button button;
    SharedPreferences sharedPreferences, sp;
    SharedPreferences.Editor editor;
    String userSeq, expertSeq, userName;
    TextInputLayout til_question;
    EditText et_question;
    private static final String TAG = "SurveyRequesteF7";
    Socket member_socket; // 서버와 연결되어있는 소켓 객체
    String selectedExpertId;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        surveyRequestMainActivity = (SurveyRequestMainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        surveyRequestMainActivity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_survey_request_f7, container, false);

        sharedPreferences = getActivity().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        expertSeq = sharedPreferences.getString("expertSeq","");
        userSeq = sharedPreferences.getString("userSeq","");
        userName = sharedPreferences.getString("userName","");

        editor = sharedPreferences.edit();
        button = rootView.findViewById(R.id.button);

        til_question = rootView.findViewById(R.id.til_question);
        et_question = til_question.getEditText();
        et_question.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                button.setEnabled(true);
                String question = et_question.getText().toString();
                editor.putString(userSeq+"question", question);
                editor.commit();

                Log.i(TAG, "fragment7에서 저장된 값 = " + sharedPreferences.getString(userSeq + "question", ""));
            }
        });

        // 요청서 보내기 버튼
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveRequest();




            }
        });

        return rootView;
    }





    public void saveRequest() {
        // 서버통신을 통해, 저장된 sharedPreference를 모두 전송하고, 값을 전달 받는 시점에서 다음 액티비티로 이동하도록 한다.
        // 그리고 저장되어있는 sharedPreference 값을 모두 삭제한다.

        String url = serverAddress + "request/insertRequest.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "response = " + response); //서버에서 받은 response 확인
                String requestId = response;
                editor.putString(requestId+"requestSent", "sent");
                editor.commit();

                Log.i(TAG, "sharedF 저장 여부 확인 = " + sharedPreferences.getString(requestId + "requestSent", ""));
                Toast.makeText(getContext(), "견적 요청을 완료했습니다.", Toast.LENGTH_SHORT).show();



                SendToServerThread2 thread2 = new SendToServerThread2(member_socket);
                thread2.start();



                Intent intent = new Intent(getContext(), UserMainActivity.class);
                startActivity(intent); // 다른 클래스에서 Activity를 동작시키려면, context를 가져와서 하면 된다 !! 2022.05.18. 깨달음.










            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                String age = sharedPreferences.getString(userSeq + "age","");
                String how = sharedPreferences.getString(userSeq + "how","");
                String day = sharedPreferences.getString(userSeq + "day","");
                String schedule = sharedPreferences.getString(userSeq + "schedule","");
                String gender = sharedPreferences.getString(userSeq + "gender","");
                String addressInfo = sharedPreferences.getString(userSeq + "addressInfo","");
                String question = sharedPreferences.getString(userSeq + "question","");
                selectedExpertId = sharedPreferences.getString(userSeq + "selectedExpertId","");
                // 해당 요청을 받을 고수의 ID도 필요하다  // 견적 요청하기를 클릭할때 shared에 저장하면 좋을 듯RF
                String userIdWhoRequest = userSeq;
                String serviceRequested = sharedPreferences.getString(userSeq + "selectedService","");
                String clientName = sharedPreferences.getString("userName","");




                params.put("age", age);
                params.put("how", how);
                params.put("day", day);
                params.put("schedule", schedule);
                params.put("gender", gender);
                params.put("addressInfo", addressInfo);
                params.put("question", question);
                params.put("selectedExpertId", selectedExpertId);
                params.put("userIdWhoRequest", userIdWhoRequest);
                params.put("serviceRequested", serviceRequested);
                params.put("clientName", clientName);

                Log.i(TAG, "serviceRequested = " + serviceRequested);


                Log.i(TAG, "sharedPreferences.getString(userSeq) = " + sharedPreferences.getString("userSeq",""));


                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }




    // 서버에 데이터를 전달하는 스레드
    class SendToServerThread2 extends Thread {
        Socket socket;

        public SendToServerThread2(Socket socket) {
            try {
                this.socket = socket;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            Log.i(TAG, "SendToServerThread 실행 1.");
            try {

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
                String userSeq = sharedPreferences.getString("userSeq", "");
                String userName = sharedPreferences.getString("userName", "");

                ChatService.chatMember.setOrderType("request"); // 전송
                ChatService.chatMember.setNickname(userName); // 닉네임
                ChatService.chatMember.setUserSeq(userSeq); // 보내는 사람 고유값
                ChatService.chatMember.setExpertSeq(selectedExpertId);// 받는 사람의 고유값도 필요하다 (고수번호가 되겠다)selectedExpertId

                Gson gson = new Gson();
                String _chatMemberJson = gson.toJson(ChatService.chatMember);

                // 서비스 클래스에 있는 PrintWriter 객체를 사용해서, 방금 전에 입력한 값을 담는다.
                ChatService.sendWriter.println(_chatMemberJson);
                ChatService.sendWriter.flush();
                // }

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}