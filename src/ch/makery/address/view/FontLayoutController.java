package ch.makery.address.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Arrays;

/**
 * Created by Smirnov_DS on 16.02.2017.
 */
public class FontLayoutController {
    @FXML
    private Slider sizeSlider;
    @FXML
    private ComboBox<String> fontBox;
    @FXML
    private ComboBox<String> sizeBox;
    @FXML
    private Label example;
    @FXML
    private CheckBox bold;
    @FXML
    private CheckBox italic;

    private Stage dialogStage;
    private Font font;
    private int[] intSize = new int[97];
    private boolean save = false;


    @FXML
    private void initialize() {
        setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 12));
        example.setFont(getFont());
        fontBox.setValue("");
        fontBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                example.setFont(Font.font(getFontBox(), getBold(), getItalic(), getSizeBox()));
            }
        });


        sizeBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                sizeSlider.setValue(Double.valueOf(sizeBox.getValue()));
                example.setFont(Font.font(getFontBox(), getBold(), getItalic(), getSizeBox()));
            }
        });

        sizeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                sizeBox.getSelectionModel().select(String.format("%.0f", sizeSlider.getValue()));
                example.setFont(Font.font(getFontBox(), getBold(), getItalic(), getSizeBox()));
            }
        });

        bold.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                example.setFont(Font.font(getFontBox(), getBold(), getItalic(), getSizeBox()));
            }
        });

        italic.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                example.setFont(Font.font(getFontBox(), getBold(), getItalic(), getSizeBox()));
            }
        });

    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void setFontBox(String fontBox) {
        this.fontBox.setItems(FXCollections.observableArrayList(Font.getFamilies()));
        this.fontBox.getSelectionModel().select(fontBox);
    }

    private void setBold(boolean bold) {
        this.bold.setSelected(bold);
    }


    private void setItalic(boolean italic) {
        this.italic.setSelected(italic);
    }

    private FontWeight getBold() {
        if (bold.isSelected())
            return FontWeight.BOLD;
        else
            return null;
    }

    private FontPosture getItalic() {
        if (italic.isSelected())
            return FontPosture.ITALIC;
        else
            return null;
    }

    private void setSizeBox(double sizeBox) {
        this.sizeBox.setValue(String.format("%.0f", sizeBox));
        setSizeSlider(sizeBox);
        for (int i = 0; i < 97; i++)
            intSize[i] = i + 2;
        String a = Arrays.toString(intSize);
        String ar[] = a.substring(1, a.length() - 1).split(", ");
        this.sizeBox.setItems(FXCollections.observableArrayList(Arrays.asList(ar)));
    }

    private void setSizeSlider(double size) {
        sizeSlider.setMin(2);
        sizeSlider.setMax(98);
        sizeSlider.setValue(size);
        sizeSlider.setSnapToTicks(true);
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setShowTickMarks(true);
        sizeSlider.setMajorTickUnit(8);
        sizeSlider.setBlockIncrement(1);
    }


    public void setFont(Font font) {
        this.font = font;
        example.setFont(font);
        setFontBox(this.font.getFamily());
        setSizeSlider(this.font.getSize());
        setSizeBox(this.font.getSize());

        if (font.getStyle().contains("Bold"))
            setBold(true);
        else
            setBold(false);
        if (font.getStyle().contains("Italic"))
            setItalic(true);
        else
            setItalic(false);

    }

    public Font getFont() {
        if (save) {
            return Font.font(getFontBox(), getBold(), getItalic(), getSizeBox());
        } else {
            return font;
        }
    }

    private String getFontBox() {
        return fontBox.getValue();
    }


    private double getSizeBox() {
        return Double.valueOf(sizeBox.getValue());
    }


    @FXML
    private void handleSave() {
        save = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        save = false;
        dialogStage.close();
    }
}
