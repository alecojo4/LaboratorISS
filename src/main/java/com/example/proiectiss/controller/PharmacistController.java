package com.example.proiectiss.controller;

import com.example.proiectiss.domain.Order;
import com.example.proiectiss.domain.OrderItem;
import com.example.proiectiss.domain.Status;
import com.example.proiectiss.domain.User;
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
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class PharmacistController implements Observer<Event> {
    private Service service;
    private Stage stage;
    private User user;
    private Order order;

    @FXML
    TableView<Order> tableViewAllOrders;
    @FXML
    TableColumn<Order, String> tableColumnAllOrdersSection;
    @FXML
    TableColumn<Order, Status> tableColumnAllOrdersStatus;
    @FXML
    TableColumn<Order, String> tableColumnAllOrdersRegisteredAt;
    @FXML
    ObservableList<Order> allOrdersList = FXCollections.observableArrayList();

    @FXML
    TableView<OrderItem> tableViewOrderItems;
    @FXML
    TableColumn<OrderItem, String> tableColumnDrugName;
    @FXML
    TableColumn<OrderItem, Integer> tableColumnDrugQuantity;
    @FXML
    ObservableList<OrderItem> orderItemsList = FXCollections.observableArrayList();

    public void setService(Service service, Stage stage, User user){
        this.service = service;
        this.stage = stage;
        this.user = user;
        service.addObserver(this);
        init();
    }

    public void init(){
        tableColumnAllOrdersSection.setCellValueFactory(new PropertyValueFactory<>("username"));
        tableColumnAllOrdersStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        tableColumnAllOrdersRegisteredAt.setCellValueFactory(new PropertyValueFactory<>("registeredAt"));
        allOrdersList.setAll(service.getAllOrders());
        tableViewAllOrders.setItems(allOrdersList);
        tableViewAllOrders.refresh();

        tableViewAllOrders.setOnMouseClicked(event -> {
            refreshSelectedOrder();
        });
    }

    public void refreshSelectedOrder(){
        order = tableViewAllOrders.getSelectionModel().getSelectedItem();
        if (order != null){
            tableColumnDrugName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDrug().getName()));
            tableColumnDrugQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            orderItemsList.setAll(service.getAllOrderItemsForOrder(order.getId()));
            tableViewOrderItems.setItems(orderItemsList);
            tableViewOrderItems.refresh();
        }
    }

    @FXML
    public void handleLogout(){
        //service.removeObserver(this);
        service.logout(user);
        stage.close();
    }

    @FXML
    public void handleHonourOrder(){
        order = tableViewAllOrders.getSelectionModel().getSelectedItem();
        if (order == null){
            MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "Error", "No order selected");
        }
        else{
            service.honourOrder(order);
            init();
            tableViewAllOrders.refresh();
        }
    }

    public void manageDrugs(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("manageDrugs.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            Stage newStage = new Stage();
            newStage.setScene(scene);

            ManageDrugsController manageDrugsController = fxmlLoader.getController();
            manageDrugsController.setService(service, user, newStage);

            newStage.setTitle("Manage Drugs " + user.getUsername());
            newStage.show();
        }catch (Exception e){
            MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "FXMLLoader error", e.getMessage());
        }
    }

    @Override
    public void update(Event event) {
        if (!(event instanceof UserLogoutEvent)) {
            init();
        }
    }
}
