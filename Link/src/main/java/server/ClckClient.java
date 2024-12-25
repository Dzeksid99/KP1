package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClckClient {

    public static String shorten(String originalUrl) throws IOException {
        String encoded = java.net.URLEncoder.encode(originalUrl, "UTF-8");
        String requestUrl = "https://clck.ru/--?url=" + encoded;

        HttpURLConnection conn = (HttpURLConnection) new URL(requestUrl).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(10_000);

        int code = conn.getResponseCode();
        if (code != 200) {
            throw new IOException("clck.ru service responded with code " + code);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line = br.readLine();
            if (line == null || line.isEmpty()) {
                throw new IOException("clck.ru returned empty response");
            }
            return line;
        } finally {
            conn.disconnect();
        }
    }
}
