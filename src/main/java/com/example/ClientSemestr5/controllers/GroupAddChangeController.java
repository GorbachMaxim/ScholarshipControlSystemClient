package com.example.ClientSemestr5.controllers;

import com.example.ClientSemestr5.DTO.DTOImpl.GroupDtoImpl;
import com.example.ClientSemestr5.Enum.RequestType;
import com.example.ClientSemestr5.Enum.ResponseType;
import com.example.ClientSemestr5.Model.entity.Faculty;
import com.example.ClientSemestr5.Model.entity.Group;
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

public class GroupAddChangeController {
    @FXML
    private TextField facultyNameField;

    @FXML
    private TextField groupNameField;

    @FXML
    private Button UpdateChangeButton;
    private boolean isChange = false;
    private Group selectedGroup;

    public void itIsChangeScene(Group group){
        isChange = true;
        facultyNameField.setText(group.getFaculty().getFacultyName());
        groupNameField.setText(group.getName());
        UpdateChangeButton.setText("Обновить");
        selectedGroup = group;
    }
    @FXML
    private void onUpdateButtonClick(ActionEvent event) throws IOException {
        if(facultyNameField.getText().equals("")){
            Alert("Поля должны быть заполнены!", "Ошибка ввода");
            return;
        }
        if(groupNameField.getText().equals("")){
            Alert("Поля должны быть заполнены!", "Ошибка ввода");
            return;
        }


        if (isChange){
            GroupDtoImpl groupDto = new GroupDtoImpl();
            selectedGroup.setName(groupNameField.getText());
            Faculty faculty = new Faculty();
            faculty.setIdFaculty(selectedGroup.getFaculty().getIdFaculty());
            faculty.setFacultyName(facultyNameField.getText());
            selectedGroup.setFaculty(faculty);


            String request = groupDto.createRequest(selectedGroup, RequestType.UPDATE_GROUP);
            ClientSocketTCP.send(request);
            String response;
            response = ClientSocketTCP.get();
            ResponseType responseType = new Gson().fromJson(response, ResponseType.class);

            if(ResponseType.SUCCESS == responseType){
                Alert("Группа успешно обновлена!", "Обновление");
                loadGroupsScene(event);
            }
            else if(ResponseType.FAIL == responseType){
                Alert("Группа с таким именем уже существует!", "Не удалось обновить группу");
            }
            else {
                Alert("Неопознанный ответ сервера!", "Ошибка");
            }
        }
        else {
            Group groupForAdding = new Group();
            groupForAdding.setName(groupNameField.getText());
            Faculty faculty = new Faculty();
            faculty.setFacultyName(facultyNameField.getText());
            groupForAdding.setFaculty(faculty);

            GroupDtoImpl groupDto = new GroupDtoImpl();

            String request = groupDto.createRequest(groupForAdding, RequestType.ADD_GROUP);
            ClientSocketTCP.send(request);


            String response;
            response = ClientSocketTCP.get();
            ResponseType responseType = new Gson().fromJson(response, ResponseType.class);



            if(ResponseType.SUCCESS == responseType){
                Alert("Группа успешно добавлена!", "Добавление");
                loadGroupsScene(event);
            }
            else if(ResponseType.FAIL == responseType){
                Alert("Группа с таким именем уже существует!", "Не удалось добавить группу");
            }
            else {
                Alert("Неопознанный ответ сервера!", "Ошибка");
            }
        }

    }
    @FXML
    private void loadGroupsScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Groups.fxml")));
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

