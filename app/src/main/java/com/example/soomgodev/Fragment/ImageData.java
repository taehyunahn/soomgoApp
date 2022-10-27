package com.example.soomgodev.Fragment;

public class ImageData {
    private String imageAddress;
    private String imageDetail;

    public ImageData(String imageAddress, String imageDetail) {
        this.imageAddress = imageAddress;
        this.imageDetail = imageDetail;
    }

    public String getImageAddress() {
        return imageAddress;
    }

    public void setImageAddress(String imageAddress) {
        this.imageAddress = imageAddress;
    }

    public String getImageDetail() {
        return imageDetail;
    }

    public void setImageDetail(String imageDetail) {
        this.imageDetail = imageDetail;
    }
}
