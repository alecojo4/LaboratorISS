package com.example.proiectiss.controller;

import com.example.proiectiss.service.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class SignUpController {

    private Service service;
    private Stage stage;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private RadioButton pharmacistRadioButton;

    @FXML
    private RadioButton workerRadioButton;

    public void setService(Service service, Stage stage){
        this.service = service;
        this.stage = stage;
        clearFields(true);
        ToggleGroup group = new ToggleGroup();
        pharmacistRadioButton.setToggleGroup(group);
        workerRadioButton.setToggleGroup(group);
    }

    public void clearFields(boolean username){
        if (username){
            usernameField.clear();
        }
        passwordField.clear();
        confirmPasswordField.clear();
    }

    @FXML
    public void changeToLogin(){
        try {
            stage.close();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            LoginController loginController = fxmlLoader.getController();
            loginController.setService(service, stage);
            stage.setTitle("Login");
            stage.show();
        }catch (Exception e){
            MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "FXMLLoader error", e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void handleSignUp(){
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            String role = "";
            if (pharmacistRadioButton.isSelected()){
                role = "pharmacist";
            }
            else if (workerRadioButton.isSelected()){
                role = "worker";
            }
            else{
                MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "Sign Up Error", "Selectati un rol.");
                return;
            }

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
                MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "Sign Up Error", "Toate campurile sunt obligatorii.");
                return;
            }
            if (!password.equals(confirmPassword)){
                MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "Sign Up Error", "Parolele nu coincid.");
                clearFields(false);
                return;
            }
            if (service.register(username, password, role)){
                MessageAlert.showMessage(stage, Alert.AlertType.CONFIRMATION, "Sign Up", "Cont creat cu succes.");
                changeToLogin();
            }
            else{
                MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "Sign Up Error", "Exista deja un cont cu acest username.");
                clearFields(true);
            }
        }catch(Exception e){
            MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "Sign Up Error", e.getMessage());
            clearFields(true);
        }
    }
}
