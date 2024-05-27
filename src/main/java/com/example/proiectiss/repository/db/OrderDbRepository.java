package com.example.proiectiss.repository.db;

import com.example.proiectiss.domain.Drug;
import com.example.proiectiss.domain.Order;
import com.example.proiectiss.domain.OrderItem;
import com.example.proiectiss.domain.Status;
import com.example.proiectiss.repository.DrugRepository;
import com.example.proiectiss.repository.OrderRepository;
import com.example.proiectiss.repository.utils.JdbcUtils;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class OrderDbRepository implements OrderRepository {
    private final JdbcUtils dbUtils;
    private final DrugRepository drugRepository;

    public OrderDbRepository(Properties props, DrugRepository drugRepository) {
        dbUtils = new JdbcUtils(props);
        this.drugRepository = drugRepository;
    }

    @Override
    public Optional<Order> findOne(Integer id) {
        return Optional.empty();
    }

    @Override
    public List<Order> findAll() {
        Connection con = dbUtils.getConnection();
        List<Order> orders = new ArrayList<>();
        try (var preStmt = con.prepareStatement("select * from orders where status=\"PENDING\" order by registeredAt")){
            try (var result = preStmt.executeQuery()) {
                while (result.next()) {
                    Integer id = result.getInt("id");
                    Timestamp registeredAt = result.getTimestamp("registeredAt");
                    String status = result.getString("status");
                    String section = result.getString("registeredBy");
                    Order order = new Order(section, Status.valueOf(status), registeredAt.toLocalDateTime());
                    order.setId(id);
                    orders.add(order);
                }
            }
        } catch (Exception ex) {
            System.err.println("Error DB " + ex);
        }
        return orders;
    }

    @Override
    public Optional<Order> save(Order order) {
        Connection con = dbUtils.getConnection();
        try (var preStmt = con.prepareStatement("insert into orders (registeredAt, status, registeredBy) values (?,?,?)")) {
            preStmt.setTimestamp(1, Timestamp.valueOf(order.getRegisteredAt()));
            preStmt.setString(2, Status.PENDING.toString());
            preStmt.setString(3, order.getUsername());
            preStmt.executeUpdate();
            return Optional.of(order);
        } catch (Exception ex) {
            System.err.println("Error DB " + ex);
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
        Connection con = dbUtils.getConnection();
        List<Order> orders = new ArrayList<>();
        try (var preStmt = con.prepareStatement("select * from orders where registeredBy=? order by registeredAt")){
            preStmt.setString(1, username);
            try (var result = preStmt.executeQuery()) {
                while (result.next()) {
                    Integer id = result.getInt("id");
                    Timestamp registeredAt = result.getTimestamp("registeredAt");
                    String status = result.getString("status");
                    String section = result.getString("registeredBy");
                    Order order = new Order(section, Status.valueOf(status), registeredAt.toLocalDateTime());
                    order.setId(id);
                    orders.add(order);
                }
            }
        } catch (Exception ex) {
            System.err.println("Error DB " + ex);
        }
        return orders;
    }

    @Override
    public void honourOrder(Order order) {
        Connection con = dbUtils.getConnection();
        try (var preStmt = con.prepareStatement("update orders set status=? where id=?")) {
            preStmt.setString(1, Status.HONORED.toString());
            preStmt.setInt(2, order.getId());
            preStmt.executeUpdate();
        } catch (Exception ex) {
            System.err.println("Error DB " + ex);
        }
    }

    @Override
    public void saveOrder(Order order, List<OrderItem> orderItems) {
        Connection con = dbUtils.getConnection();
        try (var preStmt = con.prepareStatement("insert into orders (registeredAt, status, registeredBy) values (?,?,?)")) {
            preStmt.setTimestamp(1, Timestamp.valueOf(order.getRegisteredAt()));
            preStmt.setString(2, Status.PENDING.toString());
            preStmt.setString(3, order.getUsername());
            preStmt.executeUpdate();
        } catch (Exception ex) {
            System.err.println("Error DB " + ex);
        }

        try (var preStmt = con.prepareStatement("select max(id) as maxId from orders")) {
            try (var result = preStmt.executeQuery()) {
                if (result.next()) {
                    Integer orderId = result.getInt("maxId");
                    for (OrderItem orderItem : orderItems) {
                        try (var preStmt2 = con.prepareStatement("insert into order_items (orderId, drugId, quantity) values (?,?,?)")) {
                            preStmt2.setInt(1, orderId);
                            preStmt2.setInt(2, orderItem.getDrug().getId());
                            preStmt2.setInt(3, orderItem.getQuantity());
                            preStmt2.executeUpdate();
                        } catch (Exception ex) {
                            System.err.println("Error DB " + ex);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Error DB " + ex);
        }
    }

    @Override
    public List<OrderItem> getAllOrderItemsForOrder(Integer orderId) {
        Connection con = dbUtils.getConnection();
        List<OrderItem> orderItems = new ArrayList<>();
        try (var preStmt = con.prepareStatement("select * from order_items where orderId=?")){
            preStmt.setInt(1, orderId);
            try (var result = preStmt.executeQuery()) {
                while (result.next()) {
                    Integer drugId = result.getInt("drugId");
                    Optional<Drug> drug = drugRepository.findOne(drugId);
                    if (drug.isPresent()){
                        Integer id = result.getInt("id");
                        OrderItem orderItem = new OrderItem();
                        orderItem.setOrderId(orderId);
                        orderItem.setDrug(drug.get());
                        orderItem.setQuantity(result.getInt("quantity"));
                        orderItem.setId(id);
                        orderItems.add(orderItem);
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Error DB " + ex);
        }
        return orderItems;
    }
}
