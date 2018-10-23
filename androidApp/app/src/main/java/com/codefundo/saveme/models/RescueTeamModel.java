package com.codefundo.saveme.models;

public class RescueTeamModel {

    private int drawable;
    private String text;

    public RescueTeamModel(int drawable, String text) {
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
