package com.github.hank9999.useblessingskin.shared;

import okhttp3.*;
import okhttp3.Response;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

final public class HttpMethods {
    private static final OkHttpClient client = new OkHttpClient();

    public static byte[] get(String url) throws Exception {
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

            ResponseBody body = response.body();

            if (body == null) {
                return null;
            }

            return body.bytes();
        }
    }

    public static String getString(String url) throws Exception {
        byte[] body = get(url);
        return body != null ? new String(body, StandardCharsets.UTF_8) : null;
    }

    public static boolean getPicture(String urlHttp, String path, String picName) throws Exception {

        // 确保文件夹存在
        Path pathDir = Paths.get(path);
        Files.createDirectories(pathDir);

        // 文件对象
        File picFile = new File(path, picName);

        // 读取
        byte[] body = get(urlHttp);
        if (body == null) {
            return false;
        }

        try (InputStream inputStream = new ByteArrayInputStream(body)) {
            BufferedImage img = ImageIO.read(inputStream);
            Boolean imgValid = Utils.checkImgValid(img);

            if (!imgValid) {
                return false; // 图片无效
            }

            // 保存并返回Boolean
            return ImageIO.write(img, "png", picFile);
        }
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