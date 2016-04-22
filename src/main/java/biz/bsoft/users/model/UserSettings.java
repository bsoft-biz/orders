package biz.bsoft.users.model;

import biz.bsoft.orders.model.ClientPOS;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.OneToOne;


/**
 * Created by vbabin on 18.04.2016.
 */
@Entity
@Table(name = "t_user_settings")
@NamedQuery(name = UserSettings.GET_USER_SETTINGS, query = "select us from UserSettings us where user.username=:p_username")
public class UserSettings {
    public final static String GET_USER_SETTINGS="GetUserSettings";

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne
    @JoinColumn(name = "username")
    private User user;

    @OneToOne//(cascade = {CascadeType.REFRESH,CascadeType.MERGE, CascadeType.PERSIST})//, org.hibernate.annotations.CascadeType.SAVE_UPDATE
    @Cascade(CascadeType.SAVE_UPDATE)
    private ClientPOS clientPOS;

    private String userGreeting;

    @Override
    public String toString() {
        return "UserSettings{" +
                "id=" + id +
                ", user=" + user +
                ", clientPOS=" + clientPOS +
                ", userGreeting='" + userGreeting + '\'' +
                '}';
    }

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

    public String getUserGreeting() {
        return userGreeting;
    }

    public void setUserGreeting(String userGreeting) {
        this.userGreeting = userGreeting;
    }
}
