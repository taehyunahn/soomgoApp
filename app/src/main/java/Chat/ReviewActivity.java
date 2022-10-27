package Chat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

public class ReviewActivity extends AppCompatActivity {

    Button btn_done;
    ImageButton btn_cancel;
    NetworkClient networkClient;
    ChatInterface chatInterface;
    String chatRoomNumber;
    TextView tv_expertName, tv_serviceRequested, tv_price;
    ImageView iv_expertProfileImage;

    // sharedPreference 기본설정 (변수 선언부)
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq, expertSeq, quoteId, clientOrExpert;
    RatingBar ratingBar;
    EditText et_review;

    private static final String TAG = "ReviewActivity";

    String selectedExpertId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        getSupportActionBar().hide();

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_done = findViewById(R.id.btn_done);
        tv_expertName = findViewById(R.id.tv_expertName);
        tv_serviceRequested = findViewById(R.id.tv_serviceRequested);
        iv_expertProfileImage = findViewById(R.id.iv_expertProfileImage);
        ratingBar = findViewById(R.id.ratingBar);
        et_review = findViewById(R.id.et_review);


        // (onCreate 안에)
        sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userSeq = sharedPreferences.getString("userSeq","");
        clientOrExpert = sharedPreferences.getString("clientOrExpert", "");

        Intent intent = getIntent();
        chatRoomNumber = intent.getStringExtra("chatRoomNumber");

        // 고수 정보 불러오기
        getExpertInfo(chatRoomNumber);

        // 버튼 클릭 시
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 작성완료 버튼 클릭 시,
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String reviewGrade = String.valueOf(ratingBar.getRating());
//                String reviewContents = et_review.getText().toString();
//
//                Log.i(TAG, "거래완료 버튼을 클릭했습니다.");
//                Log.i(TAG, "userSeq = " + userSeq);
//                Log.i(TAG, "reviewGrade = " + reviewGrade);
//                Log.i(TAG, "reviewContents = " + reviewContents);
//                Log.i(TAG, "selectedExpertId = " + selectedExpertId);
//
//                // 보낼 값 : userSeq, 평점, 리뷰내용, 리뷰 대상이 되는 expertSeq
//                sendReviewInfo(userSeq, reviewGrade, reviewContents, selectedExpertId);

                AlertDialog.Builder ad = new AlertDialog.Builder(ReviewActivity.this); // context부분에 getAplicationContext를 쓰면 안된다! 기억할 것
                ad.setMessage("리뷰 작성 후에는 수정이 불가능합니다. 정말로 리뷰 작성을 확정하시겠습니까?");
                ad.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                ad.setNegativeButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String reviewGrade = String.valueOf(ratingBar.getRating());
                        String reviewContents = et_review.getText().toString();
                            sendReviewInfo(userSeq, reviewGrade, reviewContents, selectedExpertId);
                    }
                });
                ad.show();
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
                tv_serviceRequested.setText(responseBody.getServiceRequested());
                selectedExpertId = responseBody.getSelectedExpertId();

                // Glide 사용해서 이미지 표시하는 방법
                Glide.with(getApplicationContext())
                        .load(responseBody.getExpertProfileImage())
                        .centerCrop()
                        .into(iv_expertProfileImage);

                quoteId = responseBody.getQuoteId();

            }

            @Override
            public void onFailure(Call<ChatRoomData> call, Throwable t) {
                Log.i(TAG, "t = " + t);
            }
        });
    }

    // 보낼 값 : userSeq, 평점, 리뷰내용, 리뷰 대상이 되는 expertSeq
    // 리뷰를 쓴 사람의 userSeq를 보내서, 이름, 프로필을 찾는다. -> 어차피 나중에 꺼낼때 쓸거니까 찾을 필요가 없겠다.
    // 평점, 리뷰내용을 보내서, 날짜, 리뷰 내용을 userSeq를 통해 얻은 정보와 함께 review 테이블에 추가한다.
    // 작성하는 리뷰의 expertSeq를 보낸다
    // review 테이블에 생성된 id값은 expertSeq 값에 맞춰서 update한다. (이걸 하려면, 현재 expert의 Seq도 알아야 한다)
    private void sendReviewInfo(String userIdWhoRequest, String reviewGrade, String reviewContents, String selectedExpertId) {

        chatInterface = networkClient.getRetrofit().create(ChatInterface.class);
        // 1. 회원고유 번호와, 2. 고수 고유번호를 보낸다.
        RequestBody userIdWhoRequestBody = RequestBody.create(MediaType.parse("text/plain"), userIdWhoRequest);
        RequestBody reviewGradeBody = RequestBody.create(MediaType.parse("text/plain"), reviewGrade);
        RequestBody reviewContentsBody = RequestBody.create(MediaType.parse("text/plain"), reviewContents);
        RequestBody selectedExpertIdBody = RequestBody.create(MediaType.parse("text/plain"), selectedExpertId);

        // 3. 서버에서 받았던 서버 이미지 경로를 다시 보낸다 + 이미지 경로 총 개수도 보낸다.
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        requestMap.put("userIdWhoRequest", userIdWhoRequestBody);
        requestMap.put("reviewGrade", reviewGradeBody);
        requestMap.put("reviewContents", reviewContentsBody);
        requestMap.put("selectedExpertId", selectedExpertIdBody);

        Call<ChatRoomData> call = chatInterface.sendReviewInfo(requestMap);
        call.enqueue(new Callback<ChatRoomData>() {
            @Override
            public void onResponse(Call<ChatRoomData> call, retrofit2.Response<ChatRoomData> response) {
//                Log.i(TAG, "onResponse = " + response);
//                Log.i(TAG, "onResponse.body() = " + response.body());
                Toast.makeText(ReviewActivity.this, "리뷰 작성을 완료했습니다.", Toast.LENGTH_SHORT).show();
                editor.putString(chatRoomNumber + "reviewDone", "done");
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                intent.putExtra("chat_room_seq", chatRoomNumber);
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