package ch.makery.address.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 1 on 15.02.2017.
 */
public class ReplaceLayoutController {

    @FXML
    private TextField replaceOld;
    @FXML
    private TextField replaceNew;
    @FXML
    private Button button;

    private Stage dialogStage;
    private TextArea textArea;

    @FXML
    private void initialize() {
        Node buttonNode = button;
        buttonNode.setDisable(true);
        replaceOld.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (replaceOld.getText().equals("")) {
                    buttonNode.setDisable(true);
                } else if (replaceNew.getText().equals("")) {
                    buttonNode.setDisable(true);
                } else {
                    buttonNode.setDisable(false);
                }
            }
        });
        replaceNew.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (replaceOld.getText().equals("")) {
                    buttonNode.setDisable(true);
                } else if (replaceNew.getText().equals("")) {
                    buttonNode.setDisable(true);
                } else {
                    buttonNode.setDisable(false);
                }
            }
        });

    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setTextArea(TextArea textArea) {
        this.textArea = textArea;
        replaceOld.setText(textArea.getSelectedText());
        if (replaceOld.getText().equals("")) {
            replaceOld.requestFocus();
        } else {
            replaceNew.requestFocus();
        }
    }

    public TextArea getTextArea() {
        return textArea;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            int findTextParts = 0;
            StringBuffer buffer = new StringBuffer();
            Pattern regexp = Pattern.compile(replaceOld.getText(), Pattern.LITERAL);
            Matcher m = regexp.matcher(textArea.getText());
            while (m.find()) {
                m.appendReplacement(buffer, replaceNew.getText());
                findTextParts++;
            }
            m.appendTail(buffer);
            textArea.setText(buffer.toString());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Успех!");
            alert.setHeaderText("Произведена замена (" + replaceOld.getText() + ") на (" + replaceNew.getText() + ").");
            alert.setContentText("Найдено фрагментов (" + replaceOld.getText() + ") в тексте: " + findTextParts);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка!");
            alert.setHeaderText("Не удаётся произвести замену!");
            alert.setContentText("Заменяемый фрагмент отсутствует в тексте!");
            alert.showAndWait();
            replaceOld.requestFocus();
            replaceOld.selectAll();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        Pattern regexp = Pattern.compile(replaceOld.getText(), Pattern.LITERAL);
        Matcher m = regexp.matcher(textArea.getText());
        if (m.find()) {
            dialogStage.close();
            return true;
        } else {
            return false;
        }
    }


}
