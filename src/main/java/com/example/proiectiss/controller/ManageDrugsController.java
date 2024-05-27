package com.example.proiectiss.controller;

import com.example.proiectiss.domain.Drug;
import com.example.proiectiss.domain.User;
import com.example.proiectiss.observer.Event;
import com.example.proiectiss.observer.Observer;
import com.example.proiectiss.observer.UserLogoutEvent;
import com.example.proiectiss.service.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;

public class ManageDrugsController implements Observer<Event> {
    private Service service;
    private User user;
    private Stage stage;

    @FXML
    TableView<Drug> tableViewDrugs;
    @FXML
    TableColumn<Drug, String> tableColumnDrugName;
    @FXML
    TableColumn<Drug, String> tableColumnDrugDescription;
    @FXML
    TableColumn<Drug, LocalDate> tableColumnDrugDateProduced;
    @FXML
    ObservableList<Drug> drugList = FXCollections.observableArrayList();

    private Integer selectedDrugId;

    @FXML
    private TextField textFieldName;
    @FXML
    private TextField textFieldDescription;
    @FXML
    private DatePicker datePickerDateProduced;


    public void setService(Service service, User user, Stage stage){
        this.service = service;
        this.user = user;
        this.stage = stage;
        drugList.setAll(service.getAllDrugs());
        tableViewDrugs.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedDrugId = newSelection.getId();
            }
        });
        tableViewDrugs.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                textFieldName.setText(newSelection.getName());
                textFieldDescription.setText(newSelection.getDescription());
                datePickerDateProduced.setValue(newSelection.getDateProduced());
            }
        });
        service.addObserver(this);
        init();
    }

    public void init(){
        tableColumnDrugName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnDrugDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        tableColumnDrugDateProduced.setCellValueFactory(new PropertyValueFactory<>("dateProduced"));
        tableViewDrugs.setItems(drugList);
        tableViewDrugs.refresh();
    }

    @FXML
    public void addDrug(){
        String name = textFieldName.getText();
        String description = textFieldDescription.getText();
        LocalDate dateProduced = datePickerDateProduced.getValue();
        Drug drug = new Drug(name, description, dateProduced);
        service.addDrug(drug);
        drugList.setAll(service.getAllDrugs());
        init();
    }

    @FXML
    public void deleteDrug(){
        service.deleteDrug(selectedDrugId);
        drugList.setAll(service.getAllDrugs());
        init();
    }

    @FXML
    public void updateDrug(){
        String name = textFieldName.getText();
        String description = textFieldDescription.getText();
        LocalDate dateProduced = datePickerDateProduced.getValue();
        Drug drug = new Drug(name, description, dateProduced);
        drug.setId(selectedDrugId);
        service.updateDrug(drug);
        drugList.setAll(service.getAllDrugs());
        init();
    }

    @Override
    public void update(Event event) {
        if (!(event instanceof UserLogoutEvent)) {
            drugList.setAll(service.getAllDrugs());
            init();
        }
    }
}
