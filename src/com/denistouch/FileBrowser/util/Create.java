package com.denistouch.FileBrowser.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Smirnov_DS on 07.07.2017.
 */
public class Create {

    public static void Dir(String nameDir) {
        File file = new File(nameDir);
        if (file.mkdirs()) {
            //nameDir = nameDir.substring(0, nameDir.length() - 1);
            //System.out.println(nameDir.substring(nameDir.lastIndexOf("\\") + 1, nameDir.length()) + " - Created");
        } else {
            //System.out.println("Fail");
        }
    }

    public static void File(String name) {
        try {
            OutputStream outputStream = new FileOutputStream(name);
            outputStream.write(' ');
            outputStream.close();
            System.out.println(name.substring(name.lastIndexOf("\\") + 1, name.length()) + " - created");
        } catch (IOException e) {
            System.out.print("Exception");
            System.out.print(e.getMessage());
        }
    }

}
