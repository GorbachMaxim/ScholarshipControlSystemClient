
package com.example.ClientSemestr5.controllers;

import com.example.ClientSemestr5.DTO.DTOImpl.SubjectDtoImpl;
import com.example.ClientSemestr5.Enum.RequestType;
import com.example.ClientSemestr5.Enum.ResponseType;
import com.example.ClientSemestr5.Model.entity.Subject;
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

public class SubjectAddChangeController {
    @FXML
    private TextField subjectNameField;

    @FXML
    private TextField hourNumberField;

    @FXML
    private Button UpdateChangeButton;
    private boolean isChange = false;


    private Subject selectedSubject;

    public void itIsChangeScene(Subject subject){
        isChange = true;
        subjectNameField.setText(subject.getName());
        hourNumberField.setText(String.valueOf(subject.getHoursNumber()));
        UpdateChangeButton.setText("Обновить");
        selectedSubject = subject;
    }

    @FXML
    private void onUpdateButtonClick(ActionEvent event) throws IOException {
        if(subjectNameField.getText().equals("")){
            Alert("Поля должны быть заполнены!", "Ошибка ввода");
            return;
        }

        if(hourNumberField.getText().equals("")){
            Alert("Поля должны быть заполнены!", "Ошибка ввода");
            return;
        }
        else {
            try {
                int hourNumber = Integer.parseInt(hourNumberField.getText());
                if (hourNumber < 1 || hourNumber > 200)
                    throw new Exception();
            }
            catch (Exception e){
                Alert("Количество часов должно быть в диапозоне от 1 до 200!", "Ошибка ввода");
                return;
            }

        }


        if (isChange){
            SubjectDtoImpl subjectDto = new SubjectDtoImpl();
            selectedSubject.setName(subjectNameField.getText());
            selectedSubject.setHoursNumber(Integer.parseInt(hourNumberField.getText()));
            String request = subjectDto.createRequest(selectedSubject, RequestType.UPDATE_SUBJECT);
            ClientSocketTCP.send(request);
            String response;
            response = ClientSocketTCP.get();
            ResponseType responseType = new Gson().fromJson(response, ResponseType.class);



            if(ResponseType.SUCCESS == responseType){
                Alert("Предмет успешно обновлен!", "Обновление");
                loadSubjectsScene(event);
            }
            else if(ResponseType.FAIL == responseType){
                Alert("Предмет с таким именем уже существует!", "Не удалось обновить предмет");
            }
            else {
                Alert("Неопознанный ответ сервера!", "Ошибка");
            }
        }
        else {
            Subject subjectForAdding = new Subject();
            subjectForAdding.setName(subjectNameField.getText());
            subjectForAdding.setHoursNumber(Integer.parseInt(hourNumberField.getText()));
            SubjectDtoImpl subjectDto = new SubjectDtoImpl();

            String request = subjectDto.createRequest(subjectForAdding, RequestType.ADD_SUBJECT);
            ClientSocketTCP.send(request);


            String response;
            response = ClientSocketTCP.get();
            ResponseType responseType = new Gson().fromJson(response, ResponseType.class);



            if(ResponseType.SUCCESS == responseType){
                Alert("Предмет успешно добавлен!", "Добавление");
                loadSubjectsScene(event);
            }
            else if(ResponseType.FAIL == responseType){
                Alert("Предмет с таким именем уже существует!", "Не удалось добавить предмет");
            }
            else {
                Alert("Неопознанный ответ сервера!", "Ошибка");
            }
        }

    }

    @FXML
    private void loadSubjectsScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Subjects.fxml")));
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

