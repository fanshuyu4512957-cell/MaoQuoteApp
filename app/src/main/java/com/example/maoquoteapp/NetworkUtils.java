package com.example.maoquoteapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {

    // 毛选语录API地址（示例，需要替换为实际的API）
    private static final String QUOTE_API_URL = "https://api.example.com/mao_quotes";

    public static String getMaoQuotes() {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(QUOTE_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result.toString();
    }
}