package Dialog;

import static android.content.ContentValues.TAG;
import static com.example.soomgodev.StaticVariable.serverAddress;
import static com.example.soomgodev.StaticVariable.tagServerToClient;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.soomgodev.Fragment.ExpertMainActivity;
import com.example.soomgodev.R;
import com.google.gson.Gson;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import Chat.ChatRoomActivity;
import Chat.ChatService;
import SurveyRequest.RequestDetail;

/*
직접 커스텀한 다이얼로그들을 띄워주고 다이얼로그 안에서의 동작을 정의하는 클래스 (싱글톤)
 */
public class CustomDialog extends Dialog {


    // sharedPreference 기본설정 (변수 선언부)
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq, expertSeq;
    private static final String TAG = "CustomDialog";

    Socket member_socket; // 서버와 연결되어있는 소켓 객체


    private static CustomDialog customDialog;

    private CustomDialog(@NonNull Context context) {
        super(context);
    }

    public static CustomDialog getInstance(Context context) {
        customDialog = new CustomDialog(context);

        return customDialog;
    }


    //다이얼로그 생성하기
    public void showDefaultDialog() {
        //참조할 다이얼로그 화면을 연결한다.
        customDialog.setContentView(R.layout.dialog_default);

        //다이얼로그의 구성요소들이 동작할 코드작성

        Button button_ok = customDialog.findViewById(R.id.default_dialog_ok_btn);
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = findViewById(R.id.dialog_default_txtview);
                textView.setText("확인 버튼을 눌렀습니다. :)");
            }
        });
        Button button_cancel = customDialog.findViewById(R.id.default_dialog_cancel_btn);
        button_cancel.setOnClickListener(clickCancel);
        customDialog.show();
    }

    public void showQuoteDialog(String requestId, String userIdWhoRequest, String chatRoomId, Context context) {
        customDialog.setContentView(R.layout.dialog_quote);

        //다이얼로그의 구성요소들이 동작할 코드작성
        Log.i(TAG, "showQuoteDialog 시작");
        Button button_ok = customDialog.findViewById(R.id.default_dialog_ok_btn);
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.dialog_et_quote);
                // 동작할 요소를 여기에 넣는다.
                Log.i("CustomDialog", "서버로 보냅니다");
                Log.i("CustomDialog", "requestId = "+requestId);
                Log.i("CustomDialog", "editText.getText() = "+ editText.getText().toString());
                String price = editText.getText().toString(); // 입력한 견적 금액

                // 견적서 발송 - 서버통신
                sendQuote(requestId, userIdWhoRequest, chatRoomId, price, context);



            }
        });

        Button button_cancel = customDialog.findViewById(R.id.default_dialog_cancel_btn);
        button_cancel.setOnClickListener(clickCancel);
        customDialog.show();
    }

    //취소버튼을 눌렀을때 일반적인 클릭리스너
    View.OnClickListener clickCancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getContext(), "취소 버튼을 눌렀습니다.", Toast.LENGTH_SHORT).show();
            customDialog.dismiss();
        }
    };



    private void sendQuote(String requestId, String userIdWhoRequest, String chatRoomId, String price, Context context) {

        Log.i(TAG, "showQuoteDialog 안에서 sendQuote 1. 시작");
        String url = serverAddress + "request/insertQuote.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "showQuoteDialog 안에서 sendQuote 2. 서버에서 받은 응답 response  = " + response);
                Toast.makeText(getContext(), "견적서를 보냈습니다.", Toast.LENGTH_SHORT).show();
                customDialog.dismiss();

                String receivedRoomNumber = response;
                Log.i(TAG, "receivedRoomNumber = " +  receivedRoomNumber);

                String updatedRoomNumber = receivedRoomNumber.substring(0, receivedRoomNumber.length() - 1);
                Log.i(TAG, "updatedRoomNumber = " +  updatedRoomNumber);




                // 견적서를 금액을 입력하면 두 가지 동작을 한다
                // 1. DB테이블에 견적서를 생성한다
                // 2. DB테이블에 채팅방을 생성한다 -> 그리고 방번호, 참여자 정보를 담는다. -> 채팅방으로 이동한다 (로그인 userSeq가 sender가 된다)

                // userSeq를 가져오기 위한 목적
                sharedPreferences = context.getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
                userSeq = sharedPreferences.getString("userSeq","");
                expertSeq = sharedPreferences.getString("expertSeq", "");
                editor = sharedPreferences.edit();
                editor.putString(requestId, "sent");
                editor.commit();

                // Thread 돌려야 한다.
                SendToServerThread3 thread3 = new SendToServerThread3(member_socket, userIdWhoRequest, updatedRoomNumber, context);
                thread3.start();

                Intent intent = new Intent(context, ChatRoomActivity.class);
                intent.putExtra("chat_room_seq", updatedRoomNumber);
                intent.putExtra("clientOrExpert", "expert");
                context.startActivity(intent); // 다른 클래스에서 Activity를 동작시키려면, context를 가져와서 하면 된다 !! 2022.05.18. 깨달음.

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("requestId", requestId);
                params.put("price", price);
                Log.i(TAG, "showQuoteDialog 안에서 sendQuote 2. 서버로 보내는 값 a. price  = " + price);
                Log.i(TAG, "showQuoteDialog 안에서 sendQuote 2. 서버로 보내는 값 b. requestId  = " + requestId);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    // 서버에 데이터를 전달하는 스레드
    class SendToServerThread3 extends Thread {
        Socket socket;
        Context context;
        String userIdWhoRequest;
        String chatRoomId;

        public SendToServerThread3(Socket socket, String userIdWhoRequest, String chatRoomId, Context context) {
            try {
                this.socket = socket;
                this.context = context;
                this.userIdWhoRequest = userIdWhoRequest;
                this.chatRoomId = chatRoomId;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            Log.i(TAG, "SendToServerThread3 실행 1.");
            try {

                SharedPreferences sharedPreferences = context.getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
                String userSeq = sharedPreferences.getString("userSeq", "");
                String userName = sharedPreferences.getString("userName", "");


                ChatService.chatMember.setOrderType("quote"); // 전송
                ChatService.chatMember.setNickname(userName); // 닉네임
                ChatService.chatMember.setUserSeq(userSeq); // 보내는 사람 고유값
                ChatService.chatMember.setUserIdWhoSent(userIdWhoRequest);// 받는 사람의 고유값도 필요하다 (고수번호가 되겠다)selectedExpertId
                ChatService.chatMember.setChat_room_seq(chatRoomId);

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
