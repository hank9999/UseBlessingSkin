package com.github.hank9999.useblessingskin.shared.response;

public final class MineSkinApiResponse {
    public ApiMineSkinData data;

    public static class ApiMineSkinData {
        public ApiTextureData texture;

        public static class ApiTextureData {
            public String value;
            public String signature;
        }
    }
}