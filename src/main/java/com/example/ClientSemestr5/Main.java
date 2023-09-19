package com.example.ClientSemestr5;

import com.example.ClientSemestr5.utility.SessionStorage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;



public class Main extends Application {


    private static Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("controllers/authorization.fxml"));
        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("Система управления стипендиями");
        Image icon = new Image("student_icon.png");
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.setResizable(false);
        darkThemeCheck(scene);
        stage.show();
    }

    public void darkThemeCheck(Scene scene){
        if(SessionStorage.isDarkTheme()) {
            String stylesheet = getClass().getResource("controllers/darkTheme.css").toExternalForm();
            scene.getStylesheets().add(stylesheet);
        }
    }

    public static void main(String[] args) {
        SessionStorage.init();
        launch();
    }




}