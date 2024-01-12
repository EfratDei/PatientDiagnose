package com.example.project;

public class checkInput {
    public static String getErrorMsg_username(String username){
        if (username.length() > 8 || username.length() < 6)
            return "The username length isn't between 6-8";
        if (!checkUsername(username))
            return "The username has more than two digits or have not an english character!";
        return "Valid Username";
    }

    public static String getErrorMsg_password(String password){
        if (password.length() > 10 || password.length() < 8)
            return "The password length isn't between 8-10";
        if (!checkPassword(password))
            return "The password doesn't have special letter/digit/letter!";
        return "Valid Password";
    }

    public static boolean checkUsername(String username) {
        int digitCount = 0;
        for (int i = 0; i < username.length(); i++) {
            if (username.charAt(i) >= '0' && username.charAt(i) <= '9')
                digitCount++;
            else if (!(username.charAt(i) >= 'a' && username.charAt(i) <= 'z' ||
                    username.charAt(i) >= 'A' && username.charAt(i) <= 'Z'))
                return false;
        }
        return digitCount <= 2;
    }

    public static boolean checkPassword(String password) {
        boolean hasLetter = false, hasDigit = false, hasSpecialChar = false;
        for (int i = 0; i < password.length(); i++) {
            if (password.charAt(i) >= '0' && password.charAt(i) <= '9')
                hasDigit = true;
            else if (password.charAt(i) >= 'a' && password.charAt(i) <= 'z' ||
                    password.charAt(i) >= 'A' && password.charAt(i) <= 'Z')
                hasLetter = true;
            else if (password.charAt(i) >= '!' && password.charAt(i) <= '*')
                hasSpecialChar = true;
        }
        return hasLetter && hasDigit && hasSpecialChar;
    }

}
