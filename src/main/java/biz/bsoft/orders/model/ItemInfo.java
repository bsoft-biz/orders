package biz.bsoft.orders.model;

import biz.bsoft.web.View;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;

/**
 * Created by vbabin on 05.05.2016.
 */
@Entity
@Table(name = "t_items_info")
public class ItemInfo {
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
    @Column(name = "min_count")
    @JsonView(View.ItemInfoShort.class)
    private Integer minCount;
    @JsonView(View.ItemInfoShort.class)
    private Integer capacity;
    private Integer rye;
    @Column(name = "prod_days")
    @JsonView(View.ItemInfoShort.class)
    private String prodDays;
    @Column(name = "site_url")
    @JsonView(View.ItemInfoShort.class)
    private String siteUrl;
    @JsonView(View.ItemInfoShort.class)
    private Float price;
    private Boolean whole;

    @JsonView(View.ItemInfoShort.class)
    @JsonProperty("itemId")
    public Integer getItemId(){
        if (item != null){
            return item.getId();
        }
        else
            return null;
    }

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

    public Integer getMinCount() {
        return minCount;
    }

    public void setMinCount(Integer minCount) {
        this.minCount = minCount;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getRye() {
        return rye;
    }

    public void setRye(Integer rye) {
        this.rye = rye;
    }

    public String getProdDays() {
        return prodDays;
    }

    public void setProdDays(String prodDays) {
        this.prodDays = prodDays;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Boolean getWhole() {
        return whole;
    }

    public void setWhole(Boolean whole) {
        this.whole = whole;
    }
}
