package biz.bsoft.orders.dao;

import biz.bsoft.orders.model.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public Order confirmOrder(Integer clientPosId, LocalDate date) {
        //checking the day - it is allowed to make orders omly on next days
        if (date.compareTo(LocalDateTime.now().toLocalDate())<=0)
            throw new RuntimeException("Заявку можно давать только на следующие дни!");
        Order order = null;
        try {
            order = findOrder(clientPosId, date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // you can confirm only from INPUT and DECLINED
        if (order.getStatus()==OrderStatus.INPUT || order.getStatus()==OrderStatus.DECLINED){
            Session session = sessionFactory.getCurrentSession();
            order.setStatus(OrderStatus.CONFIRM);
            session.save(order);
        }
        else
            throw new RuntimeException("Редактировать можно только новую заявку или в статусах ввод и отклонена!");
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
    public List<OrderItem> getOrderItems(Integer clientPosId, LocalDate date, Integer groupId) {
        List<OrderItem> orderItems;
        Session session = sessionFactory.getCurrentSession();
        Query query = session.getNamedQuery(OrderItem.GET_GROUP_ITEMS);
        query.setParameter("p_client_pos_id",clientPosId).setParameter("p_date", date).setParameter("p_group_id",groupId);
        orderItems = query.list();
        return orderItems;
    }

    @Override
    public void addItemsToOrder(List<OrderItem> orderItems, Integer clientPosId, LocalDate date, Integer groupIdInteger) {
        logger.info("**********");
        logger.info(orderItems.toString());
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
        logger.info(order.toString());
        List<OrderItem> vOrderItems = order.getOrderItems();
        if (vOrderItems == null){
            logger.info("new orderItems");
            vOrderItems = new ArrayList<OrderItem>();
            order.setOrderItems(vOrderItems);
        }
        logger.info(vOrderItems.toString());
        for(OrderItem orderItem:orderItems){
            logger.info(orderItem.toString());
            if (((orderItem.getItemCount()==null)?0:orderItem.getItemCount())
                            +((orderItem.getItemCount2()==null)?0:orderItem.getItemCount2())>0) {
                orderItem.setOrder(order);
                vOrderItems.add(orderItem);
                logger.info("aded");
            }
        }
        //order.getOrderItems().addAll(orderItems);
        session.save(order);
    }

    @Override
    public List<OrderItemError> validateItems(List<OrderItem> orderItems, Integer clientPosId, LocalDate date) {
        List<OrderItemError> orderItemErrors = new ArrayList<>();
        Session session = sessionFactory.getCurrentSession();
        ClientPOS clientPOS = (ClientPOS) session.load(ClientPOS.class, clientPosId);
        OrderItemError orderItemError;
        // you can confirm only from INPUT and DECLINED
        Order order = null;
        try {
            order = findOrder(clientPosId, date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (order!=null && !(order.getStatus()==OrderStatus.INPUT || order.getStatus()==OrderStatus.DECLINED)){
            orderItemError = new OrderItemError();
            orderItemError.setId(0);
            orderItemError.setMessage("Редактировать можно только новую заявку или в статусах ввод и отклонена!");
            orderItemErrors.add(orderItemError);
            return orderItemErrors;
        }
        //checking the day - it is allowed to make orders omly on next days
        if (date.compareTo(LocalDateTime.now().toLocalDate())<=0){
            orderItemError = new OrderItemError();
            orderItemError.setId(0);
            orderItemError.setMessage("Заказ можно делать только на следующие дни!");
            orderItemErrors.add(orderItemError);
            return orderItemErrors;
        }
        for (OrderItem  orderItem : orderItems) {
            String errMsg="";
            Integer count, count2;
            count = (orderItem.getItemCount()==null)?0:orderItem.getItemCount();
            count2 = (orderItem.getItemCount2()==null)?0:orderItem.getItemCount2();
            Item item = (Item) session.load(Item.class, orderItem.getItem().getId());
            ItemInfo itemInfo = getItemInfo(item.getId());
            //checking if count2 is denied for the client
            if (clientPOS.getDenyCount2() != null && clientPOS.getDenyCount2() == 1 && count2 > 0)
                errMsg += "Для данного клиента запрещен второй завоз!";
            if (itemInfo !=null) {
                //checking time for group
                Query query = session.getNamedQuery(ProductionTime.GET_PROD_TIME);
                query.setParameter("p_group", item.getItemGroup()).setParameter("p_rye",itemInfo.getRye());
                List<ProductionTime> productionTimes = query.list();
                if (productionTimes.size()>0) {
                    ProductionTime productionTime = productionTimes.get(0);
                    if (productionTime.getProdTime().toLocalTime().compareTo(LocalDateTime.now().toLocalTime()) < 0)
                        errMsg += "Эта продукция принимается только до " + productionTime.getProdTime().toLocalTime().toString();
                }
                //checking if counts is less then minimal
                if (itemInfo.getMinCount() != null && ((count > 0 && count < itemInfo.getMinCount()) || (count2 > 0 && count2 < itemInfo.getMinCount())))
                    errMsg += "Для этой позиции мнимальное количество для заказа = " + itemInfo.getMinCount();
                //checking if you need exact count for box
                if ((itemInfo.getCapacity() != null) && (itemInfo.getCapacity() != 0) && (count % itemInfo.getCapacity() > 0 || count2 % itemInfo.getCapacity() > 0))
                    errMsg += "Для этой позиции задана кратность = " + itemInfo.getCapacity();
                //checking if product is produced in this day
                if ((count + count2 > 0) && itemInfo.getProdDays() != null && itemInfo.getProdDays().length() > 0 &&
                        !(itemInfo.getProdDays().contains(Integer.toString(date.getDayOfWeek().getValue()))))
                    errMsg += "У этой позиции есть дни принятия заявки = " + itemInfo.getProdDays();
            }

            // if there are some errors for count then add them
            if (errMsg.length()>0)
            {
                orderItemError = new OrderItemError();
                orderItemError.setId(item.getId());
                orderItemError.setMessage(errMsg);
                orderItemErrors.add(orderItemError);
            }
        }
//        OrderItemError orderItemError = new OrderItemError();
//        orderItemError.setId("id58422count");
//        orderItemError.setMessage("Server-side error for this id58422count!");
//        orderItemErrors.add(orderItemError);
        return orderItemErrors;
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
    public void addFullItemsToOrder(List<FullOrderItem> fullOrderItems, Integer clientPosId, LocalDate date, Integer groupIdInteger) {
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
        query.setParameter("p_item_id", ItemId);
        itemPhotos = query.list();
        return itemPhotos;
    }

    @Override
    public ItemInfo getItemInfo(Integer ItemId) {
        List<ItemInfo> itemInfos;
        Session session = sessionFactory.getCurrentSession();
        Query query = session.getNamedQuery(ItemInfo.GET_ITEM_INFO);
        query.setParameter("p_item_id", ItemId);
        itemInfos = query.list();
        if (itemInfos.size() >0)
            return itemInfos.get(0);
        else
            return null;
    }

}