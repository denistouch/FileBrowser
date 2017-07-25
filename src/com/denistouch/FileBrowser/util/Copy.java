package com.denistouch.FileBrowser.util;

import java.io.*;

/**
 * Created by Smirnov_DS on 07.07.2017.
 */
public class Copy {

    public static boolean Dir(final String source, final String destination) {
        //System.out.println("Копируем каталог: " + source);
        final File sourceFile = new File(source);
        final File destinationFile = new File(destination);
        if (sourceFile.exists() && sourceFile.isDirectory() && !destinationFile.exists()) {
            destinationFile.mkdir();
            File nextSourceFile;
            String nextSourceFilename, nextDestinationFilename;
            for (String filename : sourceFile.list()) {
                nextSourceFilename = sourceFile.getAbsolutePath()
                        + File.separator + filename;
                nextDestinationFilename = destinationFile.getAbsolutePath()
                        + File.separator + filename;
                nextSourceFile = new File(nextSourceFilename);
                if (nextSourceFile.isDirectory()) {
                    Dir(nextSourceFilename, nextDestinationFilename);
                } else {
                    File(nextSourceFilename, nextDestinationFilename);
                }
            }
            return true;
        } else {
            return File(source,destination);
        }
    }

    public static boolean File(final String source, final String destination) {
        //System.out.println("Копируем файл: " + source);
        final File sourceFile = new File(source);
        final File destinationFile = new File(destination);
        if (sourceFile.exists() && sourceFile.isFile() && !destinationFile.exists()) {
            try (InputStream inputStream = new FileInputStream(sourceFile);
                 OutputStream outputStream = new FileOutputStream(destinationFile)) {
                byte[] buffer = new byte[1024];
                int bytes;
                while ((bytes = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, bytes);
                }
            } catch (FileNotFoundException e) {
                return false;
            } catch (IOException e) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

}
