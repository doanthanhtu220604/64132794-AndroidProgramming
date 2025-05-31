package com.example.gobimovie;

import java.io.Serializable;

public class Part implements Serializable {
    private String part;
    private String url;
    private String vidUrl;

    public Part() {
    }

    public Part(String part, String url, String vidUrl) {
        this.part = part;
        this.url = url;
        this.vidUrl = vidUrl;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVidUrl() {
        return vidUrl;
    }

    public void setVidUrl(String vidUrl) {
        this.vidUrl = vidUrl;
    }
}