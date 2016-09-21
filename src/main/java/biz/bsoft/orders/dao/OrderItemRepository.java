package biz.bsoft.orders.dao;

import biz.bsoft.orders.model.ItemGroup;
import biz.bsoft.orders.model.Order;
import biz.bsoft.orders.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by vbabin on 20.09.2016.
 */
public interface OrderItemRepository  extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderAndItem_ItemGroup(Order order, ItemGroup itemGroup);
}
