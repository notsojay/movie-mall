package com.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class URLUtils {

    private URLUtils() {
        // Prevent Instantiation
    }

    public static String extractLastSegmentFromPath(String pathInfo) {
        if (pathInfo == null || pathInfo.isEmpty()) {
            return null;
        } else if (pathInfo.endsWith("/")) {
            // If it ends with a slash, get the portion before the last slash
            int secondLastSlash = pathInfo.lastIndexOf('/', pathInfo.length() - 2);
            if (secondLastSlash != -1) {
                return pathInfo.substring(secondLastSlash + 1, pathInfo.length() - 1);
            }
        } else {
            // If it doesn't end with a slash, get the portion after the last slash
            int lastSlash = pathInfo.lastIndexOf('/');
            if (lastSlash != -1) {
                return pathInfo.substring(lastSlash + 1);
            }
        }
        return null;
    }

    public static String encodeToBase64(String rawData) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(rawData.getBytes(StandardCharsets.UTF_8));
    }

    public static String decodeFromBase64(String encodedData) {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedData);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}
