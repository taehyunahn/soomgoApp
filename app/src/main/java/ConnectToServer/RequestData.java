package ConnectToServer;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RequestData {

    // @SerializedName으로 일치시켜 주지않을 경우엔 클래스 변수명이 일치해야함
    @SerializedName("userSeq")
    public String userSeq;

    @SerializedName("expertSeq")
    public String expertSeq;

    @SerializedName("age")
    public String age;

    @SerializedName("how")
    public String how;

    @SerializedName("day")
    public String day;

    @SerializedName("time")
    public String time;

    @SerializedName("gender")
    public String gender;

    @SerializedName("address")
    public String address;

    @SerializedName("question")
    public String question;

    @SerializedName("expertId")
    public String expertId;


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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHow() {
        return how;
    }

    public void setHow(String how) {
        this.how = how;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getExpertId() {
        return expertId;
    }

    public void setExpertId(String expertId) {
        this.expertId = expertId;
    }
}
