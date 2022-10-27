package com.example.soomgodev;

import java.util.Comparator;

public class ExpertData {
    //한 아이템을 위한 데이터

    String expertName;
    String expertIntro;
    String userProfileImage;
    String expertProfileImage;
    String reviewCount;
    String reviewAverage;
    String hireCount;
    String expertId;
    String expertMainService;
    String count;

    public ExpertData() {
    }

    public ExpertData(String expertId) {
        this.expertId = expertId;
    }

    //생성자 추가 -> 데이터 담아두기 위한 목적
    public ExpertData(String expertName, String expertIntro, String userProfileImage, String reviewCount, String reviewAverage, String hireCount, String expertId) {
        this.expertName = expertName;
        this.expertIntro = expertIntro;
        this.userProfileImage = userProfileImage;
        this.reviewCount = reviewCount;
        this.reviewAverage = reviewAverage;
        this.hireCount = hireCount;
        this.expertId = expertId;
    }

    public ExpertData(String expertName, String expertIntro, String userProfileImage, String reviewCount, String reviewAverage, String hireCount, String expertId, String expertMainService) {
        this.expertName = expertName;
        this.expertIntro = expertIntro;
        this.userProfileImage = userProfileImage;
        this.reviewCount = reviewCount;
        this.reviewAverage = reviewAverage;
        this.hireCount = hireCount;
        this.expertId = expertId;
        this.expertMainService = expertMainService;
    }

    public String getExpertMainService() {
        return expertMainService;
    }

//    public static Comparator<ExpertData> ExpertReviewCountComparator = new Comparator<ExpertData>() {
//        @Override
//        public int compare(ExpertData o1, ExpertData o2) {
//            return o1.getReviewCount() - o2.getReviewCount();
//        }
//    };
//
//    public static Comparator<ExpertData> ExpertReviewAverageComparator = new Comparator<ExpertData>() {
//        @Override
//        public int compare(ExpertData o1, ExpertData o2) {
//            return o1.getReviewAverage() - o2.getReviewAverage();
//        }
//    };
//
//    public static Comparator<ExpertData> ExpertHireCountComparator = new Comparator<ExpertData>() {
//        @Override
//        public int compare(ExpertData o1, ExpertData o2) {
//            return o1.getHireCount() - o2.getHireCount();
//        }
//    };


    public String getExpertProfileImage() {
        return expertProfileImage;
    }

    public void setExpertProfileImage(String expertProfileImage) {
        this.expertProfileImage = expertProfileImage;
    }

    public String getExpertName() {
        return expertName;
    }

    public void setExpertName(String expertName) {
        this.expertName = expertName;
    }

    public String getExpertIntro() {
        return expertIntro;
    }

    public void setExpertIntro(String expertIntro) {
        this.expertIntro = expertIntro;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
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

    public String getHireCount() {
        return hireCount;
    }

    public void setHireCount(String hireCount) {
        this.hireCount = hireCount;
    }

    public String getExpertId() {
        return expertId;
    }

    public void setExpertId(String expertId) {
        this.expertId = expertId;
    }

    public void setExpertMainService(String expertMainService) {
        this.expertMainService = expertMainService;
    }

    public String getCount() {
        return count;
    }
}
