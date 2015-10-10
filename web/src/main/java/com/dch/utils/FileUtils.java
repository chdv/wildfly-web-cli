package com.dch.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pixel on 30.08.2015.
 */
public class FileUtils {

    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                delete(c);
        }
        if (!f.delete())
            logger.error("Failed to delete file: " + f);
    }

    public static List<String> getFileLines(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        List<String> result = new ArrayList<>();
        while((line = br.readLine())!=null) {
            result.add(line);
        }
        br.close();
        return result;
    }

    public static String getFileString(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        StringBuilder result = new StringBuilder();
        while((line = br.readLine())!=null) {
            result.append(line);
        }
        br.close();
        return result.toString();
    }

    public static void write2File(String str, File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(str);
        fileWriter.flush();
        fileWriter.close();
    }

}
