package com.bytehub.cloud;


public class LinkDTO {

    private int link_idx;
    private String user_id;
    private String link_name;
    private String url;

    public int getLink_idx() {
        return link_idx;
    }

    public void setLink_idx(int link_idx) {
        this.link_idx = link_idx;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getLink_name() {
        return link_name;
    }

    public void setLink_name(String link_name) {
        this.link_name = link_name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
