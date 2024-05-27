package com.example.proiectiss.repository.hibernate;

import com.example.proiectiss.domain.Drug;
import com.example.proiectiss.repository.DrugRepository;
import com.example.proiectiss.repository.utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class DrugHibernateRepository implements DrugRepository {

    private final SessionFactory sessionFactory;

    public DrugHibernateRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<Drug> findOne(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Drug drug = session.get(Drug.class, id);
            return Optional.ofNullable(drug);
        }
    }

    @Override
    public List<Drug> findAll() {
        try (Session session = sessionFactory.openSession()){
            return session.createQuery("from Drug ", Drug.class).list();
        }
    }

    @Override
    public Optional<Drug> save(Drug drug) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(drug);
            transaction.commit();
            return Optional.of(drug);
        }catch(Exception e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<Drug> delete(Integer id) {
        Optional<Drug> drug = findOne(id);
        if (drug.isEmpty()){
            return Optional.empty();
        }
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(drug.get());
            transaction.commit();
            return drug;
        }catch(Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Drug> update(Drug drug) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(drug);
            transaction.commit();
            return Optional.of(drug);
        }catch(Exception e){
            return Optional.empty();
        }
    }
}
