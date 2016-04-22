package biz.bsoft.orders.model;

/**
 * Created by vbabin on 12.04.2016.
 */
public class FullOrderItem {
    //private Item item;
    private Integer idOrderItem;
    //private Integer idItemGroup;
    private Integer idItem;
    private Integer itemCount ;
    private Integer itemCount2 ;

    public FullOrderItem(Integer idOrderItem/*, Integer idItemGroup*/, Integer idItem, Integer itemCount, Integer itemCount2) {
        this.idOrderItem = idOrderItem;
        //this.idItemGroup = idItemGroup;
        this.idItem = idItem;
        this.itemCount = itemCount;
        this.itemCount2 = itemCount2;
    }

    public FullOrderItem() {
    }

    @Override
    public String toString() {
        return "FullOrderItem{" +
                "idOrderItem=" + idOrderItem +
               // ", idItemGroup=" + idItemGroup +
                ", idItem=" + idItem +
                ", itemCount=" + itemCount +
                ", itemCount2=" + itemCount2 +
                '}';
    }

    //    public Item getItem() {
//        return item;
//    }
//
//    public void setItem(Item item) {
//        this.item = item;
//    }

    public Integer getIdOrderItem() {
        return idOrderItem;
    }

    public void setIdOrderItem(Integer idOrderItem) {
        this.idOrderItem = idOrderItem;
    }

    /*public Integer getIdItemGroup() {
        return idItemGroup;
    }

    public void setIdItemGroup(Integer idItemGroup) {
        this.idItemGroup = idItemGroup;
    }*/

    public Integer getIdItem() {
        return idItem;
    }

    public void setIdItem(Integer idItem) {
        this.idItem = idItem;
    }

    public Integer getItemCount() {
        //return (itemCount==null)?0:itemCount;
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    public Integer getItemCount2() {
        //return (itemCount2==null)?0:itemCount2;
        return itemCount2;
    }

    public void setItemCount2(Integer itemCount2) {
        this.itemCount2 = itemCount2;
    }

}
