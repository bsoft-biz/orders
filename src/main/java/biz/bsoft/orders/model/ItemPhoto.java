package biz.bsoft.orders.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * Created by vbabin on 05.05.2016.
 */
@Entity
@Table(name = "t_items_photo")
@NamedQuery(name = ItemPhoto.GET_ITEM_PHOTOS, query = "select p from ItemPhoto p where p.item.id = :p_item_id")
public class ItemPhoto {
    public static final String GET_ITEM_PHOTOS ="GET_ITEM_PHOTOS";
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "item_id")//, nullable = false
    @JsonIgnore
    private Item item;
    private String isDef;
    @Column(name = "photo_name")
    private String photoName;
    @Column(name = "photo_comment")
    private String photoComment;
    private byte[] photo;
    @Column(name = "photo_small")
    private byte[] photoSmall;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getIsDef() {
        return isDef;
    }

    public void setIsDef(String isDef) {
        this.isDef = isDef;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getPhotoComment() {
        return photoComment;
    }

    public void setPhotoComment(String photoComment) {
        this.photoComment = photoComment;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public byte[] getPhotoSmall() {
        return photoSmall;
    }

    public void setPhotoSmall(byte[] photoSmall) {
        this.photoSmall = photoSmall;
    }
}
