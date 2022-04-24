package com.example.brains_cloud_client;

import com.example.brains_cloud_common.AbstractMessage;
import com.example.brains_cloud_common.FileMessage;
import com.example.brains_cloud_common.FileRequest;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private TextArea addressBar;
    @FXML
    private Button enterButton;
    @FXML
    private ListView<String> filesList;
    @FXML
    private TableColumn filesListName;
    @FXML
    private Label textSelectedRadio, folderClient;
    @FXML
    private RadioButton downloadRadio, sendRadio, viewRadio, deleteRadio, renameRadio;
    @FXML
    private TextField userLogin;
    @FXML
    private PasswordField userPass;
    @FXML
    private VBox loginPanel, vBoxCloud;
    @FXML
    private HBox cloudPanel, cloudPanel1, cloudPanel2;
    @FXML
    private Hyperlink singUp;

    private int count = 0;
    private String clientName = "NoName";


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Network.start();

        folderClient.setText(clientName + "'s files:");

        downloadRadio.setOnAction(event -> {
            addressBar.clear();
            textSelectedRadio.setText("Хотите скачать файл из облока?");
        });
        sendRadio.setOnAction(event -> {
            addressBar.clear();
            textSelectedRadio.setText("Хотите загрузить файл себе в облоко?");
        });
        viewRadio.setOnAction(event -> {
            addressBar.clear();
            textSelectedRadio.setText("Хотите просмотреть файл?");
        });
        deleteRadio.setOnAction(event -> {
            addressBar.clear();
            textSelectedRadio.setText("Хотите удалить файл из облока?");
            onClickListView();
        });
        renameRadio.setOnAction(event -> {
            addressBar.clear();
            textSelectedRadio.setText("Хотите переименовать файл в облоке?");
            onClickListView();
        });


        Thread t = new Thread(() -> {
            try {
                while (true) {
                    AbstractMessage am = Network.readObject();
                    if (am instanceof FileMessage) {
                        FileMessage fm = (FileMessage) am;
                        Files.write(Paths.get("client_storage/" + fm.getFileName()), fm.getData(), StandardOpenOption.CREATE);
                        refreshLocalFilesList();
                    }
//                    if (am instanceof FileRequest) {
//                        FileRequest fr = (FileRequest) am;
//                        if (fr.getFileName().startsWith("_auth")) {
//                            String[] parts = fr.getFileName().split("\\s");
//                            boolean authIndex = Boolean.parseBoolean(parts[1]);
//                            if (authIndex) {
//                                loginPanel.setVisible(false);
//                                loginPanel.setManaged(false);
//                                vBoxCloud.setVisible(true);
//                                vBoxCloud.setManaged(true);
//                            } else {
//
////                                Alert alert = new Alert(Alert.AlertType.INFORMATION, "не верный логин или пароль");
////                                alert.showAndWait();
//                            }
//                        }
//                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                Network.stop();
            }
        });
        t.setDaemon(true);
        t.start();
        refreshLocalFilesList();
    }

    public void refreshLocalFilesList() {
        Platform.runLater(() -> {
            try {
                filesList.getItems().clear();
                Files.list(Paths.get("client_storage"))
                        .filter(p -> !Files.isDirectory(p))
                        .map(p -> p.getFileName().toString())
                        .forEach(o -> filesList.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void onClickButton() {

        if (downloadRadio.isSelected()) {

        }
        if (sendRadio.isSelected()) {
            if (!addressBar.getText().equals(null)) {
                Network.sendMsg(new FileRequest(addressBar.getText()));
                addressBar.clear();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Файла с таким именем нет");
                alert.showAndWait();
            }
        }
        if (viewRadio.isSelected()) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Файла с таким именем нет");
            alert.showAndWait();
        }
        if (deleteRadio.isSelected()) {

            if (!addressBar.getText().equals(null)) {
                try {
                    Path path = Paths.get("client_storage/" + addressBar.getText());
                    Files.delete(path);
                    refreshLocalFilesList();
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Файла с таким именем нет");
                    alert.showAndWait();
                    addressBar.clear();
                }
                addressBar.clear();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Файла с таким именем нет");
                alert.showAndWait();
                addressBar.clear();
            }
        }
        if (renameRadio.isSelected()) {

            if (!addressBar.getText().equals(null)) {
                try {
                    Path sourcePath = Paths.get("client_storage/" + addressBar.getText());

                    Path destinationPath = Paths.get("client_storage/" + addressBar.getText());

                    Files.move(sourcePath, destinationPath);
                    refreshLocalFilesList();
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Не верное действие");
                    alert.showAndWait();
                    addressBar.clear();
                }
                addressBar.clear();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "НЕ ВЕРНОЕ ДЕЙСТВИЕ");
                alert.showAndWait();
                addressBar.clear();
            }

        }
    }

    public void onClickListView() {
        MultipleSelectionModel<String> listSelectionModel = filesList.getSelectionModel();
        listSelectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> addressBar.appendText(newValue + '\n'));

    }

    public void login() {
        if (!userLogin.getText().trim().isEmpty()) {
            if (!userPass.getText().trim().isEmpty()) {
                String msg = String.format("/auth %s %s %s", userLogin.getText().trim(), userPass.getText().trim(), 0);
                Network.sendMsg(new FileRequest(msg));
                System.out.println(userLogin.getText().trim() + " " + userPass.getText().trim());
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "не верный логин или пароль");
                alert.showAndWait();
            }
        }
        try {

                AbstractMessage am = Network.readObject();
                if (am instanceof FileRequest) {
                    System.out.println(111111);
                    FileRequest fr = (FileRequest) am;
                    if (fr.getFileName().startsWith("_auth")) {
                        String[] parts = fr.getFileName().split("\\s");
                        boolean authIndex = Boolean.parseBoolean(parts[1]);
                        if (authIndex) {
                            loginPanel.setVisible(false);
                            loginPanel.setManaged(false);
                            vBoxCloud.setVisible(true);
                            vBoxCloud.setManaged(true);
                        } else {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "не верный логин или пароль");
                            alert.showAndWait();
                        }
                    }
                }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClickHyperlink() {
        Font font = new Font("Cambria Bold", 16);
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(20, 20, 20, 20));

        Label labelLogin = new Label("Login");
        labelLogin.setFont(font);
        TextField singUpLogin = new TextField();

        Label labelPass = new Label("Password");
        labelPass.setFont(font);
        PasswordField singUpPass = new PasswordField();

        Button btnEnter = new Button("Enter");
        btnEnter.setFont(font);

        BorderPane.setMargin(btnEnter, new Insets(35, 0, 0, 0));
        vBox.getChildren().addAll(labelLogin, singUpLogin, labelPass, singUpPass, btnEnter);
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(vBox);
        Scene secondScene = new Scene(secondaryLayout, 400, 200);
        Stage newWindow = new Stage();
        newWindow.setTitle("Window for sing up");
        newWindow.setScene(secondScene);
        newWindow.initModality(Modality.APPLICATION_MODAL);
        //todo сделать окно stage родительским , чтобы newWindow  его блокировало
        newWindow.show();


        btnEnter.setOnAction(event -> {
            if (!singUpLogin.getText().trim().isEmpty()) {
                if (!singUpPass.getText().trim().isEmpty()) {
                    String msg = String.format("/auth %s %s %s", singUpLogin.getText().trim(), singUpPass.getText().trim(), 1);
                    Network.sendMsg(new FileRequest(msg));
                    System.out.println(singUpLogin.getText().trim() + " " + singUpPass.getText().trim());
                }
            }
            newWindow.close();
            loginPanel.setVisible(false);
            loginPanel.setManaged(false);
            vBoxCloud.setVisible(true);
            vBoxCloud.setManaged(true);

        });

    }
}
