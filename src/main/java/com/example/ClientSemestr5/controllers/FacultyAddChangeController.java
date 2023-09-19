package com.example.ClientSemestr5.controllers;

import com.example.ClientSemestr5.DTO.DTOImpl.FacultyDtoImpl;
import com.example.ClientSemestr5.Enum.RequestType;
import com.example.ClientSemestr5.Enum.ResponseType;
import com.example.ClientSemestr5.Model.entity.Faculty;
import com.example.ClientSemestr5.utility.ClientSocketTCP;
import com.example.ClientSemestr5.utility.SessionStorage;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class FacultyAddChangeController {
    @FXML
    private TextField facultyNameField;

    @FXML
    private Button UpdateChangeButton;
    private boolean isChange = false;


    private Faculty selectedFaculty;

    public void itIsChangeScene(Faculty faculty){
        isChange = true;
        facultyNameField.setText(faculty.getFacultyName());
        UpdateChangeButton.setText("Обновить");
        selectedFaculty = faculty;
    }

    @FXML
    private void onUpdateButtonClick(ActionEvent event) throws IOException {
        if(facultyNameField.getText().equals("")){
            Alert("Поле должно быть заполнено!", "Ошибка ввода");
            return;
        }


        if (isChange){

            FacultyDtoImpl facultyDto = new FacultyDtoImpl();
            selectedFaculty.setFacultyName(facultyNameField.getText());
            String request = facultyDto.createRequest(selectedFaculty, RequestType.UPDATE_FACULTY);
            ClientSocketTCP.send(request);
            String response;
            response = ClientSocketTCP.get();
            ResponseType responseType = new Gson().fromJson(response, ResponseType.class);



            if(ResponseType.SUCCESS == responseType){
                Alert("Факультет успешно обновлен!", "Обновление");
                loadFacultiesScene(event);
            }
            else if(ResponseType.FAIL == responseType){
                Alert("Факультет с таким именем уже существует!", "Не удалось обновить факультет");
            }
            else {
                Alert("Неопознанный ответ сервера!", "Ошибка");
            }
        }
        else {
            Faculty facultyForAdding = new Faculty();
            facultyForAdding.setFacultyName(facultyNameField.getText());
            FacultyDtoImpl facultyDto = new FacultyDtoImpl();

            String request = facultyDto.createRequest(facultyForAdding, RequestType.ADD_FACULTY);
            ClientSocketTCP.send(request);


            String response;
            response = ClientSocketTCP.get();
            ResponseType responseType = new Gson().fromJson(response, ResponseType.class);



            if(ResponseType.SUCCESS == responseType){
                Alert("Факультет успешно добавлен!", "Добавление");
                loadFacultiesScene(event);
            }
            else if(ResponseType.FAIL == responseType){
                Alert("Факультет с таким именем уже существует!", "Не удалось добавить факультет");
            }
            else {
                Alert("Неопознанный ответ сервера!", "Ошибка");
            }
        }

    }

    @FXML
    private void loadFacultiesScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Faculties.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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

    public void Alert(String str1, String str2) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(str1);
        alert.setTitle(str2);
        alert.showAndWait();
    }
}
