package com.example.gobimovie;

public class Movie {
    private String Ftitle;
    private String Fdes;
    private String Fthumbnail;
    private String Tlink;


    public Movie() {
    }

    public Movie(String ftitle, String tlink, String fdes, String fthumbnail) {
        Ftitle = ftitle;
        Tlink = tlink;
        Fdes = fdes;
        Fthumbnail = fthumbnail;
    }

    public String getFtitle() {
        return Ftitle;
    }

    public void setFtitle(String ftitle) {
        Ftitle = ftitle;
    }

    public String getTlink() {
        return Tlink;
    }

    public void setTlink(String tlink) {
        Tlink = tlink;
    }

    public String getFthumbnail() {
        return Fthumbnail;
    }

    public void setFthumbnail(String fthumbnail) {
        Fthumbnail = fthumbnail;
    }

    public String getFdes() {
        return Fdes;
    }

    public void setFdes(String fdes) {
        Fdes = fdes;
    }
}
