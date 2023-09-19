package com.example.ClientSemestr5.controllers;

import com.example.ClientSemestr5.DTO.DTOImpl.UserDtoImpl;
import com.example.ClientSemestr5.Enum.RequestType;
import com.example.ClientSemestr5.Enum.ResponseType;
import com.example.ClientSemestr5.Model.entity.Faculty;
import com.example.ClientSemestr5.Model.entity.Person;
import com.example.ClientSemestr5.Model.entity.User;
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
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class UserChangeController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField lastnameField;

    @FXML
    private TextField patronymicField;

    @FXML
    private TextField facultyField;

    @FXML
    private Label loginLabel;



    @FXML
    private RadioButton radioAdmin;



    private User userForUpdating;

    public  void setUserForUpdating(User user){
        userForUpdating = user;
        if(userForUpdating.isAdmin()){
            radioAdmin.setSelected(true);
        }
        loginLabel.setText(userForUpdating.getLogin());
        nameField.setText(userForUpdating.getPerson().getFirstname());
        lastnameField.setText(userForUpdating.getPerson().getLastname());
        patronymicField.setText(userForUpdating.getPerson().getPatronymic());
        facultyField.setText(userForUpdating.getFaculty().getFacultyName());
    }



    @FXML
    private void onUpdateButtonClick(ActionEvent event) throws IOException {
        boolean error = false;
        String name = nameField.getText();
        String lastname = lastnameField.getText();
        String patronymic = patronymicField.getText();
        String facultyName = facultyField.getText();
        UserDtoImpl userDto = new UserDtoImpl();


        if(nameField.getText().equals("")){
            error = true;
        }
        if(lastnameField.getText().equals("")){
            error = true;
        }
        if(patronymicField.getText().equals("")){
            error = true;
        }
        if(facultyField.getText().equals("")){
            error = true;
        }


        if(error){
            Alert("Поля должны быть заполнены!", "Ошибка ввода");
            return;
        }





        Faculty faculty = new Faculty();
        faculty.setIdFaculty(userForUpdating.getFaculty().getIdFaculty());
        faculty.setFacultyName(facultyName);

        Person person = new Person();
        person.setIdPerson(userForUpdating.getPerson().getIdPerson());
        person.setFirstname(name);
        person.setLastname(lastname);
        person.setPatronymic(patronymic);


        if (radioAdmin.isSelected()){
            userForUpdating.setAdmin(true);
        }
        else {
            userForUpdating.setAdmin(false);
        }

        userForUpdating.setPerson(person);
        userForUpdating.setFaculty(faculty);

        String request = userDto.createRequest(userForUpdating, RequestType.UPDATE_USER);
        ClientSocketTCP.send(request);
        String response;
        response = ClientSocketTCP.get();
        ResponseType responseType = new Gson().fromJson(response, ResponseType.class);

        if(ResponseType.SUCCESS == responseType){
            Alert("Пользователь успешно изменен!", "Изменение");
            loadUsersScene(event);
        }
        else {
            Alert("Неопознанный ответ сервера!", "Ошибка");
        }


    }

    public void Alert(String str1, String str2) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(str1);
        alert.setTitle(str2);
        alert.showAndWait();
    }

    @FXML
    private void loadUsersScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Users.fxml")));
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
}
