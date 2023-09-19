package com.example.ClientSemestr5.controllers;


import com.example.ClientSemestr5.utility.SessionStorage;
import com.example.ClientSemestr5.DTO.ScholarshipListDto;
import com.example.ClientSemestr5.Enum.RequestType;
import com.example.ClientSemestr5.Enum.ResponseType;
import com.example.ClientSemestr5.Model.entity.Scholarship;
import com.example.ClientSemestr5.utility.ClientSocketTCP;
import com.google.gson.Gson;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ScholarshipController extends Menu implements Initializable {

    @FXML
    private TableView<Scholarship> scholarshipTableView;

    @FXML
    private TableColumn<Scholarship, String> markColumn;

    @FXML
    private TableColumn<Scholarship, Double> amountColumn;

    @FXML
    private TableColumn<Scholarship, Double> coefficientColumn;

    @FXML
    private Label errorLabel;

    @FXML
    private Label label1;

    @FXML
    private Button submitButton;

    @FXML
    private TextField baseTextField;

    @FXML
    private ImageView updateImageView;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(!SessionStorage.isAdmin){
            baseTextField.setVisible(false);
            label1.setVisible(false);
            submitButton.setVisible(false);
        }
        markColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMark()));
        amountColumn.setCellValueFactory(data -> new SimpleObjectProperty(data.getValue().getScholarshipAmount()));
        coefficientColumn.setCellValueFactory(data -> new SimpleObjectProperty(data.getValue().getCoefficient()));
        setData();
    }


    @FXML
    private void setData(){
        ScholarshipListDto scholarshipDto = new ScholarshipListDto();
        String request = scholarshipDto.createRequest(new ArrayList<>(), RequestType.GET_SCHOLARSHIP);
        ClientSocketTCP.send(request);
        String response = ClientSocketTCP.get();
        List<Scholarship> list = scholarshipDto.getResponseEntity(response);
        ObservableList<Scholarship> data = FXCollections.observableArrayList();
        data.addAll(list);
        scholarshipTableView.setItems(data);
    }

    @FXML
    private void onUpdateImageViewClick() {
        setData();
        imageViewRotateAnimation(updateImageView);
    }
    @FXML
    private void onSubmitButtonClick(ActionEvent event){

        try {

            double data = Double.parseDouble(baseTextField.getText());
            if(data<=0 || data>1000){
                throw new Exception();
            }
            ScholarshipListDto scholarshipDto = new ScholarshipListDto();
            String request = scholarshipDto.createRequest(new ArrayList<>(), RequestType.SET_SCHOLARSHIP);
            ClientSocketTCP.send(request);

            request = new Gson().toJson(data);
            ClientSocketTCP.send(request);
            String response = ClientSocketTCP.get();
            if(new Gson().fromJson(response, ResponseType.class) == ResponseType.SUCCESS) {
                setData();
            }

        }
        catch (Exception e){
            errorLabel.setText("Недопустимое значение");
        }


    }
}
