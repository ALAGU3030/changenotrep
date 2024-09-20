package com.teamcenter.soa.model;
public class FixString {

    public static String setWidth(String inputtedString, int targetWidth) {
    
        int origWidth = inputtedString.length();
        String fixedString = "";
    
        if (origWidth >= targetWidth) {
            fixedString = inputtedString.substring(0,targetWidth);
        } else {
            fixedString = String.format("%1$-" + targetWidth + "s", inputtedString); 
        }
    
        return fixedString;
    }

}