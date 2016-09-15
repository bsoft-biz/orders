package biz.bsoft.orders.dao;

import biz.bsoft.orders.model.*;
import biz.bsoft.web.errors.ValidateOrderException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by vbabin on 08.04.2016.
 */
@Repository
@Transactional
public class OrderDaoImpl implements OrderDao {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private MessageSource messages;

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
    public OrderGroupStatus getOrderGroupStatus(Integer clientPosId, LocalDate date, Integer groupId) {
        /*Order order = null;
        try {
            order = findOrder(clientPosId, date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (order == null)
            return null;*/
        Session session = sessionFactory.getCurrentSession();
        List<OrderGroupStatus> statuses;
        Query query = session.getNamedQuery(OrderGroupStatus.GET_ORDER_GROUP_STATUS);
        query.setParameter("p_client_pos",clientPosId).setParameter("p_date",date).setParameter("p_group",groupId);
        //query.setParameter("p_order",order).setParameter("p_group",groupId);
        statuses = query.list();
        if(statuses.size()>0) {
            return statuses.get(0);
        }
        else{
            return null;
        }
    }

    public OrderGroupStatus validateOrder(Integer clientPosId, LocalDate date, Integer groupId){
        //ResourceBundleMessageSource messages = new ResourceBundleMessageSource();
        //messages.setBasename("locale/messages");
        Locale locale = LocaleContextHolder.getLocale();
        String errMsg="";
        Session session = sessionFactory.getCurrentSession();
        Query query;
        //checking the day - it is allowed to make orders only on next days
        if (date.compareTo(LocalDateTime.now().toLocalDate())<=0){
            errMsg += messages.getMessage("error.orderNewNextDays",null,locale);
        }
        ClientPOS clientPOS = (ClientPOS) session.load(ClientPOS.class, clientPosId);
        OrderItemError orderItemError;
        // you can confirm only from INPUT and DECLINED
        //Order order = findOrder(clientPosId, date);
        List<OrderGroupStatus> statuses;
        query = session.getNamedQuery(OrderGroupStatus.GET_ORDER_GROUP_STATUS);
        query.setParameter("p_client_pos",clientPosId).setParameter("p_date",date).setParameter("p_group",groupId);
        //query.setParameter("p_order",order).setParameter("p_group",groupId);
        statuses = query.list();
        OrderGroupStatus orderGroupStatus = null;
        OrderStatus orderStatus = null;
        if(statuses.size()>0)
        {
            orderGroupStatus = statuses.get(0);
            orderStatus = orderGroupStatus .getStatus();
        }
        if (!(orderStatus==null || orderStatus==OrderStatus.INPUT || orderStatus==OrderStatus.DECLINED)){
            errMsg +=messages.getMessage("error.orderChangeStatses",null,locale);
        }
        //checking time for group, only if you place order on tomorrow
        if (DAYS.between(LocalDateTime.now().toLocalDate(),date)==1){
            query = session.getNamedQuery(ProductionTime.GET_PROD_TIME);
            query.setParameter("p_group", groupId);
            List<ProductionTime> productionTimes = query.list();
            if (productionTimes.size()>0) {
                ProductionTime productionTime = productionTimes.get(0);
                if (productionTime.getProdTime().toLocalTime().compareTo(LocalDateTime.now().toLocalTime()) < 0)
                    errMsg += messages.getMessage("error.orderProdTime",new Object[] {productionTime.getProdTime().toLocalTime()},locale);//.toString()
            }
        }
        if (errMsg.length()>0){
            throw new ValidateOrderException(errMsg);
        }
        return orderGroupStatus;
    }

    @Override
    public List<OrderItemError> validateItems(List<OrderItem> orderItems, Integer clientPosId, LocalDate date, Integer groupId) {
        List<OrderItemError> orderItemErrors = new ArrayList<>();
        OrderItemError orderItemError;
        String errMsg="";
        // validate order
        try {
            validateOrder(clientPosId, date, groupId);
        } catch (Exception e) {
            errMsg=e.getMessage();
            orderItemError = new OrderItemError();
            orderItemError.setId(0);
            orderItemError.setMessage(errMsg);
            orderItemErrors.add(orderItemError);
            return orderItemErrors;
        }

        // validate order items
        /*ResourceBundleMessageSource messages = new ResourceBundleMessageSource();
        messages.setBasename("locale/messages");*/
        Locale locale = LocaleContextHolder.getLocale();
        Session session = sessionFactory.getCurrentSession();
        ClientPOS clientPOS = (ClientPOS) session.load(ClientPOS.class, clientPosId);

        for (OrderItem  orderItem : orderItems) {
            errMsg = "";
            Integer count, count2;
            count = (orderItem.getItemCount()==null)?0:orderItem.getItemCount();
            count2 = (orderItem.getItemCount2()==null)?0:orderItem.getItemCount2();
            Item item = (Item) session.load(Item.class, orderItem.getItem().getId());
            ItemInfo itemInfo = getItemInfo(item.getId());
            //checking if count2 is denied for the client
            if (clientPOS.getDenyCount2() != null && clientPOS.getDenyCount2() == 1 && count2 > 0)
                errMsg += messages.getMessage("error.orderClientCount2",null,locale);
            if (itemInfo !=null) {
                //checking if counts is less then minimal
                if (itemInfo.getMinCount() != null && ((count > 0 && count < itemInfo.getMinCount()) || (count2 > 0 && count2 < itemInfo.getMinCount())))
                    errMsg += messages.getMessage("error.orderProdMinCount",new Object[] {itemInfo.getMinCount()},locale);
                //checking if you need exact count for box
                if ((itemInfo.getCapacity() != null) && (itemInfo.getCapacity() != 0) && (count % itemInfo.getCapacity() > 0 || count2 % itemInfo.getCapacity() > 0))
                    errMsg += messages.getMessage("error.orderProdCapacity",new Object[] {itemInfo.getCapacity()},locale);
                //checking if product is produced in this day
                if ((count + count2 > 0) && itemInfo.getProdDays() != null && itemInfo.getProdDays().length() > 0 &&
                        !(itemInfo.getProdDays().contains(Integer.toString(date.getDayOfWeek().getValue()))))
                    errMsg += messages.getMessage("error.orderProdDays",new Object[] {itemInfo.getProdDays()},locale);
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
        return orderItemErrors;
    }

    @Override
    public OrderGroupStatus confirmOrder(Integer clientPosId, LocalDate date, Integer groupId) {
        OrderGroupStatus orderGroupStatus = validateOrder(clientPosId, date, groupId);
        Order order = findOrder(clientPosId, date);
        Session session = sessionFactory.getCurrentSession();
        if (orderGroupStatus == null){
            orderGroupStatus = new OrderGroupStatus();
            orderGroupStatus.setOrder(order);
            orderGroupStatus.setGroup((ItemGroup) session.load(ItemGroup.class, groupId));
        }
        orderGroupStatus.setStatus(OrderStatus.CONFIRM);
        session.save(orderGroupStatus);
        return orderGroupStatus;
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
    public void addItemsToOrder(List<OrderItem> orderItems, Integer clientPosId, LocalDate date, Integer groupId) {
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
        List<OrderGroupStatus> statuses;
        Query query = session.getNamedQuery(OrderGroupStatus.GET_ORDER_GROUP_STATUS);
        query.setParameter("p_client_pos",clientPosId).setParameter("p_date",date).setParameter("p_group",groupId);
        //query.setParameter("p_order",order).setParameter("p_group",groupId);
        statuses = query.list();
        OrderGroupStatus orderGroupStatus;
        if(statuses.size()>0)
            orderGroupStatus = statuses.get(0);
        else
        {
            orderGroupStatus = new OrderGroupStatus();
            orderGroupStatus.setOrder(order);
            orderGroupStatus.setGroup((ItemGroup) session.load(ItemGroup.class, groupId));
        }
        orderGroupStatus.setStatus(OrderStatus.INPUT);
        session.save(orderGroupStatus);

        List<OrderItem> vOrderItems = order.getOrderItems();
        if (vOrderItems == null){
            vOrderItems = new ArrayList<OrderItem>();
            order.setOrderItems(vOrderItems);
        }
        for(OrderItem orderItem:orderItems){
            if (((orderItem.getItemCount()==null)?0:orderItem.getItemCount())
                            +((orderItem.getItemCount2()==null)?0:orderItem.getItemCount2())>0) {
                orderItem.setOrder(order);
                vOrderItems.add(orderItem);
            }
        }
        //order.getOrderItems().addAll(orderItems);
        session.save(order);
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
        }
        //Add new row in order if count1 and count2 > 0
        for (FullOrderItem foItem:fullOrderItems)
        {
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