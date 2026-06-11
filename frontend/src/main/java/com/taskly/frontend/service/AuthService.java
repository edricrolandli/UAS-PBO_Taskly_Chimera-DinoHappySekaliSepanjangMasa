package com.taskly.frontend.service;

import com.taskly.frontend.config.ApiConfig;
import com.taskly.frontend.model.JwtResponse;
import com.taskly.frontend.model.UserResponse;
import com.taskly.frontend.util.HttpClientUtil;
import java.util.Map;

public class AuthService {
    private static String token;
    private static String currentEmail;
    private static String currentFullName;

    public static UserResponse register(String fullName, String email, String password, String confirmPassword) throws Exception {
        Map<String, String> body = Map.of(
                "fullName", fullName,
                "email", email,
                "password", password,
                "confirmPassword", confirmPassword
        );
        return HttpClientUtil.sendPost(ApiConfig.BASE_URL + "/users/register", body, UserResponse.class, null);
    }

    public static JwtResponse login(String email, String password) throws Exception {
        Map<String, String> body = Map.of("email", email, "password", password);
        JwtResponse response = HttpClientUtil.sendPost(ApiConfig.BASE_URL + "/users/login", body, JwtResponse.class, null);
        token = response.getToken();
        currentEmail = email;
        currentFullName = response.getFullName();
        return response;
    }

    public static String getToken() { return token; }
    
    public static String getCurrentUser() {
        return currentEmail != null ? currentEmail.split("@")[0] : "User";
    }

    public static String getCurrentUserFullName() {
        if (currentFullName != null && !currentFullName.isBlank()) return currentFullName;
        return getCurrentUser();
    }

    public static void logout() {
        token = null;
        currentEmail = null;
        currentFullName = null;
    }
}

