package com.example.proiectiss.controller;

import com.example.proiectiss.domain.*;
import com.example.proiectiss.observer.Event;
import com.example.proiectiss.observer.Observer;
import com.example.proiectiss.observer.UserLogoutEvent;
import com.example.proiectiss.service.Service;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;

public class HospitalWorkerController implements Observer<Event> {
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
    @FXML
    TextField textFieldQuantity;
    @FXML
    TableView<OrderItem> tableViewCurrentOrder;
    @FXML
    TableColumn<OrderItem, String> tableColumnCurrentOrderDrugName;
    @FXML
    TableColumn<OrderItem, String> tableColumnCurrentOrderQuantity;
    @FXML
    ObservableList<OrderItem> currentOrderList = FXCollections.observableArrayList();
    @FXML
    TableView<Order> tableViewAllOrders;
    @FXML
    TableColumn<Order, Status> tableColumnAllOrdersStatus;
    @FXML
    TableColumn<Order, String> tableColumnAllOrdersRegisteredAt;
    @FXML
    ObservableList<Order> allOrdersList = FXCollections.observableArrayList();
    private int selectedDrugId;

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
        service.addObserver(this);
        init();
        refreshAllOrdersTable();
    }

    public void init(){
        tableColumnDrugName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnDrugDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        tableColumnDrugDateProduced.setCellValueFactory(new PropertyValueFactory<>("dateProduced"));
        drugList.setAll(service.getAllDrugs());
        tableViewDrugs.setItems(drugList);
        tableViewDrugs.refresh();
    }

    public void refreshCurrentOrder(){
        tableColumnCurrentOrderDrugName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDrug().getName()));
        tableColumnCurrentOrderQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tableViewCurrentOrder.setItems(currentOrderList);
        tableViewCurrentOrder.refresh();
    }

    public void refreshAllOrdersTable(){
        tableColumnAllOrdersStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        tableColumnAllOrdersRegisteredAt.setCellValueFactory(new PropertyValueFactory<>("registeredAt"));
        allOrdersList.setAll(service.getAllOrders(user.getUsername()));
        tableViewAllOrders.setItems(allOrdersList);
        tableViewAllOrders.refresh();
    }

    @FXML
    public void handleLogout(){
        service.logout(user);
        //service.removeObserver(this);
        stage.close();
    }

    @FXML
    public void handleAddToNewOrder(){
        int quantity;
        try{
            quantity = Integer.parseInt(textFieldQuantity.getText());
        }catch(NumberFormatException e){
            MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "Quantity error", "Quantity must be an integer");
            return;
        }
        if(quantity <= 0){
            MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "Quantity error", "Quantity must be greater than 0");
            return;
        }

        if (tableViewDrugs.getSelectionModel().isEmpty()){
            MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "Drug selection error", "Please select a drug");
            return;
        }

        boolean found = false;
        for (OrderItem orderItem : currentOrderList) {
            if (orderItem.getDrug().getId().equals(selectedDrugId)) {
                orderItem.setQuantity(quantity);
                found = true;
                break;
            }
        }

        if (!found) {
            currentOrderList.add(new OrderItem(service.getDrugById(selectedDrugId), quantity, 0));
        }

        refreshCurrentOrder();
        textFieldQuantity.clear();
    }

    @FXML
    public void handleRemoveFromNewOrder(){
        if (tableViewCurrentOrder.getSelectionModel().isEmpty()){
            MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "Order item selection error", "Please select an order item");
            return;
        }
        currentOrderList.remove(tableViewCurrentOrder.getSelectionModel().getSelectedItem());
        refreshCurrentOrder();
    }

    @FXML
    public void registerNewOrder(){
        if (currentOrderList.isEmpty()){
            MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "Order error", "Please add at least one item to the order");
            return;
        }

        try {
            service.saveNewOrder(user.getUsername(), currentOrderList);
        } catch (Exception e) {
            MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "Order error", e.getMessage());
            return;
        }
        currentOrderList.clear();
        refreshCurrentOrder();
        refreshAllOrdersTable();

        MessageAlert.showMessage(stage, Alert.AlertType.INFORMATION, "Order registered", "Order registered successfully");
    }

    @Override
    public void update(Event event) {
        if (!(event instanceof UserLogoutEvent)){
            init();
            refreshAllOrdersTable();
        }
    }
}
