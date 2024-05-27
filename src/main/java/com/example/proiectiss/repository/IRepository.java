package com.example.proiectiss.repository;

import com.example.proiectiss.domain.Entitate;

import java.util.List;
import java.util.Optional;

public interface IRepository <E extends Entitate>{
    Optional<E> findOne(Integer id);
    List<E> findAll();
    Optional<E> save(E entity);
    Optional<E> delete(Integer id);
    Optional<E> update(E entity);
}
