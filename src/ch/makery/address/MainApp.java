package ch.makery.address;

/**
 * Created by Smirnov_DS on 01.02.2017.
 */

import ch.makery.address.view.FontLayoutController;
import ch.makery.address.view.ReplaceLayoutController;
import ch.makery.address.view.RootLayoutController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.util.Optional;
import java.util.prefs.Preferences;

public class MainApp extends Application {

    public static void main(String[] args) {
        if (args.length != 0)
            startFile = new File(args[0]);
        launch(args);
    }

    public static File startFile;
    private Stage primaryStage;
    private BorderPane rootLayout;
    private String buffer;


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Блокнот");
        initRootLayout();
        System.out.println(MainApp.class.getName());
    }


    /**
     * Constructor
     */
    public MainApp() {

    }

    /**
     * Initialize the root layout
     */

    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);
            primaryStage.setOnCloseRequest(
                    new javafx.event.EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            event.consume();
                            controller.handleExit();
                        }
                    });
            File file;
            if (startFile != null) {
                file = startFile;
            } else {
                file = getPersonFilePath();
            }
            if (file != null  && controller.lastFileOpen(file)) {
                primaryStage.show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * return main stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPersonFilePath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        if (file != null) {
            prefs.put("filePath", file.getPath());
        } else {
            prefs.remove("filePath");
            primaryStage.setTitle("Блокнот");
        }
    }

    public File getPersonFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    public void setLastPath(String filePath) {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        if (filePath != null) {
            prefs.put("lastFilePath", filePath);
        } else {
            prefs.remove("lastFilePath");
        }
    }

    public String getLastPath() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        String filePath = prefs.get("lastFilePath", null);
        if (filePath != null) {
            return filePath;
        } else {
            return null;
        }
    }

    public void setFont(Font font) {
        Preferences preferences = Preferences.userNodeForPackage(MainApp.class);
        if (font.getFamily() != null)
            preferences.put("fontFamily", font.getFamily());
        else
            preferences.remove("fontFamily");
        if (font.getStyle() != null)
            preferences.put("fontStyle", font.getStyle());
        else
            preferences.remove("fontStyle");
        if (String.valueOf(font.getSize()) != null)
            preferences.put("fontSize", String.valueOf(font.getSize()));
        else
            preferences.remove("fontSize");
    }

    public Font getFont() {
        Preferences preferences = Preferences.userNodeForPackage(MainApp.class);
        String family = preferences.get("fontFamily", null);
        String size = preferences.get("fontSize", null);
        String style = preferences.get("fontStyle", null);
        if (family == null) {
            return Font.font("Arial", 12);
        } else if (size == null) {
            return Font.font("Arial", 12);
        } else if (style == null) {
            return Font.font("Arial", 12);
        }
        if (style.contains(" ")) {
            if (!style.equals("Regular")) {
                String styleAr[] = style.split(" ", 2);
                return Font.font(family, FontWeight.findByName(styleAr[0]), FontPosture.findByName(styleAr[1]), Double.valueOf(size));
            }
        }
        return Font.font(family, FontWeight.findByName(style), FontPosture.findByName(style), Double.valueOf(size));
    }

    public void setLabelVisible(boolean visible) {
        Preferences preferences = Preferences.userNodeForPackage(MainApp.class);
        if (String.valueOf(visible) != null) {
            preferences.put("labelVisible", String.valueOf(visible));
        } else {
            preferences.remove("labelVisible");
        }
    }

    public boolean getLabelVisible() {
        Preferences preferences = Preferences.userNodeForPackage(MainApp.class);
        String visible = preferences.get("labelVisible", null);
        if (visible != null)
            return Boolean.valueOf(visible);
        else return false;
    }

    public void setCarretPosition(int position) {
        Preferences preferences = Preferences.userNodeForPackage(MainApp.class);
        if (String.valueOf(position) != null) {
            preferences.put("position", String.valueOf(position));
        } else {
            preferences.remove("position");
        }
    }

    public int getCarretPosition() {
        Preferences preferences = Preferences.userNodeForPackage(MainApp.class);
        String position = preferences.get("position", null);
        if (position != null)
            return Integer.valueOf(position);
        else return 0;
    }

    /**
     * Save text to file
     */
    public void saveFile(String content, File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(content);
        fileWriter.close();
    }

    /**
     * Open file for data
     */
    public String readFile(File file) throws IOException {
        if (file != null) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line, buffer = "";
            if (file.getName().contains("txt")) {
                primaryStage.setTitle("Блокнот - " + file.toString());
            } else {
                primaryStage.setTitle("Блокнот - " + file.getName() + " " +
                        "Внимание!!! Тип файла: <" + file.getName().substring(file.getName().lastIndexOf(".") + 1).toUpperCase() + ">");
            }
            while ((line = reader.readLine()) != null)
                buffer = buffer + line + "\n";
            if (buffer.length() == 0) {
                return "";
            }
            reader.close();
            return buffer.substring(0, buffer.length() - 1);
        } else
            return buffer;
    }

    public String version() {
        String version = "0.9";
        return version;
    }

    public File readBefore(File file) throws IOException {
        if (!file.getName().substring(file.getName().lastIndexOf(".") + 1).toUpperCase().contains("TXT")) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Открыть файл?");
            alert.setHeaderText("Открыть файл?\nТип файла: " + "<" + file.getName().substring(file.getName().lastIndexOf(".") + 1).toUpperCase() + ">");
            alert.setContentText("Файл, который вы собираетесь открыть, не является текстовым и может быть отображен некорректно.");

            ButtonType buttonTypeOpen = new ButtonType("Открыть", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeOpen, buttonTypeCancel);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeCancel) {
                return null;
            } else if (result.get() == buttonTypeOpen) {
                alert.close();
            }
        }
        return file;
    }


    /**
     * load
     */

    public TextArea showReplaceDialog(TextArea textArea) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class
                    .getResource("view/ReplaceLayout.fxml"));
            GridPane pane = (GridPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Заменить");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(pane);
            dialogStage.setScene(scene);
            ReplaceLayoutController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setTextArea(textArea);
            dialogStage.showAndWait();
            textArea = controller.getTextArea();
        } catch (IOException e) {
            e.printStackTrace();

        }
        return textArea;
    }

    public Font showFontDialog(Font font) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/FontLayout.fxml"));
            GridPane pane = (GridPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Шрифт");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(pane);
            dialogStage.setScene(scene);
            FontLayoutController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setFont(font);
            dialogStage.showAndWait();
            font = controller.getFont();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return font;
    }

    public void setBuffer(String buffer) {
        this.buffer = buffer;
    }

    public String getBuffer() {
        return buffer;
    }


}
