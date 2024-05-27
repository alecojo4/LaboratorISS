package com.example.proiectiss.repository.db;

import com.example.proiectiss.domain.Drug;
import com.example.proiectiss.repository.DrugRepository;
import com.example.proiectiss.repository.utils.JdbcUtils;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class DrugDbRepository implements DrugRepository {

    private final JdbcUtils dbUtils;

    public DrugDbRepository(Properties props){
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public Optional<Drug> findOne(Integer id) {
        Connection con = dbUtils.getConnection();
        try (PreparedStatement statement = con.prepareStatement("select * from drugs where id = ?")) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    String name = result.getString("name");
                    String description = result.getString("description");
                    String dateStr = result.getString("dateProduced");
                    LocalDate dateProduced = LocalDate.parse(dateStr);
                    Drug drug = new Drug(name, description, dateProduced);
                    drug.setId(id);
                    return Optional.of(drug);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Drug> findAll() {
        Connection con = dbUtils.getConnection();
        List<Drug> drugs = new ArrayList<>();
        try(PreparedStatement statement = con.prepareStatement("select * from drugs")) {
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("id");
                    String name = result.getString("name");
                    String description = result.getString("description");

                    String dateStr = result.getString("dateProduced");
                    LocalDate dateProduced = LocalDate.parse(dateStr);

                    Drug drug = new Drug(name, description, dateProduced);
                    drug.setId(id);
                    drugs.add(drug);
                }
            }
        }catch(SQLException e){
            System.err.println("Error DB " + e);
        }
        return drugs;
    }

    //TODO: DE TRANSMIS entity.getDateProduced() de forma "yyyy-MM-dd"
    @Override
    public Optional<Drug> save(Drug entity) {
        Connection con = dbUtils.getConnection();
        try (PreparedStatement statement = con.prepareStatement("insert into drugs (name, description, dateProduced) values (?, ?, ?)")) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getDescription());
            statement.setString(3, entity.getDateProduced().toString());
            int rows = statement.executeUpdate();
            if (rows > 0){
                return Optional.of(entity);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Drug> delete(Integer id) {
        Connection con = dbUtils.getConnection();
        try (PreparedStatement statement = con.prepareStatement("delete from drugs where id = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO: Atenție la formatul datei
    @Override
    public Optional<Drug> update(Drug entity) {
        Connection con = dbUtils.getConnection();
        try (PreparedStatement statement = con.prepareStatement("update drugs set name = ?, description = ?, dateProduced = ? where id = ?")) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getDescription());
            statement.setString(3, entity.getDateProduced().toString());
            statement.setInt(4, entity.getId());
            statement.executeUpdate();
            return Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
