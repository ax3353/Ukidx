package org.zk.ip.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 驼峰与下划线互转工具类
 */
public class NamingConverter {

    private static final Pattern PATTERN = Pattern.compile("([a-z])([A-Z]+)");

    public static String convertFieldName(String fieldName) {
        if (fieldName.contains("_")) {
            return underlineToCamel(fieldName);
        } else {
            return camelToUnderline(fieldName);
        }
    }

    public static String camelToUnderline(String name) {
        Matcher matcher = PATTERN.matcher(name);
        while (matcher.find()) {
            name = name.replace(matcher.group(), matcher.group(1) + "_" + matcher.group(2).toLowerCase());
        }
        return name.toLowerCase();
    }

    public static String underlineToCamel(String name) {
        StringBuilder result = new StringBuilder();
        String[] parts = name.split("_");
        for (int i = 0; i < parts.length; i++) {
            if (i == 0) {
                result.append(parts[i]);
            } else {
                result.append(Character.toUpperCase(parts[i].charAt(0))).append(parts[i].substring(1));
            }
        }
        return result.toString();
    }
}
