package ExpertUpdate;

import static com.example.soomgodev.StaticVariable.serverAddress;
import static com.example.soomgodev.StaticVariable.tagClientToServer;
import static com.example.soomgodev.StaticVariable.tagIntentInput;
import static com.example.soomgodev.StaticVariable.tagServerToClient;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.soomgodev.Fragment.ExpertMainActivity;
import com.example.soomgodev.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ExpertUpdateService extends AppCompatActivity {

    Chip chip_addService;
    ChipGroup chipGroup;
    private static final String TAG = "ExpertUpdateService";
    String expertService, userSeq;
    StringBuilder sb;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String[] expertServiceArray, editedServiceArray;
    Button button;
    String chipUpdate1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_update_service);
        getSupportActionBar().hide();

        sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        userSeq = sharedPreferences.getString("userSeq","");

        chipGroup = findViewById(R.id.chipGroup);
        chip_addService = findViewById(R.id.chip_addService);
        button = findViewById(R.id.button);

        requestServiceListShow();

        // 수정완료 버튼
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 업데이트된 '제공서비스' 값을 서버에 보내서 '제공서비스'값을 갱신한다.
                Log.i(TAG, "btn 클릭했습니다");
                serverChipUpdate(chipUpdate1);
                finish();
            }
        });


        // 서비스 추가 chip을 선택하면, 서비스를 선택하는 activity로 이동한다
        chip_addService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.getChildCount();

                Log.i(TAG, "chipGroup.getChildCount() = " + chipGroup.getChildCount());

                // ChipGroup 내에 있는 모든 칩을 ArrayList에 담는다.
                ArrayList<String> selectedChips = new ArrayList<>();
                for (int i=1; i < chipGroup.getChildCount(); i++) {
//                    String selectedSingChip = ((Chip) chipGroup.getChildAt(i)).getText().toString();
                    String selectedSingChip = ((Chip) chipGroup.getChildAt(i)).getText().toString();
                    Log.i(TAG, "selectedSingChip = " + selectedSingChip);
                    selectedChips.add(selectedSingChip);
                }
                Intent intent = new Intent(ExpertUpdateService.this, ExpertServiceSelect.class);
                intent.putExtra("currentServiceList", selectedChips);
                startActivity(intent);
                Log.i(TAG, tagIntentInput + "currentServiceList = " + selectedChips);

            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void requestServiceListShow() {

        // 서버통신 : 회원 고유값(userSeq)을 보내서, 그에 해당되는 칼럼 정보(userName, userEmail)를 찾아서 배열로 받아온다.
        //           이름과 이메일 텍스트에 세팅해준다.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = serverAddress + "expertServiceList.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, tagServerToClient + response);

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    expertService = jsonResponse.getString("expertService");
                    Log.i(TAG, tagServerToClient + expertService);

                    expertServiceArray = expertService.split("-");

                    for (int i = 0; i< expertServiceArray.length; i++){

                        Chip chip = new Chip(ExpertUpdateService.this);
                        chip.setText(expertServiceArray[i]);
                        chip.setCloseIcon(getApplicationContext().getDrawable(R.drawable.ic_close));
                        chip.setCloseIconVisible(true);
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
                                if(chipGroup.getChildCount() == 2) {
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

                                    // 업데이트된 '제공서비스' 값을 서버에 보내서 '제공서비스'값을 갱신한다.
                                    serverChipUpdate(chipUpdate1);
                                }
                            }
                        });
                    }
                    Log.i(TAG, tagServerToClient + "expertService = " + jsonResponse.getString("expertService"));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Fragment23", "JSON에서 전달받은 에러" + e);
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
                Log.i(TAG, tagClientToServer + "userSeq = " + userSeq);
                params.put("userSeq", userSeq);
                return params;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }


    public void serverChipUpdate (String updatedService) {

        // 서버통신 : 회원 고유값(userSeq)을 보내서, 그에 해당되는 칼럼 정보(userName, userEmail)를 찾아서 배열로 받아온다.
        //           이름과 이메일 텍스트에 세팅해준다.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
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


    // 인텐트 액티비티 전환함수
            public void startActivityC(Class c) {
                Intent intent = new Intent(getApplicationContext(), c);
                startActivity(intent);
                // 화면전환 애니메이션 없애기
                overridePendingTransition(0, 0);
            }
    // 인텐트 화면전환 하는 함수
    // FLAG_ACTIVITY_CLEAR_TOP = 불러올 액티비티 위에 쌓인 액티비티 지운다.
            public void startActivityflag(Class c) {
                Intent intent = new Intent(getApplicationContext(), c);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                // 화면전환 애니메이션 없애기
                overridePendingTransition(0, 0);
            }

    // 문자열 인텐트 전달 함수
            public void startActivityString(Class c, String name , String sendString) {
                Intent intent = new Intent(getApplicationContext(), c);
                intent.putExtra(name, sendString);
                startActivity(intent);
                // 화면전환 애니메이션 없애기
                overridePendingTransition(0, 0);
            }

    // 백스택 지우고 새로 만들어 전달
            public void startActivityNewTask(Class c){
                Intent intent = new Intent(getApplicationContext(), c);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                // 화면전환 애니메이션 없애기
                overridePendingTransition(0, 0);
            }
}