package biz.bsoft.web.controller;

import biz.bsoft.orders.dao.OrderDao;
import biz.bsoft.orders.model.*;
import biz.bsoft.users.dao.UserDao;
import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    UserDao userDao;

    private static final Logger logger =
            LoggerFactory.getLogger(OrdersRestController.class);

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Order getOrder(@PathVariable("id") Integer id) {
        Order order = null;
        LocalDateTime timePoint= LocalDateTime.now();
        LocalDate today = timePoint.toLocalDate();
        //Date today= new Date();
        //today.setTime(0);
        try {
            //logger.info("Date = "+today.toString()+" id="+id);
            order = orderDao.findOrder(id, today);
            /*if (order!=null) {
                logger.info("REST order = " + order.toString());
                logger.info("REST order.OrderItems = " + order.getOrderItems().toString());
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return order;
    }

    @RequestMapping(value = "/order")
    public Order getOrder(@RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date) {
        Integer clientPosId = userDao.getCurrentUserSettings().getClientPOS().getId();
        Order order = null;
        try {
            order = orderDao.findOrder(clientPosId, date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return order;
    }

    @JsonView(View.OrderSummary.class)
    @RequestMapping(value = "/orderstatus")
    public Order getOrderStatus(@RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date) {
        Integer clientPosId = userDao.getCurrentUserSettings().getClientPOS().getId();
        Order order = null;
        try {
            order = orderDao.findOrder(clientPosId, date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return order;
    }

    @JsonView(View.OrderSummary.class)
    @RequestMapping(value = "/confirmorder", method = RequestMethod.POST)
    public Order confirmOrder(@RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date,
                              HttpServletResponse response) {
        Integer clientPosId = userDao.getCurrentUserSettings().getClientPOS().getId();
        Order order = orderDao.confirmOrder(clientPosId,date);
        return order;
    }

    //@RequestMapping(value = "/order", method = RequestMethod.POST)
    public String setOrder(@RequestBody Order order,
                           @RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date) {
        Integer clientPosId = userDao.getCurrentUserSettings().getClientPOS().getId();
        String result = null;
        try {
            orderDao.saveOrder(order);
        } catch (Exception e) {
            result = e.getMessage();
            e.printStackTrace();
        }
        return result;
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
        Integer clientPosId = userDao.getCurrentUserSettings().getClientPOS().getId();
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
        Integer clientPosId = userDao.getCurrentUserSettings().getClientPOS().getId();
        //TODO need to check if all items a from the group because you can delete all items from group and insert from other group
        List<OrderItemError> orderItemErrors = orderDao.validateItems(orderItems, clientPosId, date);
        if (orderItemErrors.size()>0) {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return orderItemErrors;
        }
        // delete old items in orders
        orderDao.deleteItemsFromOrder(clientPosId, date, groupId);
        // add recived items in orders
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
        return itemPhotos.get(0).getPhotoSmall();
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