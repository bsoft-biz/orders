package biz.bsoft.orders.model;

import biz.bsoft.web.View;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

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
    @JsonView(View.ItemsId.class)
    private Integer id;
    @Basic
    @JsonView(View.ItemsAll.class)
    @Column(name="itemname")
    private String itemName;
    //@JsonIgnore
    @ManyToOne
    @JoinColumn(name="group_id")
    @JsonView(View.ItemsAll.class)
    private ItemGroup itemGroup;
    @JsonIgnore
    @Basic
    private Integer extid;
    @Basic
    @JsonView(View.ItemsAll.class)
    private Integer ord;
    Boolean archive;
    /*@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "item")
    @JsonManagedReference
    @JsonIgnore
    //@JsonView(View.Summary.class)
    private List<ItemPhoto> itemPhotos;*/

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

    public Integer getOrd() {
        return ord;
    }

    public void setOrd(Integer ord) {
        this.ord = ord;
    }

    public Boolean isArchive() {
        return archive;
    }

    public void setArchive(Boolean archive) {
        this.archive = archive;
    }

//    public List<ItemPhoto> getItemPhotos() {
//        return itemPhotos;
//    }
//
//    public void setItemPhotos(List<ItemPhoto> itemPhotos) {
//        this.itemPhotos = itemPhotos;
//    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", itemName='" + itemName + '\'' +
                ", itemGroup=" + itemGroup +
                ", extid=" + extid +
                ", ord=" + ord +
                ", archive=" + archive +
                '}';
    }

    public Item() {
    }

    public Item(String itemName, ItemGroup itemGroup) {
        this.itemName = itemName;
        this.itemGroup = itemGroup;
    }
}
