package AccountUpdate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.soomgodev.Fragment.Fragment23;
import com.example.soomgodev.R;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AccountUpdateImage extends AppCompatActivity implements Fragment23.OnMyListener {

    ImageView iv_userPhoto;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq, userProfileImage, userEmail, userProfileImageIntent;
    Uri selectedUri;
    Bitmap bitmap;
    String encodeImage;
    Button button;
    private static final int PICK_IMAGES_CODE = 0;
    private static final String TAG = "UpdateProfileImage";
    String mTest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_update_image);
        getSupportActionBar().hide();

        Log.i(TAG, "onCreate 실행");

        sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        userSeq = sharedPreferences.getString("userSeq", "");
        editor = sharedPreferences.edit();


        button = findViewById(R.id.button);
        iv_userPhoto = findViewById(R.id.iv_userPhoto);

        Intent intent = getIntent();
        //계정설정 화면에서 인텐트로 전달받은 이미지파일 주소
        userProfileImageIntent = intent.getStringExtra("userProfileImageIntent");


        if(userProfileImageIntent != null){
            Glide.with(getApplicationContext()).load(userProfileImageIntent).circleCrop().into(iv_userPhoto);
        }


//        profileImageShow(userProfileImageIntent, iv_userPhoto);


        //수정 완료 버튼
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap != null) {
                    imageStore(bitmap);

                } else {
                    Log.i(TAG, "사진을 선택하지 않았습니다");
                    finish();

                }

            }
        });

        // 프로필 이미지 선택,
        iv_userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        //askPermission();  //사진을 갤러리에서 가져오기 버튼을 클릭시 작동 (아래 class 참조)
                //ImagePicker 라이브러리 사용 -> 카메라 촬영 또는 앨범 선택
                ImagePicker.with(AccountUpdateImage.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
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
                    iv_userPhoto.setImageBitmap(bitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("test", "onResume 실행");


    }

    //ImagePicker 라이브러리를 통해 선택한 이미지 파일을 갖고 아래 메소드 실행
    // 1. Bitmap을 String 값으로 encode 한다.
    // 2. encode한 String 값을 서버로 보낸다.
    // 3. 서버에서 encode된 이미지 파일을 jpeg 파일로 변환하고, DB에 이미지 주소값을 저장한다.
    // 4. DB에 저장된 이미지 주소값(String)을 클라이언트에서 전달 받는다.

    private void imageStore(Bitmap bitmap) {
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
                userProfileImage = response;

                editor.putString(userSeq + "userImage", userProfileImage);
                editor.commit();

                finish();

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

    private void profileImageShow(String userProfileImage, ImageView iv_userPhoto) {
        // ----- 프로필 설정 (case 1~3)
        // case 1. 카카오아이디 최초 로그인 경우, https: 포함 여부 -> glide 사용 (단순 사용)
            if (userProfileImage.contains("https:")) {
            Log.e("AccountUpdateActivity", "프로필 설정 case 1 (https:)");
            Glide.with(getApplicationContext()).load(userProfileImage).into(iv_userPhoto);

            // case 2. 앱 내부에서 사진 변경하여 서버에 jpeg 형식으로 저장한 경우, https: 포함 여부 -> glide 사용 (서버 IP 주소 포함)
        } else if (userProfileImage.contains("jpeg")) {
            Log.e("AccountUpdateActivity", "프로필 설정 case 2 (jpeg)");
            Glide.with(getApplicationContext()).load("http://54.180.133.35/image/" + userProfileImage).into(iv_userPhoto);

            // case 3. 이메일 로그인 했으며, 최초 등록한 이미지가 없는 경우(sharedPreference null), 아무것도 작동하지 않도록 함 - 기본 이미지
        } else if (userProfileImage.equals("1")) {
            Log.e("AccountUpdateActivity", "프로필 설정 case 1 (sharedPreference null)");
        }
    }

    @Override
    public void onReceivedData(Object data) {

    }
}