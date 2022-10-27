package ConnectToServer;

import com.example.soomgodev.ExpertData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface UploadApis {

    @FormUrlEncoded
    @POST("expertPhotoTest.php")
    Call<DataClass> getUserLogin(
            @Field("name") String name,
            @Field("image") String image
    );

    @FormUrlEncoded
    @POST("expertPhotoTest.php")
    Call<DataClass> sendPhoto(
            @Field("imageNameList") ArrayList<String> imageNameList
    );

    @Multipart
    @POST("uploadmultipleimages")
    Call<ResponseBody> uploadMultiImage(@Part MultipartBody.Part file1, @Part MultipartBody.Part file2);

    @Multipart // is specifying to retrofit to use multipart
    @POST("expertPhotoTest.php") //is giving the file name of the web API
    Call<String> uploadImage( // this method has two parameters. MultipartBody.Part will contain the image
            @Part MultipartBody.Part file,
            @Part("filename") RequestBody name
    );


    @Multipart // is specifying to retrofit to use multipart
    @POST("httpPractice/photoUpload.php") //is giving the file name of the web API
    Call<DataClass> uploadImage(@Part("userSeq") RequestBody userSeq,
                                @Part("expertSeq") RequestBody expertSeq,
                                @PartMap Map<String, RequestBody> params,
                                @Part List<MultipartBody.Part> files
    );


    @FormUrlEncoded
    @POST("httpPractice/photoListShow.php") //is giving the file name of the web API
    Call<List<DataClass>> photoListShow(@Field("userSeq") String userSeq,
                                        @Field("expertSeq") String expertSeq
    );


    @FormUrlEncoded
    @POST("httpPractice/removePhoto.php") //is giving the file name of the web API
    Call<DataClass> removePhoto(@Field("photoPath") String photoPath
    );


    @FormUrlEncoded
    @POST("expertUpdate.php")
    Call<DataClass> requestExpertInfo(@Field("userSeq") String userSeq);

    @FormUrlEncoded
    @POST("expertUpdatePhotoList.php")
    Call<List<Photo>> getAllPhoto(@Field("userSeq") String userSeq);




    @Multipart // is specifying to retrofit to use multipart
    @POST("fragment24/switchSearch.php") //is giving the file name of the web API
    Call<DataClass> switchSearchCheck(
                                @PartMap Map<String, RequestBody> params
    );




    // '고수찾기'에서 고수 리사이클러뷰 만들기 위한 요청
    @FormUrlEncoded
    @POST("expertListUpdated.php") //is giving the file name of the web API
    Call<List<ExpertData>> showExpertList(@Field("userSeq") String userSeq,
                                          @Field("page") String page,
                                          @Field("limit") String limit,
                                          @Field("service") String service,
                                          @Field("address") String address

    );



}
