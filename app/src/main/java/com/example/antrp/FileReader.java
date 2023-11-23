package com.example.antrp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileReader {
    public static String readToString(String filePath) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(filePath)) {
            return readToString(inputStream);
        }
    }

    public static String readToString(InputStream inputStream) throws IOException {
        int read = 0;
        byte[] buffer = new byte[8192];
        StringBuilder sb = new StringBuilder();
        while ((read = inputStream.read(buffer)) > 0) {
            sb.append(new String(buffer, 0, read));
        }

        return sb.toString();
    }
}
