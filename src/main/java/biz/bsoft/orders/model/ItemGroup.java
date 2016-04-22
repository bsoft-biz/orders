package biz.bsoft.orders.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Created by vbabin on 14.04.2016.
 */
@Entity
@Table(name = "t_items_groups")
@NamedQuery(name = ItemGroup.FIND_ALL, query = "select ig from ItemGroup ig")
public class ItemGroup {
    public static final String FIND_ALL="GET_ALL_ITEMGROUPS";
    @Id
    private int id;
    private String groupName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
