package com.group4.rankingmanagementsystem.util;

public class StringUtil {

    public static String removeExtraSpaces(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        // Loại bỏ khoảng trắng ở đầu và cuối chuỗi
        str = str.trim();
        // Thay thế các khoảng trắng thừa giữa các từ bằng một khoảng trắng duy nhất
        return str.replaceAll("\\s+", " ");
    }

}
