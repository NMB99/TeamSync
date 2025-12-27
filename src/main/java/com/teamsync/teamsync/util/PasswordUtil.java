package com.teamsync.teamsync.util;

import java.security.SecureRandom;

public class PasswordUtil {

    private static final String Characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&";
    private static final int password_Length = 10;
    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomPassword() {
        StringBuilder password = new StringBuilder(password_Length);
        for (int i = 0; i < password_Length; i++) {
            int idx = random.nextInt(Characters.length());
            password.append(Characters.charAt(idx));
        }
        return password.toString();
    }

}
