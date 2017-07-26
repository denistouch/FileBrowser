package ch.makery.address.view;

import ch.makery.address.MainApp;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Smirnov_DS on 01.02.2017.
 */
public class RootLayoutController {
    @FXML
    private TextArea textArea;
    @FXML
    private Label label;
    @FXML
    private TextField textField;
    @FXML
    private ToggleButton button;
    @FXML
    private Button up;
    @FXML
    private Button down;
    @FXML
    private CheckMenuItem labelShow;
    @FXML
    private CheckMenuItem wrapping;

    private MainApp mainApp;

    private boolean change;//Переменная отвечающая за изменение документа
    private int matchNumber;// Эти три переменные
    private int match;//необходимы для
    private int index[];//нормального функционирования поиска
    private boolean findCase;


    /**
     * The constructor
     */
    public RootLayoutController() {
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void initialize() {
        Node upNode = up;
        Node downNode = down;
        setLabel();
        matchNumber = 0;
        label.setVisible(labelShow.isSelected());
        textArea.setWrapText(wrapping.isSelected());
        textArea.setStyle("-fx-highlight-fill: yellow; -fx-highlight-text-fill: red");
        if (textField.getText().equals("")) {
            upNode.setDisable(true);
            downNode.setDisable(true);
        } else {
            upNode.setDisable(false);
            downNode.setDisable(false);
        }
        textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                setChange(true);
            }
        });
        textArea.caretPositionProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                setLabel();
                mainApp.setCarretPosition(textArea.getCaretPosition());
            }
        });
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (textField.getText().equals("")) {
                    upNode.setDisable(true);
                    downNode.setDisable(true);
                } else {
                    upNode.setDisable(false);
                    downNode.setDisable(false);
                }
                find();
            }
        });
        button.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!button.isSelected()) {
                    findCase = false;
                    label.setText(String.valueOf(findCase));
                } else {
                    findCase = true;
                    label.setText(String.valueOf(findCase));
                }
            }
        });
        wrapping.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                textArea.setWrapText(wrapping.isSelected());
                if (textArea.isWrapText()) {
                    labelShow.setDisable(true);
                    label.setVisible(false);
                }   else {
                    labelShow.setDisable(false);
                    label.setVisible(true);
                }
                //деактивировать labelShow и скрывать label
            }
        });
        labelShow.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                label.setVisible(labelShow.isSelected());
                mainApp.setLabelVisible(label.isVisible());
            }
        });
        label.visibleProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                labelShow.setSelected(label.isVisible());
            }
        });
    }


    @FXML
    private void handleNew() {
        if (!textArea.getText().trim().isEmpty()) {
            if (change) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Новый документ");
                alert.setHeaderText("Сохранить текущий документ?");
                alert.setContentText("При нажатии кнопки отмена, документ не будет закрыт\n" +
                        "и вы сможете продолжить редактирование");

                ButtonType buttonTypeSave = new ButtonType("Сохранить");
                ButtonType buttonTypeNotSave = new ButtonType("Не сохранять");
                ButtonType buttonTypeCancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(buttonTypeSave, buttonTypeNotSave, buttonTypeCancel);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == buttonTypeSave) {
                    handleSave();
                } else if (result.get() == buttonTypeCancel) {
                    alert.close();
                    return;
                }
            }
        }
        mainApp.setPersonFilePath(null);
        textArea.setFont(mainApp.getFont());
        textArea.clear();
        label.setVisible(mainApp.getLabelVisible());
        setChange(false);
    }

    /**
     * Open file and load her into textArea
     */
    @FXML
    private void handleOpen() {//ну пошла жара
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Открыть файл");
            FileChooser.ExtensionFilter filter1 = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            FileChooser.ExtensionFilter filter2 = new FileChooser.ExtensionFilter("All files (*.*)", "*.*");
            fileChooser.getExtensionFilters().addAll(filter1, filter2);
            File userDirectory;
            if (mainApp.getLastPath() != null) {
                userDirectory = new File(mainApp.getLastPath());
            } else {
                userDirectory = new File(System.getProperty("user.home"));
            }
            fileChooser.setInitialDirectory(userDirectory);
            File dataFile = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
            mainApp.setBuffer(textArea.getText());
            if (dataFile != null) {
                mainApp.setBuffer(textArea.getText());
                textArea.setText(mainApp.readFile(mainApp.readBefore(dataFile)));
                if (!textArea.getText().equals(mainApp.getBuffer())) {
                    mainApp.setPersonFilePath(dataFile);
                    mainApp.setLastPath(dataFile.getPath().substring(0, dataFile.getPath().length() - dataFile.getName().length()));
                }
            }
        } catch (IOException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Не удаётся открыть файл");
            alert.setHeaderText("Программе не удаётся открыть файл");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        textArea.setFont(mainApp.getFont());
        label.setVisible(mainApp.getLabelVisible());
        setChange(false);
    }


    /**
     * Open last file
     */
    public boolean lastFileOpen(File file) {
        try {
            File dataFile = file;
            dataFile = mainApp.readBefore(dataFile);
            if (dataFile != null) {
                mainApp.setBuffer(textArea.getText());
                mainApp.setPersonFilePath(file);
                mainApp.setLastPath(dataFile.getPath().substring(0, dataFile.getPath().length() - dataFile.getName().length()));
                textArea.setText(mainApp.readFile(dataFile));
                textArea.positionCaret(mainApp.getCarretPosition());
            }   else
                return false;

        } catch (IOException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Не удаётся открыть файл");
            alert.setHeaderText("Программе не удаётся открыть файл");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return false;
        }
        textArea.setFont(mainApp.getFont());
        label.setVisible(mainApp.getLabelVisible());
        setChange(false);
        return true;
    }

    /**
     * Saving change in file
     */
    @FXML
    private void handleSave() {
        File dataFile = mainApp.getPersonFilePath();
        if (dataFile != null) {
            try {
                mainApp.saveFile(textArea.getText(), dataFile);
            } catch (IOException e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Не удаётся сохранить файл");
                alert.setHeaderText("Программе не удаётся сохранить файл");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        } else {
            handleSaveAs();
        }
        setChange(false);
    }

    /**
     * Saving file as other file
     */
    @FXML
    private void handleSaveAs() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить как");
            FileChooser.ExtensionFilter filter1 = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            FileChooser.ExtensionFilter filter2 = new FileChooser.ExtensionFilter("All files (*.*)", "*.*");
            fileChooser.getExtensionFilters().addAll(filter1, filter2);
            File userDirectory;
            if (mainApp.getLastPath() != null) {
                userDirectory = new File(mainApp.getLastPath());
            } else {
                userDirectory = new File(System.getProperty("user.home"));
            }
            fileChooser.setInitialDirectory(userDirectory);
            File dataFile = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
            if (dataFile != null) {
                mainApp.saveFile(textArea.getText(), dataFile);
                mainApp.setPersonFilePath(dataFile);
                mainApp.setLastPath(dataFile.getPath().substring(0, dataFile.getPath().length() - dataFile.getName().length()));
            }
        } catch (IOException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Не удаётся сохранить файл");
            alert.setHeaderText("Программе не удаётся сохранить файл");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        setChange(false);
    }


    /**
     * Exit of program and if changed show saving dialog before exit
     */
    @FXML
    public void handleExit() {
        if (!textArea.getText().trim().isEmpty()) {//Check if textArea on empty
            if (change) {//check textArea on change
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Сохранить файл?");
                alert.setHeaderText("Сохранить файл?");
                alert.setContentText("При нажатии кнопки отмена, документ не будет закрыт\n" +
                        "и вы сможете продолжить редактирование");
                ButtonType buttonTypeSave = new ButtonType("Сохранить");
                ButtonType buttonTypeNotSave = new ButtonType("Не сохранять");
                ButtonType buttonTypeCancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(buttonTypeSave, buttonTypeNotSave, buttonTypeCancel);
                mainApp.setCarretPosition(textArea.getCaretPosition());
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == buttonTypeSave) {
                    handleSave();
                    mainApp.getPrimaryStage().close();
                } else if (result.get() == buttonTypeNotSave) {
                    mainApp.getPrimaryStage().close();
                } else if (result.get() == buttonTypeCancel) {
                    alert.close();
                    return;
                }
            }
        }
        //System.exit(0);
        mainApp.getPrimaryStage().close();
    }

    /**
     * Undo last operation
     */
    @FXML
    private void handleUndo() {
        textArea.undo();
    }

    /**
     * Redo last operation
     */
    @FXML
    private void handleRedo() {
        textArea.redo();
    }

    /**
     * Cut a part of selected text
     */
    @FXML
    private void handleCut() {
        textArea.cut();
    }

    /**
     * Copy a part of selected text
     */
    @FXML
    private void handleCopy() {
        textArea.copy();
    }

    /**
     * Paste a part of text in textArea
     */
    @FXML
    private void handlePaste() {
        textArea.paste();
    }

    /**
     * delete selected text
     */
    @FXML
    private void handleDelete() {
        textArea.deleteText(textArea.getSelection());
    }

    /**
     * finding a part of text in textArea
     */
    private void find() {// worked very well
        String findWord, buffer;
        if (findCase) {
            findWord = textField.getText().toLowerCase();
            buffer = textArea.getText().toLowerCase();
        } else {
            findWord = textField.getText();
            buffer = textArea.getText();
        }
        if (findWord.equals("")) {
            textArea.deselect();
        } else {
            int word = buffer.split(" ").length;
            int index[] = new int[word];
            match = 0;
            Pattern findPattern = Pattern.compile(findWord, Pattern.LITERAL);
            Matcher findMatcher = findPattern.matcher(buffer);
            while (findMatcher.find()) {
                index[match] = findMatcher.start();
                match++;
            }
            matchNumber = 0;
            this.index = index;
            if (match > 0) {
                textArea.selectRange(index[matchNumber] + findWord.length(), index[matchNumber]);
            } else {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Не удается найти фрагмент!");
                alert.setHeaderText("По запросу: (" + findWord + ")");
                alert.setContentText("Совпадений не найдено.");
                alert.showAndWait();
                textField.requestFocus();
                textField.setText(textField.getText(0, textField.getText().length() - 1));
            }
        }
    }

    /**
     * request focus on textField and get him selected text
     */
    @FXML
    private void handleFindSelect() {
        if (!textArea.getSelectedText().equals(""))
            textField.setText(textArea.getSelectedText());
        textField.requestFocus();
        textField.positionCaret(textField.getLength());
    }

    /**
     * next match findWord in buffered text
     */
    @FXML
    private void handleDown() {
        String findWord;
        if (findCase) {
            findWord = textField.getText().toLowerCase();
        } else {
            findWord = textField.getText();
        }
        if (matchNumber < match - 1) {
            matchNumber++;
            textArea.selectRange(index[matchNumber] + findWord.length(), index[matchNumber]);
        } else {
            matchNumber = match - 1;
            textArea.selectRange(index[matchNumber] + findWord.length(), index[matchNumber]);
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Не удается найти фрагмент!");
            alert.setHeaderText("По запросу: (" + findWord + ")");
            alert.setContentText("Найдено совпадений: " + (matchNumber + 1) + " .");
            alert.showAndWait();
            textField.requestFocus();
        }
    }

    /**
     * previous  match findWord in buffered text
     */
    @FXML
    private void handleUp() {
        String findWord;
        if (findCase) {
            findWord = textField.getText().toLowerCase();
        } else {
            findWord = textField.getText();
        }
        if (matchNumber < 1) {
            matchNumber = 0;
            textArea.selectRange(index[matchNumber] + findWord.length(), index[matchNumber]);
        } else {
            matchNumber--;
            textArea.selectRange(index[matchNumber] + findWord.length(), index[matchNumber]);
        }
    }

    /**
     * replace textArea (oldWord,newWord)
     */
    @FXML
    private void handleReplace() {
        textArea = mainApp.showReplaceDialog(textArea);
    }

    /**
     * go to selected string
     */
    @FXML
    private void handleGoToLine() {//переделал при помощи регулярных выражений
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Перейти");
        dialog.setHeaderText("Переход на строку");
        String findWord = "\n";
        String buffer = textArea.getText();
        int index[] = new int[buffer.split(findWord).length];
        int match = 0;
        int matchNumber = 0;
        Pattern findPattern = Pattern.compile(findWord);
        Matcher findMatcher = findPattern.matcher(buffer);
        while (findMatcher.find()) {
            index[match] = findMatcher.start();
            match++;
        }
        match++;
        dialog.setContentText("Всего строк = " + match);
        Optional<String> result = dialog.showAndWait();
        boolean showAlert = false;
        try {
            System.out.println(dialog.getResult());
            if (dialog.getResult() == null)
                showAlert = true;
            else if (Integer.parseInt(dialog.getResult()) > 0 && Integer.parseInt(dialog.getResult()) <= match) {
                matchNumber = Integer.parseInt(dialog.getResult());
            } else {
                showAlert = true;
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Ошибка!");
                alert.setHeaderText("Неверный номер строки!");
                alert.setContentText("Номер строки должен быть целым числом от 1 до " + match);
                alert.showAndWait();
            }
            if (matchNumber == 1) {
                textArea.positionCaret(0);
            } else if (matchNumber <= match) {
                textArea.positionCaret(index[matchNumber - 2] + findWord.length());
            }
        } catch (Exception e) {
            if (!showAlert) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Ошибка!");
                alert.setHeaderText("Номер строки должен быть положительным числом");
                alert.setContentText("Введите номер строки в правильном формате\n" +
                        "Например: 1");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handleSelectAll() {
        textArea.selectAll();
    }

    /**
     * Paste in focus field NOW time and date
     */
    @FXML
    private void handleTimeAndDate() {
        textArea.replaceSelection(new Date(System.currentTimeMillis()).toString());
    }

    /**
     * Change format of visible text in textArea
     */
    @FXML
    private void handleFont() {
        textArea.setFont(mainApp.showFontDialog(textArea.getFont()));
        mainApp.setFont(textArea.getFont());
    }

    /**
     * Open ReadMe for Users
     */
    @FXML
    private void handleUserGuide() {
        //debugMessage();
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Инструкция");
        alert.setHeaderText("Будь молодцом, и используй это в благих целях");
        alert.setContentText("Шучу, поступай с этим так, как сочтешь нужным");
        alert.showAndWait();
    }


    /**
     * Show window "About Program"
     */
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("О программе");
        alert.setHeaderText("Программа NotePad v" + mainApp.version());
        alert.setContentText("Разработчик: SmirnovDS\n@null_ds");
        alert.setGraphic(new ImageView(new Image(MainApp.class.getResourceAsStream("icon/notepad.png"))));
        alert.showAndWait();
    }

    /**
     * service util for
     *
     * @param change
     */
    private void setChange(boolean change) {
        this.change = change;
    }

    /**
     * Show in field label current CaretPosition in format String and Column
     */
    private void setLabel() {
        String textArray[] = textArea.getText().split("\n");
        String bufferArray[] = textArea.getText(textArea.getCaretPosition(), textArea.getText().length()).split("\n");
        String text = textArea.getText();
        int string = textArray.length - bufferArray.length + 1;
        int column = textArea.getCaretPosition() - text.lastIndexOf("\n", textArea.getCaretPosition() - 1);
        label.setText(String.valueOf(string));
        label.setText("Строка: " + string
                + " Столбец: " + column);
    }


    /**
     * Show message about disable function
     */
    private void debugMessage() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("ВНИМАНИЕ!!!");
        alert.setHeaderText("Эта фукция пока что не реализована автором");
        alert.setContentText("Экспериментальная версия!!!\n");
        alert.showAndWait();
    }

}
