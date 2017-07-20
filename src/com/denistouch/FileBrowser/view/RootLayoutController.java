package com.denistouch.FileBrowser.view;

import com.denistouch.FileBrowser.MainApp;
import com.denistouch.FileBrowser.util.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by Smirnov_DS on 12.07.2017.
 */
public class RootLayoutController {

    @FXML
    private ListView listFile;
    @FXML
    private ListView listFavorite;
    @FXML
    private TextField pathField;
    @FXML
    private TextField findField;
    @FXML
    private ImageView image;
    /*@FXML
    private ProgressIndicator loadIndicator;*/
    @FXML
    private ImageView up;
    @FXML
    private Label fileInfo;
    @FXML
    private ContextMenu contextMenu;
    @FXML
    private MenuItem openItem;
    @FXML
    private MenuItem newItem;
    @FXML
    private MenuItem cutItem;
    @FXML
    private MenuItem copyItem;
    @FXML
    private MenuItem pasteItem;
    @FXML
    private MenuItem renameItem;
    @FXML
    private MenuItem deleteItem;

    private File path;
    private File tmp;
    private Boolean newFolder;
    private ArrayList<String> listRoots;
    private ArrayList<String> favoritePath;
    private final String pc = "��� ���������";
    private boolean cuted;
    private MainApp mainApp;

    public RootLayoutController() {
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void initialize() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                listFile.requestFocus();
                listRoots = new ArrayList<String>();
                for (File file : File.listRoots())
                    listRoots.add(FileSystemView.getFileSystemView().getSystemIcon(file) + " " + file.getAbsolutePath());
                listFile.setItems(FXCollections.observableArrayList(listRoots));
            }
        });
        pathField.setText(pc);
        fileInfo.setText(null);
        up.setImage(new Image(MainApp.class.getResourceAsStream("icon/up.png")));
        image.setImage(new Image(MainApp.class.getResourceAsStream("icon/explorer.png")));
        pathField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    setOnGoToHandler();
                }
            }
        });

        pathField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (path != null) {
                    if (pathField.isFocused())
                        pathField.setText(path.getAbsolutePath());
                    else if (path.getParent() != null)
                        pathField.setText(path.getName());
                    else
                        pathField.setText(FileSystemView.getFileSystemView().getSystemIcon(path) + " " + path.getAbsolutePath());
                } else
                    pathField.setText(pc);
            }
        });

        findField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                //if (findField.getText() == null)
                if (path != null)
                    listFile.setItems(FXCollections.observableArrayList(Open.Dir(path, findField.getText().toLowerCase())));
            }
        });

        listFile.itemsProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (path != null)
                    if (path.getParent() != null)
                        pathField.setText(path.getName());
                    else
                        pathField.setText(FileSystemView.getFileSystemView().getSystemIcon(path) + " " + path.getAbsolutePath());
            }
        });

        listFile.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                listFile.setEditable(false);
                if (event.getCode() == KeyCode.ENTER) {
                    setOnOpenHandler();
                } else if (event.getCode() == KeyCode.ESCAPE) {
                    listFile.getSelectionModel().clearSelection();
                } else if (event.getCode() == KeyCode.LEFT)
                    setOnUpHandler();
            }
        });

        listFile.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (listFile.getSelectionModel().getSelectedItem() == null)
                    fileInfo.setText(null);
                else {
                    String pathString;
                    File tmp;
                    if (path == null) {
                        pathString = listFile.getSelectionModel().getSelectedItem().toString().substring(listFile.getSelectionModel().getSelectedItem().toString().lastIndexOf(" ") + 1);
                        tmp = new File(pathString);
                        fileInfo.setText(tmp.getAbsolutePath() +  "\n" + FileSystemView.getFileSystemView().getSystemIcon(tmp));
                    } else if (path.getParent() == null) {
                        pathString = path.getAbsolutePath() + listFile.getSelectionModel().getSelectedItem();
                        tmp = new File(pathString);
                        fileInfo.setText(tmp.getName() +  "\n" + FileSystemView.getFileSystemView().getSystemIcon(tmp));
                    } else {
                        pathString = path.getAbsolutePath() + File.separator + listFile.getSelectionModel().getSelectedItem();
                        tmp = new File(pathString);
                        fileInfo.setText(tmp.getName() +  "\n" + FileSystemView.getFileSystemView().getSystemIcon(tmp));
                    }
                }
            }
        });

        listFile.setCellFactory(param -> new TextFieldListCell<String>() {
            private ImageView imageView = new ImageView();
            private TextField textField = new TextField();

            {
                textField.setOnAction(event -> {
                    commitEdit(getItem());
                });
                textField.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
                    if (event.getCode() == KeyCode.ESCAPE) {
                        cancelEdit();
                    }
                });
            }

            @Override
            public void startEdit() {
                super.startEdit();
                textField.setText(getItem());
                setText(null);
                setGraphic(textField);
                textField.selectAll();
                textField.requestFocus();
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                if (newFolder) {
                    setText(null);
                    setGraphic(null);
                    listFile.setItems(FXCollections.observableArrayList(Open.Dir(path, "")));
                } else {
                    setText(getItem());
                    if (path == null) {
                        imageView.setImage(Icon.getFileIcon(getItem().substring(getItem().lastIndexOf(" ") + 1)));
                    } else if (path.getParent() == null && new File(path.getAbsolutePath() + File.separator + getItem()).isDirectory())
                        imageView.setImage(new Image(MainApp.class.getResourceAsStream("icon/folder.png")));
                    else {
                        imageView.setImage(Icon.getFileIcon(path.getAbsolutePath() + File.separator + getItem()));
                    }
                    setText(getItem());
                    setGraphic(imageView);
                }
            }

            @Override
            public void commitEdit(String name) {
                super.commitEdit(name);
                setText(textField.getText());
                if (path == null) {
                    imageView.setImage(Icon.getFileIcon(getItem().substring(getItem().lastIndexOf(" ") + 1)));
                } else if (path.getParent() == null && new File(path.getAbsolutePath() + File.separator + getItem()).isDirectory()) {
                    if (newFolder) {
                        //if (!new File(path.getAbsolutePath() + getText()).mkdir())
                            //throw new IOException("����� �� �������, ����� ����� ��� ����������.");
                            //System.out.println("Dir not created " + getText());
                    } else {
                        new File(path.getAbsolutePath() + name).renameTo(new File(path.getAbsolutePath() + getText()));
                    }
                    imageView.setImage(new Image(MainApp.class.getResourceAsStream("icon/folder.png")));
                } else {
                    if (newFolder) {
                        //if (!new File(path.getAbsolutePath() + File.separator + getText()).mkdir())
                            //throw new IOException("����� �� �������, ����� ����� ��� ����������.");
                            //System.out.println("Dir not created " + getText());
                    } else {
                        new File(path.getAbsolutePath() + name).renameTo(new File(path.getAbsolutePath() + getText()));
                    }
                    imageView.setImage(Icon.getFileIcon(path.getAbsolutePath() + File.separator + getItem()));
                }
                cancelEdit();
                setGraphic(imageView);
                listFile.setItems(FXCollections.observableArrayList(Open.Dir(path, "")));
                listFile.refresh();
            }

            @Override
            public void updateItem(String name, boolean empty) {
                textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        if (!textField.isFocused())
                            cancelEdit();
                    }
                });
                super.updateItem(name, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else if (isEditing()) {
                    textField.setText(name);
                    setText(null);
                    setGraphic(textField);
                } else {
                    //if (pathField.getText().equals(pc)) {
                    if (path == null) {
                        imageView.setImage(Icon.getFileIcon(name.substring(name.lastIndexOf(" ") + 1)));
                    } else if (path.getParent() == null && new File(path.getAbsolutePath() + File.separator + getItem()).isDirectory())
                        imageView.setImage(new Image(MainApp.class.getResourceAsStream("icon/folder.png")));
                    else {
                        imageView.setImage(Icon.getFileIcon(path.getAbsolutePath() + File.separator + name));
                    }
                    setText(name);
                    setGraphic(imageView);
                }
                setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        listFile.setEditable(false);
                        if (getItem() == null) {
                            listFile.getSelectionModel().clearSelection();
                        } else {
                            if (event.getButton() == MouseButton.PRIMARY &&
                                    listFile.getSelectionModel().getSelectedItem() != null && event.getClickCount() == 2) {
                                setOnOpenHandler();
                            }
                        }
                    }
                });
            }
        });

        contextMenu.showingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue && listFile.getSelectionModel().getSelectedItem() != null) {
                    //System.out.println("true " + listFile.getSelectionModel().getSelectedItem());
                    if (path == null) {
                        openItem.setVisible(true);
                        newItem.setVisible(false);
                        cutItem.setVisible(false);
                        copyItem.setVisible(false);
                        pasteItem.setVisible(false);
                        renameItem.setVisible(false);
                        deleteItem.setVisible(false);
                        //System.out.println("Open.visible(true)\n" + "Other.visible(false)");
                    } else {
                        //System.out.println("all.visible(true)");
                        openItem.setVisible(true);
                        newItem.setVisible(true);
                        cutItem.setVisible(true);
                        copyItem.setVisible(true);
                        pasteItem.setVisible(true);
                        renameItem.setVisible(true);
                        deleteItem.setVisible(true);
                    }
                } else if (newValue && listFile.getSelectionModel().getSelectedItem() == null) {
                    //System.out.println("true " + "null");
                    if (path == null) {
                        //System.out.println("all.visible(false)");
                        openItem.setVisible(false);
                        newItem.setVisible(false);
                        cutItem.setVisible(false);
                        copyItem.setVisible(false);
                        pasteItem.setVisible(false);
                        renameItem.setVisible(false);
                        deleteItem.setVisible(false);
                    } else {
                        openItem.setVisible(false);
                        newItem.setVisible(true);
                        cutItem.setVisible(false);
                        copyItem.setVisible(false);
                        pasteItem.setVisible(tmp!=null);
                        renameItem.setVisible(false);
                        deleteItem.setVisible(false);
                        //System.out.println("new.visible(true)\n" + "paste.visible(true)\n" + "other.visible(false)");
                    }
                } else {
                    //System.out.println();
                }
            }
        });

    }


    @FXML
    private void setOnOpenHandler() {
        //��� ������ ��� ������� ����������� �� ��������� � ������
        String pathString;
        //if (pathField.getText().equals(pc))
        if (path == null)
            pathString = listFile.getSelectionModel().getSelectedItem().toString().substring(listFile.getSelectionModel().getSelectedItem().toString().lastIndexOf(" ") + 1);
        else if (path.getParent() == null)
            pathString = path.getAbsolutePath() + listFile.getSelectionModel().getSelectedItem();
        else
            pathString = path.getAbsolutePath() + File.separator + listFile.getSelectionModel().getSelectedItem();
        try {
            path = new File(pathString);
            if (path.isDirectory()) {
                if (Open.Dir(path, "") == null)
                    throw new Exception("Access denied");
                //loadIndicator.setVisible(true);
                mainApp.getPrimaryStage().getScene().setCursor(Cursor.WAIT);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(200);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        } finally {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    //loadIndicator.setVisible(false);
                                    mainApp.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
                                    listFile.setItems(FXCollections.observableArrayList(Open.Dir(path, "")));
                                    findField.setText("");
                                    listFile.scrollTo(0);
                                    listFile.getSelectionModel().clearSelection();
                                }
                            });
                        }
                    }
                }).start();
            } else if (path.isFile()) {
                if (Open.File(path) != null)
                    throw new Exception(Open.File(path));
                path = path.getParentFile();
            } else if (path.getUsableSpace() == 0) {
                //System.out.println(path.getAbsolutePath() + "\n" + path.getCanonicalPath() + "\n" + path.getTotalSpace() + "\n" + path.getUsableSpace() + "\n" + path.getFreeSpace());
                throw new Exception("���������� ������� ����");
            }

        } catch (Exception e) {
            path = path.getParentFile();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Attention!");
            alert.setHeaderText("Can not to be open!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void setOnUpHandler() {
        //if (!pathField.getText().equals(pc) && pathField.getText().length() != 3) {
        if (path != null && path.getParent() != null) {
            path = path.getParentFile();
            if (path.exists() && path.isDirectory() && path != null)
                listFile.setItems(FXCollections.observableArrayList(Open.Dir(path, "")));
        } else {
            listFile.setItems(FXCollections.observableArrayList(listRoots));
            path = null;
            pathField.setText(pc);
        }
        findField.setText("");
        listFile.scrollTo(0);
    }

    @FXML
    private void setOnCutHandler() {
        tmp = new File(path.getAbsolutePath() + File.separator
                + listFile.getSelectionModel().getSelectedItem().toString());
        cuted = true;
    }

    @FXML
    private void setOnCopyHandler() {
        tmp = new File(path.getAbsolutePath() + File.separator
                + listFile.getSelectionModel().getSelectedItem().toString());
        cuted = false;
    }

    @FXML
    private void setOnPasteHandler() {
        if (tmp != null)
            if (Copy.Dir(tmp.getAbsolutePath(), path.getAbsolutePath() + File.separator + tmp.getName()) && cuted)
                Delete.Delete(tmp);
        if (path.exists() && path.isDirectory())
            listFile.setItems(FXCollections.observableArrayList(Open.Dir(path, "")));
        cuted = false;
        listFile.scrollTo(0);
    }

    @FXML
    private void setOnDeleteHandler() {
        if (path == null)
            return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("������� ����?");
        alert.setHeaderText("������� ����?");
        alert.setContentText("��� ������� ������ ������, ���� �� ����� ������");
        ButtonType buttonTypeSave = new ButtonType("�������", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("������", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeCancel, buttonTypeSave);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeSave) {
            if (Delete.Delete(new File(path.getAbsolutePath() + File.separator + listFile.getSelectionModel().getSelectedItem().toString())) && path.exists() && path.isDirectory())
                listFile.setItems(FXCollections.observableArrayList(Open.Dir(path, "")));
        } else if (result.get() == buttonTypeCancel) {
            alert.close();
            return;
        }
    }

    @FXML
    private void setOnRenameHandler() {
        if (path != null) {
            newFolder = Boolean.FALSE;
            listFile.setEditable(true);
            new AnimationTimer() {
                int frameCount = 0;

                @Override
                public void handle(long now) {
                    frameCount++;
                    if (frameCount > 1) {
                        listFile.edit(listFile.getSelectionModel().getSelectedIndex());
                        stop();
                    }
                }
            }.start();
        }
    }

    @FXML
    private void setOnNewHandler() {
        if (path != null) {
            newFolder = Boolean.TRUE;
            listFile.setEditable(true);
            String newFolder = "new folder";
            listFile.getItems().add(listFile.getItems().size(), newFolder);
            listFile.getSelectionModel().clearSelection();
            listFile.scrollTo(listFile.getItems().size() - 1);
            listFile.getSelectionModel().select(listFile.getItems().size() - 1);
            new AnimationTimer() {
                int frameCount = 0;

                @Override
                public void handle(long now) {
                    frameCount++;
                    if (frameCount > 1) {
                        listFile.edit(listFile.getItems().size() - 1);
                        stop();
                    }
                }
            }.start();
        }
    }

    @FXML
    private void setOnGoToHandler() {
        try {
            File tmp;
            if (pathField.getText().equals(pc)) {
                listFile.setItems(FXCollections.observableArrayList(listRoots));
                path = null;
            } else {
                tmp = new File(pathField.getText());
                if (tmp.isDirectory()) {
                    if (Open.Dir(tmp, "") == null)
                        throw new Exception("Access denied");
                    listFile.setItems(FXCollections.observableArrayList(Open.Dir(tmp, "")));
                    path = tmp.getAbsoluteFile();
                } else if (tmp.isFile()) {
                    if (Open.File(tmp) != null)
                        throw new Exception(Open.File(tmp));
                } else if (tmp.getUsableSpace() == 0)
                    throw new Exception("���������� ������� ����");
            }
            listFile.requestFocus();
            listFile.scrollTo(0);
            findField.setText("");
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning!");
            alert.setHeaderText(e.getMessage());
            alert.setContentText(pathField.getText() + ".");
            alert.showAndWait();
        }
        if (path != null)
            pathField.setText(path.getAbsolutePath());
        else if (path.getParent() != null)
            pathField.setText(path.getName());
        else
            pathField.setText(FileSystemView.getFileSystemView().getSystemIcon(path) + " " + path.getAbsolutePath());
    }

    @FXML
    private void setAddToFavoriteHandler() {
        System.out.println(listFile.getSelectionModel().getSelectedItem());
        listFavorite.getItems().add(listFile.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void setAboutHandler() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("FileBrowser");
        alert.setHeaderText("FileBrowserApp\nfor desktop");
        alert.setContentText("Author: SmirnovDS\n@null_ds");
        alert.showAndWait();
    }

    @FXML
    private void setOnMouseEntered() {
        mainApp.getPrimaryStage().getScene().setCursor(Cursor.HAND);
    }

    @FXML
    private void setOnMouseExited() {
        mainApp.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
    }


}
