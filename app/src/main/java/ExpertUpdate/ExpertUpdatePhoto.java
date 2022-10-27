package ExpertUpdate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soomgodev.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import ConnectToServer.DataClass;
import ConnectToServer.NetworkClient;
import ConnectToServer.UploadApis;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExpertUpdatePhoto extends AppCompatActivity {

    // 레트로핏 통신 공통
    NetworkClient networkClient;
    UploadApis uploadApis;

    private static Retrofit retrofit;
    private static String BASE_URL = "http://54.180.133.35/";

    // sharedPreference 공통
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userSeq, expertSeq;

    private static final String TAG = "ExpertUpdatePhoto";
    private static final String IMAGE_DIRECTORY = "/Pictures";
    TextView tv_edit;
    Button button,btn_addPhoto, btn_update;
    Uri imageUri;
    Boolean cameFromAlbum;

    private ArrayList<File> fileArrayList1 = new ArrayList<>();
    private ArrayList<File> fileArrayList2 = new ArrayList<>();
    private ArrayList<String> photoPathList = new ArrayList<>();
    private ArrayList<Uri> uriArrayList2 = new ArrayList<>(); //이미지의 uri를 담을 ArrayList 객체

    private ArrayList<Uri> uriArrayList1 = new ArrayList<>(); //이미지의 uri를 담을 ArrayList 객체
    private RecyclerView recyclerView;
    MultiImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_update_photo);
        getSupportActionBar().hide();

        //앨범 조회권한
        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

        // UI 연결부
        button = findViewById(R.id.button);
        btn_addPhoto = findViewById(R.id.btn_addPhoto);
        btn_update = findViewById(R.id.btn_update);
        tv_edit = findViewById(R.id.tv_edit);
        recyclerView = findViewById(R.id.recyclerView);

        // sharedPreference 공통
        sharedPreferences = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
        userSeq = sharedPreferences.getString("userSeq", "");
        expertSeq= sharedPreferences.getString("expertSeq", "");
        editor = sharedPreferences.edit();

        Log.i(TAG, "onCreate에서 uriArrayList1 = " + uriArrayList1);

        // 서버에서 사진 목록 가져오기 - (1. 서버에 있는 이미지를 모두 출력해준다.)
        photoListShowWithRetrofit(userSeq, expertSeq);



        // 수정하기 버튼 (서버에 저장하기)
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "수정하기 버튼 누른 직후");

                // 현재 리사이클러뷰에 있는 아이템을 서버로 보낸다
                //   1) 서버에서 가져온 사진은 이미지 경로를 그대로 서버에 다시 보낸다
                //   2) 앨범에서 선택한 사진은 URI로 파일을 생성하여 서버에 보낸다. (두 가지 형태로 보낸다)
                Log.i(TAG, "리사이클러뷰 아이템을 모두 파일로 만들어서 서버로 보내는 단계");
                Log.i(TAG, "adapter.getItemCount() = " + adapter.getItemCount());

                // 현재 리사이클러뷰(MultiImageAdapter 사용)에 있는 모든 아이템 수만큼 반복문을 돌린다.

                for(int i = 0; i < adapter.getItemCount(); i++) {
                    Log.i(TAG, "adapter.getItemUri(i) = " + adapter.getItemUri(i));
                    // Uri를 뽑아낸다.
                    Uri imageUri = adapter.getItemUri(i);
                    String filePath = createCopyAndReturnRealPath(getApplicationContext(), imageUri);
                    // 파일 경로가 null이 아닌 경우? 앨범에서 선택한 경우!
                    if(filePath != null) {
                        Log.i(TAG, "filePath = " + filePath);
                        File file = new File(filePath);
                        Log.i(TAG, "file = " + file);
                        fileArrayList1.add(file); //서버에 저장하는 fileArrayList1
                        Log.i(TAG, "fileArrayList1 = " + fileArrayList1);
                    } else {
                        // 파일 경로가 null인 경우? 서버에서 가져온 경우!
                        photoPathList.add(String.valueOf(imageUri));
                        Log.i(TAG, "서버로 보낼 imageUri = " + imageUri);
                    }
                }

                //현재 리사이클러뷰에 표시된 모든 이미지를 서버로 저장한다.
                //인자 1. 사진 앨범에서 선택한 파일 형태의 이미지
                //인자 2. 서버에서 가져왔던 이미지 링크(서버)
                photoUploadWithRetrofit(fileArrayList1, photoPathList);
            }
        });

        // 사진 편집 버튼
        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 리사이클러뷰의 뷰 타입을 변경한다
                // 1번 클릭하면, 편집 -> 취소로 변경. 그리고 리사이클러뷰 아이템 우측 상단에 x표시가 있는 뷰 타입으로 변경
                // 다시 클릭하면, 취소 -> 편집으로 변경. 리사이클러뷰 아이템 우측 상단에 x표시가 없는 뷰 타입으로 변경
            }
        });

        // 사진등록하기
        btn_addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2222);
            }
        });
    }

    // 앨범에서 액티비티로 돌아온 후 실행되는 메서드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2222){
            if(data == null){   // 어떤 이미지도 선택하지 않은 경우
                Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
                }
                else{// 이미지를 하나라도 선택한 경우
                    ClipData clipData = data.getClipData();
                     // 선택한 이미지가 11장 이상인 경우
                    if(clipData.getItemCount() > 10){
                        Toast.makeText(getApplicationContext(), "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                    }
                    else{
                     // 선택한 이미지가 1장 이상 10장 이하인 경우
                        Log.i(TAG, "선택한 이미지가 1장 이상 10장 이하인 경우");
                        for (int i = 0; i < clipData.getItemCount(); i++){
                            imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                            uriArrayList1.add(imageUri); //서버에서 가져온 이미지 + 앨범에서 선택한 이미지
                            uriArrayList2.add(imageUri); //앨범에서 선택한 이미지만
                            File file = new File(createCopyAndReturnRealPath(getApplicationContext(), imageUri));
                            fileArrayList2.add(file);
                        }

                        Log.i(TAG, "사진 파일을 선택하고 난 직후의 uriArrayList와 fileArrayList");
                        Log.i(TAG, "uriArrayList1" + String.valueOf(uriArrayList1));
                        Log.i(TAG, "uriArrayList2" + String.valueOf(uriArrayList2));
                        Log.i(TAG, "fileArrayList2" + String.valueOf(fileArrayList2));

                        adapter = new MultiImageAdapter(uriArrayList1, getApplicationContext());
                        recyclerView.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
                        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));     // 리사이클러뷰 수평 스크롤 적용
                        adapter.notifyDataSetChanged();
                }
            }
        }
    }

    // (설명) 레트로핏을 사용하여, 서버 DB에 있는 고수 사진 정보를 받아와서 출력한다.
    // (동작) 고수고유값, 회원고유값을 보낸다. DB의 photo 테이블에서 고수고유값에 해당하는 모든 이미지주소를 가져온다.
    private void photoListShowWithRetrofit(String userSeq, String expertSeq){

        uploadApis = networkClient.getRetrofit().create(UploadApis.class);



        Call<List<DataClass>> call = uploadApis.photoListShow(userSeq, expertSeq);
        call.enqueue(new Callback<List<DataClass>>() {
            @Override
            public void onResponse(Call<List<DataClass>> call, retrofit2.Response<List<DataClass>> response) {
                if(response.isSuccessful()){
                    List<DataClass> responseBody = response.body();
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

    // (설명) 레트로핏을 사용하여, 현재 리사이클러뷰에 띄워진 아이템 목록을 서버로 전송한다.
    // (동작) 고수고유값, 회원고유값을 보낸다. 파일을 생성한다. DB의 photo 테이블에 이미지주소를 insert하고, 고수고유값도 함께 저장한다.
    private void photoUploadWithRetrofit(ArrayList<File> fileArrayList, ArrayList<String> photoPathList) {

        Log.i(TAG, "photoUploadWithRetrofit는 시작됨");

        uploadApis = networkClient.getRetrofit().create(UploadApis.class);


        // 1. 회원고유 번호와, 2. 고수 고유번호를 보낸다.
        RequestBody userSeqBody = RequestBody.create(MediaType.parse("text/plain"), userSeq);
        RequestBody expertSeqBody = RequestBody.create(MediaType.parse("text/plain"), expertSeq);

        // 3. 서버에서 받았던 서버 이미지 경로를 다시 보낸다 + 이미지 경로 총 개수도 보낸다.
        HashMap<String, RequestBody> requestMap = new HashMap<>();
        requestMap.put("fileCount", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(fileArrayList.size())));
        Log.i(TAG, "photoUploadWithRetrofit에서 fileArrayList.size() = "+ fileArrayList.size());

        // 이미지 파일 경로 수만큼 만든다.
        for (int i = 0; i < photoPathList.size(); i++) {
            requestMap.put("photoPath"+i, RequestBody.create(MediaType.parse("text/plain"), photoPathList.get(i)));
        }
        requestMap.put("photoPathCount", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(photoPathList.size())));

        //---------------------------

        // 사진앨범에서 선택한 이미지 파일을 전송한다.
        ArrayList<MultipartBody.Part> files = new ArrayList<>();
        for (int i = 0; i < fileArrayList.size(); i++) {
            RequestBody fileRequestBody = RequestBody.create(MediaType.parse("image/jpg"), fileArrayList.get(i));
            MultipartBody.Part fileMultiPartBody = MultipartBody.Part.createFormData("imageName"+i, "("+i+")"+".jpg", fileRequestBody);
            files.add(fileMultiPartBody);
        }

        //---------------------------


        Call<DataClass> call = uploadApis.uploadImage(userSeqBody, expertSeqBody, requestMap, files);
        call.enqueue(new Callback<DataClass>() {
            @Override
            public void onResponse(Call<DataClass> call, retrofit2.Response<DataClass> response) {
                Log.i(TAG, "onResponse = " + response);
                finish();
            }

            @Override
            public void onFailure(Call<DataClass> call, Throwable t) {
                Log.i(TAG, "t = " + t);
            }
        });
    }


    // 절대경로 파악할 때 사용된 메소드
    @Nullable
    public static String createCopyAndReturnRealPath(@NonNull Context context, @NonNull Uri uri) {
        final ContentResolver contentResolver = context.getContentResolver();

        if (contentResolver == null)
            return null;

        // 파일 경로를 만듬
        String filePath = context.getApplicationInfo().dataDir + File.separator
                + System.currentTimeMillis();

        File file = new File(filePath);
        try {
            // 매개변수로 받은 uri 를 통해  이미지에 필요한 데이터를 불러 들인다.
            InputStream inputStream = contentResolver.openInputStream(uri);
            if (inputStream == null)
                return null;
            // 이미지 데이터를 다시 내보내면서 file 객체에  만들었던 경로를 이용한다.

            OutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0)
                outputStream.write(buf, 0, len);
            outputStream.close();
            inputStream.close();

        } catch (IOException ignore) {
            return null;
        }

        return file.getAbsolutePath();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "onResume 실행");
        Log.i(TAG, "uriArrayList1 = " + uriArrayList1);


//        photoListShowWithRetrofit(userSeq, expertSeq);


    }
//
//    //ImagePicker 라이브러리를 통해 선택한 이미지 파일을 갖고 아래 메소드 실행
//    // 1. Bitmap을 String 값으로 encode 한다.
//    // 2. encode한 String 값을 서버로 보낸다.
//    // 3. 서버에서 encode된 이미지 파일을 jpeg 파일로 변환하고, DB에 이미지 주소값을 저장한다.
//    // 4. DB에 저장된 이미지 주소값(String)을 클라이언트에서 전달 받는다.
//

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
    public void startActivityString(Class c, String name, String sendString) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.putExtra(name, sendString);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

    // 백스택 지우고 새로 만들어 전달
    public void startActivityNewTask(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.

        Log.i(TAG, "wallpaperDirectory = " +  wallpaperDirectory);
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
            Log.i(TAG, "saveImage's error = " +  e1);

        }
        return "";
    }

    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        }
    };
}


