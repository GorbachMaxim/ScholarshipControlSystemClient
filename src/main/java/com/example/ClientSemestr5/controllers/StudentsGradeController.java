package com.example.ClientSemestr5.controllers;

import com.example.ClientSemestr5.DTO.DTOImpl.StudentDtoImpl;
import com.example.ClientSemestr5.DTO.DTOImpl.UserDtoImpl;
import com.example.ClientSemestr5.Enum.RequestType;
import com.example.ClientSemestr5.Enum.ResponseType;
import com.example.ClientSemestr5.Model.entity.Mark;
import com.example.ClientSemestr5.Model.entity.Student;
import com.example.ClientSemestr5.utility.ClientSocketTCP;
import com.example.ClientSemestr5.utility.MarkGsonAdapter;
import com.example.ClientSemestr5.utility.SessionStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StudentsGradeController {
    @FXML
    private Label label1;
    @FXML
    private Label label2;
    @FXML
    private Label label3;
    @FXML
    private Label label4;
    @FXML
    private Label label5;
    @FXML
    private TextField textField1;
    @FXML
    private TextField textField2;
    @FXML
    private TextField textField3;
    @FXML
    private TextField textField4;
    @FXML
    private TextField textField5;

    @FXML
    private Label examsNotFoundLabel;
    private Student selectedStudent;

    private int numberOfExams;
    public void  setStudent(Student student){
        selectedStudent = student;
        List<Label> labelList = new ArrayList<>();
        labelList.add(label1);
        labelList.add(label2);
        labelList.add(label3);
        labelList.add(label4);
        labelList.add(label5);

        List<TextField> textFieldList = new ArrayList<>();
        textFieldList.add(textField1);
        textFieldList.add(textField2);
        textFieldList.add(textField3);
        textFieldList.add(textField4);
        textFieldList.add(textField5);
        if(student.getGroup().getSubjects().size() == 0){
            examsNotFoundLabel.setVisible(true);
        }
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Student.class, new MarkGsonAdapter(student.getGroup().getSubjects()));
        Gson gson = builder.create();

        ArrayList<Mark> marksList = gson.fromJson(student.getMarks(), new TypeToken<List<Mark>>(){}.getType());

        int i = 0;
        while (i<student.getGroup().getSubjects().size()){
            labelList.get(i).setText(student.getGroup().getSubjects().get(i).getName());
            if(marksList != null) {
                for (Mark m : marksList) {
                    if (m.getSubjectId() == student.getGroup().getSubjects().get(i).getIdSubject()) {
                        textFieldList.get(i).setText(String.valueOf(m.getMark()));
                    }
                }
            }
            i++;
        }
        numberOfExams = i;
        while (i<5){
            textFieldList.get(i).setVisible(false);
            labelList.get(i).setVisible(false);
            i++;
        }
    }

    @FXML
    private void onUpdateButtonClick(ActionEvent event){
        List<TextField> textFieldList = new ArrayList<>();
        textFieldList.add(textField1);
        textFieldList.add(textField2);
        textFieldList.add(textField3);
        textFieldList.add(textField4);
        textFieldList.add(textField5);
        List<Mark> markList = new ArrayList<>();
        try {
            for (int i = 0; i<numberOfExams;i++){
                int mark = Integer.parseInt(textFieldList.get(i).getText());
                if(mark<1 || mark>10){
                    throw new Exception();
                }
                Mark newMark = new Mark();
                newMark.setSubjectId(selectedStudent.getGroup().getSubjects().get(i).getIdSubject());
                newMark.setMark(mark);
                markList.add(newMark);
            }
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Student.class, new MarkGsonAdapter());
            Gson gson = builder.create();
            selectedStudent.setMarks(gson.toJson(markList));
            StudentDtoImpl studentDto = new StudentDtoImpl();
            String request = studentDto.createRequest(selectedStudent, RequestType.UPDATE_STUDENT);
            ClientSocketTCP.send(request);
            String response = ClientSocketTCP.get();
            ResponseType responseType = new Gson().fromJson(response, ResponseType.class);
            if(responseType == ResponseType.SUCCESS){
                Alert("Оценки обновлены успешно!", "Обновление оценок");
                loadStudentsScene(event);
            }
            else
                Alert("Не удалось установить оценки!", "Ошибка");
        }
        catch (Exception e){
            Alert("Поля должны быть заполнены и быть в промежутке от 1 до 10!", "Ошибка ввода");
            return;
        }


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

    public void Alert(String text, String header) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(text);
        alert.setTitle(header);
        alert.showAndWait();
    }
}
