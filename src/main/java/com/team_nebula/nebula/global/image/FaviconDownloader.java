package com.team_nebula.nebula.global.image;

import org.apache.commons.io.IOUtils;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class FaviconDownloader {
    public static File downloadFavicon(String faviconUrl) {
        try {
            URL url = new URL(faviconUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                File tempFile = File.createTempFile("favicon", ".png");

                try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                    IOUtils.copy(inputStream, outputStream);
                }

                return tempFile;
            } else {
                throw new IOException("Failed to fetch favicon: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Favicon download failed", e);
        }
    }
}
