package biz.bsoft.users.service;

import biz.bsoft.users.model.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserPrincipal extends org.springframework.security.core.userdetails.User {

    private User user;

    public UserPrincipal(User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getUsername(), user.getPassword(), user.isEnabled(), true, true, !user.isLocked(), authorities);
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
