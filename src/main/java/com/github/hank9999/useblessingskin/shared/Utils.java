package com.github.hank9999.useblessingskin.shared;

import com.github.hank9999.useblessingskin.shared.model.MineSkinData;
import com.github.hank9999.useblessingskin.shared.model.Result;
import com.github.hank9999.useblessingskin.shared.model.SkinCSLData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.github.hank9999.useblessingskin.shared.service.SkinParser.*;
import static com.github.hank9999.useblessingskin.shared.HttpMethods.*;

final public class Utils {
    public static Result<SkinCSLData> getTextureId(String url) {
        try {
            String profile = getString(url);
            if (profile == null) {
                return Result.failure(ErrorCode.ROLE_NOT_EXIST);
            } else if (profile.isEmpty() || profile.equals("{}")) {
                return Result.failure(ErrorCode.ROLE_RESPONSE_EMPTY);
            }
            SkinCSLData skinData = parseTextureData(profile);
            if (skinData.getTextureId() == null) {
                return Result.failure(ErrorCode.ROLE_SKIN_NOT_EXIST);
            }
            return Result.success(skinData);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(ErrorCode.UNKNOWN_ERROR);
        }
    }

    public static Result<Boolean> savePic(String urlHttp, String path, String picName) {
        try {
            boolean result = getPicture(urlHttp, path, picName);
            return result ? Result.success(true) : Result.failure(ErrorCode.SAVE_TEXTURE_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(ErrorCode.SAVE_TEXTURE_ERROR);
        }
    }

    public static Result<MineSkinData> MineSkinApi(String urlHttp, String picName, String picPath, boolean isSlim) {
        try {
            if (isSlim) {
                urlHttp += "?model=slim";
            }
            String apiResponse = postPic(urlHttp, picName, picPath);
            if (apiResponse.isEmpty()) {
                return Result.failure(ErrorCode.NO_RESULT);
            }
            MineSkinData mineSkinData = parseMineSkinData(apiResponse);
            if (mineSkinData.getSignature() == null || mineSkinData.getValue() == null) {
                return Result.failure(ErrorCode.NO_RESULT);
            }
            return Result.success(mineSkinData);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(ErrorCode.UPLOAD_TEXTURE_ERROR);
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

        return checkImgValid(img);
    }

    public static Boolean checkImgValid(BufferedImage img) {
        if (img == null) {
            return false;
        }

        int skinWidth = img.getWidth();
        int skinHeight = img.getHeight();

        return (skinWidth == 64 && skinHeight == 64) || (skinWidth == 64 && skinHeight == 32);
    }
}
