package com.vbuilds.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Cloudinary is opt-in. Local uploads continue to work until all three
 * environment variables are set. Never commit credentials.
 */
public final class ImageStorage {
    private ImageStorage() {}

    private static String setting(String key) {
        String value = System.getenv(key);
        return value == null ? "" : value.trim();
    }

    public static boolean enabled() {
        return !setting("CLOUDINARY_CLOUD_NAME").isEmpty()
                && !setting("CLOUDINARY_API_KEY").isEmpty()
                && !setting("CLOUDINARY_API_SECRET").isEmpty();
    }

    public static String upload(MultipartFile file, String folder) throws IOException {
        if (!enabled()) throw new IllegalStateException("Cloudinary is not configured");
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", setting("CLOUDINARY_CLOUD_NAME"),
                "api_key", setting("CLOUDINARY_API_KEY"),
                "api_secret", setting("CLOUDINARY_API_SECRET"),
                "secure", true
        ));
        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "vbuilds/" + folder,
                            "resource_type", "image",
                            "overwrite", false));
            Object url = result.get("secure_url");
            if (!(url instanceof String) || !isCloudinaryUrl((String) url))
                throw new IOException("Cloudinary returned an invalid image URL");
            return (String) url;
        } catch (Exception e) {
            throw new IOException("Image upload to Cloudinary failed", e);
        }
    }

    public static boolean isCloudinaryUrl(String url) {
        if (url == null || !enabled()) return false;
        try {
            java.net.URI uri = java.net.URI.create(url);
            String path = uri.getRawPath();
            return "https".equals(uri.getScheme())
                    && "res.cloudinary.com".equals(uri.getHost())
                    && uri.getUserInfo() == null && uri.getPort() == -1
                    && uri.getRawQuery() == null && uri.getRawFragment() == null
                    && path != null && path.startsWith("/" + setting("CLOUDINARY_CLOUD_NAME") + "/image/upload/")
                    && url.length() <= 1000;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean validBuildUrl(String url) {
        return url != null && url.length() <= 1000 &&
                ((url.startsWith("/uploads/builds/") && !url.contains("..") && !url.contains("\\"))
                        || isCloudinaryUrl(url));
    }
}
