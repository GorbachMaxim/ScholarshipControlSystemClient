package com.example.ClientSemestr5.controllers;

import com.example.ClientSemestr5.utility.SessionStorage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitMenuButton;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController extends Menu implements Initializable {
    @FXML
    private SplitMenuButton menu;



    @FXML
    private void onLightClick(ActionEvent event) {
        if(SessionStorage.isDarkTheme()) {
            menu.setText("Светлая тема");
            String stylesheet = getClass().getResource("darkTheme.css").toExternalForm();
            menu.getScene().getStylesheets().remove(stylesheet);
            SessionStorage.setLightTheme();
        }
    }

    @FXML
    private void onDarkClick(ActionEvent event) {
        if(!SessionStorage.isDarkTheme()) {
            menu.setText("Темная тема");
            String stylesheet = getClass().getResource("darkTheme.css").toExternalForm();
            menu.getScene().getStylesheets().add(stylesheet);
            SessionStorage.setDarkTheme();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(SessionStorage.isDarkTheme()){
            menu.setText("Темная тема");
        }
    }
}
