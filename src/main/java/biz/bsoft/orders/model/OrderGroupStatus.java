package biz.bsoft.orders.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * Created by vbabin on 08.08.2016.
 */
@Entity
@Table(name = "t_orders_groups_statuses")
//@NamedQuery(name = OrderGroupStatus.GET_ORDER_GROUP_STATUS, query = "select s from OrderGroupStatus s where s.order=:p_order and s.group.id=:p_group")
@NamedQuery(name = OrderGroupStatus.GET_ORDER_GROUP_STATUS, query = "select s from OrderGroupStatus s " +
        "where s.order.clientPOS.id=:p_client_pos and s.order.orderDate=:p_date and s.group.id=:p_group")
public class OrderGroupStatus {
    public static final String GET_ORDER_GROUP_STATUS="GET_ORDER_GROUP_STATUS";
    @Id
    @GeneratedValue
    @JsonIgnore
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "order_id")
    //@JsonBackReference
    @JsonIgnore
    private Order order;
    @ManyToOne
    @JoinColumn(name="group_id")
    @JsonIgnore
    private ItemGroup group;
    @Column(name = "status_id")
    @Enumerated
    private OrderStatus status;
    private String commentText;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return "OrderGroupStatus{" +
                "id=" + id +
                ", order=" + order +
                ", group=" + group +
                ", status=" + status +
                ", commentText='" + commentText + '\'' +
                '}';
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public ItemGroup getGroup() {
        return group;
    }

    public void setGroup(ItemGroup group) {
        this.group = group;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
}
