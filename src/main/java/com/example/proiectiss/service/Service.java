package com.example.proiectiss.service;

import com.example.proiectiss.domain.*;
import com.example.proiectiss.observer.Event;
import com.example.proiectiss.observer.Observable;
import com.example.proiectiss.observer.Observer;
import com.example.proiectiss.observer.UserLogoutEvent;
import com.example.proiectiss.repository.DrugRepository;
import com.example.proiectiss.repository.OrderRepository;
import com.example.proiectiss.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Service implements Observable<Event> {
    private final UserRepository userRepository;
    private final DrugRepository drugRepository;
    private final OrderRepository orderRepository;
    private final List<Observer<Event>> observers = new ArrayList<>();

    public Service(UserRepository userRepository, DrugRepository drugRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.drugRepository = drugRepository;
        this.orderRepository = orderRepository;
    }

    public Optional<User> login(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()){
            if (user.get().getPassword().equals(password)){
                System.out.println("Login successful");
                return user;
            }
            else{
                throw new IllegalArgumentException("Invalid username or password");
            }
        }
        else{
            throw new IllegalArgumentException("User not found");
        }
    }

    public boolean register(String username, String password, String role) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()){
            throw new IllegalArgumentException("Username already exists");
        }
        user = userRepository.save(new User(username, password, role));
        if (user.isPresent()){
            System.out.println("Registered successfully");
            return true;
        }
        return false;
    }

    public List<Drug> getAllDrugs(){
        return drugRepository.findAll();
    }

    public Drug getDrugById(int id){
        return drugRepository.findOne(id).orElseThrow(() -> new IllegalArgumentException("Drug not found"));
    }

    public void saveNewOrder(String username, List<OrderItem> orderItems){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = now.format(formatter);
        Order order = new Order(username, Status.PENDING, LocalDateTime.parse(formattedDate, formatter));
        orderRepository.saveOrder(order, orderItems);
        notify(null);
    }

    public List<Order> getAllOrders(String section){
        return orderRepository.getAllOrdersForSection(section);
    }

    public List<Order> getAllOrders(){
        return orderRepository.findAll();
    }

    public List<OrderItem> getAllOrderItemsForOrder(Integer orderId){
        return orderRepository.getAllOrderItemsForOrder(orderId);
    }

    public void honourOrder(Order order){
        orderRepository.honourOrder(order);
        notify(null);
    }

    public void addDrug(Drug drug){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = drug.getDateProduced().format(formatter);
        drug.setDateProduced(LocalDate.parse(formattedDate));
        System.out.println(drug.getDateProduced());
        drugRepository.save(drug);
        notify(null);
    }

    public void deleteDrug(Integer id){
        drugRepository.delete(id);
        notify(null);
    }

    public void updateDrug(Drug drug){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = drug.getDateProduced().format(formatter);
        drug.setDateProduced(LocalDate.parse(formattedDate));
        drugRepository.update(drug);
        notify(null);
    }

    public void logout(User user){
        notify(new UserLogoutEvent(user));
    }

    @Override
    public void addObserver(Observer<Event> o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer<Event> o) {
        observers.remove(o);
    }

    @Override
    public void notify(Event t) {
        observers.forEach(o -> o.update(t));
    }
}
