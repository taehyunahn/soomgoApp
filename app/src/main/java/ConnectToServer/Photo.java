package ConnectToServer;

import com.google.gson.annotations.SerializedName;

public class Photo {

    @SerializedName("photoPath")
    public String photoPath;

    public Photo(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getPhotoPath() {
        return photoPath;
    }
}
