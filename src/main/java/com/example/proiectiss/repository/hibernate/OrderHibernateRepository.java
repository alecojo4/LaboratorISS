package com.example.proiectiss.repository.hibernate;

import com.example.proiectiss.domain.Order;
import com.example.proiectiss.domain.OrderItem;
import com.example.proiectiss.domain.Status;
import com.example.proiectiss.repository.OrderRepository;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.SelectionQuery;

import java.util.List;
import java.util.Optional;


public class OrderHibernateRepository implements OrderRepository {
    private final SessionFactory sessionFactory;

    public OrderHibernateRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<Order> findOne(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Order order = session.get(Order.class, id);
            return Optional.ofNullable(order);
        }
    }

    @Override
    public List<Order> findAll() {
        try (Session session = sessionFactory.openSession()){
            Status status = Status.valueOf("PENDING");
            SelectionQuery<Order> query = session.createSelectionQuery("from Order where status=:status order by registeredAt ", Order.class);
            query.setParameter("status", status);
            return query.getResultList();
        }
    }

    @Override
    public Optional<Order> save(Order entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
            return Optional.of(entity);
        }catch(Exception e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<Order> delete(Integer id) {
        return Optional.empty();
    }

    @Override
    public Optional<Order> update(Order entity) {
        return Optional.empty();
    }

    @Override
    public List<Order> getAllOrdersForSection(String username) {
        try (Session session = sessionFactory.openSession()) {
            return session.createSelectionQuery("from Order where username=:registeredBy order by registeredAt ", Order.class)
                    .setParameter("registeredBy", username)
                    .list();
        }
    }

    @Override
    public void honourOrder(Order order) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            order.setStatus(Status.HONORED);
            session.merge(order);
            transaction.commit();
        }
    }

    @Override
    public void saveOrder(Order order, List<OrderItem> orderItems) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            order.setStatus(Status.PENDING);
            session.persist(order);
            session.flush();
            orderItems.forEach(orderItem -> {
                orderItem.setOrderId(order.getId());
                session.persist(orderItem);
            });
            transaction.commit();
        }
    }

    @Override
    public List<OrderItem> getAllOrderItemsForOrder(Integer orderId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createSelectionQuery("from OrderItem where orderId=:orderId ", OrderItem.class)
                    .setParameter("orderId", orderId)
                    .list();
        }
    }
}
