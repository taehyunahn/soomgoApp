package com.example.soomgodev.Fragment;

import static com.example.soomgodev.StaticVariable.serverAddress;
import static com.example.soomgodev.StaticVariable.tagClientToServer;
import static com.example.soomgodev.StaticVariable.tagServerToClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Rating;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Chat.ChatInterface;
import ChatRoomList.ChatRoomData;
import ConnectToServer.DataClass;
import ConnectToServer.NetworkClient;
import ConnectToServer.UploadApis;
import ExpertUpdate.ExertUpdateAddress;
import ExpertUpdate.ExertUpdateMainService;
import ExpertUpdate.ExertUpdateTime;
import ExpertUpdate.ExertUpdateYear;
import ExpertUpdate.ExpertReviewActivity;
import ExpertUpdate.ExpertUpdateImage;
import ExpertUpdate.ExpertUpdateIntro;
import ExpertUpdate.ExpertUpdateIntroDetail;
import ExpertUpdate.ExpertUpdateName;
import ExpertUpdate.ExpertUpdatePhoto;
import ExpertUpdate.ExpertUpdateService;
import ExpertUpdate.MultiImageAdapter;
import ExpertUpdate.ReviewAdapter;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class Fragment23 extends Fragment {

    public interface OnMyListener {
        void onReceivedData(Object data);
    }

    private OnMyListener mOnMyListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(getActivity() != null && getActivity() instanceof OnMyListener) {
            mOnMyListener = (OnMyListener) getActivity();
        }
    }


    // sharedPreference 공통
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq, expertSeq;

    // 레트로핏 통신 공통
    NetworkClient networkClient;
    private UploadApis uploadApis;
    private ChatInterface chatInterface;

    private String userProfileImage, expertService, expertProfileImage;
    ImageView iv_expertProfileImage;
    ChipGroup chipGroup;

    TextView tv_expertName, tv_expertMainService, tv_expertIntro, tv_expertAddress, tv_expertTime, tv_expertYear, tv_expertIntroDetail;
    TextView tv_updateName, tv_updateMainService, tv_updateIntro, tv_updateAddress, tv_updateTime, tv_updateYear, tv_updateIntroDetail;
    TextView tv_updateService, tv_updatePhoto;
    TextView tv_reviewAverage, tv_reviewCount;
    RatingBar ratingBar;
    StringBuilder sb;
    Button btn_morePhoto, btn_moreReview;

    private RecyclerView recyclerView;
    MultiImageAdapter adapter;
    private static final String TAG = "Fragment23";

    private RecyclerView review_recyclerview;
    ReviewAdapter reviewAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView 실행");

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment2_3, container, false);

        sharedPreferences = this.getActivity().getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        userSeq = sharedPreferences.getString("userSeq","");
        expertSeq= sharedPreferences.getString("expertSeq", "");
        editor = sharedPreferences.edit();

        recyclerView = rootView.findViewById(R.id.photo_recyclerview);
        tv_expertName = rootView.findViewById(R.id.tv_expertName);
        tv_expertMainService = rootView.findViewById(R.id.tv_expertMainService);
        tv_expertIntro = rootView.findViewById(R.id.tv_expertIntro);
        tv_expertAddress = rootView.findViewById(R.id.tv_expertAddress);
        tv_expertTime = rootView.findViewById(R.id.tv_expertTime);
        tv_expertYear = rootView.findViewById(R.id.tv_expertYear);
        chipGroup = rootView.findViewById(R.id.chipGroup);
        tv_expertIntroDetail = rootView.findViewById(R.id.tv_expertIntroDetail);
        iv_expertProfileImage = rootView.findViewById(R.id.iv_expertProfileImage);
        tv_updateService = rootView.findViewById(R.id.tv_updateService);
        tv_updateName = rootView.findViewById(R.id.tv_updateName);
        tv_updateIntro = rootView.findViewById(R.id.tv_updateIntro);
        tv_updateMainService = rootView.findViewById(R.id.tv_updateMainService);
        tv_updateTime = rootView.findViewById(R.id.tv_updateTime);
        tv_updateAddress = rootView.findViewById(R.id.tv_updateAddress);
        tv_updateYear = rootView.findViewById(R.id.tv_updateYear);
        tv_updateIntroDetail = rootView.findViewById(R.id.tv_updateIntroDetail);
        tv_updatePhoto = rootView.findViewById(R.id.tv_updatePhoto);
        btn_morePhoto = rootView.findViewById(R.id.btn_morePhoto);
        btn_moreReview = rootView.findViewById(R.id.btn_moreReview);
        review_recyclerview = rootView.findViewById(R.id.review_recyclerview);
        tv_reviewAverage = rootView.findViewById(R.id.tv_reviewAverage);
        tv_reviewCount = rootView.findViewById(R.id.tv_reviewCount);
        ratingBar = rootView.findViewById(R.id.ratingBar);

        // 프로필 사진 수정
        iv_expertProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExpertUpdateImage.class);
                // 프래그먼트에서 액티비티로 이동은 하는데, 아래 intent.puExtra 값은 전달되지 않습니다.
                // 프래그먼트에서 액티비티로 값을 전달하려면, 별도 interface callback을 정의하여 전달해야 한다고 하는데,
                // 비슷하게 따라해도 동작하지 않고, 해당 코드가 잘 이해되지 않아서 질문드리고 싶습니다.

                Log.i(TAG, "userProfileImage = " + userProfileImage);
//                intent.putExtra("userProfileImageIntent", userProfileImage);
                intent.putExtra("expertProfileImageIntent", expertProfileImage);
                startActivity(intent);
                chipGroup.removeAllViews();
            }
        });

        // 사진 더보기 버튼
        btn_morePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExpertUpdatePhoto.class);
                startActivity(intent);
                chipGroup.removeAllViews();
            }
        });

        // 리뷰 더보기 버튼
        btn_moreReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExpertReviewActivity.class);
                intent.putExtra("selectedExpertId", expertSeq);
                startActivity(intent);
                chipGroup.removeAllViews();
            }
        });


        // 이름 수정
        tv_updateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExpertUpdateName.class);
                intent.putExtra("updateName", tv_expertName.getText().toString());
                startActivity(intent);
                chipGroup.removeAllViews();
            }
        });

        // 대표 서비스 수정
        tv_updateMainService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExertUpdateMainService.class);
                startActivity(intent);
                chipGroup.removeAllViews();
            }
        });

        // 한줄소개 수정
        tv_updateIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExpertUpdateIntro.class);
                startActivity(intent);
                chipGroup.removeAllViews();
            }
        });

        // 활동지역 수정
        tv_updateAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExertUpdateAddress.class);
                startActivity(intent);
                chipGroup.removeAllViews();
            }
        });

        // 연락가능시간 수정
        tv_updateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExertUpdateTime.class);
                startActivity(intent);
                chipGroup.removeAllViews();
            }
        });

        // 경력 수정
        tv_updateYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExertUpdateYear.class);
                startActivity(intent);
                chipGroup.removeAllViews();
            }
        });

        // 제공 서비스 수정
        tv_updateService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExpertUpdateService.class);
                startActivity(intent);
                chipGroup.removeAllViews();
            }
        });

        // 고수서비스 상세설명 수정
        tv_updateIntroDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExpertUpdateIntroDetail.class);
                startActivity(intent);
                chipGroup.removeAllViews();
            }
        });

        // 사진 수정
        tv_updatePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExpertUpdatePhoto.class);
                startActivity(intent);
                chipGroup.removeAllViews();
            }
        });
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart 실행");
        requestProfileInfoRetrofit(userSeq); // 고수 프로필 정보를 가져오는 서버통신
        photoListShowWithRetrofit(userSeq, expertSeq);
        getReviewList(expertSeq);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume 실행");
    }


    private void requestProfileInfoRetrofit(String userSeq) {

        uploadApis = networkClient.getRetrofit().create(UploadApis.class);
        //    @POST("expertUpdate.php")
        Call<DataClass> call = uploadApis.requestExpertInfo(userSeq);
        call.enqueue(new Callback<DataClass>() {
            @Override
            public void onResponse(Call<DataClass> call, retrofit2.Response<DataClass> response) {
                Log.d("onResponse", response.body().toString());

                if(!response.isSuccessful()){
                    return;
                }

                DataClass responseBody = response.body();


                String expertName = responseBody.getExpertName();
                if(expertName.equals("")){
                    expertName = "이름을 입력하세요";
                }

                String expertIntro = responseBody.getExpertIntro();
                Log.i(TAG, "expertIntro = " + expertIntro);
                if(expertIntro == null){
                    expertIntro = "한줄소개를 입력하세요";
                }

                String expertAddress = responseBody.getExpertAddress();

                String expertTime = responseBody.getExpertTime();
                if(expertTime == null){
                    expertTime = "연락 가능한 시간을 선택하세요";
                }

                String expertYear = responseBody.getExpertYear();
                if(expertYear == null){
                    expertYear = "경력을 선택하세요";
                }

                String expertIntroDetail = responseBody.getExpertIntroDetail();
                if(expertIntroDetail == null){
                    expertIntroDetail = "서비스를 상세히 소개해주세요";
                }

                Log.i(TAG, "expertName = " + expertName);
                Log.i(TAG, "expertIntro = " + expertIntro);
                Log.i(TAG, "expertAddress = " + expertAddress);
                Log.i(TAG, "expertTime = " + expertTime);
                Log.i(TAG, "expertYear = " + expertYear);
                Log.i(TAG, "expertIntroDetail = " + expertIntroDetail);


                tv_expertName.setText(expertName);
                tv_expertIntro.setText(expertIntro);
                tv_expertAddress.setText(expertAddress);
                tv_expertTime.setText(expertTime);
                tv_expertYear.setText(expertYear);
                tv_expertIntroDetail.setText(expertIntroDetail);
                tv_expertMainService.setText(sharedPreferences.getString(userSeq+"mainService", "대표 서비스를 선택하세요"));
                expertService = responseBody.getExpertService();
                createServiceChips(expertService);
//                userProfileImage = responseBody.getUserProfileImage();
                expertProfileImage = responseBody.getExpertProfileImage();



                tv_reviewCount.setText("받은 리뷰 " + responseBody.getReviewCount() + " 건");

                if(responseBody.getReviewAverage() != null) {

                    try{
                        String a = responseBody.getReviewAverage();
                        float f = Float.parseFloat(a);
                        String s = String.format("%.1f", f);
                        Log.i(TAG, "a = " + a);
                        Log.i(TAG, "f = " + f);
                        Log.i(TAG, "s = " + s);
                        tv_reviewAverage.setText(s);
                        ratingBar.setRating(f);
                    }catch (Exception e){
                        tv_reviewCount.setText("받은 리뷰 0 건");
                        tv_reviewAverage.setText("0");
                        ratingBar.setRating(0);
                    }
                }



                // 고수에 해당하는 평점의 평균과 리뷰 숫자를 가져오자
                // 1) 평점의 평균
                // 2) 리뷰 총 숫자

                Log.i(TAG, "profileImageShow의 인자 userProfileImage  = " + userProfileImage);
                Log.i(TAG, "profileImageShow의 인자 iv_expertProfileImage  = " + iv_expertProfileImage);

//                if(userProfileImage != null){
//                    Glide.with(getContext()).load(userProfileImage).circleCrop().into(iv_expertProfileImage);
//                }

                Log.i(TAG, "expertProfileImage = " +  expertProfileImage);

                if(expertProfileImage != null && !expertProfileImage.equals("0")){
                    Log.i(TAG, "if문 안에서 expertProfileImage = " +  expertProfileImage);

                    Glide.with(getContext()).load(expertProfileImage).circleCrop().into(iv_expertProfileImage);
                }

            }

            @Override
            public void onFailure(Call<DataClass> call, Throwable t) {
                Log.i(TAG, "onFailure = " + t);
            }
        });


    }

    // 1)Chip Close가 반영된, '제공 서비스' String 값을 서버에 보낸다.
    // 2)서버에 '제공 서비스'를 update 한다
    // 3)update된 '제공 서비스' 값을 클라이언트에서 받는다

    public void serverChipUpdate (String updatedService) {
        // 서버통신 : 회원 고유값(userSeq)을 보내서, 그에 해당되는 칼럼 정보(userName, userEmail)를 찾아서 배열로 받아온다.
        //           이름과 이메일 텍스트에 세팅해준다.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = serverAddress + "expertUpdateService.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, tagServerToClient + response);

                // 3)update된 '제공 서비스' 값을 클라이언트에서 받는다 -> expertService를 갱신한다.
                expertService = response;

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
                // 1)Chip Close가 반영된, '제공 서비스' String 값을 서버에 보낸다.
                params.put("updatedService", updatedService);
                params.put("userSeq", userSeq);
                Log.i(TAG, tagClientToServer + "updatedService = " + updatedService);
                Log.i(TAG, tagClientToServer + "userSeq = " + userSeq);

                return params;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private void createServiceChips(String expertService) {
        // '제공 서비스' Chip 생성 (XML에서 ChipGroup만 만들고, Java 파일로 Chip 생성)
        // 서버의 저장 형태 = 서비스1-서비스2-서비스3

        String[] expertServiceArray = expertService.split("-");
        for (int i = 0; i< expertServiceArray.length; i++){

            Chip chip = new Chip(getContext());
            chip.setText(expertServiceArray[i]);
            chip.setCloseIcon(getContext().getDrawable(R.drawable.ic_close));
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
                    Toast.makeText(getContext(), "chip을 닫기를 선택했습니다", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "chipGroup.getChildCount() = " + String.valueOf(chipGroup.getChildCount()));
                    if(chipGroup.getChildCount()==1) {
                        Toast.makeText(getContext(), "마지막 서비스는 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
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

                        // 업데이트된 '제공서비스' 값을 서버에 보내서 '제공서비스'값을 갱신한다.
                        serverChipUpdate(chipUpdate1);
                    }
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }



    // 프래그먼트에서 사용
    // (설명) 레트로핏을 사용하여, 서버 DB에 있는 고수 사진 정보를 받아와서 출력한다.
    // (동작) 고수고유값, 회원고유값을 보낸다. DB의 photo 테이블에서 고수고유값에 해당하는 모든 이미지주소를 가져온다.
    private void photoListShowWithRetrofit(String userSeq, String expertSeq){
        // 서버통신을 위한 세팅

        uploadApis = networkClient.getRetrofit().create(UploadApis.class);

        Call<List<DataClass>> call = uploadApis.photoListShow(userSeq, expertSeq);
        call.enqueue(new Callback<List<DataClass>>() {
            @Override
            public void onResponse(Call<List<DataClass>> call, retrofit2.Response<List<DataClass>> response) {
                if(response.isSuccessful()){
                    Log.i(TAG, "response = " + response);
                    List<DataClass> responseBody = response.body();
                    Log.i(TAG, "response.body() = " + response.body());


                    ArrayList<Uri> uriArrayList = new ArrayList<>(); //이미지의 uri를 담을 ArrayList 객체

                    for(int i = 0; i < responseBody.size(); i++) {
                        String photoPath = responseBody.get(i).getPhotoPath();
                        Log.i(TAG, "photoPath = " + photoPath);
                        Uri uri = Uri.parse(photoPath);
                        uriArrayList.add(uri);
                    }

                    // ArrayList<Uri>를 사용하여, 리사이클러뷰 아이템을 생성한다.
                    adapter = new MultiImageAdapter(uriArrayList, getContext());

//                    adapter = new MultiImageAdapter(uriList, getApplicationContext());
//                    for (int i = 0; i < adapter.getItemCount(); i++) {
//                        adapter.removeItem(i);
//                    }
//                    adapter.notifyItemRangeRemoved(0, adapter.getItemCount());

                    recyclerView.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));     // 리사이클러뷰 수평 스크롤 적용
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<List<DataClass>> call, Throwable t) {
                Log.i(TAG, "t = " + t);
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
                reviewAdapter = new ReviewAdapter(reviewList, getContext());
                review_recyclerview.setAdapter(reviewAdapter);
                review_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));     // 리사이클러뷰 수평 스크롤 적용
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