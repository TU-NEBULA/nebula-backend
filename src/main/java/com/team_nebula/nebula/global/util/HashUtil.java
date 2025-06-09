package com.team_nebula.nebula.global.util;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
    public static String hashUserId(Long userId){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(userId.toString().getBytes());
            return bytesToHex(encodedHash).substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("UserId Hash Algorithm failed to calculate hash", e);
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
