package biz.bsoft.orders.service;

import biz.bsoft.orders.dao.*;
import biz.bsoft.orders.model.*;
import biz.bsoft.service.MailService;
import biz.bsoft.users.model.User;
import biz.bsoft.users.service.UserService;
import biz.bsoft.web.dto.OrderItemError;
import biz.bsoft.web.errors.ValidateOrderException;
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

@Repository
@Transactional
public class OrderServiceImpl implements OrderService {
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

//    private static final Logger logger =
//            LoggerFactory.getLogger(OrderDaoImpl.class);

    @Override
    public OrderGroupStatus getOrderGroupStatus(Integer clientPosId, LocalDate date, Integer groupId) {

        List<OrderGroupStatus> statuses;
        statuses = orderGroupStatusRepository.findByOrder_ClientPos_IdAndOrder_OrderDateAndGroup_Id(clientPosId, date, groupId);
        if(statuses.size()>0) {
            return statuses.get(0);
        }
        else{
            return null;
        }
    }

    private OrderGroupStatus validateOrder(Integer clientPosId, LocalDate date, Integer groupId){
        Locale locale = LocaleContextHolder.getLocale();
        String errMsg="";
        //checking the day - it is allowed to make orders only on next days
        if (date.compareTo(LocalDateTime.now().toLocalDate())<=0){
            errMsg += messages.getMessage("error.orderNewNextDays",null,locale);
        }
        // you can confirm only from INPUT and DECLINED
        List<OrderGroupStatus> statuses;
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
        String errMsg;
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
        Locale locale = LocaleContextHolder.getLocale();
        ClientPOS clientPOS = clientPosRepository.findOne(clientPosId);

        for (OrderItem  orderItem : orderItems) {
            errMsg = "";
            Integer count, count2;
            count = (orderItem.getItemCount()==null)?0:orderItem.getItemCount();
            count2 = (orderItem.getItemCount2()==null)?0:orderItem.getItemCount2();
            Item item = itemRepository.findOne(orderItem.getItem().getId());
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
        if (orderGroupStatus == null){
            orderGroupStatus = new OrderGroupStatus();
            orderGroupStatus.setOrder(order);
            orderGroupStatus.setGroup(itemGroupRepository.findOne(groupId));
        }
        orderGroupStatus.setStatus(OrderStatus.CONFIRM);
        orderGroupStatusRepository.save(orderGroupStatus);
        mailService.sendNotificationEmailConfirmOperator(orderGroupStatus);
        User currentUser = userService.getCurrentUser();
        //send e-mail to client
        mailService.sendNotificationEmailConfirmClient(orderGroupStatus,currentUser);
        return orderGroupStatus;
    }

    @Override
    public List<Item> getAllItems() {
        return (List) itemRepository.findByArchive(false);
    }

    @Override
    public List<ItemGroup> getAllGroups() {
        return (List) itemGroupRepository.findAll();
    }

    @Override
    public List<OrderItem> getOrderItems(Integer clientPosId, LocalDate date, Integer groupId) {
        return orderItemRepository.findByOrder_ClientPos_IdAndOrder_OrderDateAndItem_ItemGroup_Id(clientPosId, date, groupId);
    }

    @Override
    public void addItemsToOrder(List<OrderItem> orderItems, Integer clientPosId, LocalDate date, Integer groupId) {
        Order order = repository.findOrderByClientPos_IdAndOrderDate(clientPosId, date);
        if (order==null)
        {
            order = new Order();
            order.setOrderDate(date);
            order.setClientPos(clientPosRepository.findOne (clientPosId));//ClientPOS) session.load(ClientPOS.class, clientPosId));
            repository.save(order);
        }
        List<OrderGroupStatus> statuses;
        statuses = orderGroupStatusRepository.findByOrder_ClientPos_IdAndOrder_OrderDateAndGroup_Id(clientPosId,date,groupId);
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

        List<OrderItem> vOrderItems = order.getOrderItems();
        if (vOrderItems == null){
            vOrderItems = new ArrayList<>();
            order.setOrderItems(vOrderItems);
        }
        for(OrderItem orderItem:orderItems){
            if (((orderItem.getItemCount()==null)?0:orderItem.getItemCount())
                            +((orderItem.getItemCount2()==null)?0:orderItem.getItemCount2())>0) {
                orderItem.setOrder(order);
                orderItemRepository. save(orderItem);
                vOrderItems.add(orderItem);
            }
        }
        repository.save(order);
    }

    @Override
    public void deleteItemsFromOrder(Integer clientPosId, LocalDate date, Integer groupId) {
        orderItemRepository.deleteByOrder_ClientPos_IdAndOrder_OrderDateAndItem_ItemGroup_Id(clientPosId,date,groupId);
    }

    @Override
    public List<ItemPhoto> getItemPhotos(Integer ItemId) {
        List<ItemPhoto> itemPhotos;
        itemPhotos = itemPhotoRepository.findByItem_Id(ItemId);
        return itemPhotos;
    }

    @Override
    public ItemInfo getItemInfo(Integer ItemId) {
        List<ItemInfo> itemInfos;
        itemInfos = itemInfoRepository.findByItem_Id(ItemId);
        if (itemInfos.size() >0)
            return itemInfos.get(0);
        else
            return null;
    }

}