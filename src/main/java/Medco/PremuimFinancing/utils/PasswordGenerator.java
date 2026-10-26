package Medco.PremuimFinancing.utils;

import java.security.SecureRandom;

public class PasswordGenerator {
    private static final SecureRandom random = new SecureRandom();
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";

    public static String generatePassword() {
        StringBuilder password = new StringBuilder();
        
        // 1 capital letter (first letter)
        String firstLetterCapital = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        password.append(firstLetterCapital.charAt(random.nextInt(firstLetterCapital.length())));
        
        // 4 small letters
        for (int i = 0; i < 4; i++) {
            password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        }
        
        // 3 numbers
        for (int i = 0; i < 3; i++) {
            password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        }
        
        return password.toString();
    }
}
