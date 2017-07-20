package com.denistouch.FileBrowser.util;

import com.denistouch.FileBrowser.MainApp;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by 1 on 14.07.2017.
 */
public class Icon {

    public static javax.swing.Icon getJSwingIconFromFileSystem(File file) {
        FileSystemView view = FileSystemView.getFileSystemView();
        javax.swing.Icon icon = view.getSystemIcon(file);
        return icon;
    }

    public static Image jswingIconToImage(javax.swing.Icon jswingIcon) {
        BufferedImage bufferedImage = new BufferedImage(jswingIcon.getIconWidth(), jswingIcon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        jswingIcon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    public static Image getFileIcon(String fname) {
        Image fileIcon;
        javax.swing.Icon jswingIcon;
        File file = new File(fname);
        if (file.exists()) {
            jswingIcon = getJSwingIconFromFileSystem(file);
        } else {
            jswingIcon = null;
        }
        if (jswingIcon != null) {
            fileIcon = jswingIconToImage(jswingIcon);
        }   else
            fileIcon = new Image(MainApp.class.getResourceAsStream("icon/icon.png"));
        return fileIcon;
    }
}
