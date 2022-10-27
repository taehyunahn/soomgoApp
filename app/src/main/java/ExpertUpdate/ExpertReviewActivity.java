package ExpertUpdate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.soomgodev.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Chat.ChatInterface;
import ChatRoomList.ChatRoomData;
import ConnectToServer.NetworkClient;
import ConnectToServer.UploadApis;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class ExpertReviewActivity extends AppCompatActivity {

    // 레트로핏 통신 공통
    NetworkClient networkClient;
    private UploadApis uploadApis;
    private ChatInterface chatInterface;

    private static final String TAG = "ExpertReviewActivity";


    ImageButton btn_cancel;
    private RecyclerView review_recyclerview;
    ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_review);
        getSupportActionBar().hide();

        review_recyclerview = findViewById(R.id.review_recyclerview);
        btn_cancel = findViewById(R.id.btn_cancel);

        Intent intent = getIntent();
        String selectedExpertId = intent.getStringExtra("selectedExpertId");
        getReviewList(selectedExpertId);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    // 보내야 할 값 : 고수의 고유값(expertSeq)을 보낸다
    // 서버에서 할 일 :
    // 1. review 테이블의 id_expert 칼럼이 고수의 고유값과 일치하는 모든 목록을 뽑아서 배열로 담는다.
    // 2. 평점의 총합계 / 총 개수하여 평점의 평균을 구해서, 배열로 담을 때 넣는다.
    // 돌려 받을 값 : 1) 작성자 이름, 2) 평점, 3) 작성내용, 4) 작성날짜, 5) 평점의 평균
    // 돌려 받은 값으로 리사이클러뷰를 생성한다.
    private void getReviewList(String selectedExpertId) {
        // 필요한 값
        // 고수 이름, 서비스 이름, 받은 견적 금액

        Log.i(TAG, "getReviewList 안에서 ");

        chatInterface = networkClient.getRetrofit().create(ChatInterface.class);

        // 1. 회원고유 번호와, 2. 고수 고유번호를 보낸다.
        RequestBody selectedExpertIdBody = RequestBody.create(MediaType.parse("text/plain"), selectedExpertId);

        // 3. 서버에서 받았던 서버 이미지 경로를 다시 보낸다 + 이미지 경로 총 개수도 보낸다.
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        requestMap.put("selectedExpertId", selectedExpertIdBody);

        Call<List<ChatRoomData>> call = chatInterface.getReviewInfo(requestMap);
        call.enqueue(new Callback<List<ChatRoomData>>() {
            @Override
            public void onResponse(Call<List<ChatRoomData>> call, retrofit2.Response<List<ChatRoomData>> response) {
                Log.i(TAG, "onResponse = " + response);
                Log.i(TAG, "onResponse.body() = " + response.body());
                /*ChatRoomData responseBody = response.body();
                tv_expertName.setText(responseBody.getExpertName());*/

                ArrayList<ChatRoomData> reviewList = new ArrayList<>(); //이미지의 uri를 담을 ArrayList 객체

                List<ChatRoomData> responseBody = response.body();
                for(int i = 0; i < responseBody.size(); i++) {
                    reviewList.add(responseBody.get(i));
                    Log.i(TAG, "responseBody.get(i) = " + responseBody.get(i));
                    Log.i(TAG, "responseBody.get(i).getReviewWriterName() = " + responseBody.get(i).getReviewWriterName());
                    Log.i(TAG, "responseBody.get(i).getReviewGrade() = " + responseBody.get(i).getReviewGrade());
                    Log.i(TAG, "responseBody.get(i).getReviewContents() = " + responseBody.get(i).getReviewContents());
                    Log.i(TAG, "responseBody.get(i).getReviewDate() = " + responseBody.get(i).getReviewDate());
                }
//                if(responseBody.size() > 0){
//                    tv_expertCount.setText(responseBody.get(0).getCount()  + "명의 고수");
//                }

                Log.i(TAG, "reviewList = " + reviewList);
                reviewAdapter = new ReviewAdapter(reviewList, getApplicationContext());
                review_recyclerview.setAdapter(reviewAdapter);
                review_recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));     // 리사이클러뷰 수평 스크롤 적용
//                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));     // 리사이클러뷰 수평 스크롤 적용

                reviewAdapter.notifyDataSetChanged();


            }

            @Override
            public void onFailure(Call<List<ChatRoomData>> call, Throwable t) {
                Log.i(TAG, "getReviewList의 t = " + t);
            }
        });
    }


}