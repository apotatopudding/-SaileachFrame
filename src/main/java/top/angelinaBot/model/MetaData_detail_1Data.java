package top.angelinaBot.model;

public class MetaData_detail_1Data {
    public Integer appType;
    public String appid;
    public String desc;
    public String gamePoints;
    public String gamePointsUrl;
    public String icon;
    public String preview;
    public String qqdocurl;
    public Integer scene;
    public ShareTemplateData shareTemplateData;
    public String shareTemplateId;
    public String showLittleTail;
    public String title;
    public String url;
    public hostData host;

    public MetaData_detail_1Data(){
        super();
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getQqdocurl() {
        return qqdocurl;
    }

    public void setQqdocurl(String qqdocurl) {
        this.qqdocurl = qqdocurl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
