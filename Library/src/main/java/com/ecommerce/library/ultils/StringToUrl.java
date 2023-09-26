package com.ecommerce.library.ultils;


import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringToUrl {

    public static String removeAccent(String s) {

        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }
    public  static String convertSpaceToSlash(String input) {
        if (input == null) {
            return null;
        }

        // Sử dụng phương thức replaceAll để thay thế khoảng trắng bằng "/"
        String converted = input.replaceAll(" ", "-");

        return converted;
    }


}
