package biz.bsoft.orders.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Created by vbabin on 28.11.2015.
 */
@Entity
@Table(name = "t_orders")
@NamedQueries ({@NamedQuery(name = "FIND_ALL", query = "SELECT o FROM Order o"),
        @NamedQuery(name = Order.FIND_ALL, query = "select o as iOrder, i as iOrderItem, z as iItem, o.id as idOrder, i.itemCount as itemCount, i.itemCount2 as itemCount2, z.itemName as itemName " +
                "from Order o inner join o.orderItems i on (o.orderDate=:p_date and o.clientPOS.id=:p_client_pos_id) right join i.item z"),
        @NamedQuery(name=Order.FIND_FULL, query = "select new biz.bsoft.orders.model.FullOrderItem(i.id, z.id, i.itemCount, i.itemCount2) " +
                "from Order o inner join o.orderItems i on (o.orderDate=:p_date and o.clientPOS.id=:p_client_pos_id ) right join i.item z " +
                "where z.itemGroup.id=:p_group_id")})//and i.item.itemGroup.id=:p_group_id
public class Order implements Serializable {

    public static final String FIND_ALL= "FindAllItemsOrder";
    public static final String FIND_FULL= "FindFullOrderItems";

    @GeneratedValue
    @Id
    private Integer id;

    //@Temporal(TemporalType.DATE)
    @Convert(converter = LocalDateAttributeConverter.class)
    @JsonFormat(pattern="dd.MM.yyyy")
    private LocalDate orderDate;

    @OneToOne()
    private ClientPOS clientPOS;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "order")
    @JsonManagedReference
    //@JoinColumn(name = "oder_id")
    private List<OrderItem> orderItems;

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public ClientPOS getClientPOS() {
        return clientPOS;
    }

    public void setClientPOS(ClientPOS clientPOS) {
        this.clientPOS = clientPOS;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

}
