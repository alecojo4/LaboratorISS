package com.example.proiectiss.controller;

import com.example.proiectiss.domain.User;
import com.example.proiectiss.observer.Event;
import com.example.proiectiss.observer.Observer;
import com.example.proiectiss.observer.UserLogoutEvent;
import com.example.proiectiss.service.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LoginController implements Observer<Event> {

    private Service service;
    private Stage stage;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private final List<User> loggedUsers = new ArrayList<>();

    public void setService(Service service, Stage stage){
        this.service = service;
        this.stage = stage;
        service.addObserver(this);

    }

    public void clearFields(){
        usernameField.clear();
        passwordField.clear();
    }

    @FXML
    public void changeToSignUp(){
        try {
            stage.close();

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("signUp.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);

            SignUpController signUpController = fxmlLoader.getController();
            signUpController.setService(service, stage);

            stage.setTitle("Sign Up");
            stage.show();
        }catch (Exception e){
            MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "FXMLLoader error", e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void handleLogin(){
        String username = usernameField.getText();
        String password = passwordField.getText();
        try {
            Optional<User> user = service.login(username, password);
            if (user.isPresent()){
                System.out.println(loggedUsers);
                if (loggedUsers.contains(user.get())){
                    MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "Login Error", "User already logged in.");
                    clearFields();
                    return;
                }
                loggedUsers.add(user.get());
                clearFields();
                MessageAlert.showMessage(stage, Alert.AlertType.INFORMATION, "Login", "Login successful.");
                if (user.get().getRole().equals("pharmacist")){
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("view-farmacist.fxml"));

                    Scene scene = new Scene(fxmlLoader.load());
                    Stage newStage = new Stage();
                    newStage.setScene(scene);

                    PharmacistController pharmacistController = fxmlLoader.getController();
                    pharmacistController.setService(service, newStage, user.get());

                    newStage.setTitle("Farmacist - " + user.get().getUsername());
                    newStage.show();
                }
                else {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("view-personalSectie.fxml"));

                    Scene scene = new Scene(fxmlLoader.load());
                    Stage newStage = new Stage();
                    newStage.setScene(scene);

                    HospitalWorkerController hospitalWorkerController = fxmlLoader.getController();
                    hospitalWorkerController.setService(service, user.get(), newStage);

                    newStage.setTitle("Personal Sectie - " + user.get().getUsername());
                    newStage.show();
                }
            }
            else{
                MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "Login Error", "Username or password incorrect.");
                clearFields();
            }
        }catch (Exception e){
            MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "Login Error", e.getMessage());
            clearFields();
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Event event) {
        if (event instanceof UserLogoutEvent){
            System.out.println("Logout event");
            User user = (User) event.getData();
            loggedUsers.remove(user);
        }
    }
}
