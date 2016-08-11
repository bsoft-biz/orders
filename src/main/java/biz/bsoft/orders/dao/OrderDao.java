package biz.bsoft.orders.dao;

import biz.bsoft.orders.model.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by vbabin on 08.04.2016.
 */
public interface OrderDao {
    Order findOrder(Integer clientPosId, LocalDate date);
    void saveOrder(Order order);
    Order add(Integer clientId, LocalDate date);
    OrderGroupStatus confirmOrder(Integer clientPosId, LocalDate date, Integer groupId);
    OrderGroupStatus getOrderGroupStatus(Integer clientPosId, LocalDate date, Integer groupId);

    List<Item> getAllItems();
    List<ItemGroup> getAllGroups();

    List<OrderItem> getOrderItems(Integer clientPosId, LocalDate date, Integer groupId);
    void addItemsToOrder(List<OrderItem> orderItems, Integer clientPosId, LocalDate date, Integer groupId);
    List<OrderItemError> validateItems(List<OrderItem> orderItems, Integer clientPosId, LocalDate date, Integer groupId);

    List<FullOrderItem> getFullOrderItems(Integer clientPosId, LocalDate date, Integer groupId);
    void addFullItemsToOrder(List<FullOrderItem> fullOrderItems, Integer clientPosId, LocalDate date, Integer groupIdInteger);
    void deleteItemsFromOrder(Integer clientPosId, LocalDate date, Integer groupId);

    List<ItemPhoto> getItemPhotos(Integer ItemId);
    ItemInfo getItemInfo(Integer ItemId);
}
