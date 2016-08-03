package biz.bsoft.orders.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Created by vbabin on 28.07.2016.
 */
@Entity
@Table(name = "t_production_time")
@NamedQuery(name = ProductionTime.GET_PROD_TIME, query = "select t from ProductionTime t where t.itemGroup=:p_group and (t.rye=:p_rye or t.rye is null)")
public class ProductionTime {
    public static final String GET_PROD_TIME = "GET_PROD_TIME";
    @Id
    private Integer id;
    @ManyToOne
    @JoinColumn(name="group_id")
    private ItemGroup itemGroup;
    private Integer rye;
    @Column(name = "prod_time")
    private LocalDateTime prodTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ItemGroup getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(ItemGroup itemGroup) {
        this.itemGroup = itemGroup;
    }

    public Integer getRye() {
        return rye;
    }

    public void setRye(Integer rye) {
        this.rye = rye;
    }

    public LocalDateTime getProdTime() {
        return prodTime;
    }

    public void setProdTime(LocalDateTime prodTime) {
        this.prodTime = prodTime;
    }

    @Override
    public String toString() {
        return "ProductionTime{" +
                "id=" + id +
                ", itemGroup=" + itemGroup +
                ", rye=" + rye +
                ", prodTime=" + prodTime +
                '}';
    }
}
