package com.dch.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Created by dcherdyntsev on 30.08.2015.
 */
public class ZipUtils {

    public static void unzip(File input, String outDir) throws IOException {
        new File(outDir).mkdir();

        ZipFile zipFile = new ZipFile(input);
        Enumeration<?> enu = zipFile.entries();
        while (enu.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) enu.nextElement();

            String name = zipEntry.getName();
            long size = zipEntry.getSize();
            long compressedSize = zipEntry.getCompressedSize();

//            System.out.printf("name: %-20s | size: %6d | compressed size: %6d\n", name, size, compressedSize);

            File file = new File(outDir + File.separator + name);
            if (name.endsWith("/")) {
                file.mkdirs();
                continue;
            }

            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }

            InputStream is = zipFile.getInputStream(zipEntry);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = is.read(bytes)) >= 0) {
                fos.write(bytes, 0, length);
            }
            is.close();
            fos.close();

        }
        zipFile.close();
    }

    public static void unzip(InputStream inputStream, String outDir) throws IOException {
        new File(outDir).mkdir();

        ZipInputStream zipFile = new ZipInputStream(inputStream);

        ZipEntry zipEntry = null;
        while ((zipEntry=zipFile.getNextEntry())!=null) {
            String name = zipEntry.getName();
            long size = zipEntry.getSize();
            long compressedSize = zipEntry.getCompressedSize();

//            System.out.printf("name: %-20s | size: %6d | compressed size: %6d\n", name, size, compressedSize);

            File file = new File(outDir + File.separator + name);
            if (name.endsWith("/")) {
                file.mkdirs();
                continue;
            }

            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }

            byte[] buffer = new byte[1024];
            FileOutputStream fos = new FileOutputStream(file);
            int len;
            while ((len = zipFile.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
        }
        zipFile.close();
    }

}
