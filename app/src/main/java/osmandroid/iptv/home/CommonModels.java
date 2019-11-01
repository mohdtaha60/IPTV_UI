package osmandroid.iptv.home;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

public class CommonModels {


    public String image_url;
    public String title;
    public String video_url;

    public CommonModels() {
    }

    public CommonModels(String title, String image_url, String video_url) {
        this.image_url = image_url;
        this.title = title;
        this.video_url = video_url;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String stremURL) {
        this.video_url = stremURL;
    }


    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String imageUrl) {
        this.image_url = imageUrl;
    }



    public String getImage() {
        return image_url;
    }

    public void setImage(int image) {
        this.image_url = image_url;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}

