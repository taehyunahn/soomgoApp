package com.example.soomgodev.Fragment;

import static com.example.soomgodev.StaticVariable.serverAddress;
import static com.example.soomgodev.StaticVariable.tagIntentOutput;
import static com.example.soomgodev.StaticVariable.tagServerToClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.soomgodev.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Chat.ChatInterface;
import Chat.ChatService;
import Chat.ViewType;
import ChatRoomList.ChatRoomData;
import ConnectToServer.DataClass;
import ConnectToServer.NetworkClient;
import ConnectToServer.UploadApis;
import ExpertUpdate.ExpertReviewActivity;
import ExpertUpdate.MultiImageAdapter;
import ExpertUpdate.ReviewAdapter;
import SurveyRequest.SurveyRequestMainActivity;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class ExpertProfileActivity extends AppCompatActivity {

    ImageView iv_expertProfileImage;
    TextView tv_expertName, tv_expertMainService, tv_expertIntro, tv_expertAddress, tv_expertTime, tv_expertYear, tv_expertIntroDetail;
    TextView tv_reviewAverage, tv_reviewCount, tv_hireCount, tv_moreReview;
    TextView tv_reviewAverageBig, tv_reviewCountBig;
    String selectedExpertId;
    ChipGroup chipGroup;
    private static final String TAG = "ExpertProfileActivity";
    Socket member_socket; // 서버와 연결되어있는 소켓 객체


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq, expertSeq;


    // 레트로핏 통신 공통
    NetworkClient networkClient;
    UploadApis uploadApis;
    private ChatInterface chatInterface;


    private RecyclerView review_recyclerview;
    ReviewAdapter reviewAdapter;


    private ArrayList<Uri> uriArrayList1 = new ArrayList<>(); //이미지의 uri를 담을 ArrayList 객체
    private RecyclerView recyclerView;
    MultiImageAdapter adapter;
    StringBuilder sb;
    Button btn_request;
    RatingBar ratingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_profile);
        getSupportActionBar().hide();

        iv_expertProfileImage = findViewById(R.id.iv_expertProfileImage); // 1. 프로필 사진
        tv_expertName = findViewById(R.id.tv_expertName); // 2. 이름
        tv_expertMainService = findViewById(R.id.tv_expertMainService); // 3. 메인서비스 필요
        tv_reviewAverage = findViewById(R.id.tv_reviewAverage); // 4. 평점 평균
        tv_reviewCount = findViewById(R.id.tv_reviewCount); //5. 리뷰 수
        tv_expertIntro = findViewById(R.id.tv_expertIntro); //6. 한줄소개
        tv_hireCount = findViewById(R.id.tv_hireCount); // 7. 고용횟수
        tv_expertAddress = findViewById(R.id.tv_expertAddress); // 8. 활동지역 expertAddress
        tv_expertTime = findViewById(R.id.tv_expertTime); // 9. 연락 가능 시간 expertTime
        tv_expertYear = findViewById(R.id.tv_expertYear); // 10. 경력 expertYear
        tv_expertIntroDetail = findViewById(R.id.tv_expertIntroDetail);  // 12. 서비스 상세 설명 expertIntroDetail
        tv_reviewAverageBig = findViewById(R.id.tv_reviewAverageBig);  // 12. 서비스 상세 설명 expertIntroDetail
        tv_reviewCountBig = findViewById(R.id.tv_reviewCountBig);  // 12. 서비스 상세 설명 expertIntroDetail
        ratingBar = findViewById(R.id.ratingBar);  // 12. 서비스 상세 설명 expertIntroDetail

        tv_moreReview = findViewById(R.id.tv_moreReview);

        btn_request = findViewById(R.id.btn_request);

        chipGroup = findViewById(R.id.chipGroup);
        recyclerView = findViewById(R.id.recyclerView);
        review_recyclerview = findViewById(R.id.review_recyclerview);


        sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userSeq = sharedPreferences.getString("userSeq",""); // 현재 로그인한 계정의 고객 고유값을 의미
        expertSeq = sharedPreferences.getString("expertSeq", ""); // 현재 로그인한 계정의 고수 고유값을 의미

        Intent intent = getIntent();
        selectedExpertId = intent.getStringExtra("selectedExpertId"); // 0. 선택한 리싸이클러뷰 아이템(고수정보)의 고수 고유번호, 서버로 보내서 각종 정보를 받아오기 위함
        String expertName = intent.getStringExtra("expertName"); // 2. 이름
        // 3. 메인서비스 필요 expertMainService
        String reviewAverage = intent.getStringExtra("reviewAverage"); // 4. 평점 평균
        String reviewCount = intent.getStringExtra("reviewCount"); // 5. 리뷰 수
        String expertIntro = intent.getStringExtra("expertIntro"); // 6. 한줄소개
        String hireCount = intent.getStringExtra("hireCount"); // 7. 고용횟수
        // 8. 활동지역 expertAddress
        // 9. 연락 가능 시간 expertTime
        // 10. 경력 expertYear
        // 11. 제공서비스 expertService
        // 12. 서비스 상세 설명 expertIntroDetail
        // 13. 사진
        // 14. 리뷰


        tv_expertName.setText(expertName); //2.이름
//        tv_reviewAverage.setText(String.valueOf(reviewAverage)); //4.평점평균
//        tv_reviewCount.setText("("+String.valueOf(reviewCount)+")"); //5.리뷰수
        tv_expertIntro.setText(expertIntro); //6.한줄소개
//        tv_hireCount.setText(String.valueOf(hireCount)+"회 고용됨"); //7.고용횟수

        // 서버에서 가져올 값 : 메인서비스, 활동지역, 연락 가능 시간, 경력, 제공서비스, 서비스 상세 설명, (사진이랑 리뷰는 추후 작업할 것)
        // 서버통신 프로세스 :
        // 1. 고수의 고유값을 서버로 보낸다.
        // 2. 고수의 고유값으로 해당 row(행)을 검색한다
        // 3. 고수의 고유값에 해당하는 row(행)에서 필요한 정보를 배열로 담아서, 클라이언트가 받는다.
        // 4. 받은 정보로 setText를 한다.
        sendExpertIdGetExpertInfo(selectedExpertId);
        photoListShowWithRetrofit("1", selectedExpertId);
        getReviewList(selectedExpertId);

        // 서버에서 현재 프로필의 고수 아이디를 보낸다.
        // request 테이블의 selectedExpertId를 기준으로 seq(request 고유값)을 가져온다
        // sharedPreference를 사용해서, 고유값의 이름으로 저장된 값이 있으면 button enable을 false로 변경한다.


        // 리뷰 더보기 버튼
        tv_moreReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ExpertReviewActivity.class);
                intent.putExtra("selectedExpertId", selectedExpertId);
                startActivity(intent);
            }
        });



        // 견적 요청하기 버튼
        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SurveyRequestMainActivity.class);
                editor.putString(userSeq + "selectedExpertId", selectedExpertId); // 현재 조회하고 있는 고수의 고유값을 s.f에 저장 → 견적 요청 최종 페이지에서 사용
                editor.commit();
                startActivity(intent);

//
//                SendToServerThread2 thread2 = new SendToServerThread2(member_socket);
//                thread2.start();



            }
        });

    }

    private void sendExpertIdGetExpertInfo(String selectedExpertId) {
        Log.i(TAG, "sendExpertIdGetExpertInfo() 메서드 실행은 됐습니다");
        // 서버통신 : 회원 고유값(userSeq)을 보내서, 그에 해당되는 칼럼 정보(userName, userEmail)를 찾아서 배열로 받아온다.
        //           이름과 이메일 텍스트에 세팅해준다.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = serverAddress + "expertInfoDetail.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "response = " + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Log.i(TAG, "jsonResponse = " + jsonResponse);

                    // 3. 메인서비스 필요 expertMainService
                    // 8. 활동지역 expertAddress
                    // 9. 연락 가능 시간 expertTime
                    // 10. 경력 expertYear
                    // 11. 제공서비스 expertService -> 추후 Chip 생성하는 로직 만들 예정 -> 별도 메소드로 구현하도록 하자
                    // 12. 서비스 상세 설명 expertIntroDetail

                    String expertMainService = jsonResponse.getString("expertMainService");
                    String expertAddress = jsonResponse.getString("expertAddress");
                    String expertTime = jsonResponse.getString("expertTime");
                    String expertYear = jsonResponse.getString("expertYear");
                    String expertIntroDetail = jsonResponse.getString("expertIntroDetail");
                    String expertService = jsonResponse.getString("expertService");
                    String requestId = jsonResponse.getString("requestId");
                    String requestSent = sharedPreferences.getString("requestSent","");
                    String expertProfileImage = jsonResponse.getString("expertProfileImage");


                    if(expertProfileImage.equals(null)) {
                        Glide.with(getApplicationContext()).load(R.drawable.ic_person).centerCrop().into(iv_expertProfileImage);
                    } else {
                        Glide.with(getApplicationContext()).load(expertProfileImage).centerCrop().into(iv_expertProfileImage);
                    }


                    Log.i(TAG, "jsonResponse.getString(\"reviewAverage\") = " + jsonResponse.getString("reviewAverage"));

                    if(jsonResponse.getString("reviewAverage") != null ) {
                        String reviewCount = jsonResponse.getString("reviewCount");
                        String reviewAverage = jsonResponse.getString("reviewAverage");

                        try{
                            float f = Float.parseFloat(reviewAverage);
                            String s = String.format("%.1f", f);

                            tv_reviewCount.setText("받은 리뷰 " + reviewCount + " 건");
                            tv_reviewAverage.setText(s);

                            tv_reviewCountBig.setText("받은 리뷰 " + reviewCount + " 건");
                            tv_reviewAverageBig.setText(s);
                            ratingBar.setRating(f);
                        }catch (Exception e){
                            tv_reviewCount.setText("받은 리뷰 0 건");
                            tv_reviewAverage.setText("0");

                            tv_reviewCountBig.setText("받은 리뷰 0 건");
                            tv_reviewAverageBig.setText("0");
                            ratingBar.setRating(0);
                        }

                    }

                    String hireCount = jsonResponse.getString("hireCount");
                    Log.i(TAG, "hireCount = " + hireCount);
                    tv_hireCount.setText(hireCount + "회 고용됨");


                    Log.i(TAG, "expertMainService = " + expertMainService);
                    Log.i(TAG, "expertAddress = " + expertAddress);
                    Log.i(TAG, "expertTime = " + expertTime);
                    Log.i(TAG, "expertYear = " + expertYear);
                    Log.i(TAG, "expertIntroDetail = " + expertIntroDetail);
                    Log.i(TAG, "expertService = " + expertService);
                    Log.i(TAG, "requestId = " + requestId);
                    Log.i(TAG, "requestSent = " + requestSent);

                    Log.i(TAG, "sharedPreferences.getString(userSeq) = " + sharedPreferences.getString("userSeq",""));



                    // 고수 프로필의 상세정보를 서버에서 가져온다. -> 이 중에서 대표 서비스를 S.F에 저장한다
                    editor.putString(userSeq + "selectedService", expertMainService); // 현재 조회하고 있는 고수의 고유값을 s.f에 저장 → 견적 요청 최종 페이지에서 사용

                    tv_expertMainService.setText(expertMainService);
                    tv_expertAddress.setText(expertAddress);
                    tv_expertTime.setText(expertTime);
                    tv_expertYear.setText(expertYear);
                    tv_expertIntroDetail.setText(expertIntroDetail);
                    createServiceChips(expertService);




                    if (requestSent.equals("sent")){
                        btn_request.setEnabled(false);
                        btn_request.setText("이미 요청서를 보냈습니다.");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "JSON에서 전달받은 에러 = " + e);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // textView.setText("That didn't work!");
                Log.e(TAG, String.valueOf(error));
            }
        }) {
            // 포스트 파라미터 넣기
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("selectedExpertId", selectedExpertId);
                Log.i(TAG, "selectedExpertId = " + selectedExpertId);


                return params;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    // (설명) 레트로핏을 사용하여, 서버 DB에 있는 고수 사진 정보를 받아와서 출력한다.
    // (동작) 고수고유값, 회원고유값을 보낸다. DB의 photo 테이블에서 고수고유값에 해당하는 모든 이미지주소를 가져온다.
    private void photoListShowWithRetrofit(String userSeq, String expertSeq){

        Log.i(TAG, "photoListShowWithRetrofit이 시작은 됩니다.");
        uploadApis = networkClient.getRetrofit().create(UploadApis.class);

        Call<List<DataClass>> call = uploadApis.photoListShow(userSeq, expertSeq);
        call.enqueue(new Callback<List<DataClass>>() {
            @Override
            public void onResponse(Call<List<DataClass>> call, retrofit2.Response<List<DataClass>> response) {
                if(response.isSuccessful()){
                    List<DataClass> responseBody = response.body();

                    Log.i(TAG, "uriArrayList1 = " + uriArrayList1);
                    for(int i = 0; i < responseBody.size(); i++) {
                        String photoPath = responseBody.get(i).getPhotoPath();
                        Log.i(TAG, "photoPath = " + photoPath);
                        Uri uri = Uri.parse(photoPath);
                        uriArrayList1.add(uri);
                    }
                    // ArrayList<Uri>를 사용하여, 리사이클러뷰 아이템을 생성한다.
                    adapter = new MultiImageAdapter(uriArrayList1, getApplicationContext());
                    recyclerView.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
                    recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));     // 리사이클러뷰 수평 스크롤 적용
                }
            }
            @Override
            public void onFailure(Call<List<DataClass>> call, Throwable t) {
                Log.i(TAG, "t = " + t);
            }
        });
    }


    private void createServiceChips(String expertService) {
        // '제공 서비스' Chip 생성 (XML에서 ChipGroup만 만들고, Java 파일로 Chip 생성)
        // 서버의 저장 형태 = 서비스1-서비스2-서비스3

        String[] expertServiceArray = expertService.split("-");
        for (int i = 0; i< expertServiceArray.length; i++){

            Chip chip = new Chip(this);
            chip.setText(expertServiceArray[i]);
            chip.setCloseIcon(getApplicationContext().getDrawable(R.drawable.ic_close));
            chip.setCloseIconVisible(false);
            chip.setTextAppearanceResource(R.style.ChipTextStyle);
            chip.setChipBackgroundColorResource(R.color.cardview_dark_background);
            chipGroup.addView(chip);

            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "chip을 닫기를 선택했습니다", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "chipGroup.getChildCount() = " + String.valueOf(chipGroup.getChildCount()));
                    if(chipGroup.getChildCount()==1) {
                        Toast.makeText(getApplicationContext(), "마지막 서비스는 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        chipGroup.removeView(v);
                        Log.i(TAG, "선택한 chip 이름" + chip.getText());
                        //몇 번째뷰인지 값으로 넘어옴
                        int position = Arrays.asList(expertServiceArray).indexOf(chip.getText());
                        Log.i(TAG, "선택한 chip 위치 : " + position);

                        sb = new StringBuilder();
                        Log.i(TAG, "반복문 입장 전, sb : " + sb);
                        for (int i = 0; i < expertServiceArray.length; i++) {
                            if (i != position){
                                sb.append(expertServiceArray[i]+"-");
                            }
                        }
                        Log.i(TAG, "반복문 완료 후, sb : " + sb);
                        String chipUpdate1 = sb.toString();
                        chipUpdate1 = chipUpdate1.substring(0, chipUpdate1.length() - 1);
                        Log.i(TAG, "처리완료값 : " + chipUpdate1);

                    }
                }
            });
        }
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