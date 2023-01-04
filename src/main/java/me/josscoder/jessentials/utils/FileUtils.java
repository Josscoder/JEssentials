package me.josscoder.jessentials.utils;

import java.io.*;
import java.nio.file.Files;

public class FileUtils {

    public static void copy(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            if (!destination.exists()) destination.mkdir();

            String[] files = source.list();
            if (files == null) return;

            for (String file : files) {
                File newSource = new File(source, file);
                File newDestination = new File(destination, file);
                copy(newSource, newDestination);
            }
        } else {
            InputStream inputStream = Files.newInputStream(source.toPath());
            OutputStream outputStream = Files.newOutputStream(destination.toPath());

            byte[] buffer = new byte[1024];

            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();
        }
    }

    public static void delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) return;

            for (File child : files) delete(child);
        }

        file.delete();
    }
}
