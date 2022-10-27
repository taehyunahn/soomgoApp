package Chat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.soomgodev.Fragment.ExpertMainActivity;
import com.example.soomgodev.Fragment.UserMainActivity;
import com.example.soomgodev.R;

import java.text.DecimalFormat;
import java.util.HashMap;

import ChatRoomList.ChatRoomData;
import ConnectToServer.NetworkClient;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class DealActivity extends AppCompatActivity {

    Button btn_done;
    ImageButton btn_cancel;
    NetworkClient networkClient;
    ChatInterface chatInterface;
    String chatRoomNumber;
    String selectedDate = "";
    TextView tv_expertName, tv_serviceName, tv_price, tv_selectDate;
    ImageView iv_expertPhoto;
    EditText et_finalPrice;
    String finalPrice;

    // sharedPreference 기본설정 (변수 선언부)
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq, expertSeq, quoteId, clientOrExpert;

    private static final String TAG = "DealActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
        getSupportActionBar().hide();

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_done = findViewById(R.id.btn_done);
        tv_expertName = findViewById(R.id.tv_expertName);
        tv_serviceName = findViewById(R.id.tv_serviceName);
        tv_price = findViewById(R.id.tv_price);
        tv_selectDate = findViewById(R.id.tv_selectDate);
        iv_expertPhoto = findViewById(R.id.iv_expertPhoto);
        et_finalPrice = findViewById(R.id.et_finalPrice);


        // (onCreate 안에)
        sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userSeq = sharedPreferences.getString("userSeq","");
        clientOrExpert = sharedPreferences.getString("clientOrExpert", "");

        Intent intent = getIntent();
        chatRoomNumber = intent.getStringExtra("chatRoomNumber");
        if(intent.getStringExtra("finalPrice") != null){
            finalPrice = intent.getStringExtra("finalPrice");
            et_finalPrice.setText(finalPrice);
            et_finalPrice.setSelection(finalPrice.length());
            Log.i(TAG, "finalPrice = " + finalPrice);
        }


        selectedDate = sharedPreferences.getString(userSeq + "selectedDate", "날짜선택");
        tv_selectDate.setText(selectedDate);

        // 고수 정보 불러오기
        getExpertInfo(chatRoomNumber);

        //sharedPreference로 저장된 서비스 금액이 있다면, 여기에서 setText를 해준다.
        //저장하는 위치는 현재 액티비티가 Stop 되는 부분에서!




        // 버튼 클릭 시
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 거래완료 버튼 클릭 시,
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder ad = new AlertDialog.Builder(DealActivity.this); // context부분에 getAplicationContext를 쓰면 안된다! 기억할 것
                ad.setMessage("거래 확정 후에는 수정이 불가능합니다. 정말로 거래를 확정하시겠습니까?");
                ad.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                ad.setNegativeButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        Toast.makeText(DealActivity.this, "거래를 확정했습니다.", Toast.LENGTH_SHORT).show();
                        editor.remove(chatRoomNumber+"dealDone");
                        editor.commit();
                        dialog.dismiss();

                        String dealPrice = tv_price.getText().toString();
                        String dealDate = tv_selectDate.getText().toString();
                        if(dealDate.equals("날짜선택")){
                            Toast.makeText(DealActivity.this, "날짜를 선택하세요", Toast.LENGTH_SHORT).show();
                        } else {
                            sendDealInfo(quoteId, dealPrice, dealDate);
                        }
                    }
                });

                ad.show();





            }
        });

        tv_selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), CalendarActivity.class);
                intent1.putExtra("chatRoomNumber", chatRoomNumber);
                intent1.putExtra("finalPrice", et_finalPrice.getText().toString());

                Log.i(TAG, "et_finalPrice.getText().toString() = " + et_finalPrice.getText().toString());
                startActivity(intent1);
            }
        });
    }

    private void getExpertInfo(String chatRoomNumber) {
        // 필요한 값
        // 고수 이름, 서비스 이름, 받은 견적 금액

        chatInterface = networkClient.getRetrofit().create(ChatInterface.class);

        // 1. 회원고유 번호와, 2. 고수 고유번호를 보낸다.
        RequestBody chatRoomNumberBody = RequestBody.create(MediaType.parse("text/plain"), chatRoomNumber);

        // 3. 서버에서 받았던 서버 이미지 경로를 다시 보낸다 + 이미지 경로 총 개수도 보낸다.
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        requestMap.put("chatRoomNumber", chatRoomNumberBody);

        Call<ChatRoomData> call = chatInterface.getExpertInfo(requestMap);
        call.enqueue(new Callback<ChatRoomData>() {
            @Override
            public void onResponse(Call<ChatRoomData> call, retrofit2.Response<ChatRoomData> response) {
                Log.i(TAG, "onResponse = " + response);
                Log.i(TAG, "onResponse.body() = " + response.body());
                ChatRoomData responseBody = response.body();
                tv_expertName.setText(responseBody.getExpertName());
                tv_serviceName.setText(responseBody.getServiceRequested());
                tv_price.setText(responseBody.getPrice());

                // 천단위 마다 콤마를 넣는 방법
                if(responseBody.getPrice() != null) {
                    long value = Long.parseLong(responseBody.getPrice());
                    DecimalFormat format = new DecimalFormat("###,###"); //콤마
                    format.format(value);
                    String result = format.format(value);
                    tv_price.setText("시간당 " +result+ "원 부터~");
                }

                // Glide 사용해서 이미지 표시하는 방법
                Glide.with(getApplicationContext())
                        .load(responseBody.getExpertProfileImage())
                        .centerCrop()
                        .into(iv_expertPhoto);

                quoteId = responseBody.getQuoteId();

            }

            @Override
            public void onFailure(Call<ChatRoomData> call, Throwable t) {
                Log.i(TAG, "t = " + t);
            }
        });
    }

    // 거래완료 버튼을 누르면,
    // (서버로 보낼 것) 견적서 번호(quote' seq) 최종 서비스 합의 금액(dealPrice)과 서비스 진행일자(dealDate)를 보낸다.
    // (서버에서 할 것) quote 테이블을 update한다. dealPrice, dealDone, dealDate
    // (돌아와서 할 것) 채팅방 번호를 가져오면서 현재 액티비티를 닫는다. 채팅방 번호와 sharedPreference를 사용해서 ChatRoomActivity의 '거래하기'버튼 색상을 회색으로 바꾸고, 이름은 '거래하기 수정'으로 한다.
    // (돌아와서 할 것) 리뷰작성 버튼을 활성화한다.

    private void sendDealInfo(String quoteId, String dealPrice, String dealDate) {

        chatInterface = networkClient.getRetrofit().create(ChatInterface.class);
        // 1. 회원고유 번호와, 2. 고수 고유번호를 보낸다.
        RequestBody quoteIdBody = RequestBody.create(MediaType.parse("text/plain"), quoteId);
        RequestBody dealPriceBody = RequestBody.create(MediaType.parse("text/plain"), dealPrice);
        RequestBody dealDateBody = RequestBody.create(MediaType.parse("text/plain"), dealDate);

        // 3. 서버에서 받았던 서버 이미지 경로를 다시 보낸다 + 이미지 경로 총 개수도 보낸다.
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        requestMap.put("quoteId", quoteIdBody);
        requestMap.put("dealPrice", dealPriceBody);
        requestMap.put("dealDate", dealDateBody);

        Call<ChatRoomData> call = chatInterface.sendDealInfo(requestMap);
        call.enqueue(new Callback<ChatRoomData>() {
            @Override
            public void onResponse(Call<ChatRoomData> call, retrofit2.Response<ChatRoomData> response) {
                Log.i(TAG, "onResponse = " + response);
                Log.i(TAG, "onResponse.body() = " + response.body());
                ChatRoomData responseBody = response.body();

                String chatRoomNumberFromS = responseBody.getChatRoomNumber();
                editor.putString(chatRoomNumberFromS + "dealDone", "done");
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                intent.putExtra("chat_room_seq", chatRoomNumberFromS);
                intent.putExtra("clientOrExpert", "client");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
            }

            @Override
            public void onFailure(Call<ChatRoomData> call, Throwable t) {
                Log.i(TAG, "t = " + t);
            }
        });

    }




}