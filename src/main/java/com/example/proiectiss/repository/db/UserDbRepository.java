package com.example.proiectiss.repository.db;

import com.example.proiectiss.domain.User;
import com.example.proiectiss.repository.UserRepository;
import com.example.proiectiss.repository.utils.JdbcUtils;

import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Predicate;

public class UserDbRepository implements UserRepository {

    private final JdbcUtils dbUtils;

    public UserDbRepository(Properties props) {
        dbUtils = new JdbcUtils(props);
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
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("insert into users (username, password, role) values (?,?,?)"))
        {
            preStmt.setString(1,user.getUsername());
            preStmt.setString(2,user.getPassword());
            preStmt.setString(3, user.getRole());
            preStmt.executeUpdate();
        }
        catch (SQLException ex){
            System.err.println("Error DB" + ex);
            return Optional.empty();
        }
        return Optional.of(user);
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
        Connection con = dbUtils.getConnection();
        User user = null;
        try(PreparedStatement statement = con.prepareStatement("select * from users where username=?")){
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    int id = result.getInt("id");
                    String password = result.getString("password");
                    String role = result.getString("role");
                    user = new User(username, password, role);
                    user.setId(id);
                    return Optional.of(user);
                }
            }
        }
        catch(SQLException e){
            System.err.println("Error DB " + e);
        }
        return Optional.empty();
    }
}
