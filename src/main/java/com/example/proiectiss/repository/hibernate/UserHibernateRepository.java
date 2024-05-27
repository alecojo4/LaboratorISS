package com.example.proiectiss.repository.hibernate;

import com.example.proiectiss.domain.User;
import com.example.proiectiss.repository.UserRepository;
import com.example.proiectiss.repository.utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class UserHibernateRepository implements UserRepository {

    public final SessionFactory sessionFactory;

    public UserHibernateRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<User> findOne(Integer id) {
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public Optional<User> save(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            return Optional.of(user);
        }catch(Exception e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> delete(Integer id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> update(User entity) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try (Session session = sessionFactory.openSession()){
            User user = session.createSelectionQuery("from User where username=:username ", User.class)
                    .setParameter("username", username)
                    .getSingleResultOrNull();
            if(user == null){
                return Optional.empty();
            }
            return Optional.of(user);
        }
    }
}
