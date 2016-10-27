package biz.bsoft.users.model;

import biz.bsoft.orders.model.ClientPOS;
import biz.bsoft.orders.model.View;
import biz.bsoft.users.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;

/**
 * Created by vbabin on 19.10.2016.
 */
@Entity
@Table(name = "t_user_poses")
public class UserPos {
    @Id
    private Integer id;

    @OneToOne
    @JoinColumn(name = "username")
    private User user;

    @JsonView(View.Summary.class)
    @OneToOne
    private ClientPOS clientPOS;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ClientPOS getClientPOS() {
        return clientPOS;
    }

    public void setClientPOS(ClientPOS clientPOS) {
        this.clientPOS = clientPOS;
    }
}