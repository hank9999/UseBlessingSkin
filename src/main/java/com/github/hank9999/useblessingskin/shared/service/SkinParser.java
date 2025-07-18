package com.github.hank9999.useblessingskin.shared.service;

import com.github.hank9999.useblessingskin.shared.model.MineSkinData;
import com.github.hank9999.useblessingskin.shared.model.SkinCSLData;
import com.github.hank9999.useblessingskin.shared.response.MineSkinApiResponse;
import com.github.hank9999.useblessingskin.shared.response.SkinCSLApiResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.LinkedHashMap;
import java.util.Map;

public final class SkinParser {
    private static final Gson GSON = new Gson();


    public static SkinCSLData parseTextureData(String jsonProfile) throws JsonSyntaxException {
        if (jsonProfile == null || jsonProfile.trim().isEmpty()) {
            return new SkinCSLData(false, null);
        }

        SkinCSLApiResponse response = GSON.fromJson(jsonProfile, SkinCSLApiResponse.class);

        if (response == null || response.skins == null) {
            return new SkinCSLData(false, null);
        }

        String textureId;
        boolean isSlim;

        boolean hasSteve = response.skins.steve != null && !response.skins.steve.trim().isEmpty();
        boolean hasSlim = response.skins.slim != null && !response.skins.slim.trim().isEmpty();
        // 同时返回了steve和slim皮肤，按照字段顺序判断优先级 例如 skin.prinzeugen.net
        if (hasSteve && hasSlim) {
            Map<String, Map<String, Object>> rootMap = GSON.fromJson(jsonProfile, new TypeToken<LinkedHashMap<String, Object>>(){}.getType());
            Map<String, Object> skinsMap = rootMap.get("skins");
            String firstSkinType = skinsMap.keySet().iterator().next();
            if ("default".equals(firstSkinType)) {
                textureId = response.skins.steve;
                isSlim = false;
            } else if ("slim".equals(firstSkinType)) {
                textureId = response.skins.slim;
                isSlim = true;
            } else {
                textureId = response.skins.steve;
                isSlim = false;
            }
        } else if (hasSteve) {
            textureId = response.skins.steve;
            isSlim = false;
        } else if (hasSlim) {
            textureId = response.skins.slim;
            isSlim = true;
        } else {
            textureId = null;
            isSlim = false;
        }

        return new SkinCSLData(isSlim, textureId);
    }

    public static MineSkinData parseMineSkinData(String jsonData) throws JsonSyntaxException {
        if (jsonData == null || jsonData.trim().isEmpty()) {
            return new MineSkinData(null, null);
        }

        MineSkinApiResponse response = GSON.fromJson(jsonData, MineSkinApiResponse.class);

        if (response == null || response.data == null || response.data.texture == null) {
            return new MineSkinData(null, null);
        }

        return new MineSkinData(response.data.texture.value, response.data.texture.signature);
    }
}
