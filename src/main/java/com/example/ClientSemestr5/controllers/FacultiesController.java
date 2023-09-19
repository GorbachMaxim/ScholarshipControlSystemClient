package com.example.ClientSemestr5.controllers;

import com.example.ClientSemestr5.DTO.DTOImpl.FacultyDtoImpl;
import com.example.ClientSemestr5.Enum.RequestType;
import com.example.ClientSemestr5.Enum.ResponseType;
import com.example.ClientSemestr5.Model.entity.Faculty;
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

public class FacultiesController extends Menu implements Initializable {
    @FXML
    private TableView<Faculty> facultiesTableView;

    @FXML
    private TableColumn<Faculty, String> facultyNameColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button changeButton;

    @FXML
    private Button deleteButton;

    @FXML
    private ImageView updateImageView;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(!SessionStorage.isAdmin){
            addButton.setVisible(false);
            changeButton.setVisible(false);
            deleteButton.setVisible(false);
        }
        facultyNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFacultyName()));
        setData();
    }

    @FXML
    private void setData(){
        FacultyDtoImpl facultyDto = new FacultyDtoImpl();
        String request = facultyDto.createRequest(new Faculty(), RequestType.GET_FACULTIES);
        ClientSocketTCP.send(request);
        String response = ClientSocketTCP.get();
        ArrayList<Faculty> list = facultyDto.getResponseEntityList(response);
        ObservableList<Faculty> facultyObservableList;
        facultyObservableList = FXCollections.observableArrayList();
        facultyObservableList.addAll(list);
        facultiesTableView.setItems(facultyObservableList);
    }

    @FXML
    private void onUpdateImageViewClick() {
        setData();
        imageViewRotateAnimation(updateImageView);
    }



    @FXML
    private void onAddButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("FacultyAddChange.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        darkThemeCheck(scene);
        stage.show();
    }

    @FXML
    private void onChangeButtonClick(ActionEvent event) throws IOException {
        Faculty selectedFaculty = facultiesTableView.getSelectionModel().getSelectedItem();
        if(selectedFaculty == null)
            return;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("FacultyAddChange.fxml"));
        Parent root = loader.load();

        FacultyAddChangeController controller = loader.getController();
        controller.itIsChangeScene(selectedFaculty);


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        darkThemeCheck(scene);
        stage.setScene(scene);
    }

    @FXML
    private void onDeleteButtonClick(ActionEvent event){
        Faculty selectedFaculty = facultiesTableView.getSelectionModel().getSelectedItem();
        if(selectedFaculty == null)
            return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Вы точно хотите удалить факультет?");
        alert.setTitle("Подтверждение");
        alert.setContentText("Удалятся все связанные с ним пользователи и группы");
        Optional<ButtonType> result = alert.showAndWait();
        if(!result.isPresent() || result.get() != ButtonType.OK) {
            return;
        } else {
            FacultyDtoImpl facultyDto = new FacultyDtoImpl();
            String request = facultyDto.createRequest(selectedFaculty, RequestType.DELETE_FACULTY);
            ClientSocketTCP.send(request);
            String  response = ClientSocketTCP.get();
            ResponseType responseType = new Gson().fromJson(response, ResponseType.class);
            if(responseType == ResponseType.SUCCESS)
                setData();
            else
                alert("Ошибка на сервербе", "Не удалось удалить факультет!");
        }

    }
}
