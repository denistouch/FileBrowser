package com.denistouch.FileBrowser.util;

import ch.makery.address.MainApp;
import javafx.stage.Stage;
import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by 1 on 09.07.2017.
 */
public class Open {

    public static String File(File file) throws Exception {
        try {
            if (file.getName().contains(".txt") || file.getName().contains(".ini") || file.getName().contains(".java") || file.getName().contains(".log")) {
                MainApp.startFile = file;
                MainApp notePad = new MainApp();
                notePad.start(new Stage());
            } else {
                //Start with default Application
                Desktop desktop = null;
                if (Desktop.isDesktopSupported())
                    desktop = Desktop.getDesktop();
                try {
                    desktop.open(file);
                } catch (IOException e) {
                    return e.getMessage();
                }
            }
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static ArrayList<String> Dir(File file, String filter) {
        ArrayList<String> listString;
        ArrayList<File> fileList;
        ArrayList<File> dirList;
        FilenameFilter filenameFilter;
        if (!filter.equals(""))
            filenameFilter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().contains(filter);
                }
            };
        else
            filenameFilter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return true;
                }
            };
        if (file.listFiles(filenameFilter) != null) {
            listString = new ArrayList<String>();
            fileList = new ArrayList<>();
            dirList = new ArrayList<>();
            for (File file1 : file.listFiles(filenameFilter)) {
                if (file1.isDirectory()) {
                    dirList.add(file1);
                } else if (file1.isFile()) {
                    fileList.add(file1);
                }
            }
            for (File file1 : dirList)
                listString.add(file1.getName());
            for (File file1 : fileList)
                listString.add(file1.getName());
            return listString;
        } else
            return null;
    }

}
