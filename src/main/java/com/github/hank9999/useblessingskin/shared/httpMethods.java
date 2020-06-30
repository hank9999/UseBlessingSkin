package com.github.hank9999.useblessingskin.shared;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

final public class httpMethods {
    public static String getUrl(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "UseBlessingSkinPlugin/1.0");
        con.setConnectTimeout(10000);
        con.setReadTimeout(10000);
        con.setDoOutput(true);
        int responseCode = con.getResponseCode();
        if (responseCode == 404 || responseCode == 204) {
            return null;
        }
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
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

        String BOUNDARY = "========7d4a6d158c9";

        URL url = new URL(urlHttp);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // 设置POST请求
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);

        // 设置header参数
        conn.setRequestProperty("User-Agent", "UseBlessingSkinPlugin/1.0");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

        OutputStream out = new DataOutputStream(conn.getOutputStream());

        // 文件
        File file = new File(picPath);

        // 构建Body Header
        String PostBody = "--" +
                BOUNDARY +
                "\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"" + picName + "\"" + "\r\n" +
                "Content-Type: image/png" +
                "\r\n" +
                "\r\n";

        // 将Body Header写入输出流
        out.write(PostBody.getBytes());

        // 读取文件数据
        DataInputStream in = new DataInputStream(new FileInputStream(
                file));

        // 每次读1KB
        byte[] bufferOut = new byte[1024];
        int bytes;

        // 写入输出流
        while ((bytes = in.read(bufferOut)) != -1) {
            out.write(bufferOut, 0, bytes);
        }

        // 添加换行
        out.write("\r\n".getBytes());
        in.close();

        // 定义结尾数据分割线。
        byte[] end_data = ("\r\n" + "--" + BOUNDARY + "--" + "\r\n").getBytes();

        // 添加结尾标识
        out.write(end_data);
        out.flush();
        out.close();

        // 定义BufferedReader输入流来读取URL的响应
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        return response.toString();
    }
}
