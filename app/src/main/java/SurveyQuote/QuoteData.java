package SurveyQuote;

import com.google.gson.annotations.SerializedName;

public class QuoteData {

    @SerializedName("expertImage")
    public String expertImage;

    @SerializedName("expertName")
    public String expertName;

    @SerializedName("reviewAverage")
    public String reviewAverage;

    @SerializedName("reviewCount")
    public String reviewCount;

    @SerializedName("hireCount")
    public String hireCount;

    @SerializedName("price")
    public String price;

    @SerializedName("roomNumber")
    public String roomNumber;

    public QuoteData(String expertImage, String expertName, String reviewAverage, String reviewCount, String hireCount, String price) {
        this.expertImage = expertImage;
        this.expertName = expertName;
        this.reviewAverage = reviewAverage;
        this.reviewCount = reviewCount;
        this.hireCount = hireCount;
        this.price = price;
    }

    public QuoteData(String expertImage, String expertName, String reviewAverage, String reviewCount, String hireCount, String price, String roomNumber) {
        this.expertImage = expertImage;
        this.expertName = expertName;
        this.reviewAverage = reviewAverage;
        this.reviewCount = reviewCount;
        this.hireCount = hireCount;
        this.price = price;
        this.roomNumber = roomNumber;

    }


    public QuoteData() {
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
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

    public void setExpertName(String expertName) {
        this.expertName = expertName;
    }

    public String getReviewAverage() {
        return reviewAverage;
    }

    public void setReviewAverage(String reviewAverage) {
        this.reviewAverage = reviewAverage;
    }

    public String getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(String reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getHireCount() {
        return hireCount;
    }

    public void setHireCount(String hireCount) {
        this.hireCount = hireCount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
