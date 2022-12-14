package ExpertUpdate;

import static com.example.soomgodev.StaticVariable.serverAddress;
import static com.example.soomgodev.StaticVariable.tagClientToServer;
import static com.example.soomgodev.StaticVariable.tagServerToClient;
import static com.example.soomgodev.StaticVariable.tagSharedPreferenceOutput;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import com.example.soomgodev.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ExertUpdateMainService extends AppCompatActivity {

    ChipGroup chipGroup;
    Chip chip_addService;
    private static final String TAG = "ExertUpdateMainService";
    String expertService, userSeq;
    StringBuilder sb;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String[] expertServiceArray;
    int test = 0;
    String selected;
    String clickedService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exert_update_main_service);
        getSupportActionBar().hide();

        sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userSeq = sharedPreferences.getString("userSeq","");

        Button btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "?????? ???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                editor.putString(userSeq + "mainService", clickedService);
                editor.commit();
                requestMainServiceUpdate(clickedService);

            }
        });

        requestAllServiceInfo();


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void requestAllServiceInfo() {
        // ???????????? : ?????? ?????????(userSeq)??? ?????????, ?????? ???????????? ?????? ??????(userName, userEmail)??? ????????? ????????? ????????????.
        //           ????????? ????????? ???????????? ???????????????.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = serverAddress + "expertServiceList.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, tagServerToClient + response);

                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    // ???????????? ???????????? ????????? ????????? ????????? ?????????.
                    expertService = jsonResponse.getString("expertService");
                    Log.i(TAG, tagServerToClient + expertService);

                    // ?????? ????????? ????????? - ???????????? ????????? ????????? ?????????.
                    expertServiceArray = expertService.split("-");
                    chipGroup = findViewById(R.id.chipGroup);

                    //ChipGroup ????????? ????????? Chip??? ??????????????? ?????????
                   chipGroup.setSingleSelection(true);

                    // ?????? ????????? ????????? ????????? Chip?????? Create ??????.
                    for (int i = 0; i< expertServiceArray.length; i++){
                        Chip chip = new Chip(ExertUpdateMainService.this);
                        chip.setText(expertServiceArray[i]);
//                        chip.setCloseIconVisible(true);
                        chip.setCheckable(true);
                        chip.setTextAppearanceResource(R.style.ChipTextStyle);
                        chip.setChipBackgroundColorResource(R.color.cardview_dark_background);
                        chipGroup.addView(chip);

                        // Chip ?????? ???,
                        chip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clickedService = (String) chip.getText();
                                Log.i(TAG, "clickedService = " + clickedService);
                                chip.isChecked();
                            }
                        });

                    }
                    Log.i(TAG, tagServerToClient + "expertService = " + jsonResponse.getString("expertService"));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Fragment23", "JSON?????? ???????????? ??????" + e);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // textView.setText("That didn't work!");
            }
        }) {
            // ????????? ???????????? ??????
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


    private void requestMainServiceUpdate (String updatedService) {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = serverAddress + "expertUpdateMainService.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, tagServerToClient + response);

                // 3)update??? '?????? ?????????' ?????? ????????????????????? ????????? -> expertService??? ????????????.
                expertService = response;
                finish();


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // textView.setText("That didn't work!");
            }
        }) {
            // ????????? ???????????? ??????
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                // 1)Chip Close??? ?????????, '?????? ?????????' String ?????? ????????? ?????????.
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
}