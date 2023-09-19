package com.example.ClientSemestr5.controllers;

import com.example.ClientSemestr5.DTO.DTOImpl.GroupDtoImpl;
import com.example.ClientSemestr5.DTO.DTOImpl.SubjectDtoImpl;
import com.example.ClientSemestr5.Enum.RequestType;
import com.example.ClientSemestr5.Model.entity.Group;
import com.example.ClientSemestr5.Model.entity.Subject;
import com.example.ClientSemestr5.utility.ClientSocketTCP;
import com.example.ClientSemestr5.utility.SessionStorage;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class GroupExamsController{
    @FXML
    private TableView<Subject> thisSubjectsTableView;

    @FXML
    private TableView<Subject> otherSubjectsTableView;

    @FXML
    private TableColumn<Subject, String> thisSubjectNameColumn;

    @FXML
    private TableColumn<Subject, Integer> thisHoursNumberColumn;

    @FXML
    private TableColumn<Subject, String> otherSubjectNameColumn;

    @FXML
    private TableColumn<Subject, Integer> otherHoursNumberColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Label groupNameLabel;

    private Group selectedGroup;

    public void setGroup(Group group){
        selectedGroup = group;
        if(!SessionStorage.isAdmin){
            addButton.setVisible(false);
            deleteButton.setVisible(false);
        }
        groupNameLabel.setText("Группа: " + selectedGroup.getName());
        thisSubjectNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        thisHoursNumberColumn.setCellValueFactory(data -> new SimpleObjectProperty(data.getValue().getHoursNumber()));
        otherSubjectNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        otherHoursNumberColumn.setCellValueFactory(data -> new SimpleObjectProperty(data.getValue().getHoursNumber()));
        setData();
    }


    @FXML
    private void setData(){
        SubjectDtoImpl subjectDto = new SubjectDtoImpl();
        String request = subjectDto.createRequest(new Subject(), RequestType.GET_SUBJECTS);
        ClientSocketTCP.send(request);
        String response = ClientSocketTCP.get();
        ArrayList<Subject> subjectArrayList = subjectDto.getResponseEntityList(response);

        GroupDtoImpl groupDto = new GroupDtoImpl();
        request = groupDto.createRequest(new Group(), RequestType.GET_GROUPS);
        ClientSocketTCP.send(request);
        response = ClientSocketTCP.get();
        ArrayList<Group> groupArrayList = groupDto.getResponseEntityList(response);

        for (Group gr: groupArrayList) {
            if(gr.getName().equals(selectedGroup.getName())){
                selectedGroup = gr;
            }
        }
        for (Subject s: subjectArrayList) {
            s.setGroups(null);
        }
        subjectArrayList.removeAll(selectedGroup.getSubjects());


        ObservableList<Subject> thisSubjectObservableList;
        thisSubjectObservableList = FXCollections.observableArrayList();
        thisSubjectObservableList.addAll(selectedGroup.getSubjects());
        thisSubjectsTableView.setItems(thisSubjectObservableList);


        ObservableList<Subject> otherSubjectObservableList;
        otherSubjectObservableList = FXCollections.observableArrayList();
        otherSubjectObservableList.addAll(subjectArrayList);
        otherSubjectsTableView.setItems(otherSubjectObservableList);
    }


    public void onDeleteButtonClick(ActionEvent event) {
        Subject thisSelectedSubject = thisSubjectsTableView.getSelectionModel().getSelectedItem();
        if(thisSelectedSubject == null)
            return;
        selectedGroup.getSubjects().remove(thisSelectedSubject);

        GroupDtoImpl groupDto = new GroupDtoImpl();
        String request = groupDto.createRequest(selectedGroup, RequestType.UPDATE_EXAM);
        ClientSocketTCP.send(request);
        String response = ClientSocketTCP.get();
        setData();
    }

    public void onAddButtonClick(ActionEvent event) {
        if(selectedGroup.getSubjects().size() >= 5){
            alert("У группы не может быть больше 5 экзаменов!", "Ошибка добавления");
            return;
        }
        Subject otherSelectedSubject = otherSubjectsTableView.getSelectionModel().getSelectedItem();
        if(otherSelectedSubject == null)
            return;
        selectedGroup.getSubjects().add(otherSelectedSubject);

        GroupDtoImpl groupDto = new GroupDtoImpl();
        String request = groupDto.createRequest(selectedGroup, RequestType.UPDATE_EXAM);
        ClientSocketTCP.send(request);
        String response = ClientSocketTCP.get();
        setData();
    }

    public void loadGroupsScene(ActionEvent event) throws IOException {
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

    public void alert(String text, String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(text);
        alert.setTitle(title);
        alert.showAndWait();
    }

}
