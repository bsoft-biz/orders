package biz.bsoft.web.controller;

import biz.bsoft.orders.dao.ItemInfoRepository;
import biz.bsoft.orders.dao.OrderRepository;
import biz.bsoft.orders.model.*;
import biz.bsoft.orders.service.OrderDao;
import biz.bsoft.users.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by vbabin on 08.04.2016.
 */
@RestController
@RequestMapping("/orders")
public class OrdersRestController {
    @Autowired
    OrderDao orderDao;
    @Autowired
    UserService userService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ItemInfoRepository itemInfoRepository;
    private static final Logger logger =
            LoggerFactory.getLogger(OrdersRestController.class);

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Order getOrder(@PathVariable("id") Integer id) {
        Order order = orderRepository.findOne(id);
        //LocalDateTime timePoint= LocalDateTime.now();
        //LocalDate today = timePoint.toLocalDate();
        //Date today= new Date();
        //today.setTime(0);
//        try {
//            //logger.info("Date = "+today.toString()+" id="+id);
//            order = orderRepository.findOne(id);
//                    //orderDao.findOrder(id, today);
//            /*if (order!=null) {
//                logger.info("REST order = " + order.toString());
//                logger.info("REST order.OrderItems = " + order.getOrderItems().toString());
//            }*/
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return order;
    }

    @RequestMapping(value = "/order")
    public Order getOrder(@RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date) {
        Integer clientPosId = userService.getCurrentUserSettings().getClientPOS().getId();
        Order order = null;
        try {
            order = orderRepository.findOrderByClientPos_IdAndOrderDate(clientPosId,date);
                    //orderDao.findOrder(clientPosId, date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return order;
    }

    @RequestMapping(value = "/orderstatus")
    public OrderGroupStatus getOrderStatus(@RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date,
                                           @RequestParam("group_id") Integer groupId) {
        Integer clientPosId = userService.getCurrentUserSettings().getClientPOS().getId();
        OrderGroupStatus orderGroupStatus = orderDao.getOrderGroupStatus(clientPosId,date,groupId);
        return orderGroupStatus;
    }

    @RequestMapping(value = "/confirmorder", method = RequestMethod.POST)
    public OrderGroupStatus confirmOrder(@RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date,
                                         @RequestParam("group_id") Integer groupId,
                                         HttpServletResponse response) {
        Integer clientPosId = userService.getCurrentUserSettings().getClientPOS().getId();
        OrderGroupStatus orderGroupStatus = orderDao.confirmOrder(clientPosId,date,groupId);
        return orderGroupStatus;
    }


    @JsonView(View.ItemsAll.class)
    @RequestMapping(value = "/items")
    public List<Item> getAllItems() {
        List<Item> items = null;
        try {
            items = orderDao.getAllItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    @JsonView(View.ItemInfoShort.class)
    @RequestMapping(value = "/items_info")
    public List<ItemInfo> getAllItemInfo() {
        return (List) itemInfoRepository.findAll();
    }

    @RequestMapping(value = "/item_groups")
    public List<ItemGroup> getAllItemGroups() {
        List<ItemGroup> itemGroups = null;
        try {
            itemGroups = orderDao.getAllGroups();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemGroups;
    }

    @JsonView(View.ItemsId.class)
    @RequestMapping(value = "/orderitems")
    public List<OrderItem> getOrderItems(@RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date,
                                         @RequestParam("group_id") Integer groupId){
        Integer clientPosId = userService.getCurrentUserSettings().getClientPOS().getId();
        List<OrderItem> items = null;
        try {
            items = orderDao.getOrderItems(clientPosId, date, groupId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    @RequestMapping(value = "/orderitems", method = RequestMethod.POST)
    public List<OrderItemError> setOrderItems(@RequestBody List<OrderItem> orderItems,
                                              @RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date,
                                              @RequestParam("group_id") Integer groupId,
                                              HttpServletResponse response){
        Integer clientPosId = userService.getCurrentUserSettings().getClientPOS().getId();
        //TODO need to check if all items are from the group because you can delete all items from group and insert from other group
        List<OrderItemError> orderItemErrors = orderDao.validateItems(orderItems, clientPosId, date, groupId);
        if (orderItemErrors.size()>0) {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return orderItemErrors;
        }
        // delete old items in orders
        orderDao.deleteItemsFromOrder(clientPosId, date, groupId);
        // add received items in orders
        orderDao.addItemsToOrder(orderItems, clientPosId, date, groupId);
        return null;
    }

    @RequestMapping(value = "/item_photos")
    public List<ItemPhoto> getItemPhotos(@RequestParam("item_id") Integer itemId) {
        List<ItemPhoto> itemPhotos = null;
        try {
            itemPhotos = orderDao.getItemPhotos(itemId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemPhotos;
    }

    @RequestMapping(value = "/items/{item_id}/photos", method = RequestMethod.GET)
    public List<ItemPhoto> getItemPhotosPath(@PathVariable("item_id") Integer itemId) {
        List<ItemPhoto> itemPhotos = null;
        try {
            itemPhotos = orderDao.getItemPhotos(itemId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemPhotos;
    }


    @RequestMapping(value = "/items/{item_id}/thumb", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getItemThumbPhoto(@PathVariable("item_id") Integer itemId) {
        List<ItemPhoto> itemPhotos = null;
        try {
            itemPhotos = orderDao.getItemPhotos(itemId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (itemPhotos.size()>0)
            return itemPhotos.get(0).getPhotoSmall();
        else
            return null;
    }

    @RequestMapping(value = "/items/{item_id}/info", method = RequestMethod.GET)
    public ItemInfo getItemInfo(@PathVariable("item_id") Integer itemId) {
        ItemInfo itemInfo = null;
        try {
            itemInfo = orderDao.getItemInfo(itemId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemInfo;
    }
}