package ChatRoomList;

import com.google.gson.annotations.SerializedName;

public class ChatRoomData {

    @SerializedName("expertImage")
    public String expertImage;

    @SerializedName("expertName")
    public String expertName;

    @SerializedName("serviceName")
    public String serviceName;

    @SerializedName("expertAddress")
    public String expertAddress;

    @SerializedName("price")
    public String price;

    @SerializedName("lastMsg")
    public String lastMsg;

    @SerializedName("lastDate")
    public String lastDate;

    @SerializedName("chatRoomNumber")
    public String chatRoomNumber;

    @SerializedName("selectedExpertId")
    public String selectedExpertId;

    @SerializedName("userIdWhoRequest")
    public String userIdWhoRequest;

    @SerializedName("clientName")
    public String clientName;

    @SerializedName("addressInfo")
    public String addressInfo;

    @SerializedName("serviceRequested")
    public String serviceRequested;

    @SerializedName("requestDate")
    public String requestDate;

    @SerializedName("expertMainService")
    public String expertMainService;

    @SerializedName("userProfileImage")
    public String userProfileImage;

    @SerializedName("expertProfileImage")
    public String expertProfileImage;

    @SerializedName("quoteId")
    public String quoteId;


    @SerializedName("reviewWriterName")
    public String reviewWriterName;

    @SerializedName("reviewGrade")
    public String reviewGrade;

    @SerializedName("reviewContents")
    public String reviewContents;

    @SerializedName("reviewDate")
    public String reviewDate;



    public ChatRoomData(String expertImage, String expertName, String serviceName, String expertAddress, String price, String lastMsg, String lastDate, String chatRoomNumber) {
        this.expertImage = expertImage;
        this.expertName = expertName;
        this.serviceName = serviceName;
        this.expertAddress = expertAddress;
        this.price = price;
        this.lastMsg = lastMsg;
        this.lastDate = lastDate;
        this.chatRoomNumber = chatRoomNumber;
    }

    public ChatRoomData(String expertName, String chatRoomNumber) {
        this.expertName = expertName;
        this.chatRoomNumber = chatRoomNumber;
    }

    public ChatRoomData(String reviewWriterName, String reviewGrade, String reviewContents, String reviewDate) {
        this.reviewWriterName = reviewWriterName;
        this.reviewGrade = reviewGrade;
        this.reviewContents = reviewContents;
        this.reviewDate = reviewDate;
    }

    public String getReviewWriterName() {
        return reviewWriterName;
    }

    public void setReviewWriterName(String reviewWriterName) {
        this.reviewWriterName = reviewWriterName;
    }

    public String getReviewGrade() {
        return reviewGrade;
    }

    public void setReviewGrade(String reviewGrade) {
        this.reviewGrade = reviewGrade;
    }

    public String getReviewContents() {
        return reviewContents;
    }

    public void setReviewContents(String reviewContents) {
        this.reviewContents = reviewContents;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    public ChatRoomData(String chatRoomNumber, String selectedExpertId, String userIdWhoRequest) {
        this.chatRoomNumber = chatRoomNumber;
        this.selectedExpertId = selectedExpertId;
        this.userIdWhoRequest = userIdWhoRequest;
    }

    public ChatRoomData(String clientName) {
        this.clientName = clientName;
    }

    public ChatRoomData() {
    }

    public String getAddressInfo() {
        return addressInfo;
    }

    public void setAddressInfo(String addressInfo) {
        this.addressInfo = addressInfo;
    }

    public String getServiceRequested() {
        return serviceRequested;
    }

    public void setServiceRequested(String serviceRequested) {
        this.serviceRequested = serviceRequested;
    }


    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getExpertMainService() {
        return expertMainService;
    }

    public void setExpertMainService(String expertMainService) {
        this.expertMainService = expertMainService;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    public String getSelectedExpertId() {
        return selectedExpertId;
    }

    public void setSelectedExpertId(String selectedExpertId) {
        this.selectedExpertId = selectedExpertId;
    }

    public String getUserIdWhoRequest() {
        return userIdWhoRequest;
    }

    public void setUserIdWhoRequest(String userIdWhoRequest) {
        this.userIdWhoRequest = userIdWhoRequest;
    }


    public String getExpertImage() {
        return expertImage;
    }

    public void setExpertImage(String expertImage) {
        this.expertImage = expertImage;
    }

    public String getExpertName() {
        return expertName;
    }

    public String getChatRoomNumber() {
        return chatRoomNumber;
    }

    public void setChatRoomNumber(String chatRoomNumber) {
        this.chatRoomNumber = chatRoomNumber;
    }

    public void setExpertName(String expertName) {
        this.expertName = expertName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getExpertAddress() {
        return expertAddress;
    }

    public void setExpertAddress(String expertAddress) {
        this.expertAddress = expertAddress;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public String getExpertProfileImage() {
        return expertProfileImage;
    }

    public void setExpertProfileImage(String expertProfileImage) {
        this.expertProfileImage = expertProfileImage;
    }
}
