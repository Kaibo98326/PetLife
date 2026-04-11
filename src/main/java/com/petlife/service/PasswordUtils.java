package com.petlife.service;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    //產生雜湊
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    //驗證密碼
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
