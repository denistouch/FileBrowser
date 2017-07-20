package com.denistouch.FileBrowser.util;

import java.io.File;

/**
 * Created by 1 on 09.07.2017.
 */
public class Delete {

    public static boolean Delete(File file) {
        if (!file.exists())
            return false;
        if (file.isDirectory()) {
            for (File fileDelete : file.listFiles())
                Delete(fileDelete);
            file.delete();
        } else {
            file.delete();
        }
        return true;
    }

}
