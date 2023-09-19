package com.example.ClientSemestr5.controllers;

import com.example.ClientSemestr5.DTO.DTOImpl.UserDtoImpl;
import com.example.ClientSemestr5.Enum.RequestType;
import com.example.ClientSemestr5.Enum.ResponseType;
import com.example.ClientSemestr5.Model.entity.User;
import com.example.ClientSemestr5.utility.ClientSocketTCP;
import com.example.ClientSemestr5.utility.SessionStorage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;


public class AuthorizationController implements Initializable {
    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordHideField;
    @FXML
    private TextField passwordShowField;
    private TextField passwordField = passwordHideField;
    @FXML
    private ImageView passwordImageView;
    @FXML
    private Button loginButton;

    @FXML
    private Label registrationLabel;

    @FXML
    private Label passwordError;

    @FXML
    private Label loginError;


    @FXML
    private AnchorPane anchorPane;

    @FXML
    protected void onPasswordImageViewClick(){
        if(passwordHideField.isVisible()) {
            passwordImageView.setImage(new Image("showed_icon.png"));
            passwordHideField.setVisible(false);
            passwordShowField.setVisible(true);
            passwordField = passwordShowField;
            passwordShowField.setText(passwordHideField.getText());
        }
        else {
            passwordImageView.setImage(new Image("hidden_icon.png"));
            passwordShowField.setVisible(false);
            passwordHideField.setVisible(true);
            passwordField = passwordHideField;
            passwordHideField.setText(passwordShowField.getText());
        }
    }

    @FXML
    protected void onLoginButtonClick(ActionEvent event) throws IOException {
        loginError.setText("");
        passwordError.setText("");
        boolean error = false;
        if(loginField.getLength() < 5) {
            loginError.setText("Логин меньше 5 символов!");
            loginError.setTextFill(Color.rgb(210, 39, 30));
            error = true;
        }
        if(passwordField.getText().length() < 5){
            passwordError.setText("Пароль меньше 5 символов!");
            passwordError.setTextFill(Color.rgb(210, 39, 30));
            error = true;
        }
        if (error)
            return;




        String log = loginField.getText();
        int pass = User.RSHash(passwordField.getText());
        UserDtoImpl userDto = new UserDtoImpl();
        User user = new User();
        user.setLogin(log);
        user.setPassword(pass);
        String request = userDto.createRequest(user, RequestType.LOGIN);
        ClientSocketTCP.send(request);
        String response;
        response = ClientSocketTCP.get();
        ResponseType responseType = userDto.getResponseType(response);
        if (responseType == ResponseType.SUCCESS){
            user = userDto.getResponseEntity(response);
            SessionStorage.isAdmin = user.isAdmin();
            SessionStorage.login = user.getLogin();
            loadMenuScene();
        }
        else if(ResponseType.FAIL == responseType){
            Alert("Неверный логин или пароль!", "Не удалось авторизоваться");
        }
        else {
            Alert("Неопознанный ответ сервера!", "Ошибка");
        }
    }

//    @FXML
//    protected void onLoginButtonClick(ActionEvent event) throws IOException {
//        loadAdminMenuScene();
//    }


    public void Alert(String str1, String str2) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(str1);
        alert.setTitle(str2);
        alert.showAndWait();
    }

    @FXML
    private void loadRegistrationScene() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Registration.fxml")));
        Stage stage = (Stage) loginButton.getScene().getWindow();
        Scene scene = new Scene(root);
        darkThemeCheck(scene);
        stage.setScene(scene);
    }
    public void cursorHandle (MouseEvent me){
        registrationLabel.getScene().setCursor(Cursor.HAND);
    }

    public void cursorDefault (MouseEvent me){
        registrationLabel.getScene().setCursor(Cursor.DEFAULT);
    }

    public void setLogData(String login, String password){
        loginField.setText(login);
        passwordField.setText(password);
    }






    @FXML
    private void loadMenuScene() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Students.fxml")));
        Stage stage = (Stage) loginButton.getScene().getWindow();
        Scene scene = new Scene(root);
        darkThemeCheck(scene);
        stage.setScene(scene);
    }

    public void darkThemeCheck(Scene scene){
        if(SessionStorage.isDarkTheme()) {
            String stylesheet = getClass().getResource("darkTheme.css").toExternalForm();
            scene.getStylesheets().add(stylesheet);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        passwordField = passwordHideField;
    }
}