package com.github.hank9999.useblessingskin.shared;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

final class SkinWebResponse {
    public SkinData skins;

    static class SkinData {
        @SerializedName("default")
        public String steve;
        public String slim;
    }
}

final class MineSkinResponse {
    public D1 data;
    static class D1 {
        public D2 texture;
        static class D2 {
            String value;
            String signature;
        }
    }
}

final public class Response {
    public static String[] TextureIdParser(String profile) {
        SkinWebResponse item = new Gson().fromJson(profile, SkinWebResponse.class);
        String texture_id = null;
        String isSlim = "false";
        if (item.skins.steve != null) {
            texture_id = item.skins.steve;
        }
        if (item.skins.slim != null) {
            texture_id = item.skins.slim;
            isSlim = "true";
        }
        return new String[] {isSlim, texture_id};
    }

    public static String[] MineSkinApiParser(String jsonData) {
        MineSkinResponse item = new Gson().fromJson(jsonData, MineSkinResponse.class);
        return new String[] {item.data.texture.value, item.data.texture.signature};
    }
}

