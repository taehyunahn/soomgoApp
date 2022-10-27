package ExpertUpdate;

import android.net.Uri;

import java.util.ArrayList;

public class MultiImageData {
    private String photoId;

    public MultiImageData(String photoId) {
        this.photoId = photoId;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }
}
