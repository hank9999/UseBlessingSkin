package com.github.hank9999.useblessingskin.shared;

import okhttp3.*;
import okhttp3.Response;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

final public class HttpMethods {
    private static final OkHttpClient client = new OkHttpClient();

    public static String getUrl(String url) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "UseBlessingSkinPlugin/1.0")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 404 || response.code() == 204) {
                return null;
            }

            if (!response.isSuccessful()) {
                throw new IOException("HTTP error code: " + response.code() + " | " + response.message());
            }

            return response.body() != null ? response.body().string() : null;
        }
    }

    public static boolean getPicture(String urlHttp, String path, String picName) throws Exception {
        // 文件对象
        String file = path + picName;
        File f1 = new File(file);

        // 防止文件夹未建立
        f1.mkdirs();

        // 读取
        URL url = new URL(urlHttp);
        BufferedImage img = ImageIO.read(url);

        int skinWidth = img.getWidth();
        int skinHeight = img.getHeight();
        if (!((skinWidth == 64 && skinHeight == 64) || (skinWidth == 64 && skinHeight == 32))) {
            return false;
        }

        // 保存并返回Boolean
        return ImageIO.write(img, "png", f1);
    }

    public static String postPic(String urlHttp, String picName, String picPath) throws Exception {
        File filePath = new File(picPath);
        MediaType mediaType = MediaType.parse("image/png");

        // 构建请求体
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", picName, RequestBody.create(mediaType, filePath))
                .build();

        // 构建请求
        Request request = new Request.Builder()
                .url(urlHttp)
                .post(requestBody)
                .addHeader("User-Agent", "UseBlessingSkinPlugin/1.0")
                .build();

        // 执行请求并获取响应
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP error code: " + response.code() + " | " + response.message());
            }

            return response.body() != null ? response.body().string() : "{}";
        }
    }
}