package com.example.soomgodev;


public class Photo {
    //한 아이템을 위한 데이터

    String photoId;
    String photoAddress;
    String id_expert;

    public Photo(String photoId, String photoAddress, String id_expert) {
        this.photoId = photoId;
        this.photoAddress = photoAddress;
        this.id_expert = id_expert;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getPhotoAddress() {
        return photoAddress;
    }

    public void setPhotoAddress(String photoAddress) {
        this.photoAddress = photoAddress;
    }

    public String getId_expert() {
        return id_expert;
    }

    public void setId_expert(String id_expert) {
        this.id_expert = id_expert;
    }
}
