package com.example.gobimovie;

public class Series {
    private String Stitle;
    private String Sdes;
    private String Sthumbnail;
    private String Slink;
    private String Sgenre;
    private String Spos;

    public Series() {
    }

    public Series(String stitle, String spos, String sgenre, String slink, String sthumbnail, String sdes) {
        Stitle = stitle;
        Spos = spos;
        Sgenre = sgenre;
        Slink = slink;
        Sthumbnail = sthumbnail;
        Sdes = sdes;
    }

    public String getStitle() {
        return Stitle;
    }

    public void setStitle(String stitle) {
        Stitle = stitle;
    }

    public String getSdes() {
        return Sdes;
    }

    public void setSdes(String sdes) {
        Sdes = sdes;
    }

    public String getSthumbnail() {
        return Sthumbnail;
    }

    public void setSthumbnail(String sthumbnail) {
        Sthumbnail = sthumbnail;
    }

    public String getSlink() {
        return Slink;
    }

    public void setSlink(String slink) {
        Slink = slink;
    }

    public String getSgenre() {
        return Sgenre;
    }

    public void setSgenre(String sgenre) {
        Sgenre = sgenre;
    }

    public String getSpos() {
        return Spos;
    }

    public void setSpos(String spos) {
        Spos = spos;
    }
}
