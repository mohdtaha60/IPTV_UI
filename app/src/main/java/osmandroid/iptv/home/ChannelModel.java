package osmandroid.iptv.home;

public class ChannelModel {
    String group_title,logo_url,name,video_url;

    public ChannelModel()
    {
    }

    public ChannelModel(String group_title, String logo_url, String name, String video_url) {
        this.group_title = group_title;
        this.logo_url = logo_url;
        this.name = name;
        this.video_url = video_url;
    }

    public String getGroup_title() {
        return group_title;
    }

    public void setGroup_title(String group_title) {
        this.group_title = group_title;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }
}
