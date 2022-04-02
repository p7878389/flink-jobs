package com.shareworks;

import java.io.*;
import java.util.stream.Collectors;

/**
 * @author martin.peng
 */
public class PropertiesUtils {

    public static String readFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException("找不到指定文件 path " + path);
        }
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            return bufferedReader.lines().collect(Collectors.joining());
        } catch (IOException e) {
            throw e;
        }
    }
}
