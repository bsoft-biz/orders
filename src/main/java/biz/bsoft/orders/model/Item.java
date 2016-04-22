package biz.bsoft.orders.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by vbabin on 28.11.2015.
 */
@Entity
@Table(name = "t_items")
@NamedQuery(name = "GET_ALL_ITEMS", query = "SELECT i FROM Item i")
public class Item implements Serializable {
    @GeneratedValue
    @Id
    private Integer id;
    @Basic
    private String itemName;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="group_id")
    private ItemGroup itemGroup;
    @JsonIgnore
    @Basic
    private Integer extid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getExtid() {
        return extid;
    }

    public void setExtid(Integer extid) {
        this.extid = extid;
    }

    public ItemGroup getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(ItemGroup itemGroup) {
        this.itemGroup = itemGroup;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", itemName='" + itemName + '\'' +
                ", extid=" + extid +
                '}';
    }
}
