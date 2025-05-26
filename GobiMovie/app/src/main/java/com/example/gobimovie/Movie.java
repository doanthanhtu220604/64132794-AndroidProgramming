package com.example.gobimovie;

public class Movie {
    private String Ftitle;
    private String Fdes;
    private String Fthumbnail;
    private String Tlink;
    private String Fgenre;
    private String Fpos; // Thuộc tính mới cho poster

    public Movie() {
    }

    public Movie(String ftitle, String tlink, String fdes, String fthumbnail, String fgenre, String fpos) {
        Ftitle = ftitle;
        Tlink = tlink;
        Fdes = fdes;
        Fthumbnail = fthumbnail;
        Fgenre = fgenre;
        Fpos = fpos; // Thêm Fpos vào constructor
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

    public String getFgenre() {
        return Fgenre;
    }

    public void setFgenre(String fgenre) {
        Fgenre = fgenre;
    }

    public String getFpos() {
        return Fpos;
    }

    public void setFpos(String fpos) {
        Fpos = fpos;
    }
}