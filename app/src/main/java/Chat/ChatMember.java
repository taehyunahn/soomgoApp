package Chat;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class ChatMember implements Serializable {

    private static final long serialVersionUID = 1L;
    @SerializedName("nickname")
    private String nickname;
//    private String seq;

    @SerializedName("message")
    private String message;

    @SerializedName("orderType")
    private String orderType;

    private String post_seq;

    private String command;

    private String recipient;

    @SerializedName("chat_room_seq")
    private String chat_room_seq;

    private String profile_img;

    @SerializedName("viewType")
    private int viewType;

    @SerializedName("nicknameUser")
    private String nicknameUser;

    @SerializedName("nicknameExpert")
    private String nicknameExpert;

    @SerializedName("now_time")
    private String time;
    private int msg_count;
    private String expertSeq;
    private String userSeq;

    @SerializedName("selectedExpertId")
    private String selectedExpertId;

    @SerializedName("userIdWhoSent")
    private String userIdWhoSent;

    @SerializedName("userProfileImage")
    private String userProfileImage;

    @SerializedName("clientOrExpert")
    private String clientOrExpert;

    @SerializedName("readOrNot")
    private String readOrNot;

    @SerializedName("expertName")
    private String expertName;

    @SerializedName("serviceRequest")
    private String serviceRequest;

    @SerializedName("price")
    private String price;

    @SerializedName("expertProfileImage")
    private String expertProfileImage;

    @SerializedName("unread")
    private String unread;


    public String getSelectedExpertId() {
        return selectedExpertId;
    }

    public void setSelectedExpertId(String selectedExpertId) {
        this.selectedExpertId = selectedExpertId;
    }

    public String getUserIdWhoSent() {
        return userIdWhoSent;
    }

    public void setUserIdWhoSent(String userIdWhoSent) {
        this.userIdWhoSent = userIdWhoSent;
    }

    public ChatMember(String message, String nickname, String time, int viewType) {
        this.message = message;
        this.nickname = nickname;
        this.time = time;
        this.viewType = viewType;
    }

    public ChatMember() {
    }

    public ChatMember(String nickname, String orderType) {
        this.nickname = nickname;
        this.orderType = orderType;
    }


    public String getUnread() {
        return unread;
    }

    public void setUnread(String unread) {
        this.unread = unread;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getNicknameUser() {
        return nicknameUser;
    }

    public void setNicknameUser(String nicknameUser) {
        this.nicknameUser = nicknameUser;
    }

    public String getNicknameExpert() {
        return nicknameExpert;
    }


    public String getReadOrNot() {
        return readOrNot;
    }

    public void setReadOrNot(String readOrNot) {
        this.readOrNot = readOrNot;
    }

    public String getClientOrExpert() {
        return clientOrExpert;
    }

    public void setClientOrExpert(String clientOrExpert) {
        this.clientOrExpert = clientOrExpert;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public void setNicknameExpert(String nicknameExpert) {
        this.nicknameExpert = nicknameExpert;
    }

    public String getUserSeq() {
        return userSeq;
    }

    public void setUserSeq(String userSeq) {
        this.userSeq = userSeq;
    }

    public String getExpertSeq() {
        return expertSeq;
    }

    public void setExpertSeq(String expertSeq) {
        this.expertSeq = expertSeq;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

//    public String getSeq() {
//        return seq;
//    }
//
//    public void setSeq(String seq) {
//        this.seq = seq;
//    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPost_seq() {
        return post_seq;
    }

    public void setPost_seq(String post_seq) {
        this.post_seq = post_seq;
    }

    public String getCommand(){
        return command;
    }

    public void setCommand(String command){
        this.command= command;
    }

    public String getRecipient(){
        return recipient;
    }

    public void setRecipient(String recipient){
        this.recipient= recipient;
    }

    public String getChat_room_seq(){
        return chat_room_seq;
    }

    public void setChat_room_seq(String chat_room_seq){
        this.chat_room_seq= chat_room_seq;
    }

    public String getProfile_img(){
        return profile_img;
    }

    public void setProfile_img(String profile_img){
        this.profile_img= profile_img;
    }

    public int getViewType(){
        return viewType;
    }

    public void setViewType(int viewType){
        this.viewType= viewType;
    }

    public String getTime(){
        return time;
    }

    public int getMsg_count() {
        return msg_count;
    }

    public void setMsg_count(int msg_count){
        this.msg_count= msg_count;
    }


    public String getExpertName() {
        return expertName;
    }

    public void setExpertName(String expertName) {
        this.expertName = expertName;
    }

    public String getServiceRequest() {
        return serviceRequest;
    }

    public void setServiceRequest(String serviceRequest) {
        this.serviceRequest = serviceRequest;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getExpertProfileImage() {
        return expertProfileImage;
    }

    public void setExpertProfileImage(String expertProfileImage) {
        this.expertProfileImage = expertProfileImage;
    }
}
