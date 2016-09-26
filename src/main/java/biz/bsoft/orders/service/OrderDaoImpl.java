package biz.bsoft.orders.service;

import biz.bsoft.orders.dao.*;
import biz.bsoft.orders.model.*;
import biz.bsoft.users.service.UserService;
import biz.bsoft.users.model.UserSettings;
import biz.bsoft.service.MailService;
import biz.bsoft.web.errors.ValidateOrderException;
import org.hibernate.Query;
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
//    @Autowired
//    private SessionFactory sessionFactory;
    @Autowired
    private OrderRepository repository;
    @Autowired
    private ClientPosRepository clientPosRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemInfoRepository itemInfoRepository;
    @Autowired
    private ItemPhotoRepository itemPhotoRepository;
    @Autowired
    private ItemGroupRepository itemGroupRepository;
    @Autowired
    private OrderGroupStatusRepository orderGroupStatusRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductionTimeRepository productionTimeRepository;
    @Autowired
    private MessageSource messages;
    @Autowired
    MailService mailService;
    @Autowired
    UserService userService;

    private static final Logger logger =
            LoggerFactory.getLogger(OrderDaoImpl.class);

//    @Override
//    public Order findOrder(Integer clientPosId, LocalDate date) {
        //return repository.findOrderByClientPos_IdAndOrderDate(clientPosId, date);
//        List<Order> orders= new ArrayList<>();
//        Session session = sessionFactory.getCurrentSession();
//        orders = session.createQuery("from Order ord where ord.clientPOS.id = :p_client_id and orderDate=:p_date")
//                .setParameter("p_client_id",clientPosId).setParameter("p_date",date)
//                .list();
//        if(orders.size()>0){
//            return orders.get(0);
//        }
//        else {
//            return null;
//        }
//    }



    @Override
    public OrderGroupStatus getOrderGroupStatus(Integer clientPosId, LocalDate date, Integer groupId) {

        List<OrderGroupStatus> statuses;
        statuses = orderGroupStatusRepository.findByOrder_ClientPos_IdAndOrder_OrderDateAndGroup_Id(clientPosId, date, groupId);
//        Session session = sessionFactory.getCurrentSession();
//        Query query = session.getNamedQuery(OrderGroupStatus.GET_ORDER_GROUP_STATUS);
//        query.setParameter("p_client_pos",clientPosId).setParameter("p_date",date).setParameter("p_group",groupId);
//        //query.setParameter("p_order",order).setParameter("p_group",groupId);
//        statuses = query.list();
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
        //Session session = sessionFactory.getCurrentSession();
        Query query;
        //checking the day - it is allowed to make orders only on next days
        if (date.compareTo(LocalDateTime.now().toLocalDate())<=0){
            errMsg += messages.getMessage("error.orderNewNextDays",null,locale);
        }
        //ClientPOS clientPOS = (ClientPOS) session.load(ClientPOS.class, clientPosId);
        OrderItemError orderItemError;
        // you can confirm only from INPUT and DECLINED
        //Order order = findOrder(clientPosId, date);
        List<OrderGroupStatus> statuses;
//        query = session.getNamedQuery(OrderGroupStatus.GET_ORDER_GROUP_STATUS);
//        query.setParameter("p_client_pos",clientPosId).setParameter("p_date",date).setParameter("p_group",groupId);
//        //query.setParameter("p_order",order).setParameter("p_group",groupId);
//        statuses = query.list();
        statuses = orderGroupStatusRepository.findByOrder_ClientPos_IdAndOrder_OrderDateAndGroup_Id(clientPosId, date, groupId);
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
//            query = session.getNamedQuery(ProductionTime.GET_PROD_TIME);
//            query.setParameter("p_group", groupId);
//            List<ProductionTime> productionTimes = query.list();
            List<ProductionTime> productionTimes = productionTimeRepository.findByItemGroup_Id(groupId);
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
        //Session session = sessionFactory.getCurrentSession();
        ClientPOS clientPOS = clientPosRepository.findOne(clientPosId);
                //(ClientPOS) session.load(ClientPOS.class, clientPosId);

        for (OrderItem  orderItem : orderItems) {
            errMsg = "";
            Integer count, count2;
            count = (orderItem.getItemCount()==null)?0:orderItem.getItemCount();
            count2 = (orderItem.getItemCount2()==null)?0:orderItem.getItemCount2();
            Item item = itemRepository.findOne(orderItem.getItem().getId());
                    //(Item) session.load(Item.class, orderItem.getItem().getId());
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
        Order order = repository.findOrderByClientPos_IdAndOrderDate(clientPosId, date);
        //Session session = sessionFactory.getCurrentSession();
        if (orderGroupStatus == null){
            orderGroupStatus = new OrderGroupStatus();
            orderGroupStatus.setOrder(order);
            orderGroupStatus.setGroup(itemGroupRepository.findOne(groupId));
                    //ItemGroup) session.load(ItemGroup.class, groupId));
        }
        orderGroupStatus.setStatus(OrderStatus.CONFIRM);
        orderGroupStatusRepository.save(orderGroupStatus);
        //session.save(orderGroupStatus);
        //send e-mail to operators
        mailService.sendNotificationEmailConfirmOperator(orderGroupStatus);
        UserSettings currentUserSettings = userService.getCurrentUserSettings();
        //send e-mail to client
        mailService.sendNotificationEmailConfirmClient(orderGroupStatus,currentUserSettings);
        return orderGroupStatus;
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
//        List<Item> items= new ArrayList<>();
//        Session session = sessionFactory.getCurrentSession();
//        Query query = session.getNamedQuery("GET_ALL_ITEMS");
//        items = query.list();
//        return items;
//        if(items.size()>0){
//            return items;
//        }
//        else {
//            return null;
//        }
    }

    @Override
    public List<ItemGroup> getAllGroups() {
        return itemGroupRepository.findAll();
//        List<ItemGroup> itemGroups= new ArrayList<>();
//        Session session = sessionFactory.getCurrentSession();
//        Query query = session.getNamedQuery(ItemGroup.FIND_ALL);
//        itemGroups = query.list();
//        return itemGroups;
    }

    @Override
    public List<OrderItem> getOrderItems(Integer clientPosId, LocalDate date, Integer groupId) {
        return orderItemRepository.findByOrder_ClientPos_IdAndOrder_OrderDateAndItem_ItemGroup_Id(clientPosId, date, groupId);
//        List<OrderItem> orderItems;
//        Session session = sessionFactory.getCurrentSession();
//        Query query = session.getNamedQuery(OrderItem.GET_GROUP_ITEMS);
//        query.setParameter("p_client_pos_id",clientPosId).setParameter("p_date", date).setParameter("p_group_id",groupId);
//        orderItems = query.list();
//        return orderItems;
    }

    @Override
    public void addItemsToOrder(List<OrderItem> orderItems, Integer clientPosId, LocalDate date, Integer groupId) {
        //Session session = sessionFactory.getCurrentSession();
        Order order = repository.findOrderByClientPos_IdAndOrderDate(clientPosId, date);
        if (order==null)
        {
            order = new Order();
            order.setOrderDate(date);
            order.setClientPos(clientPosRepository.findOne (clientPosId));//ClientPOS) session.load(ClientPOS.class, clientPosId));
            repository.save(order);
            //session.save(order);
            //session.flush();
        }
        List<OrderGroupStatus> statuses;
        statuses = orderGroupStatusRepository.findByOrder_ClientPos_IdAndOrder_OrderDateAndGroup_Id(clientPosId,date,groupId);
//        Query query = session.getNamedQuery(OrderGroupStatus.GET_ORDER_GROUP_STATUS);
//        query.setParameter("p_client_pos",clientPosId).setParameter("p_date",date).setParameter("p_group",groupId);
//        //query.setParameter("p_order",order).setParameter("p_group",groupId);
//        statuses = query.list();
        OrderGroupStatus orderGroupStatus;
        if(statuses.size()>0)
            orderGroupStatus = statuses.get(0);
        else
        {
            orderGroupStatus = new OrderGroupStatus();
            orderGroupStatus.setOrder(order);
            orderGroupStatus.setGroup(itemGroupRepository.findOne(groupId));//(ItemGroup) session.load(ItemGroup.class, groupId));
        }
        orderGroupStatus.setStatus(OrderStatus.INPUT);
        orderGroupStatusRepository.save(orderGroupStatus);
        //session.save(orderGroupStatus);

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
        repository.save(order);
        //session.save(order);
    }

    @Override
    public void deleteItemsFromOrder(Integer clientPosId, LocalDate date, Integer groupId) {
        orderItemRepository.deleteByOrder_ClientPos_IdAndOrder_OrderDateAndItem_ItemGroup_Id(clientPosId,date,groupId);
//        Order order = findOrder(clientPosId, date);
//        Session session = sessionFactory.getCurrentSession();
//        Query query = session.getNamedQuery(OrderItem.DELETE_GROUP_ITEMS);
//        query.setParameter("p_group_id", groupId).setParameter("p_order",order);
//        query.executeUpdate();
    }

    @Override
    public List<ItemPhoto> getItemPhotos(Integer ItemId) {
        List<ItemPhoto> itemPhotos;
        itemPhotos = itemPhotoRepository.findByItem_Id(ItemId);
//        Session session = sessionFactory.getCurrentSession();
//        Query query = session.getNamedQuery(ItemPhoto.GET_ITEM_PHOTOS);
//        query.setParameter("p_item_id", ItemId);
//        itemPhotos = query.list();
        return itemPhotos;
    }

    @Override
    public ItemInfo getItemInfo(Integer ItemId) {
        List<ItemInfo> itemInfos;
        itemInfos = itemInfoRepository.findByItem_Id(ItemId);
//        Session session = sessionFactory.getCurrentSession();
//        Query query = session.getNamedQuery(ItemInfo.GET_ITEM_INFO);
//        query.setParameter("p_item_id", ItemId);
//        itemInfos = query.list();
        if (itemInfos.size() >0)
            return itemInfos.get(0);
        else
            return null;
    }

}