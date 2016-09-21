package biz.bsoft.orders.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;

/**
 * Created by vbabin on 14.04.2016.
 */
@Entity
@Table(name = "t_items_groups")
@NamedQuery(name = ItemGroup.FIND_ALL, query = "select ig from ItemGroup ig")
public class ItemGroup {
    public static final String FIND_ALL="GET_ALL_ITEMGROUPS";
    @Id
    @JsonView(View.GroupsId.class)
    private int id;
    @Column(name="groupname")
    private String groupName;

    @Override
    public String toString() {
        return "ItemGroup{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                '}';
    }

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
