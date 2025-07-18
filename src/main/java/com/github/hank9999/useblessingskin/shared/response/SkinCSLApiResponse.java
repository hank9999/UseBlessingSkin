package com.github.hank9999.useblessingskin.shared.response;

import com.google.gson.annotations.SerializedName;

public final class SkinCSLApiResponse {
    public ApiSkinData skins;

    public static class ApiSkinData {
        @SerializedName("default")
        public String steve;
        public String slim;
    }
}