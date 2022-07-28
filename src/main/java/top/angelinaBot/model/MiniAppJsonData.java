package top.angelinaBot.model;

public class MiniAppJsonData {
    public String app;
    public String desc;
    public String view;
    public String ver;
    public String prompt;
    public String appid;
    public MetaData meta;
    public ConfigData config;

    public MiniAppJsonData(){
        super();
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public MetaData getMeta() {
        return meta;
    }

    public void setMeta(MetaData meta) {
        this.meta = meta;
    }
}

