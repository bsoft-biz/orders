package biz.bsoft.orders.model;

/**
 * Created by vbabin on 10.05.2016.
 */
public class View {
    public interface GroupsId {}
    public interface ItemsId extends GroupsId {}
    public interface ItemsAll extends ItemsId {}
    public interface Summary extends ItemsId {}
}