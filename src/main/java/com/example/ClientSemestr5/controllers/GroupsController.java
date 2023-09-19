package com.example.ClientSemestr5.controllers;

import com.example.ClientSemestr5.DTO.DTOImpl.FacultyDtoImpl;
import com.example.ClientSemestr5.DTO.DTOImpl.GroupDtoImpl;
import com.example.ClientSemestr5.Enum.RequestType;
import com.example.ClientSemestr5.Enum.ResponseType;
import com.example.ClientSemestr5.Model.entity.Faculty;
import com.example.ClientSemestr5.Model.entity.Group;
import com.example.ClientSemestr5.Model.entity.Student;
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

public class GroupsController extends Menu implements Initializable {
    @FXML
    private TableView<Group> groupsTableView;

    @FXML
    private TableColumn<Group, String> groupNameColumn;

    @FXML
    private TableColumn<Group, String> facultyNameColumn;
    @FXML
    private TableColumn<Group, Integer> studentNumberColumn;
    @FXML
    private TableColumn<Group, Integer> hourNumberColumn;

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
        groupNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        facultyNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFaculty().getFacultyName()));
        studentNumberColumn.setCellValueFactory(data -> new SimpleObjectProperty(data.getValue().getStudents().size()));
        hourNumberColumn.setCellValueFactory(data -> {
            int hourNumber = 0;
            for (Subject s : data.getValue().getSubjects()) {
                hourNumber += s.getHoursNumber();
            }
            return new SimpleObjectProperty(hourNumber);
        });
        setData();
    }

    @FXML
    private void setData(){
        GroupDtoImpl groupDto = new GroupDtoImpl();
        String request = groupDto.createRequest(new Group(), RequestType.GET_GROUPS);
        ClientSocketTCP.send(request);
        String response = ClientSocketTCP.get();
        ArrayList<Group> list = groupDto.getResponseEntityList(response);
        ObservableList<Group> groupObservableList;
        groupObservableList = FXCollections.observableArrayList();
        groupObservableList.addAll(list);
        groupsTableView.setItems(groupObservableList);
    }

    @FXML
    private void onUpdateImageViewClick() {
        setData();
        imageViewRotateAnimation(updateImageView);
    }



    @FXML
    private void onAddButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("GroupAddChange.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        darkThemeCheck(scene);
        stage.show();
    }

    @FXML
    private void onChangeButtonClick(ActionEvent event) throws IOException {
        Group selectedGroup = groupsTableView.getSelectionModel().getSelectedItem();
        if(selectedGroup == null)
            return;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("GroupAddChange.fxml"));
        Parent root = loader.load();

        GroupAddChangeController controller = loader.getController();
        controller.itIsChangeScene(selectedGroup);


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        darkThemeCheck(scene);
        stage.setScene(scene);
    }

    @FXML
    private void onDeleteButtonClick(ActionEvent event){
        Group selectedGroup = groupsTableView.getSelectionModel().getSelectedItem();
        if(selectedGroup == null)
            return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Вы точно хотите удалить группу?");
        alert.setTitle("Подтверждение");
        alert.setContentText("Удалятся все связанные с ней студенты");
        Optional<ButtonType> result = alert.showAndWait();
        if(!result.isPresent() || result.get() != ButtonType.OK) {
            return;
        } else {
            GroupDtoImpl groupDto = new GroupDtoImpl();
            String request = groupDto.createRequest(selectedGroup, RequestType.DELETE_GROUP);
            ClientSocketTCP.send(request);
            String  response = ClientSocketTCP.get();
            ResponseType responseType = new Gson().fromJson(response, ResponseType.class);
            if(responseType == ResponseType.SUCCESS)
                setData();
            else
                alert("Ошибка на сервербе", "Не удалось удалить группу!");
        }

    }

    public void onExamsButtonClick(ActionEvent event) throws IOException {
        Group selectedGroup = groupsTableView.getSelectionModel().getSelectedItem();
        if(selectedGroup == null)
            return;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("GroupExams.fxml"));
        Parent root = loader.load();

        GroupExamsController controller = loader.getController();
        controller.setGroup(selectedGroup);


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        darkThemeCheck(scene);
        stage.setScene(scene);
    }
}
