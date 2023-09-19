package com.example.ClientSemestr5.controllers;

import com.example.ClientSemestr5.DTO.DTOImpl.StudentDtoImpl;
import com.example.ClientSemestr5.DTO.ScholarshipListDto;
import com.example.ClientSemestr5.Enum.RequestType;
import com.example.ClientSemestr5.Enum.ResponseType;
import com.example.ClientSemestr5.Model.entity.Mark;
import com.example.ClientSemestr5.Model.entity.Scholarship;
import com.example.ClientSemestr5.Model.entity.Student;
import com.example.ClientSemestr5.Model.entity.Subject;
import com.example.ClientSemestr5.utility.ClientSocketTCP;
import com.example.ClientSemestr5.utility.MarkGsonAdapter;
import com.example.ClientSemestr5.utility.SessionStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.apache.commons.math3.util.Precision;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class StudentsController extends Menu implements Initializable {
    @FXML
    private TableView<Student> studentsTableView;


    @FXML
    private TableColumn<Student, String> firstnameColumn;

    @FXML
    private TableColumn<Student, String> lastnameColumn;

    @FXML
    private TableColumn<Student, String> groupNameColumn;

    @FXML
    private TableColumn<Student, Double> averageMarkColumn;

    @FXML
    private TableColumn<Student, String> isBudgetColumn;

    @FXML
    private TableColumn<Student, Double> scholarshipColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button changeButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button fileReportButton;

    @FXML
    private Button gradesButton;

    @FXML
    private ImageView updateImageView;

    private List<Student> students;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ScholarshipListDto scholarshipListDto = new ScholarshipListDto();
        String request = scholarshipListDto.createRequest(new ArrayList<>(), RequestType.GET_SCHOLARSHIP);
        ClientSocketTCP.send(request);
        String response = ClientSocketTCP.get();
        List<Scholarship> scholarshipList = scholarshipListDto.getResponseEntity(response);

        if(!SessionStorage.isAdmin){
            addButton.setVisible(false);
            changeButton.setVisible(false);
            deleteButton.setVisible(false);
            gradesButton.setVisible(false);
            fileReportButton.setLayoutX(380);
            fileReportButton.setLayoutX(380);
        }
        firstnameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFirstname()));
        lastnameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLastname()));
        groupNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGroup().getName()));

        averageMarkColumn.setCellValueFactory(data -> {
            double totalMark = 0;
            int markNumber = 0;
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Student.class, new MarkGsonAdapter(data.getValue().getGroup().getSubjects()));
            Gson gson = builder.create();

            ArrayList<Mark> list = gson.fromJson(data.getValue().getMarks(), new TypeToken<List<Mark>>(){}.getType());
            if (list == null)
                return new SimpleObjectProperty("Нет оценок");

            boolean isNoMarks = true;
            for(Mark m : list){
                if(m.getMark()!=0) {
                    for(Subject s: data.getValue().getGroup().getSubjects())
                        if(m.getSubjectId() == s.getIdSubject()) {
                            totalMark += m.getMark();
                            markNumber++;
                            isNoMarks = false;
                        }
                }
            }
            if (isNoMarks)
                return new SimpleObjectProperty("Нет оценок");
            Double averageMark = Precision.round(totalMark/markNumber, 2);
            data.getValue().setAverageMark(averageMark);
            return new SimpleObjectProperty(averageMark);
        });


        isBudgetColumn.setCellValueFactory(data -> {
            if(data.getValue().isBudget()) {
                return new SimpleStringProperty("Да");
            }
            return new SimpleStringProperty("Нет");
        });

        scholarshipColumn.setCellValueFactory(data -> {
            if(!data.getValue().isBudget()){
                return new SimpleObjectProperty(0);
            }

            Double av = data.getValue().getAverageMark();
            if(av == null){
                return new SimpleObjectProperty(0);
            }
            for (Scholarship sc: scholarshipList){
                int from = sc.getFromMark();
                int to = sc.getToMark();
                if (to == 10){
                    to = 11;
                }
                if(av>=from && av<to){
                    return new SimpleObjectProperty(sc.getScholarshipAmount());
                }
            }
            return new SimpleObjectProperty(0);
        });
        setData();
    }

    @FXML
    private void setData(){
        StudentDtoImpl studentDto = new StudentDtoImpl();
        String request = studentDto.createRequest(new Student(), RequestType.GET_STUDENTS);
        ClientSocketTCP.send(request);
        String response = ClientSocketTCP.get();
        students = studentDto.getResponseEntityList(response);
        ObservableList<Student> facultyObservableList;
        facultyObservableList = FXCollections.observableArrayList();
        facultyObservableList.addAll(students);
        studentsTableView.setItems(facultyObservableList);
    }

    @FXML
    private void onGradesButtonClick(ActionEvent event) throws IOException {
        Student selectedStudent = studentsTableView.getSelectionModel().getSelectedItem();
        if(selectedStudent == null)
            return;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("StudentsGrade.fxml"));
        Parent root = loader.load();

        StudentsGradeController controller = loader.getController();
        controller.setStudent(selectedStudent);


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        darkThemeCheck(scene);
        stage.setScene(scene);
    }

    @FXML
    private void onUpdateImageViewClick() {
        setData();
        imageViewRotateAnimation(updateImageView);
    }

    @FXML
    private void onDeleteButtonClick(ActionEvent event){
        Student selectedStudent = studentsTableView.getSelectionModel().getSelectedItem();
        if(selectedStudent == null)
            return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Вы точно хотите удалить студента?");
        alert.setTitle("Подтверждение");
        Optional<ButtonType> result = alert.showAndWait();
        if(!result.isPresent() || result.get() != ButtonType.OK) {
            return;
        } else {
            StudentDtoImpl studentDto = new StudentDtoImpl();
            String request = studentDto.createRequest(selectedStudent, RequestType.DELETE_STUDENT);
            ClientSocketTCP.send(request);
            String  response = ClientSocketTCP.get();
            ResponseType responseType = new Gson().fromJson(response, ResponseType.class);
            if(responseType == ResponseType.SUCCESS)
                setData();
            else
                alert("Ошибка на сервербе", "Не удалось удалить студента!");
        }

    }

    @FXML
    private void onChangeButtonClick(ActionEvent event) throws IOException {
        Student selectedStudent = studentsTableView.getSelectionModel().getSelectedItem();
        if(selectedStudent == null)
            return;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("StudentAddChange.fxml"));
        Parent root = loader.load();

        StudentAddChangeController controller = loader.getController();
        controller.itIsChangeScene(selectedStudent);


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        darkThemeCheck(scene);
        stage.setScene(scene);
    }

    @FXML
    private void onAddButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("StudentAddChange.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        darkThemeCheck(scene);
        stage.show();
    }


    @FXML
    private void onFileReportButtonClick(ActionEvent event){
        try {
            // к которой будем прикручивать наполнение (колонтитулы, текст)
            if (students == null){
                throw  new Exception();
            }
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH-mm-ss");
            File file = new File("Reports/Отчёт о студентах " + formatter.format(date) + ".txt");
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
            StringBuffer stringBuffer = new StringBuffer();
            for (Student s : students) {
                stringBuffer.append("Студент: " + s.getLastname() + " " + s.getFirstname())
                        .append(" Группа: " + s.getGroup().getName())
                        .append(" Стипендия: " + getScholarship(s) + "\n");

            }
            fos.write(stringBuffer.toString().getBytes());
            fos.close();
            alert("Отчёт успешно сформирован", "Отчёт");
        } catch (Exception e) {
            alert("Не удалось сформировать отчёт", "Отчёт");
            e.printStackTrace();
        }



    }

    private String getScholarship(Student student){
        if(!student.isBudget()){
            return  "0";
        }
        ScholarshipListDto scholarshipListDto = new ScholarshipListDto();
        String request = scholarshipListDto.createRequest(new ArrayList<>(), RequestType.GET_SCHOLARSHIP);
        ClientSocketTCP.send(request);
        String response = ClientSocketTCP.get();
        List<Scholarship> list = scholarshipListDto.getResponseEntity(response);
        Double av = student.getAverageMark();
        if(av == null){
            return "0";
        }
        for (Scholarship sc: list){
            int from = sc.getFromMark();
            int to = sc.getToMark();
            if (to == 10){
                to = 11;
            }
            if(av>=from && av<to){
                return String.valueOf(sc.getScholarshipAmount());
            }
        }
        return "0";
    }


}
