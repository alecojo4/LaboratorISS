package com.example.proiectiss;

import com.example.proiectiss.controller.LoginController;
import com.example.proiectiss.repository.*;
import com.example.proiectiss.repository.db.DrugDbRepository;
import com.example.proiectiss.repository.db.OrderDbRepository;
import com.example.proiectiss.repository.db.UserDbRepository;
import com.example.proiectiss.repository.hibernate.DrugHibernateRepository;
import com.example.proiectiss.repository.hibernate.OrderHibernateRepository;
import com.example.proiectiss.repository.hibernate.UserHibernateRepository;
import com.example.proiectiss.repository.utils.HibernateUtils;
import com.example.proiectiss.service.Service;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Main extends Application{
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        LoginController controller = fxmlLoader.getController();
        Properties props = new Properties();
        try {
            props.load(new FileReader("db.properties"));
        } catch (Exception e) {
            throw new RuntimeException("Cannot find db.properties " + e);
        }

//        UserRepository userRepository = new UserDbRepository(props);
//        DrugRepository drugRepository = new DrugDbRepository(props);
//        OrderRepository orderRepository = new OrderDbRepository(props, drugRepository);

        SessionFactory sessionFactory = HibernateUtils.getSessionFactory();

        UserRepository userRepository = new UserHibernateRepository(sessionFactory);
        DrugRepository drugRepository = new DrugHibernateRepository(sessionFactory);
        OrderRepository orderRepository = new OrderHibernateRepository(sessionFactory);

        Service service = new Service(userRepository, drugRepository, orderRepository);

        controller.setService(service, stage);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();

//        sessionFactory.close();
    }

    public static void main(String[] args) {
        launch();
    }
}
