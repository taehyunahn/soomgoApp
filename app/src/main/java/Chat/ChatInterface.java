package Chat;

import com.example.soomgodev.ExpertData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ChatRoomList.ChatRoomData;
import ConnectToServer.DataClass;
import ConnectToServer.Photo;
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

public interface ChatInterface {

    @Multipart // is specifying to retrofit to use multipart
    @POST("chat/chatRoomList.php") //is giving the file name of the web API
    Call<ChatRoomData> showChatRoomList2(@PartMap Map<String, RequestBody> params,
                                        @Part List<MultipartBody.Part> files
    );

    @Multipart // is specifying to retrofit to use multipart
    @POST("chat/chatRoomList1.php") //is giving the file name of the web API
    Call<ChatRoomData> showChatRoomList(@PartMap Map<String, RequestBody> params
    );

    @FormUrlEncoded
    @POST("chat/getChattingMsgList.php") //is giving the file name of the web API
    Call<List<ChatMember>> get_chatting_msg_list(@Field("userSeq") String userSeq,
                                                 @Field("chat_room_seq") String chat_room_seq,
                                                 @Field("clientOrExpert") String clientOrExpert

    );

    @FormUrlEncoded
    @POST("chat/getChattingMsgListPaging.php") //is giving the file name of the web API
    Call<List<ChatMember>> get_chatting_msg_list_paging(@Field("userSeq") String userSeq,
                                                 @Field("chat_room_seq") String chat_room_seq,
                                                 @Field("page") Integer page
    );


    @Multipart // is specifying to retrofit to use multipart
    @POST("chat/getInfoRelatedChatRoomTitle.php") //is giving the file name of the web API
    Call<ChatRoomData> getInfoRelatedChatRoomTitle(@PartMap Map<String, RequestBody> params
    );


    @Multipart // is specifying to retrofit to use multipart
    @POST("chat/getInfoRelatedChatRoomTitleClient.php") //is giving the file name of the web API
    Call<ChatRoomData> getInfoRelatedChatRoomTitleClient(@PartMap Map<String, RequestBody> params
    );



    @Multipart // is specifying to retrofit to use multipart
    @POST("chat/getExpertInfo.php") //is giving the file name of the web API
    Call<ChatRoomData> getExpertInfo(@PartMap Map<String, RequestBody> params
    );

    @Multipart // is specifying to retrofit to use multipart
    @POST("chat/sendDealInfo.php") //is giving the file name of the web API
    Call<ChatRoomData> sendDealInfo(@PartMap Map<String, RequestBody> params
    );

    @Multipart // is specifying to retrofit to use multipart
    @POST("chat/sendReviewInfo.php") //is giving the file name of the web API
    Call<ChatRoomData> sendReviewInfo(@PartMap Map<String, RequestBody> params
    );

    @Multipart // is specifying to retrofit to use multipart
    @POST("chat/getReviewInfo.php") //is giving the file name of the web API
    Call<List<ChatRoomData>> getReviewInfo(@PartMap Map<String, RequestBody> params
    );





}
