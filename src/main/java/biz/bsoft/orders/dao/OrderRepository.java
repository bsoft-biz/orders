package biz.bsoft.orders.dao;

import biz.bsoft.orders.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

/**
 * Created by vbabin on 25.09.2016.
 */
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Order findOrderByClientPos_IdAndOrderDate(Integer posId, LocalDate orderDate);
}
