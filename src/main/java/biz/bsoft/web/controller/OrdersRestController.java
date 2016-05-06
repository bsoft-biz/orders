package biz.bsoft.web.controller;

import biz.bsoft.orders.dao.OrderDao;
import biz.bsoft.orders.model.*;
import biz.bsoft.users.dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
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

    @RequestMapping(value = "/order")//, params = { "client_pos_id", "date" })//
    public Order getOrder(//@RequestParam("client_pos_id") Integer clientPosId,
                          @RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date) {
        Integer clientPosId = userDao.getCurrentUserSettings().getClientPOS().getId();
        Order order = null;
        try {
            //logger.info("Date = "+date.toString()+" id="+clientPosId);
            order = orderDao.findOrder(clientPosId, date);
            /*if (order!=null) {
                logger.info("REST order = " + order.toString());
                logger.info("REST order.OrderItems = " + order.getOrderItems().toString());
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return order;
    }

    @RequestMapping(value = "/order", method = RequestMethod.POST)//iso = DateTimeFormat.ISO.DATE //, params = { "client_pos_id", "date" }
    public String setOrder(@RequestBody Order order,
                           //@RequestParam("client_pos_id") Integer clientPosId,
                           @RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date) {
        Integer clientPosId = userDao.getCurrentUserSettings().getClientPOS().getId();
        String result = null;
        //logger.info("order = "+order+ "order id ="+order.getId());
        //logger.info("order.getOrderItems().get(0).getItemCount() = " + order.getOrderItems().get(0).getItemCount());
        try {
            orderDao.saveOrder(order);
        } catch (Exception e) {
            result = e.getMessage();
            e.printStackTrace();
        }


//        Order order = null;
//        try {
//            logger.info("Date = "+date.toString()+" id="+clientPosId);
//            order = orderDao.findOrder(clientPosId, date);
//            if (order!=null) {
//                logger.info("REST order = " + order.toString());
//                logger.info("REST order.OrderItems = " + order.getOrderItems().toString());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return result;
    }

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

    @RequestMapping(value = "/fullorderitems")
    public List<FullOrderItem> getFullOrderItems(//@RequestParam("client_pos_id") Integer clientPosId,
                                                 @RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date,
                                                 @RequestParam("group_id") Integer groupId){
        Integer clientPosId = userDao.getCurrentUserSettings().getClientPOS().getId();
        List<FullOrderItem> items = null;
        try {
            items = orderDao.getFullOrderItems(clientPosId, date, groupId);
            /*for(FullOrderItem item : items)
            {
                logger.info(item.toString());
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    @RequestMapping(value = "/fullorderitems", method = RequestMethod.POST)
    public String setFullOrderItems(@RequestBody List<FullOrderItem> fullOrderItems,
                                                 //@RequestParam("client_pos_id") Integer clientPosId,
                                                 @RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date,
                                                 @RequestParam("group_id") Integer groupId){
        Integer clientPosId = userDao.getCurrentUserSettings().getClientPOS().getId();
        String result = null;
        //logger.info(fullOrderItems.toString());
        // удалить старые позици в ордерс
        orderDao.deleteItemsFromOrder(clientPosId, date, groupId);
        //добавляем полученные позици
        orderDao.addItemsToOrder(fullOrderItems, clientPosId, date, groupId);
        //
        return result;
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
