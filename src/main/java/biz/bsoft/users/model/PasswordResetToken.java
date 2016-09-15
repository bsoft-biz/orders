package biz.bsoft.users.model;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by vbabin on 14.08.2016.
 */
@Entity
@Table(name = "passwordresettoken")
public class PasswordResetToken {
    @Id
    @GeneratedValue
    private Integer id;
    @OneToOne
    @JoinColumn(name = "username", nullable = false)
    private User user;
    private String token;
    @Column(name="expirydate")
    private Date expiryDate;

    public PasswordResetToken() {
    }

    public PasswordResetToken(User user, String token) {
        this.user = user;
        this.token = token;

        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, 60 * 24);
        this.expiryDate = cal.getTime();
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
