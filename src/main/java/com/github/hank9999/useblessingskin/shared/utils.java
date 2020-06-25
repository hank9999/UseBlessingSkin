package com.github.hank9999.useblessingskin.shared;

import static com.github.hank9999.useblessingskin.shared.Response.*;
import static com.github.hank9999.useblessingskin.shared.httpMethods.*;

final public class utils {
    public static String getTextureId(String url) {
        try {
            String profile = getUrl(url);
            if (profile == null) {
                return "Role does not exist";
            } else if (profile.isEmpty()) {
                return "Role response is empty";
            }
            String textureId = TextureIdParser(profile);
            if (textureId == null) {
                return "Role does not have skin";
            }
            return textureId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean savePic(String urlHttp, String path, String picName) {
        try {
            return getPicture(urlHttp, path, picName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String[] MineSkinApi(String urlHttp, String picName, String picPath) {
        try {
            String apiResponse = postPic(urlHttp, picName, picPath);
            String[] textureData = MineSkinApiParser(apiResponse);
            if (textureData.length == 0) {
                return new String[] {"NoResult"};
            } else if ((textureData[0] == null) || (textureData[1] == null)) {
                return new String[] {"SomeResultMissing"};
            }
            return new String[] {"OK", textureData[0], textureData[1]};

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
