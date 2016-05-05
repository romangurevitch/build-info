package org.jfrog.build.extractor.clientConfiguration.util;

import javafx.util.Pair;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tamirh on 04/05/2016.
 */
public class PlaceholderReplacementUtils {

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

    /**
     * @pre targetPath and srcPath are not empty.
     * @pre targetPath Contains "/"
     * @param targetPath the path which the file name will be taken targetDir/targetPath/targetFileName
     * @param srcPath the path which the file name will be replace srcDir/srcPath/srcFileName
     * @return return pair of the target without the file name and the srcPath with the new file name
     * <targetDir/targetPath, srcDir/srcPath/targetFileName>
     */
    public static Pair<String,String> replaceFilesName(String targetPath, String srcPath) {
        String targetDirPath = StringUtils.substringBeforeLast(targetPath, "/");
        String targetFileName = StringUtils.substringAfterLast(targetPath, "/");
        return new Pair<String, String>(targetDirPath, srcPath.contains("/") ?
                StringUtils.substringBeforeLast(srcPath, "/") + "/" + targetFileName :
                targetFileName);
    }
}
