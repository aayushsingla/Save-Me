package com.codefundo.saveme.models;

public class AdminModel {

    private int drawable;
    private String text;

    public AdminModel(int drawable, String text) {
        this.drawable = drawable;
        this.text = text;
    }

    public int getDrawable() {
        return drawable;
    }

    public String getText() {
        return text;
    }
}
