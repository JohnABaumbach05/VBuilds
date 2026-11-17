package com.vbuilds.service;

import com.vbuilds.dao.UserDAO;
import com.vbuilds.dto.CreateAccountRequest;
import com.vbuilds.dto.UpdateAccountRequest;
import com.vbuilds.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class UserService {
    private final UserDAO userDAO = new UserDAO();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User getUserByEmail(String email) throws SQLException { return userDAO.getUserByEmail(email); }
    public User getUserById(int id) throws SQLException { return userDAO.getUserById(id); }
    public User getUserByUsername(String username) throws SQLException { return userDAO.getUserByUsername(username); }
    public List<User> getAllUsers() throws SQLException { return userDAO.getAllUsers(); }
    public void deleteUser(int id) throws SQLException { userDAO.deleteUser(id); }
    public void updateUser(User user) throws SQLException { userDAO.updateUser(user); }

    public User login(String username, String password) throws SQLException {
        User user = userDAO.getUserByUsername(username);
        return user != null && password != null &&
                passwordEncoder.matches(password, user.getPasswordHash()) ? user : null;
    }

    public void addUser(CreateAccountRequest request) throws SQLException {
        if (request.getUsername() == null || request.getUsername().isBlank() ||
                request.getEmail() == null || request.getEmail().isBlank() ||
                request.getPassword() == null || request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Username, email, and password of at least 8 characters are required");
        }
        if (userDAO.getUserByUsername(request.getUsername()) != null ||
                userDAO.getUserByEmail(request.getEmail()) != null) {
            throw new IllegalArgumentException("Username or email already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setDateJoined(LocalDate.now().toString());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userDAO.addUser(user);
    }

    public User updateAccount(int userId, UpdateAccountRequest request) throws SQLException {
        User user = userDAO.getUserById(userId);
        if (user == null) throw new IllegalArgumentException("Account not found");
        String username = request.getUsername() == null ? "" : request.getUsername().trim();
        String email = request.getEmail() == null ? "" : request.getEmail().trim();
        String bio = request.getBio() == null ? "" : request.getBio().trim();
        if (username.length() < 3 || username.length() > 30)
            throw new IllegalArgumentException("Username must be between 3 and 30 characters");
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))
            throw new IllegalArgumentException("Invalid email address");
        if (bio.length() > 500) throw new IllegalArgumentException("Bio cannot exceed 500 characters");
        User other = userDAO.getUserByUsername(username);
        if (other != null && other.getId() != userId)
            throw new IllegalArgumentException("Username already taken");
        other = userDAO.getUserByEmail(email);
        if (other != null && other.getId() != userId)
            throw new IllegalArgumentException("Email already in use");

        String newPassword = request.getNewPassword();
        if (newPassword != null && !newPassword.isEmpty()) {
            if (request.getCurrentPassword() == null ||
                    !passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash()))
                throw new IllegalArgumentException("Current password is incorrect");
            if (newPassword.length() < 8)
                throw new IllegalArgumentException("New password must contain at least 8 characters");
            user.setPasswordHash(passwordEncoder.encode(newPassword));
        }
        user.setUsername(username);
        user.setEmail(email);
        user.setBio(bio);
        user.setXUrl(validateSocialUrl(request.getXUrl(), "X", "x.com", "twitter.com"));
        user.setInstagramUrl(validateSocialUrl(request.getInstagramUrl(), "Instagram", "instagram.com"));
        user.setTiktokUrl(validateSocialUrl(request.getTiktokUrl(), "TikTok", "tiktok.com"));
        user.setSnapchatUrl(validateSocialUrl(request.getSnapchatUrl(), "Snapchat", "snapchat.com"));
        userDAO.updateUser(user);
        return user;
    }

    private String validateSocialUrl(String raw, String platform, String... hosts) {
        if (raw == null || raw.isBlank()) return null;
        if (raw.length() > 500) throw new IllegalArgumentException(platform + " URL is too long");
        try {
            java.net.URI uri = new java.net.URI(raw.trim());
            String host = uri.getHost();
            boolean allowed = host != null && java.util.Arrays.stream(hosts)
                    .anyMatch(h -> host.equalsIgnoreCase(h) || host.equalsIgnoreCase("www." + h));
            if (!"https".equalsIgnoreCase(uri.getScheme()) || !allowed ||
                    uri.getUserInfo() != null || uri.getPort() != -1 ||
                    uri.getRawFragment() != null || uri.getRawPath() == null ||
                    uri.getRawPath().equals("/") || uri.getRawPath().isBlank())
                throw new IllegalArgumentException("Invalid " + platform + " profile URL");
            String path = uri.getPath();
            String handle = platform.equals("Snapchat") && path.startsWith("/add/")
                    ? path.substring(5)
                    : platform.equals("TikTok") && path.startsWith("/@")
                    ? path.substring(2)
                    : path.startsWith("/") ? path.substring(1) : "";
            String pattern = platform.equals("Instagram") ? "[A-Za-z0-9._]{1,30}"
                    : platform.equals("TikTok") ? "[A-Za-z0-9._]{1,24}"
                    : platform.equals("Snapchat") ? "[A-Za-z][A-Za-z0-9._-]{2,14}"
                    : "[A-Za-z0-9_]{1,15}";
            if (!handle.matches(pattern) || uri.getRawQuery() != null)
                throw new IllegalArgumentException("Invalid " + platform + " username");
            return uri.toASCIIString();
        } catch (java.net.URISyntaxException e) {
            throw new IllegalArgumentException("Invalid " + platform + " profile URL");
        }
    }

    public String saveProfilePicture(int userId, MultipartFile file) throws IOException, SQLException {
        if (userDAO.getUserById(userId) == null)
            throw new IllegalArgumentException("Account not found");
        if (file == null || file.isEmpty() || file.getSize() > 10L * 1024 * 1024)
            throw new IllegalArgumentException("Select an image no larger than 10 MB");
        String type = file.getContentType();
        String ext;
        if ("image/jpeg".equals(type)) ext = ".jpg";
        else if ("image/png".equals(type)) ext = ".png";
        else if ("image/webp".equals(type)) ext = ".webp";
        else throw new IllegalArgumentException("Only JPG, PNG, and WebP are supported");
        byte[] header;
        try (var input = file.getInputStream()) {
            header = input.readNBytes(12);
        }
        if (header.length < 12) throw new IllegalArgumentException("Invalid image");
        boolean jpeg = (header[0] & 255) == 255 && (header[1] & 255) == 216 &&
                (header[2] & 255) == 255;
        boolean png = (header[0] & 255) == 137 && header[1] == 80 &&
                header[2] == 78 && header[3] == 71;
        boolean webp = new String(header, 0, 4, java.nio.charset.StandardCharsets.US_ASCII)
                .equals("RIFF") &&
                new String(header, 8, 4, java.nio.charset.StandardCharsets.US_ASCII)
                .equals("WEBP");
        if (!((jpeg && ".jpg".equals(ext)) || (png && ".png".equals(ext)) ||
                (webp && ".webp".equals(ext))))
            throw new IllegalArgumentException("Image content does not match its type");
        if (!webp) {
            BufferedImage decoded = ImageIO.read(file.getInputStream());
            if (decoded == null || decoded.getWidth() > 6000 || decoded.getHeight() > 6000)
                throw new IllegalArgumentException("Invalid image or dimensions too large");
        }
        Path directory = Paths.get(System.getProperty("vbuilds.profile-upload-dir",
                "uploads/profiles")).toAbsolutePath().normalize();
        Files.createDirectories(directory);
        String filename = UUID.randomUUID() + ext;
        Path destination = directory.resolve(filename);
        try {
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            String url = "/uploads/profiles/" + filename;
            userDAO.updateProfilePicture(userId, url);
            return url;
        } catch (IOException | SQLException e) {
            Files.deleteIfExists(destination);
            throw e;
        }
    }
}
