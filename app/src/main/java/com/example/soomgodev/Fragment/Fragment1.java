package com.example.soomgodev.Fragment;

import static com.example.soomgodev.StaticVariable.serverAddress;
import static com.example.soomgodev.StaticVariable.tagClientToServer;
import static com.example.soomgodev.StaticVariable.tagServerToClient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.soomgodev.ExpertAdapter2;
import com.example.soomgodev.ExpertData;
import com.example.soomgodev.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import Chat.ChatRoomActivity;

public class Fragment1 extends Fragment {

    // sharedPreference 기본설정
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq, expertSeq;
    TextView tv_serviceSearch, tv_showAll;
    Button btn_change;
    ImageButton ib_engish, ib_business, ib_phone, ib_test;
    private static final String TAG = "Fragment1";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    String expertListCount , sort;
    List<ExpertData> expertDataList;

    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;

    private String[] images = new String[] {
            "https://assets.cdn.soomgo.com/images/banner/banner-safeways-web-main@2x.png",
            "https://assets.cdn.soomgo.com/images/banner/banner-model-recommend-service-web.png",
            "https://assets.cdn.soomgo.com/images/banner/banner-exhibitions-exercise-web-main@2x.png"
    };


    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.
    private Boolean handlerWork;
    private ImageData ImageData;

    BottomNavigationView bottomNavigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 고수 찾기 화면
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_1, container, false);

        // (프래그먼트에서) sharedPreference 기본설정
        sharedPreferences = this.getActivity().getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userSeq = sharedPreferences.getString("userSeq","");

        ib_engish = rootView.findViewById(R.id.ib_engish);
        ib_business = rootView.findViewById(R.id.ib_business);
        ib_phone = rootView.findViewById(R.id.ib_phone);
        ib_test = rootView.findViewById(R.id.ib_test);
        btn_change = rootView.findViewById(R.id.btn_change);
        tv_showAll = rootView.findViewById(R.id.tv_showAll);

        tv_serviceSearch = rootView.findViewById(R.id.tv_serviceSearch);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        //리사이클러뷰 초기 세팅
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<ImageData> imagesArray = new ArrayList<>();
        imagesArray.add(new ImageData("https://assets.cdn.soomgo.com/images/banner/banner-safeways-web-main@2x.png", serverAddress + "soomgo/ad1.php"));
        imagesArray.add(new ImageData("https://assets.cdn.soomgo.com/images/banner/banner-model-recommend-service-web.png", serverAddress + "soomgo/ad2.php"));
        imagesArray.add(new ImageData("https://assets.cdn.soomgo.com/images/banner/banner-exhibitions-exercise-web-main@2x.png", serverAddress + "soomgo/ad3.php"));

        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);

        // 만약에 고수로 최종 등록했던 절차를 밟지 않았다면, invisible
        String expertRegisteredOrNot = sharedPreferences.getString(userSeq+"expertRegistered", "");
        if(!expertRegisteredOrNot.equals("done")){
            btn_change.setVisibility(View.INVISIBLE);
        }

        // 고수로 전환 버튼
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putString(userSeq + "clientOrExpert", "expert");
                editor.commit();
                Log.i(TAG, "sharedPreference에 저장된 값 : " + sharedPreferences.getString(userSeq + "clientOrExpert", "client"));
                requestExpertSeqSaveItSF();
//                bottomNavigationView.setSelectedItemId(R.id.tab2); // 왜 만든건지 모르겠다 2022.05.17.
//                Intent intent = new Intent(getContext(), ChatRoomActivity.class); // 채팅방 작업을 위해 임시로 만듬 2022.05.17.
//                startActivity(intent);
            }
        });


        // 서비스 검색 버튼
        tv_serviceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ServiceSearchActivity.class);
                startActivity(intent);
            }
        });




        sliderViewPager = rootView.findViewById(R.id.sliderViewPager);
        layoutIndicator = rootView.findViewById(R.id.layoutIndicators);

        // viewpager를 사용할 때 이전 혹은 다음페이지를 몇개까지 미리 로딩할지 정하는 함수
        sliderViewPager.setOffscreenPageLimit(1);
//        sliderViewPager.setAdapter(new ImageSliderAdapter(getContext(), images));
        sliderViewPager.setAdapter(new ImageSliderAdapter2(getContext(), imagesArray));
        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //  선택된 페이지 색상 다르게 하기
                setCurrentIndicator(position);
            }
        });


        // 사진의 숫자만큼 동그란 점 만들기 → image를 담고 있는 배열의 길이
        setupIndicators(imagesArray.size());

//        // ----  자동 넘김 핸들러 사용 시작 ---- (참고 블로그 : https://salix97.tistory.com/90)
//        final Handler handler = new Handler();
//        final Runnable Update = new Runnable() {
//            @Override
//            public void run() {
//                if(handlerWork) {
//                    Log.i(TAG, "currentPage = " + currentPage);
//                    Log.i(TAG, "DELAY_MS = " + DELAY_MS);
//                    Log.i(TAG, "DELAY_MS = " + PERIOD_MS);
//
//                    if(currentPage == 4) {
//                        currentPage = 0;
//                    }
//                    sliderViewPager.setCurrentItem(currentPage++, true);
//                }
//            }
//        };
//
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(Update);
//            }
//
//        }, DELAY_MS, PERIOD_MS);
//        // ----  자동 넘김 핸들러 사용 종료 ---- (참고 블로그 : https://salix97.tistory.com/90)

        //추천 고수 목록
        requestExpertList(userSeq);


        tv_showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("test", "ib_test 클릭했습니다");
                fragmentToFragmentData("service", "모든 서비스");
                bottomNavigationView.setSelectedItemId(R.id.tab2);
            }
        });

        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.ib_engish:
                        // 고수찾기 프래그먼트로 이동한다.
                        // onCreate 할 때, 서비스 선택 스피너를 영어과외로 선택한다.
                        Log.i("test", "ib_english를 클릭했습니다");
                        fragmentToFragmentData("service", "영어 과외");
                        break;
                    case R.id.ib_business:
                        Log.i("test", "ib_business 클릭했습니다");
                        fragmentToFragmentData("service", "비즈니스 영어");
                        break;
                    case R.id.ib_phone:
                        Log.i("test", "ib_phone 클릭했습니다");
                        fragmentToFragmentData("service", "화상영어/전화영어 회화");
                        break;
                    case R.id.ib_test:
                        Log.i("test", "ib_test 클릭했습니다");
                        fragmentToFragmentData("service", "TOEIC/speaking/writing 과외");
                        break;

                }
            }
        };

        ib_engish.setOnClickListener(onClickListener);
        ib_business.setOnClickListener(onClickListener);
        ib_phone.setOnClickListener(onClickListener);
        ib_test.setOnClickListener(onClickListener);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        handlerWork = true;

    }

    @Override
    public void onPause() {
        super.onPause();

        handlerWork = false;

    }

    private void fragmentToFragmentData(String key, String data) {
        Bundle bundle = new Bundle();
        bundle.putString(key, data);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction(); // 프래그먼트 관리하는 친구 -> 교체/가져올때/현재 값 확인할 때 사용
        Fragment2 fragment2 = new Fragment2();
        fragment2.setArguments(bundle);
        transaction.replace(R.id.scrollView, fragment2); // 첫번째 인자 : 교체할 화면 영억, 두번째인자 : 무엇으로 바꿀 것인지
        transaction.commit(); // 프래그먼트 상태를 저장
    }


    // 서버에 고수 목록 요청
    public void requestExpertList(String userSeq) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = serverAddress + "expertList.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, tagServerToClient +  response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("data"); //JSONArray로 배열객체를 받아서, 변수에 담는다.
                    Log.i(TAG, tagServerToClient + "jsonArray : " + jsonArray);
                    Log.i(TAG, tagServerToClient + "jsonArray.length() : " + jsonArray.length());

                    // ArrayList를 선언한다.
                    expertDataList = new ArrayList<>();
                    expertListCount = String.valueOf(jsonArray.length());

                    // 전달받은 배열 객체의 길이만큼 반복문을 실행한다
                    // 1. 고수이름, 고수사진, 한줄소개를 Person 객체에 담아서 ArrayList에 추가한다
                    // (필요한 추가작업 - 2022.04.13.) 1) 평점, 2)리뷰수, 3) 고용횟수
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String expertName = object.getString("expertName");
//                        String userProfileImage = object.getString("userProfileImage");
                        String expertProfileImage = object.getString("expertProfileImage");
                        String expertIntro = object.getString("expertIntro");
                        String reviewCount = object.getString("reviewCount");
                        String reviewAverage = object.getString("reviewAverage");
                        String hireCount = object.getString("hireCount");
                        String expertId = object.getString("expertId");
                        String expertMainService = object.getString("expertMainService");


                        expertDataList.add(new ExpertData(expertName, expertIntro, expertProfileImage, reviewCount, reviewAverage, hireCount, expertId, expertMainService));
                    }

                    mAdapter = new ExpertAdapter2(expertDataList, getContext());
                    recyclerView.setAdapter(mAdapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "JSON에서 전달받은 에러" + e);
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
                Log.i(TAG, tagClientToServer + "sort = " + sort);

                return params;
            }

        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    // 사진의 숫자만큼 동그란 점 만들기
    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(16, 8, 16, 8);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getContext(),
                    R.drawable.bg_indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutIndicator.addView(indicators[i]);
        }
        setCurrentIndicator(0);
    }

    //  선택된 페이지 색상 다르게 하기
    private void setCurrentIndicator(int position) {
        int childCount = layoutIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getContext(),
                        R.drawable.bg_indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getContext(),
                        R.drawable.bg_indicator_inactive
                ));
            }
        }
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