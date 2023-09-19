package com.example.ClientSemestr5.controllers;

import com.example.ClientSemestr5.utility.SessionStorage;
import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class Menu {

    public void darkThemeCheck(Scene scene){
        if(SessionStorage.isDarkTheme()) {
            String stylesheet = getClass().getResource("darkTheme.css").toExternalForm();
            scene.getStylesheets().add(stylesheet);
        }
    }

    private void changeScene(ActionEvent event, String source) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(Menu.class.getResource(source)));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        darkThemeCheck(scene);
        stage.setScene(scene);
    }

     @FXML
     void onScholarshipButtonClick(ActionEvent event) throws IOException {
         changeScene(event,"Scholarship.fxml");
    }

    @FXML
    void onStudentsButtonClick(ActionEvent event) throws IOException {
        changeScene(event,"Students.fxml");

    }

    @FXML
    void onSubjectsButtonClick(ActionEvent event) throws IOException {
        changeScene(event,"Subjects.fxml");
    }

    @FXML
    void onUsersButtonClick(ActionEvent event) throws IOException {
        changeScene(event,"Users.fxml");
    }

    @FXML
    void onFacultiesButtonClick(ActionEvent event) throws IOException {
        changeScene(event,"Faculties.fxml");
    }

    @FXML
    void onGroupsButtonClick(ActionEvent event) throws IOException {
        changeScene(event,"Groups.fxml");
    }

    @FXML
    void onSettingsButtonClick(ActionEvent event) throws IOException {
        changeScene(event,"Settings.fxml");
    }

    @FXML
    void onExitButtonClick(ActionEvent event) throws IOException {
        changeScene(event,"authorization.fxml");
    }

    protected void alert(String text, String header) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(text);
        alert.setTitle(header);
        alert.showAndWait();
    }

    protected void alert(String text, String header, String additionalText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(text);
        alert.setContentText(additionalText);
        alert.setTitle(header);
        alert.showAndWait();
    }


    private boolean isAnimationPlaying = false;
    protected void imageViewRotateAnimation(ImageView imageView){
        RotateTransition rotateTransition = new RotateTransition();
        rotateTransition.setDuration(Duration.millis(600));
        rotateTransition.setNode(imageView);
        rotateTransition.setByAngle(360);
        rotateTransition.setAutoReverse(false);
        rotateTransition.play();
    }
}
