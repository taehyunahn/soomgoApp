package com.example.soomgodev.Fragment;

import static com.example.soomgodev.StaticVariable.tagClientToServer;
import static com.example.soomgodev.StaticVariable.tagServerToClient;
import static com.example.soomgodev.StaticVariable.tagSharedPreferenceInput;
import static com.example.soomgodev.StaticVariable.tagSharedPreferenceOutput;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
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
import com.example.soomgodev.ExpertData;
import com.example.soomgodev.ExpertAdapter;
import com.example.soomgodev.R;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ConnectToServer.DataClass;
import ConnectToServer.NetworkClient;
import ConnectToServer.UploadApis;
import ExpertUpdate.MultiImageAdapter;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;

public class Fragment2 extends Fragment {

    private static final String TAG = "Fragment2";

    // sharedPreference 기본설정
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq, expertSeq;

    // 레트로핏 통신 공통
    NetworkClient networkClient;
    private UploadApis uploadApis;

    // 지역 출력할 때 사용하는 ArrayAdapter
    private ArrayAdapter<String> arrayAdapter;
    Spinner spinner_si, spinner_gu;

    // 기타 UI
    String sort;
    Button btn_address, btn_service;
    TextView tv_expertCount, tv_array;

    // 고수 목록 출력을 위한 RecyclerView 세팅
    ExpertAdapter adapter;
    List<ExpertData> expertDataList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    // 페이징 처리
    NestedScrollView nestedScrollView;
    ProgressBar progressBar;
    // 1페이지에 10개씩 데이터를 불러온다
    int page = 1;
    int limit = 10;

    Handler handler = new Handler(); //핸들러 객체를 만든다.
    int cnt = 0;

    String service, address;

    BottomNavigationView bottomNavigationView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView 시작");

        // 고수 찾기 화면
                ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_2, container, false);

        // (프래그먼트에서) sharedPreference 기본설정
        sharedPreferences = this.getActivity().getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userSeq = sharedPreferences.getString("userSeq","");
        expertSeq = sharedPreferences.getString("expertSeq", "");
        service = sharedPreferences.getString(userSeq+"searchService", "");
        address = sharedPreferences.getString(userSeq+"searchAddress", "");

        // 코드와 XML 파일 연결
        btn_address = rootView.findViewById(R.id.btn_address);
        btn_service = rootView.findViewById(R.id.btn_service);
        tv_expertCount = rootView.findViewById(R.id.tv_expertCount);
        tv_array = rootView.findViewById(R.id.tv_array);

        // 리사이클러뷰와 페이징처리
        nestedScrollView = rootView.findViewById(R.id.scroll_view);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        progressBar = rootView.findViewById(R.id.progress_bar);

        //리사이클러뷰 초기 세팅
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // 하단 바
        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);


        // -- 초기 스피너 세팅 시작
        // 1. 지역 선택 버튼의 글씨 설정 초기값 -> 지역선택
        btn_address.setText(sharedPreferences.getString(userSeq+"searchAddress", "지역 선택"));
        // 2. 서비스 선택 버튼의 글씨 설정 초기값 -> 서비스 선택
        //   2-1. 홈 프래그먼트에서 서비스를 선택해서 이곳에 오면, 거기서 intent로 가져온 값을 지정한다. 2-2는 건너뛴다.


        Log.i(TAG, "프래그먼트1에서 전달받은 값이 있는지 확인 시작");
        if (getArguments() != null) { // null : 빈 값
            Log.i(TAG, "프래그먼트1에서 전달받은 값이 있다");
            String result = getArguments().getString("service"); // 프래그먼트1에서 받아온 값
            editor.putString(userSeq+"searchService", result);
            editor.commit();

            Log.i(TAG, "SearviceSearchActivity → UserMainActivty → Fragment2에서 받은 service 이름 = " + result);

            Log.i(TAG, "프래그먼트1에서 전달받은 값은 = " + result);

            bottomNavigationView.setSelectedItemId(R.id.tab2);
            Log.i(TAG, "bottomNavigationView 클릭 변경 요청");


            Log.e(TAG, "btn_service.setText(result)하는 시점에서 result = " + result);
            btn_service.setText(result);
            Log.i(TAG, "프래그먼트1에서 프래그먼트2로 전달한 service값" + result);

        } else {
            Log.i(TAG, "프래그먼트1에서 전달받은 값이 없다");
            btn_service.setText(sharedPreferences.getString(userSeq+"searchService", "서비스 선택"));

            Log.e(TAG, "btn_service.setText(result)하는 시점에서 sharedPreferences.getString(userSeq+\"searchService\", \"서비스 선택\") = " + sharedPreferences.getString(userSeq+"searchService", "서비스 선택"));
        }

        // if(프래그먼트1에서 도착 여부 true) { 프래그먼트에서 보낸 값이 null이 아니라면,
        // btn_service.setText(프래그먼트1에서 선택한 서비스 이름);
        // } else {
        ////   2-2. 다른 화면에서 이곳에 오면, 이전에 선택한 서비스 값을 sharedPreference에서 저장하여 가져온다.
        // btn_service.setText(sharedPreferences.getString(userSeq+"searchService", "서비스 선택"));

        //   2-2. 다른 화면에서 이곳에 오면, 이전에 선택한 서비스 값을 sharedPreference에서 저장하여 가져온다.
//        btn_service.setText(sharedPreferences.getString(userSeq+"searchService", "서비스 선택"));
        // -- 스피너 세팅 종료


        // 주소 선택 버튼
        btn_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 주소 선택하는 이중 스피너 사용 메소드
                addressDial();
            }
        });

        // 정렬 기준 선택
        tv_array.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialArray();  // -> 현재 동작 안함 (2022.04.30.기준)

            }
        });

        // 서비스 선택 버튼
        btn_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 서비스 선택 목록
                final CharSequence[] items = { "모든 서비스", "영어 과외", "비즈니스 영어", "화상영어/전화영어 회화", "TOEIC/speaking/writing 과외" };
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getContext());
                // 제목셋팅
                alertDialogBuilder.setTitle("서비스 선택");
                alertDialogBuilder.setItems(items,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int id) {
                            // id는 서비스 선택 목록에서 내가 클릭한 값의 위치 (0, 1, 2 ...)를 의미한다
                            String searchService = String.valueOf(items[id]);
                            Log.i(TAG, tagSharedPreferenceInput + "searchService = " + searchService);
                            // 선택한 서비스의 위치에 해당되는 배열 값으로 버튼 text를 세팅한다.
                            btn_service.setText(items[id]);
                            // 로그인 회원고유값(userSeq)와 searchService라는 이름을 key로 넣고, 해당 값을 s.f에 저장한다.
                            // 나중에 화면 나가고 돌아와도, 고수 찾기 기준(지역 또는 서비스)를 동일하게 유지하기 위함
                            editor.putString(userSeq + "searchService", searchService);
                            editor.commit();

                            service = sharedPreferences.getString(userSeq + "searchService", "모든 서비스");
                            Log.i(TAG, tagSharedPreferenceOutput + "searchService = " + sharedPreferences.getString(userSeq + "searchService", ""));
                            dialog.dismiss();
                            // 서버에 고수 목록 요청하는 메소드
                            // 서비스 선택이 변경될 때마다, 다른 고수 목록을 출력하기 위해 해당 메소드 사용

//                            List<ExpertData> expertDataList1 = new ArrayList<>();   // <--- List<ExpertData>를 초기화한 것이 문제였다 !!



                            // 리사이클러뷰 초기화
                            expertDataList.removeAll(expertDataList);
                            adapter = new ExpertAdapter(expertDataList, getContext());
                            recyclerView.setAdapter(adapter);

                            page = 1;

                            Log.i(TAG, "showExpertListWithRegrofit - page = " + page);
                            Log.i(TAG, "showExpertListWithRegrofit - service = " + service);
                            Log.i(TAG, "showExpertListWithRegrofit - address = " + address);
                            showExpertListWithRetrofit(userSeq, Integer.toString(page), Integer.toString(limit), service, address);
                        }
                    });
                // 다이얼로그 생성
                AlertDialog alertDialog = alertDialogBuilder.create();
                // 다이얼로그 보여주기
                alertDialog.show();
            }
        });


//        sort = sharedPreferences.getString(userSeq + "sort", ""); //나중에 작업 예정

        mAdapter = new ExpertAdapter(expertDataList, getContext());
        recyclerView.setAdapter(mAdapter);

        // 서버에 고수 목록 요청하는 메소드
//        requestExpertList(Integer.toString(page), Integer.toString(limit));

        // 리사이클러뷰 초기화
        expertDataList.removeAll(expertDataList);
        adapter = new ExpertAdapter(expertDataList, getContext());
        recyclerView.setAdapter(adapter);

        showExpertListWithRetrofit(userSeq, Integer.toString(page), Integer.toString(limit), service, address);


        // nestedScrollView 동작 부분
        Log.i(TAG, "nestedScrollView 들어가기 전");
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;

//                    progressBar.setVisibility(View.VISIBLE);
//                    BackgroundThread thread = new BackgroundThread();
//                    thread.start();

                    showExpertListWithRetrofit(userSeq, Integer.toString(page), Integer.toString(limit), service, address);

                    Log.i(TAG, "onScrollChange page = " + page);
                }
            }
        });
        Log.i(TAG, "nestedScrollView 들어간 후");

        return rootView;
    }

    // 지역 선택 버튼 - 다이얼
    public void addressDial() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext()); //다이얼로그를 생성한다.
        View mView = getLayoutInflater().inflate(R.layout.dialog_spinner, null); //다이얼로그 표현을 위한 커스텀 뷰를 생성해서 연결한다.
        mBuilder.setTitle("지역을 선택하세요"); //다이얼로그의 제목

        //스피너를 생성하는데, 주의할 건, 위에 만든 커스텀 뷰 안에 있는 스피너를 연결해야 함!
        spinner_si = (Spinner) mView.findViewById(R.id.spinner_si);
        arrayAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,
                (String[])getResources().getStringArray(R.array.spinner_region));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_si.setAdapter(arrayAdapter);

        spinner_gu = (Spinner) mView.findViewById(R.id.spinner_gu);

        initAddressSpinner();

        mBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String si = (String) spinner_si.getSelectedItem().toString();
                if(spinner_gu.getSelectedItem() != null) {
                    Log.i(TAG, "gu = null이 아닙니다");
                    String gu = (String) spinner_gu.getSelectedItem().toString();
                    String searchAddress = si + " " + gu;
                    btn_address.setText(searchAddress);
                    editor.putString(userSeq + "searchAddress", searchAddress);
                    editor.commit();
                } else {
                    Log.i(TAG, "gu = null입니다.");
                    String searchAddress = si;
                    btn_address.setText(searchAddress);
                    editor.putString(userSeq + "searchAddress", searchAddress);
                    editor.commit();
                }
                showExpertListWithRetrofit(userSeq, Integer.toString(page), Integer.toString(limit), service, address);
            }
        });
        mBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void initAddressSpinner() {
        spinner_si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 시군구, 동의 스피너를 초기화한다.
                switch (position) {
                    case 0:
                        spinner_gu.setAdapter(null);
                        break;
                    case 1:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_seoul);
                        break;
                    case 2:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_busan);
                        break;
                    case 3:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_daegu);
                        break;
                    case 4:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_incheon);
                        break;
                    case 5:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_gwangju);
                        break;
                    case 6:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_daejeon);
                        break;
                    case 7:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_ulsan);
                        break;
                    case 8:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_sejong);
                        break;
                    case 9:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_gyeonggi);
                        break;
                    case 10:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_gangwon);
                        break;
                    case 11:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_chung_buk);
                        break;
                    case 12:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_chung_nam);

                        break;
                    case 13:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_jeon_buk);
                        break;
                    case 14:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_jeon_nam);
                        break;
                    case 15:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_gyeong_buk);
                        break;
                    case 16:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_gyeong_nam);
                        break;
                    case 17:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_jeju);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setSigunguSpinnerAdapterItem(int array_resource) {
        if (arrayAdapter != null) {
            spinner_gu.setAdapter(null);
            arrayAdapter = null;
        }
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, (String[])getResources().getStringArray(array_resource));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_gu.setAdapter(arrayAdapter);
    }

    // 정렬 기준 선택 다이얼 -> 현재 동작 안함 (2022.04.30.기준)
    public void dialArray() {

        // 서비스 선택 목록
        final CharSequence[] items = { "평점순", "리뷰 많은순", "고용 횟수순"};
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getContext());
        // 제목셋팅
        alertDialogBuilder.setTitle("정렬 기준 선택");
        alertDialogBuilder.setItems(items,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        // id는 서비스 선택 목록에서 내가 클릭한 값의 위치 (0, 1, 2 ...)를 의미한다
                        sort = String.valueOf(items[id]);
                        Log.i(TAG, tagSharedPreferenceInput + "sort = " + sort);
                        // 선택한 서비스의 위치에 해당되는 배열 값으로 버튼 text를 세팅한다.
                        tv_array.setText(items[id]);
                        // 로그인 회원고유값(userSeq)와 searchService라는 이름을 key로 넣고, 해당 값을 s.f에 저장한다.
                        // 나중에 화면 나가고 돌아와도, 고수 찾기 기준(지역 또는 서비스)를 동일하게 유지하기 위함
                        editor.putString(userSeq + "sort", sort);
                        editor.commit();
                        Log.i(TAG, tagSharedPreferenceOutput + "searchService = " + sharedPreferences.getString(userSeq + "searchService", ""));
                        dialog.dismiss();
                        // 서버에 고수 목록 요청하는 메소드
                        // 서비스 선택이 변경될 때마다, 다른 고수 목록을 출력하기 위해 해당 메소드 사용

                        if(id == 0) {
                            Toast.makeText(getContext(), "평점순 선택", Toast.LENGTH_SHORT).show();
                        } else if (id == 1) {
                            Toast.makeText(getContext(), "리뷰많은순 선택", Toast.LENGTH_SHORT).show();
                        } else if (id == 2) {
                            Toast.makeText(getContext(), "고용많은순 선택", Toast.LENGTH_SHORT).show();
                        }

                        showExpertListWithRetrofit(userSeq, Integer.toString(page), Integer.toString(limit), service, address);

                    }
                });

        // 다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();
        // 다이얼로그 보여주기
        alertDialog.show();

    }


    // 프래그먼트에서 사용
    // (설명) 레트로핏을 사용하여, 서버 DB에 있는 고수 사진 정보를 받아와서 출력한다.
    // (동작) 고수고유값, 회원고유값을 보낸다. DB의 photo 테이블에서 고수고유값에 해당하는 모든 이미지주소를 가져온다.
    private void showExpertListWithRetrofit(String userSeq, String page, String limit, String service, String address){
        // 서버통신을 위한 세팅
        uploadApis = networkClient.getRetrofit().create(UploadApis.class);
//        @POST("expertListUpdated.php")
        Call<List<ExpertData>> call = uploadApis.showExpertList(userSeq, page, limit, service, address);
        call.enqueue(new Callback<List<ExpertData>>() {
            @Override
            public void onResponse(Call<List<ExpertData>> call, retrofit2.Response<List<ExpertData>> response) {
                if(response.isSuccessful() && response.body() != null){

                    progressBar.setVisibility(View.GONE);
                    Log.i(TAG, "response = " + String.valueOf(response));
                    Log.i(TAG, "response.body = " + String.valueOf(response.body()));

                    // 새롭게 시도 - 2022.05.05.(목) 06:42
                    List<ExpertData> responseBody = response.body();
                    for(int i = 0; i < responseBody.size(); i++) {
                        expertDataList.add(responseBody.get(i));
                    }
                    if(responseBody.size() > 0){
                        tv_expertCount.setText(responseBody.get(0).getCount()  + "명의 고수");
                    }
                    Log.i(TAG, responseBody.toString());
                    adapter = new ExpertAdapter(expertDataList, getContext());
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<List<ExpertData>> call, Throwable t) {
                Log.i(TAG, "t = " + t);
            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();

        page = 1;

    }



//    // 스레드를 상속한 클래스
//    class BackgroundThread extends Thread {
//        int value = 0;
//        public void run() {
//            for (int i = 0; i < 100; i++) {
//                try {
//                    Thread.sleep(1000);
//                } catch(Exception e) {}
//
//                value += 1;
//                Log.d("MyThread", "value : " + value);
//
//                handler.post(new Runnable() { //post함수 사용
//                    @Override
//                    public void run() {
//                        progressBar.setVisibility(View.GONE);
//                    }
//                });
////
////                Message message = handler.obtainMessage();
////                Bundle bundle = new Bundle();
////                bundle.putInt("value", value);
////                message.setData(bundle);
////
////                handler.sendMessage(message); // 핸들러를 통과하게 해준다.
////                textView.setText("값 : " + value);
//                // 쓰레드에서 핸들러 쪽으로 보내고, 핸들러가 순서대로 실행한다. (비정상 종료 문제 해결)
//            }
//        }
//    }




}