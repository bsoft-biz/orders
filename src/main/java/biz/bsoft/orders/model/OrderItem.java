package biz.bsoft.orders.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;

/**
 * Created by vbabin on 28.11.2015.
 */
@Entity
@Table(name = "t_order_items")
@NamedQueries({
        @NamedQuery(name = "P", query = "delete from OrderItem oi where oi.order.clientPOS.id =:p_client_pos_id and oi.order.orderDate=:p_date  and oi.item.itemGroup.id=:p_group_id"),
        @NamedQuery(name = OrderItem.DELETE_GROUP_ITEMS,query = "delete from OrderItem oi where oi.order=:p_order and oi.item in (select i from Item i where i.itemGroup.id=:p_group_id)"),
        @NamedQuery(name = OrderItem.GET_GROUP_ITEMS,query = "select oi from OrderItem oi where oi.order.clientPOS.id =:p_client_pos_id and oi.order.orderDate=:p_date  and oi.item.itemGroup.id=:p_group_id")}
)
public class OrderItem {
    public static final String DELETE_GROUP_ITEMS="DeleteGroupItems";
    public static final String GET_GROUP_ITEMS="GetGroupItems";
    @GeneratedValue
    @Id
    private Integer id;

    @JsonView(View.ItemsId.class)
    private Integer itemCount;

    @Basic
    @JsonView(View.ItemsId.class)
    private Integer itemCount2;

    @OneToOne()
    @JsonView(View.ItemsId.class)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "oder_id")//, nullable = false
    @JsonBackReference
    private Order order;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    public Integer getItemCount2() {
        return itemCount2;
    }

    public void setItemCount2(Integer itemCount2) {
        this.itemCount2 = itemCount2;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}