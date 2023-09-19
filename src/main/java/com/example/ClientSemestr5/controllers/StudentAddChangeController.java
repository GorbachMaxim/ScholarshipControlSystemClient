package com.example.ClientSemestr5.controllers;

import com.example.ClientSemestr5.DTO.DTOImpl.StudentDtoImpl;
import com.example.ClientSemestr5.Enum.RequestType;
import com.example.ClientSemestr5.Enum.ResponseType;
import com.example.ClientSemestr5.Model.entity.Group;
import com.example.ClientSemestr5.Model.entity.Student;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class StudentAddChangeController {

    @FXML
    private TextField firstnameField;

    @FXML
    private TextField lastnameField;
    @FXML
    private TextField groupNameField;

    @FXML
    private Button UpdateChangeButton;
    @FXML
    private RadioButton radioPaid;

    private boolean isChange = false;


    private Student selectedStudent;

    public void itIsChangeScene(Student student){
        isChange = true;
        if(!student.isBudget()){
            radioPaid.setSelected(true);
        }

        firstnameField.setText(student.getFirstname());
        lastnameField.setText(student.getLastname());
        groupNameField.setText(student.getGroup().getName());
        UpdateChangeButton.setText("Обновить");
        selectedStudent = student;
    }


    @FXML
    private void onAddChangeButtonClick(ActionEvent event) throws IOException {
        if(groupNameField.getText().equals("")){
            Alert("Поля должны быть заполнено!", "Ошибка ввода");
            return;
        }
        if(firstnameField.getText().equals("")){
            Alert("Поля должны быть заполнено!", "Ошибка ввода");
            return;
        }
        if(lastnameField.getText().equals("")){
            Alert("Поля должны быть заполнено!", "Ошибка ввода");
            return;
        }


        if (isChange){

            selectedStudent.setFirstname(firstnameField.getText());
            selectedStudent.setLastname(lastnameField.getText());
            if (!radioPaid.isSelected()){
                selectedStudent.setBudget(true);
            }
            Group group = new Group();
            group.setName(groupNameField.getText());
            selectedStudent.setGroup(group);
            StudentDtoImpl studentDto = new StudentDtoImpl();
            String request = studentDto.createRequest(selectedStudent, RequestType.UPDATE_STUDENT);
            ClientSocketTCP.send(request);
            String response;
            response = ClientSocketTCP.get();
            ResponseType responseType = new Gson().fromJson(response, ResponseType.class);



            if(ResponseType.SUCCESS == responseType){
                Alert("Студент успешно обновлен!", "Обновление");
                loadStudentsScene(event);
            }
            else if(ResponseType.FAIL == responseType){
                Alert("Не удалось обновить студента!  Такой группы не существует!", "Обновление");
            }
            else {
                Alert("Неопознанный ответ сервера!", "Ошибка");
            }
        }
        else {
            Student studentForAdding = new Student();

            studentForAdding.setFirstname(firstnameField.getText());
            studentForAdding.setLastname(lastnameField.getText());
            if (!radioPaid.isSelected()){
                studentForAdding.setBudget(true);
            }
            Group group = new Group();
            group.setName(groupNameField.getText());
            studentForAdding.setGroup(group);
            StudentDtoImpl studentDto = new StudentDtoImpl();

            String request = studentDto.createRequest(studentForAdding, RequestType.ADD_STUDENT);
            ClientSocketTCP.send(request);


            String response;
            response = ClientSocketTCP.get();
            ResponseType responseType = new Gson().fromJson(response, ResponseType.class);



            if(ResponseType.SUCCESS == responseType){
                Alert("Студент успешно добавлен!", "Добавление");
                loadStudentsScene(event);
            }
            else if(ResponseType.FAIL == responseType){
                Alert("Не удалось добавить студента! Такой группы не существует!", "Добавление");
            }
            else {
                Alert("Неопознанный ответ сервера!", "Ошибка");
            }
        }

    }

    public void Alert(String str1, String str2) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(str1);
        alert.setTitle(str2);
        alert.showAndWait();
    }

    @FXML
    private void loadStudentsScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Students.fxml")));
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
