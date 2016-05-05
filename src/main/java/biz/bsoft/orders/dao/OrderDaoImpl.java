package biz.bsoft.orders.dao;

import biz.bsoft.orders.model.*;
import javafx.application.Application;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vbabin on 08.04.2016.
 */
@Repository
@Transactional
public class OrderDaoImpl implements OrderDao {
    @Autowired
    private SessionFactory sessionFactory;

    private static final Logger logger =
            LoggerFactory.getLogger(OrderDaoImpl.class);

    @Override
    public Order findOrder(Integer clientPosId, LocalDate date) {
        List<Order> orders= new ArrayList<>();
        Session session = sessionFactory.getCurrentSession();
        orders = session.createQuery("from Order ord where ord.clientPOS.id = :p_client_id and orderDate=:p_date")
                .setParameter("p_client_id",clientPosId).setParameter("p_date",date)
                .list();
        if(orders.size()>0){
            return orders.get(0);
        }
        else {
            return null;
        }
    }

    @Override
    public void saveOrder(Order order) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(order);
    }

    @Override
    public Order add(Integer clientId, LocalDate date) {
        Order order =new Order();
        ClientPOS clientPOS;
        Session session = sessionFactory.getCurrentSession();
        clientPOS = (ClientPOS) session.get(ClientPOS.class, clientId);
        order.setClientPOS(clientPOS);
        order.setOrderDate(date);
        session.save(order);
        return order;
    }

    @Override
    public List<Item> getAllItems() {
        List<Item> items= new ArrayList<>();
        Session session = sessionFactory.getCurrentSession();
        Query query = session.getNamedQuery("GET_ALL_ITEMS");
        items = query.list();
        return items;
//        if(items.size()>0){
//            return items;
//        }
//        else {
//            return null;
//        }
    }

    @Override
    public List<ItemGroup> getAllGroups() {
        List<ItemGroup> itemGroups= new ArrayList<>();
        Session session = sessionFactory.getCurrentSession();
        Query query = session.getNamedQuery(ItemGroup.FIND_ALL);
        itemGroups = query.list();
        return itemGroups;
    }

    @Override
    public List<FullOrderItem> getFullOrderItems(Integer clientPosId, LocalDate date, Integer groupId) {
        List<FullOrderItem> fullOrderItems;
        Session session = sessionFactory.getCurrentSession();
        Query query = session.getNamedQuery(Order.FIND_FULL);
        query.setParameter("p_client_pos_id",clientPosId).setParameter("p_date", date).setParameter("p_group_id",groupId);
        fullOrderItems = query.list();
        return fullOrderItems;
    }

    @Override
    public void addItemsToOrder(List<FullOrderItem> fullOrderItems, Integer clientPosId, LocalDate date, Integer groupIdInteger) {
        Session session = sessionFactory.getCurrentSession();
        Order order = findOrder(clientPosId, date);
        if (order==null)
        {
            order = new Order();
            order.setOrderDate(date);
            order.setClientPOS((ClientPOS) session.load(ClientPOS.class, clientPosId));
            session.save(order);
            //session.flush();
        }
        //logger.info("order Id = "+ order.getId());
        //Добавляем каждую строку в заявку, если кол-во 1 или 2 > 0
        for (FullOrderItem foItem:fullOrderItems)
        {//(itemCount==null)?0:itemCount
            if (foItem != null && (
                    ((foItem.getItemCount()==null)?0:foItem.getItemCount())
                                    +((foItem.getItemCount2()==null)?0:foItem.getItemCount2())>0
            ))
            {
                OrderItem orderItem = new OrderItem();
                Item item = (Item) session.load(Item.class, foItem.getIdItem());
                orderItem.setItem(item);
                orderItem.setItemCount(foItem.getItemCount());
                orderItem.setItemCount2(foItem.getItemCount2());
                orderItem.setOrder(order);
                List<OrderItem> orderItems = order.getOrderItems();
                if (orderItems == null){
                    //logger.info("new orderItems");
                    orderItems = new ArrayList<OrderItem>();
                    order.setOrderItems(orderItems);
                }
                orderItems.add(orderItem);
            }
        }
        session.save(order);
    }

    @Override
    public void deleteItemsFromOrder(Integer clientPosId, LocalDate date, Integer groupId) {
        Order order = findOrder(clientPosId, date);
        Session session = sessionFactory.getCurrentSession();
        Query query = session.getNamedQuery(OrderItem.DELETE_GROUP_ITEMS);
        query.setParameter("p_group_id", groupId).setParameter("p_order",order);
        query.executeUpdate();
    }

    @Override
    public List<ItemPhoto> getItemPhotos(Integer ItemId) {
        List<ItemPhoto> itemPhotos;
        Session session = sessionFactory.getCurrentSession();
        Query query = session.getNamedQuery(ItemPhoto.GET_ITEM_PHOTOS);
        query.setParameter("p_item_id",ItemId);
        itemPhotos = query.list();
        return itemPhotos;
    }

}