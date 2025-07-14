package com.github.hank9999.useblessingskin.shared;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.github.hank9999.useblessingskin.shared.Response.*;
import static com.github.hank9999.useblessingskin.shared.HttpMethods.*;

final public class Utils {
    public static String[] getTextureId(String url) {
        try {
            String profile = getString(url);
            if (profile == null) {
                return new String[] {"false", "Role does not exist"};
            } else if (profile.isEmpty() || profile.equals("{}")) {
                return new String[] {"false", "Role response is empty"};
            }
            String[] TextureIdParseData = TextureIdParser(profile);
            String isSlim = TextureIdParseData[0];
            String textureId = TextureIdParseData[1];
            if (textureId == null) {
                return new String[] {"false", "Role does not have skin"};
            }
            return new String[] {isSlim, textureId};
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

    public static String[] MineSkinApi(String urlHttp, String picName, String picPath, String isSlim) {
        try {
            if (isSlim.equalsIgnoreCase("true")) {
                urlHttp += "?model=slim";
            }
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

    public static Boolean checkCache(String picPath) {
        File file = new File(picPath);
        if (!file.exists()) {
            return false;
        }

        BufferedImage img = null;
        try {
            img = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (img == null) {
            return false;
        }

        return checkImgValid(img);
    }

    public static Boolean checkImgValid(BufferedImage img) {
        int skinWidth = img.getWidth();
        int skinHeight = img.getHeight();

        return (skinWidth == 64 && skinHeight == 64) || (skinWidth == 64 && skinHeight == 32);
    }
}
