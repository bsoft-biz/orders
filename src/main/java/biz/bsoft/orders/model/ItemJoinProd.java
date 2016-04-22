package biz.bsoft.orders.model;

//import javax.persistence.*;

/**
 * Created by vbereza on 21.03.2016.
 */
public class ItemJoinProd {

    private Order iOrder;
    private OrderItem iOrderItem;
    private Item iItem;
    private Integer idOrder;
    private Integer itemCount ;
    private Integer itemCount2 ;
    private String itemName;

    public static Order ORDER;

    public Order getiOrder() {
        return iOrder;
    }

    public void setiOrder(Order iOrder) {
        this.iOrder = iOrder;
    }

    public OrderItem getiOrderItem() {
        return iOrderItem;
    }

    public void setiOrderItem(OrderItem iOrderItem) {
        this.iOrderItem = iOrderItem;
    }

    public Item getiItem() {
        return iItem;
    }

    public void setiItem(Item iItem) {
        this.iItem = iItem;
    }

    public Integer getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(Integer idOrder) {
        this.idOrder = idOrder;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getItemCount2() {
        return itemCount2;
    }

    public void setItemCount2(Integer itemCount2) {
        this.itemCount2 = itemCount2;
    }
}