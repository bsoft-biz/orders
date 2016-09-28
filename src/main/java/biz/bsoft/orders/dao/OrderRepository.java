package biz.bsoft.orders.dao;

import biz.bsoft.orders.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

/**
 * Created by vbabin on 25.09.2016.
 */
public interface OrderRepository extends CrudRepository<Order, Integer> {
    Order findOrderByClientPos_IdAndOrderDate(Integer posId, LocalDate orderDate);
}
