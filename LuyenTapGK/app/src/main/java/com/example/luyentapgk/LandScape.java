package com.example.luyentapgk;

public class LandScape {
    String LandImageFileName;
    String LandCation;

    public String getLandImageFileName() {
        return LandImageFileName;
    }

    public void setLandImageFileName(String landImageFileName) {
        LandImageFileName = landImageFileName;
    }

    public String getLandCation() {
        return LandCation;
    }

    public void setLandCation(String landCation) {
        LandCation = landCation;
    }

    public LandScape(String landImageFileName, String landCation) {
        LandImageFileName = landImageFileName;
        LandCation = landCation;
    }
}
