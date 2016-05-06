package biz.bsoft.orders.dao;

import biz.bsoft.orders.model.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Created by vbabin on 08.04.2016.
 */
public interface OrderDao {
    Order findOrder(Integer clientPosId, LocalDate date);
    void saveOrder(Order order);
    Order add(Integer clientId, LocalDate date);

    List<Item> getAllItems();
    List<ItemGroup> getAllGroups();

    List<FullOrderItem> getFullOrderItems(Integer clientPosId, LocalDate date, Integer groupId);
    void addItemsToOrder(List<FullOrderItem> fullOrderItems, Integer clientPosId, LocalDate date, Integer groupIdInteger);
    void deleteItemsFromOrder(Integer clientPosId, LocalDate date, Integer groupId);

    List<ItemPhoto> getItemPhotos(Integer ItemId);
    ItemInfo getItemInfo(Integer ItemId);
}
