package biz.bsoft.orders.model;

import javax.persistence.*;

/**
 * Created by vbabin on 05.05.2016.
 */
@Entity
@Table(name = "t_items_info")
@NamedQuery(name = ItemInfo.GET_ITEM_INFO, query = "select i from ItemInfo i where i.item.id = :p_item_id")
public class ItemInfo {
    public static final String GET_ITEM_INFO = "GET_ITEM_INFO";
    @Id
    @GeneratedValue
    private Integer id;
    @OneToOne
    @JoinColumn(name="item_id")
    private Item item;
    private Float volume;
    private String barcode;
    private String sert;
    private String unit;
    @Column(name = "term_of_use")
    private String termOfUse;
    private String iso;
    private String ingredients;
    private Float protein;
    private Float fat;
    private Float carbohydrate;
    private String calories;
    private String description;

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

    public Float getVolume() {
        return volume;
    }

    public void setVolume(Float volume) {
        this.volume = volume;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getSert() {
        return sert;
    }

    public void setSert(String sert) {
        this.sert = sert;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTermOfUse() {
        return termOfUse;
    }

    public void setTermOfUse(String termOfUse) {
        this.termOfUse = termOfUse;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public Float getProtein() {
        return protein;
    }

    public void setProtein(Float protein) {
        this.protein = protein;
    }

    public Float getFat() {
        return fat;
    }

    public void setFat(Float fat) {
        this.fat = fat;
    }

    public Float getCarbohydrate() {
        return carbohydrate;
    }

    public void setCarbohydrate(Float carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
