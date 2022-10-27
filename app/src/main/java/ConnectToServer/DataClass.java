package ConnectToServer;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DataClass {

    // @SerializedName으로 일치시켜 주지않을 경우엔 클래스 변수명이 일치해야함
    @SerializedName("userSeq")
    public String userSeq;

    @SerializedName("expertSeq")
    public String expertSeq;

    @SerializedName("expertName")
    public String expertName;

    @SerializedName("userProfileImage")
    public String userProfileImage;

    @SerializedName("expertProfileImage")
    public String expertProfileImage;


    @SerializedName("expertIntro")
    public String expertIntro;

    @SerializedName("expertAddress")
    public String expertAddress;

    @SerializedName("expertYear")
    public String expertYear;

    @SerializedName("expertService")
    public String expertService;

    @SerializedName("expertMainService")
    public String expertMainService;

    @SerializedName("expertIntroDetail")
    public String expertIntroDetail;

    @SerializedName("userName")
    public String userName;

    @SerializedName("userEmail")
    public String userEmail;

    @SerializedName("photoPath")
    public String photoPath;

    @SerializedName("expertTime")
    public String expertTime;

    @SerializedName("photoArrayList")
    public ArrayList photoArrayList;

    @SerializedName("reviewCount")
    public String reviewCount;

    @SerializedName("reviewAverage")
    public String reviewAverage;

    public DataClass(ArrayList photoArrayList) {
        this.photoArrayList = photoArrayList;
    }

    public ArrayList getPhotoArrayList() {
        return photoArrayList;
    }

    public DataClass(String userSeq, String expertSeq, String photoPath) {
        this.userSeq = userSeq;
        this.expertSeq = expertSeq;
        this.photoPath = photoPath;
    }

    public DataClass(String userSeq, String expertName, String userProfileImage, String expertIntro, String expertAddress, String expertYear, String expertService, String expertMainService, String expertIntroDetail, String userName, String userEmail, String photoPath, String expertTime) {
        this.userSeq = userSeq;
        this.expertName = expertName;
        this.userProfileImage = userProfileImage;
        this.expertIntro = expertIntro;
        this.expertAddress = expertAddress;
        this.expertYear = expertYear;
        this.expertService = expertService;
        this.expertMainService = expertMainService;
        this.expertIntroDetail = expertIntroDetail;
        this.userName = userName;
        this.userEmail = userEmail;
        this.photoPath = photoPath;
        this.expertTime = expertTime;
    }

    public String getExpertTime() {
        return expertTime;
    }

    public String getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(String reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getReviewAverage() {
        return reviewAverage;
    }

    public void setReviewAverage(String reviewAverage) {
        this.reviewAverage = reviewAverage;
    }

    public String getUserSeq() {
        return userSeq;
    }

    public String getExpertName() {
        return expertName;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public String getExpertIntro() {
        return expertIntro;
    }

    public String getExpertAddress() {
        return expertAddress;
    }

    public String getExpertYear() {
        return expertYear;
    }

    public String getExpertService() {
        return expertService;
    }

    public String getExpertMainService() {
        return expertMainService;
    }

    public String getExpertIntroDetail() {
        return expertIntroDetail;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public String getExpertSeq() {
        return expertSeq;
    }

    public String getExpertProfileImage() {
        return expertProfileImage;
    }

    public void setExpertProfileImage(String expertProfileImage) {
        this.expertProfileImage = expertProfileImage;
    }
}
