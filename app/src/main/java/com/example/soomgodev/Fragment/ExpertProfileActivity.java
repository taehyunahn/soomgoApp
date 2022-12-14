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
    Socket member_socket; // ????????? ?????????????????? ?????? ??????


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq, expertSeq;


    // ???????????? ?????? ??????
    NetworkClient networkClient;
    UploadApis uploadApis;
    private ChatInterface chatInterface;


    private RecyclerView review_recyclerview;
    ReviewAdapter reviewAdapter;


    private ArrayList<Uri> uriArrayList1 = new ArrayList<>(); //???????????? uri??? ?????? ArrayList ??????
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

        iv_expertProfileImage = findViewById(R.id.iv_expertProfileImage); // 1. ????????? ??????
        tv_expertName = findViewById(R.id.tv_expertName); // 2. ??????
        tv_expertMainService = findViewById(R.id.tv_expertMainService); // 3. ??????????????? ??????
        tv_reviewAverage = findViewById(R.id.tv_reviewAverage); // 4. ?????? ??????
        tv_reviewCount = findViewById(R.id.tv_reviewCount); //5. ?????? ???
        tv_expertIntro = findViewById(R.id.tv_expertIntro); //6. ????????????
        tv_hireCount = findViewById(R.id.tv_hireCount); // 7. ????????????
        tv_expertAddress = findViewById(R.id.tv_expertAddress); // 8. ???????????? expertAddress
        tv_expertTime = findViewById(R.id.tv_expertTime); // 9. ?????? ?????? ?????? expertTime
        tv_expertYear = findViewById(R.id.tv_expertYear); // 10. ?????? expertYear
        tv_expertIntroDetail = findViewById(R.id.tv_expertIntroDetail);  // 12. ????????? ?????? ?????? expertIntroDetail
        tv_reviewAverageBig = findViewById(R.id.tv_reviewAverageBig);  // 12. ????????? ?????? ?????? expertIntroDetail
        tv_reviewCountBig = findViewById(R.id.tv_reviewCountBig);  // 12. ????????? ?????? ?????? expertIntroDetail
        ratingBar = findViewById(R.id.ratingBar);  // 12. ????????? ?????? ?????? expertIntroDetail

        tv_moreReview = findViewById(R.id.tv_moreReview);

        btn_request = findViewById(R.id.btn_request);

        chipGroup = findViewById(R.id.chipGroup);
        recyclerView = findViewById(R.id.recyclerView);
        review_recyclerview = findViewById(R.id.review_recyclerview);


        sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userSeq = sharedPreferences.getString("userSeq",""); // ?????? ???????????? ????????? ?????? ???????????? ??????
        expertSeq = sharedPreferences.getString("expertSeq", ""); // ?????? ???????????? ????????? ?????? ???????????? ??????

        Intent intent = getIntent();
        selectedExpertId = intent.getStringExtra("selectedExpertId"); // 0. ????????? ?????????????????? ?????????(????????????)??? ?????? ????????????, ????????? ????????? ?????? ????????? ???????????? ??????
        String expertName = intent.getStringExtra("expertName"); // 2. ??????
        // 3. ??????????????? ?????? expertMainService
        String reviewAverage = intent.getStringExtra("reviewAverage"); // 4. ?????? ??????
        String reviewCount = intent.getStringExtra("reviewCount"); // 5. ?????? ???
        String expertIntro = intent.getStringExtra("expertIntro"); // 6. ????????????
        String hireCount = intent.getStringExtra("hireCount"); // 7. ????????????
        // 8. ???????????? expertAddress
        // 9. ?????? ?????? ?????? expertTime
        // 10. ?????? expertYear
        // 11. ??????????????? expertService
        // 12. ????????? ?????? ?????? expertIntroDetail
        // 13. ??????
        // 14. ??????


        tv_expertName.setText(expertName); //2.??????
//        tv_reviewAverage.setText(String.valueOf(reviewAverage)); //4.????????????
//        tv_reviewCount.setText("("+String.valueOf(reviewCount)+")"); //5.?????????
        tv_expertIntro.setText(expertIntro); //6.????????????
//        tv_hireCount.setText(String.valueOf(hireCount)+"??? ?????????"); //7.????????????

        // ???????????? ????????? ??? : ???????????????, ????????????, ?????? ?????? ??????, ??????, ???????????????, ????????? ?????? ??????, (???????????? ????????? ?????? ????????? ???)
        // ???????????? ???????????? :
        // 1. ????????? ???????????? ????????? ?????????.
        // 2. ????????? ??????????????? ?????? row(???)??? ????????????
        // 3. ????????? ???????????? ???????????? row(???)?????? ????????? ????????? ????????? ?????????, ?????????????????? ?????????.
        // 4. ?????? ????????? setText??? ??????.
        sendExpertIdGetExpertInfo(selectedExpertId);
        photoListShowWithRetrofit("1", selectedExpertId);
        getReviewList(selectedExpertId);

        // ???????????? ?????? ???????????? ?????? ???????????? ?????????.
        // request ???????????? selectedExpertId??? ???????????? seq(request ?????????)??? ????????????
        // sharedPreference??? ????????????, ???????????? ???????????? ????????? ?????? ????????? button enable??? false??? ????????????.


        // ?????? ????????? ??????
        tv_moreReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ExpertReviewActivity.class);
                intent.putExtra("selectedExpertId", selectedExpertId);
                startActivity(intent);
            }
        });



        // ?????? ???????????? ??????
        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SurveyRequestMainActivity.class);
                editor.putString(userSeq + "selectedExpertId", selectedExpertId); // ?????? ???????????? ?????? ????????? ???????????? s.f??? ?????? ??? ?????? ?????? ?????? ??????????????? ??????
                editor.commit();
                startActivity(intent);

//
//                SendToServerThread2 thread2 = new SendToServerThread2(member_socket);
//                thread2.start();



            }
        });

    }

    private void sendExpertIdGetExpertInfo(String selectedExpertId) {
        Log.i(TAG, "sendExpertIdGetExpertInfo() ????????? ????????? ????????????");
        // ???????????? : ?????? ?????????(userSeq)??? ?????????, ?????? ???????????? ?????? ??????(userName, userEmail)??? ????????? ????????? ????????????.
        //           ????????? ????????? ???????????? ???????????????.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = serverAddress + "expertInfoDetail.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "response = " + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Log.i(TAG, "jsonResponse = " + jsonResponse);

                    // 3. ??????????????? ?????? expertMainService
                    // 8. ???????????? expertAddress
                    // 9. ?????? ?????? ?????? expertTime
                    // 10. ?????? expertYear
                    // 11. ??????????????? expertService -> ?????? Chip ???????????? ?????? ?????? ?????? -> ?????? ???????????? ??????????????? ??????
                    // 12. ????????? ?????? ?????? expertIntroDetail

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

                            tv_reviewCount.setText("?????? ?????? " + reviewCount + " ???");
                            tv_reviewAverage.setText(s);

                            tv_reviewCountBig.setText("?????? ?????? " + reviewCount + " ???");
                            tv_reviewAverageBig.setText(s);
                            ratingBar.setRating(f);
                        }catch (Exception e){
                            tv_reviewCount.setText("?????? ?????? 0 ???");
                            tv_reviewAverage.setText("0");

                            tv_reviewCountBig.setText("?????? ?????? 0 ???");
                            tv_reviewAverageBig.setText("0");
                            ratingBar.setRating(0);
                        }

                    }

                    String hireCount = jsonResponse.getString("hireCount");
                    Log.i(TAG, "hireCount = " + hireCount);
                    tv_hireCount.setText(hireCount + "??? ?????????");


                    Log.i(TAG, "expertMainService = " + expertMainService);
                    Log.i(TAG, "expertAddress = " + expertAddress);
                    Log.i(TAG, "expertTime = " + expertTime);
                    Log.i(TAG, "expertYear = " + expertYear);
                    Log.i(TAG, "expertIntroDetail = " + expertIntroDetail);
                    Log.i(TAG, "expertService = " + expertService);
                    Log.i(TAG, "requestId = " + requestId);
                    Log.i(TAG, "requestSent = " + requestSent);

                    Log.i(TAG, "sharedPreferences.getString(userSeq) = " + sharedPreferences.getString("userSeq",""));



                    // ?????? ???????????? ??????????????? ???????????? ????????????. -> ??? ????????? ?????? ???????????? S.F??? ????????????
                    editor.putString(userSeq + "selectedService", expertMainService); // ?????? ???????????? ?????? ????????? ???????????? s.f??? ?????? ??? ?????? ?????? ?????? ??????????????? ??????

                    tv_expertMainService.setText(expertMainService);
                    tv_expertAddress.setText(expertAddress);
                    tv_expertTime.setText(expertTime);
                    tv_expertYear.setText(expertYear);
                    tv_expertIntroDetail.setText(expertIntroDetail);
                    createServiceChips(expertService);




                    if (requestSent.equals("sent")){
                        btn_request.setEnabled(false);
                        btn_request.setText("?????? ???????????? ???????????????.");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "JSON?????? ???????????? ?????? = " + e);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // textView.setText("That didn't work!");
                Log.e(TAG, String.valueOf(error));
            }
        }) {
            // ????????? ???????????? ??????
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

    // (??????) ??????????????? ????????????, ?????? DB??? ?????? ?????? ?????? ????????? ???????????? ????????????.
    // (??????) ???????????????, ?????????????????? ?????????. DB??? photo ??????????????? ?????????????????? ???????????? ?????? ?????????????????? ????????????.
    private void photoListShowWithRetrofit(String userSeq, String expertSeq){

        Log.i(TAG, "photoListShowWithRetrofit??? ????????? ?????????.");
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
                    // ArrayList<Uri>??? ????????????, ?????????????????? ???????????? ????????????.
                    adapter = new MultiImageAdapter(uriArrayList1, getApplicationContext());
                    recyclerView.setAdapter(adapter);   // ????????????????????? ????????? ??????
                    recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));     // ?????????????????? ?????? ????????? ??????
                }
            }
            @Override
            public void onFailure(Call<List<DataClass>> call, Throwable t) {
                Log.i(TAG, "t = " + t);
            }
        });
    }


    private void createServiceChips(String expertService) {
        // '?????? ?????????' Chip ?????? (XML?????? ChipGroup??? ?????????, Java ????????? Chip ??????)
        // ????????? ?????? ?????? = ?????????1-?????????2-?????????3

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
                    Toast.makeText(getApplicationContext(), "chip??? ????????? ??????????????????", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "chipGroup.getChildCount() = " + String.valueOf(chipGroup.getChildCount()));
                    if(chipGroup.getChildCount()==1) {
                        Toast.makeText(getApplicationContext(), "????????? ???????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
                    } else {
                        chipGroup.removeView(v);
                        Log.i(TAG, "????????? chip ??????" + chip.getText());
                        //??? ??????????????? ????????? ?????????
                        int position = Arrays.asList(expertServiceArray).indexOf(chip.getText());
                        Log.i(TAG, "????????? chip ?????? : " + position);

                        sb = new StringBuilder();
                        Log.i(TAG, "????????? ?????? ???, sb : " + sb);
                        for (int i = 0; i < expertServiceArray.length; i++) {
                            if (i != position){
                                sb.append(expertServiceArray[i]+"-");
                            }
                        }
                        Log.i(TAG, "????????? ?????? ???, sb : " + sb);
                        String chipUpdate1 = sb.toString();
                        chipUpdate1 = chipUpdate1.substring(0, chipUpdate1.length() - 1);
                        Log.i(TAG, "??????????????? : " + chipUpdate1);

                    }
                }
            });
        }
    }

    // ????????? ??? ??? : ????????? ?????????(expertSeq)??? ?????????
    // ???????????? ??? ??? :
    // 1. review ???????????? id_expert ????????? ????????? ???????????? ???????????? ?????? ????????? ????????? ????????? ?????????.
    // 2. ????????? ????????? / ??? ???????????? ????????? ????????? ?????????, ????????? ?????? ??? ?????????.
    // ?????? ?????? ??? : 1) ????????? ??????, 2) ??????, 3) ????????????, 4) ????????????, 5) ????????? ??????
    // ?????? ?????? ????????? ????????????????????? ????????????.
    private void getReviewList(String selectedExpertId) {
        // ????????? ???
        // ?????? ??????, ????????? ??????, ?????? ?????? ??????

        Log.i(TAG, "getReviewList ????????? ");

        chatInterface = networkClient.getRetrofit().create(ChatInterface.class);

        // 1. ???????????? ?????????, 2. ?????? ??????????????? ?????????.
        RequestBody selectedExpertIdBody = RequestBody.create(MediaType.parse("text/plain"), selectedExpertId);

        // 3. ???????????? ????????? ?????? ????????? ????????? ?????? ????????? + ????????? ?????? ??? ????????? ?????????.
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

                ArrayList<ChatRoomData> reviewList = new ArrayList<>(); //???????????? uri??? ?????? ArrayList ??????

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
//                    tv_expertCount.setText(responseBody.get(0).getCount()  + "?????? ??????");
//                }

                Log.i(TAG, "reviewList = " + reviewList);
                reviewAdapter = new ReviewAdapter(reviewList, getApplicationContext());
                review_recyclerview.setAdapter(reviewAdapter);
                review_recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));     // ?????????????????? ?????? ????????? ??????
//                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));     // ?????????????????? ?????? ????????? ??????

                reviewAdapter.notifyDataSetChanged();


            }

            @Override
            public void onFailure(Call<List<ChatRoomData>> call, Throwable t) {
                Log.i(TAG, "getReviewList??? t = " + t);
            }
        });
    }





}