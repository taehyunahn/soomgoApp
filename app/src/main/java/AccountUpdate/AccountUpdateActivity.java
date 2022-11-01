package AccountUpdate;

import static com.example.soomgodev.StaticVariable.serverAddress;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.example.soomgodev.LoginActivity;
import com.example.soomgodev.R;
import com.google.gson.Gson;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import Chat.ChatMember;
import Chat.ChatRoomActivity;
import Chat.ChatService;
import Chat.ViewType;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class AccountUpdateActivity extends AppCompatActivity {

    TextView tv_logout, tv_userName, tv_userEmail, tv_userPassword, tv_userPhoneNumber, tv_signUpCancel;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq, userProfileImage, userEmail, userKakaoID;
    ImageView iv_userPhoto;

    Uri selectedUri;
    Bitmap bitmap;
    String encodeImage;

    private static final int PICK_IMAGES_CODE = 0;

    private static final String TAG = "AccountUpdateActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("AccountUpdateActivity", "onCreate ---------------------");

        sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        userSeq = sharedPreferences.getString("userSeq", "");
        userEmail = sharedPreferences.getString("userEmail", "");
        editor = sharedPreferences.edit(); // 커밋 추가

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

    @Override
    protected void onResume() {
        super.onResume();

            Log.e("AccountUpdateActivity", "onResume ---------------------");


        setContentView(R.layout.activity_account_update);
        getSupportActionBar().hide();

        sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        userSeq = sharedPreferences.getString("userSeq", "");
        userEmail = sharedPreferences.getString("userEmail", "");
        editor = sharedPreferences.edit();

        tv_userName = findViewById(R.id.tv_userName);
        tv_userEmail = findViewById(R.id.tv_userEmail);
        tv_userPassword = findViewById(R.id.tv_userPassword);
        tv_userPhoneNumber = findViewById(R.id.tv_userPhoneNumber);
        iv_userPhoto = findViewById(R.id.iv_userPhoto);
        tv_signUpCancel = findViewById(R.id.tv_signUpCancel);


        tv_signUpCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder ad = new AlertDialog.Builder(AccountUpdateActivity.this); // context부분에 getAplicationContext를 쓰면 안된다! 기억할 것
                ad.setMessage("정말로 계정을 탈퇴하시겠습니까?");
                ad.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(AccountUpdateActivity.this, "계정탈퇴를 취소했습니다.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                ad.setNegativeButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // 계정탈퇴를 위한 서버 통신
                        resignAccountInfo(userSeq);
                        dialog.dismiss();
                    }
                });

                ad.show();
            }
        });


        // 계정설정 첫화면 정보를 서버에서 가져오는 메서드
        getAccountInfo(userSeq);


        // 프로필 이미지 선택,
        iv_userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityString(AccountUpdateImage.class, "userProfileImageIntent", userProfileImage);

            }
        });

        ViewGroup layout_name = (ViewGroup) findViewById(R.id.layout_name);
        ViewGroup layout_password = (ViewGroup) findViewById(R.id.layout_password);
        ViewGroup layout_email = (ViewGroup) findViewById(R.id.layout_email);
        ViewGroup layout_phoneNumber = (ViewGroup) findViewById(R.id.layout_phoneNumber);

        // '이름' 수정 액티비티로 이동
        layout_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    startActivityString(AccountUpdateName.class, "userName", tv_userName.getText().toString());
            }
        });


        // '이메일' 수정 액티비티로 이동
        layout_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!userKakaoID.contains("1")) {
                    startActivityString(AccountUpdateEmail.class, "userEmail", tv_userEmail.getText().toString());
                    Log.e("AccountUpdateActivity","userKakaoID가 null인 경우 :" + userKakaoID);
                } else if (userKakaoID.contains("1")) {
                    Toast.makeText(getApplicationContext(), "SNS로 로그인한 경우, 이메일 변경이 불가합니다.", Toast.LENGTH_SHORT).show();
                    Log.e("AccountUpdateActivity","userKakaoID가 null이 아닌 :" + userKakaoID);
                }

                Log.e("AccountUpdateActivity",userKakaoID);


            }
        });

        // '비밀번호' 수정 액티비티로 이동
        layout_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userKakaoID.contains("1")) {
                    startActivityC(AccountUpdatePassword.class);
                    Log.e("AccountUpdateActivity","userKakaoID가 null인 경우 :" + userKakaoID);
                } else if (userKakaoID.contains("1")) {
                    Toast.makeText(getApplicationContext(), "SNS로 로그인한 경우, 비밀번호 변경이 불가합니다.", Toast.LENGTH_SHORT).show();
                    Log.e("AccountUpdateActivity","userKakaoID가 null이 아닌 :" + userKakaoID);
                }



            }
        });

        // 로그아웃 -> sharedPreference에 저장된 값 모두 삭제
        tv_logout = (TextView) findViewById(R.id.tv_logout);
        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.remove("userSeq");
                editor.remove("userName");
                editor.remove("userProfileImage");
                editor.remove("userEmail");
                editor.commit();

                if(!sharedPreferences.getString("userKakaoId","").equals(null)) {
                    Log.e("AccountUpdateActivity", "kakoID null체크");
                    //카카오톡으로 로그인 한 상황이라면, 다른 조건으로 처리해야 함
                    UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                        @Override
                        public Unit invoke(Throwable throwable) {
                            

                            return null;
                        }
                    });

//                    UserApiClient.getInstance().unlink(new Function1<Throwable, Unit>() {
//                        @Override
//                        public Unit invoke(Throwable throwable) {
//                            return null;
//                        }
//                    });

                    Log.e("AccountUpdateActivity", "kakoID 로그아웃작업완료");
                }



                new Thread() {
                    public void run() {
                        try {
                            ChatService.chatMember = new ChatMember();
                            ChatService.chatMember.setCommand("EXIT"); // 소켓해제
                            ChatService.chatMember.setOrderType("chat");
                            Gson gson = new Gson();
                            String _chatMemberJson = gson.toJson(ChatService.chatMember);

                            Log.i(TAG, "_chatMemberJson = " + _chatMemberJson);
                            ChatService.sendWriter.println(_chatMemberJson);
                            ChatService.sendWriter.flush();
                        } catch (NullPointerException e) { // 소켓서버에 연결되지 않았을때 앱이 꺼지는 것을 방지 (oos가 null값임)
                            e.printStackTrace();
                        }
                    }
                }.start();


                startActivityNewTask(LoginActivity.class);




            }
        });

        tv_userPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AccountUpdatePhoneNumber.class);
                startActivity(intent);
            }
        });



    }

    private void askPermission() { // 1  권한을 먼저 물어본다.
        // 여기서도 권한을 물어보지만 manifest 에서 저장장치 사용 permission을 미리 넣어두지 않으면 작동안함

        Log.e("AccountUpdateActivity", "askPermission() 실행");

        //Dexter라는 라이브러리를 사용하였음
        Dexter.withContext(AccountUpdateActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE) // 저장장치 사용허가
                .withListener(new PermissionListener() {

                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Log.e("AccountUpdateActivity", "chooseImage() 실행");
                        chooseImage(); // 권한을 허락한경우 작동하는 이미지 불러오기 class (아래참조)
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        // 권한을 거절하는경우
                        Toast.makeText(getApplicationContext(), "permission required", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                }).check();


    }

    private void chooseImage() { // 갤러리에서 이미지를 가져오는 클래스
        Log.e("AccountUpdateActivity", "chooseImage() 실행");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, 1); // onActivityResult로 받음
        Log.e("AccountUpdateActivity", "chooseImage()의 startActivityForResult 실행 직전");
        startActivityForResult(Intent.createChooser(intent, "Select Image(s)"), PICK_IMAGES_CODE);
        Log.e("AccountUpdateActivity", "chooseImage()의 startActivityForResult 실행 직후, 이미지 선택 전");

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("AccountUpdateActivity", "chooseImage()의 onActivityResult 실행, 이미지 선택 후");
        if ( data != null ) {
            if (requestCode == 1 || requestCode == RESULT_OK || data.getData() != null) {

                selectedUri = data.getData();

                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedUri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    Log.e("AccountUpdateActivity", "chooseImage에서 선택한 사진의 bitmap : " + String.valueOf(bitmap));
                    // 이미지를 비트맵으로 가져오고
//                    iv_userPhoto.setImageBitmap(bitmap);
                    imageStore(bitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    private void imageStore(Bitmap bitmap) {
        Log.e("AccountUpdateActivity", "imageStore() 실행");

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        byte[] imagebyte = stream.toByteArray();
        // 이미지를 스트링화시켜서 위에서 만들어둔 encodeImage 변수에 담아준다.   위의 StringRequest 맵핑하는 부분으로 돌아간다.
        encodeImage = android.util.Base64.encodeToString(imagebyte, Base64.DEFAULT);




        // 선택한 이미지(프로필 사진)을 DB에 저장하는 서버 통신
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://54.180.133.35/updateProfileImage.php";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("AccountUpdateActivity", "imageStore() 구동 후 받은 response : " + response);
                editor.putString("userProfileImage", response);
                editor.commit();
                Log.e("AccountUpdateActivity", "imageStore() 실행을 통해 userProfileImage에 담겨진 값 : " + response);
                Log.e("AccountUpdateActivity", "위 값은 userProfileImage 변수에 넣는다(sharedPreference) : " + sharedPreferences.getString("userProfileImage",""));


                //                Glide.with(getApplicationContext()).load("http://54.180.133.35/image/" + response).circleCrop().into(iv_userPhoto);

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
                params.put("encodeImage", encodeImage);
                params.put("userSeq", userSeq);

                return params;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);




        Log.e("AccountUpdateActivity", "imageStore() 종료");

    }


    // 사용자가 카카오톡 로그인이 되었있는지를 확인
    private void updatedKakaoLoginUi () {
        // 로그인 여부 확인
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {

            // invoke라는 메서드를 콜백으로 호출
            @Override
            public Unit invoke(User user, Throwable throwable) {
                // 로그인이 되어있다면,
                if (user != null) {


//                    name.setText(user.getKakaoAccount().getEmail());
//                    Glide.with(profileImage).load(user.getKakaoAccount().getProfile().getThumbnailImageUrl()).circleCrop().into(profileImage);

                    //프로필 이미지의 URL
//                    user.getKakaoAccount().getProfile().getThumbnailImageUrl();
                    // 로그인이 되어있지 않다면,
                } else {
//                    name.setText(null);
//                    profileImage.setImageURI(null);
                }
                return null;
            }
        });

    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.e("AccountUpdateActivity", "onStop ---------------------");


    }

    private void getAccountInfo(String userSeq) {
        // 서버통신 : 회원 고유값(userSeq)을 보내서, 그에 해당되는 칼럼 정보(userName, userEmail)를 찾아서 배열로 받아온다.
        //           이름과 이메일 텍스트에 세팅해준다.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url =  serverAddress + "accountUpdate.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "response = " + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    String userPhoneNumber = jsonResponse.getString("phoneNumber");
                    if(userPhoneNumber.equals("0")){
                        userPhoneNumber = "전화번호를 입력하세요";
                    }

                    userKakaoID = jsonResponse.getString("userKakaoID");

                    // ---- 이름, 이메일, 휴대폰번호 설정
                    tv_userName.setText(jsonResponse.getString("userName"));
                    tv_userEmail.setText(jsonResponse.getString("userEmail"));
                    userProfileImage = jsonResponse.getString("userProfileImage");
                    tv_userPhoneNumber.setText(userPhoneNumber);
                    Log.i(TAG, "userProfileImage = "+ userProfileImage);

                    if(userProfileImage != null && !userProfileImage.equals("0")){
                        Glide.with(getApplicationContext()).load(userProfileImage).circleCrop().into(iv_userPhoto);
                    }


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

                return params;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private void resignAccountInfo(String userSeq) {
        // 서버통신 : 회원 고유값(userSeq)을 보내서, 그에 해당되는 칼럼 정보(userName, userEmail)를 찾아서 배열로 받아온다.
        //           이름과 이메일 텍스트에 세팅해준다.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = serverAddress + "resignAccountInfo.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "response = " + response);

                editor.remove("userSeq");
                editor.remove("userName");
                editor.remove("userProfileImage");
                editor.remove("userEmail");
                editor.commit();
                Toast.makeText(AccountUpdateActivity.this, "계정탈퇴를 완료했습니다.", Toast.LENGTH_SHORT).show();
                startActivityNewTask(LoginActivity.class);


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

                return params;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

}