package biz.bsoft.orders.dao;

import biz.bsoft.orders.model.ItemGroup;
import biz.bsoft.orders.model.Order;
import biz.bsoft.orders.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by vbabin on 20.09.2016.
 */
public interface OrderItemRepository  extends CrudRepository<OrderItem, Long> {
    List<OrderItem> findByOrderAndItem_ItemGroup(Order order, ItemGroup itemGroup);
    @Modifying
    //@Query("delete from OrderItem oi where oi.order.clientPos.id =?1 and oi.order.orderDate=?2  and oi.item.itemGroup.id=?3")
    @Query("delete from OrderItem oi where oi.order in (select o from Order o where o.clientPos.id =?1 and o.orderDate=?2) and oi.item in (select i from Item i where i.itemGroup.id=?3)")
    void deleteByOrder_ClientPos_IdAndOrder_OrderDateAndItem_ItemGroup_Id(Integer clientPosId, LocalDate orderDate, Integer itemGroupId);
    List<OrderItem> findByOrder_ClientPos_IdAndOrder_OrderDateAndItem_ItemGroup_Id(Integer clientPosId, LocalDate orderDate, Integer itemGroupId);
}
