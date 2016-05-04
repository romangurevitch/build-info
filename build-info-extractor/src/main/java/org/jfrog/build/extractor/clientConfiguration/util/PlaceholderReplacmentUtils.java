package org.jfrog.build.extractor.clientConfiguration.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tamirh on 04/05/2016.
 */
public class PlaceholderReplacmentUtils {

    public static String reformatRegexp(String sourceString, String destString, Pattern regexPattern) {
        String target = destString;
        Matcher matcher = regexPattern.matcher(sourceString);
        if (matcher.find()) {
            int groupCount = matcher.groupCount();
            for (int i = 1; i <= groupCount; i++) {
                String currentGroup = matcher.group(i);
                currentGroup.replace("\\", "/");
                target = target.replace("{" + i + "}", currentGroup);
            }
        }
        return target;
    }

    public static String pathToRegExp(String path) {
        String wildcard = ".*";
        String newPath = path.replaceAll("\\*", wildcard);
        if (newPath.endsWith("/")) {
            newPath += wildcard;
        }
        else {
            if (newPath.endsWith("\\")) {
                int size = newPath.length();
                if (size > 1 && newPath.substring(size - 2, size - 1) != "\\") {
                    newPath += "\\";
                }
                newPath += wildcard;
            }
        }
        newPath = "^" + newPath + "$";
        return newPath;
    }
}
