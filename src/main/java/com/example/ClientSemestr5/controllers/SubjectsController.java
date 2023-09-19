package com.example.ClientSemestr5.controllers;

import com.example.ClientSemestr5.DTO.DTOImpl.SubjectDtoImpl;
import com.example.ClientSemestr5.Enum.RequestType;
import com.example.ClientSemestr5.Enum.ResponseType;
import com.example.ClientSemestr5.Model.entity.Subject;
import com.example.ClientSemestr5.utility.ClientSocketTCP;
import com.example.ClientSemestr5.utility.SessionStorage;
import com.google.gson.Gson;
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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class SubjectsController extends Menu implements Initializable {
    @FXML
    private TableView<Subject> subjectsTableView;
    @FXML
    private TableColumn<Subject, String> subjectNameColumn;

    @FXML
    private TableColumn<Subject, Integer> hoursNumberColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button changeButton;

    @FXML
    private Button deleteButton;

    @FXML
    private ImageView updateImageView;

    @FXML
    private void onUpdateImageViewClick() {
        setData();
        imageViewRotateAnimation(updateImageView);
    }

    @FXML
    private void setData(){
        SubjectDtoImpl subjectDto = new SubjectDtoImpl();
        String request = subjectDto.createRequest(new Subject(), RequestType.GET_SUBJECTS);
        ClientSocketTCP.send(request);
        String response = ClientSocketTCP.get();
        ArrayList<Subject> list = subjectDto.getResponseEntityList(response);
        ObservableList<Subject> subjectObservableList;
        subjectObservableList = FXCollections.observableArrayList();
        subjectObservableList.addAll(list);
        subjectsTableView.setItems(subjectObservableList);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(!SessionStorage.isAdmin){
            addButton.setVisible(false);
            changeButton.setVisible(false);
            deleteButton.setVisible(false);
        }
        subjectNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        hoursNumberColumn.setCellValueFactory(data -> new SimpleObjectProperty(data.getValue().getHoursNumber()));
        setData();
    }

    @FXML
    private void onAddButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("SubjectAddChange.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        darkThemeCheck(scene);
        stage.show();
    }

    @FXML
    private void onChangeButtonClick(ActionEvent event) throws IOException {
        Subject selectedSubject = subjectsTableView.getSelectionModel().getSelectedItem();
        if(selectedSubject == null)
            return;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("SubjectAddChange.fxml"));
        Parent root = loader.load();

        SubjectAddChangeController controller = loader.getController();
        controller.itIsChangeScene(selectedSubject);


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        darkThemeCheck(scene);
        stage.setScene(scene);
    }

    @FXML
    private void onDeleteButtonClick(ActionEvent event){
        Subject selectedSubject = subjectsTableView.getSelectionModel().getSelectedItem();
        if(selectedSubject == null)
            return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Вы точно хотите удалить предмет?");
        alert.setTitle("Подтверждение");

        Optional<ButtonType> result = alert.showAndWait();
        if(!result.isPresent() || result.get() != ButtonType.OK) {
            return;
        } else {
            SubjectDtoImpl subjectDto = new SubjectDtoImpl();
            String request = subjectDto.createRequest(selectedSubject, RequestType.DELETE_SUBJECT);
            ClientSocketTCP.send(request);
            String  response = ClientSocketTCP.get();
            ResponseType responseType = new Gson().fromJson(response, ResponseType.class);
            if(responseType == ResponseType.SUCCESS)
                setData();
            else
                alert("Ошибка на сервербе", "Не удалось удалить предмет!");
        }
    }

    @FXML
    private void onStatisticButtonClick(ActionEvent event) throws IOException {
        Subject selectedSubject = subjectsTableView.getSelectionModel().getSelectedItem();
        if(selectedSubject == null){
            return;
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("SubjectStatistic.fxml"));
        Parent root = loader.load();

        SubjectStatisticController controller = loader.getController();
        controller.setData(selectedSubject);


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        darkThemeCheck(scene);
        stage.setScene(scene);
    }
}
