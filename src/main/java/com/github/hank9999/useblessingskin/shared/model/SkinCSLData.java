package com.github.hank9999.useblessingskin.shared.model;

public class SkinCSLData {
    private final boolean isSlim;
    private final String textureId;

    public SkinCSLData(boolean isSlim, String textureId) {
        this.isSlim = isSlim;
        this.textureId = textureId;
    }

    public boolean isSlim() {
        return isSlim;
    }

    public String getTextureId() {
        return textureId;
    }

    @Override
    public String toString() {
        return "SkinTextureResult{" +
                "isSlim=" + isSlim +
                ", textureId='" + textureId + '\'' +
                '}';
    }
}
