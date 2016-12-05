package biz.bsoft.users.service;

import biz.bsoft.users.dao.UserRepository;
import biz.bsoft.users.model.UserRole;
import biz.bsoft.users.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by vbabin on 27.03.2016.
 */
@Service("userDetailsService")
public class MyUserDetailsService implements UserDetailsService{
    @Autowired
    private UserRepository userRepository;

    @Transactional//(readOnly=true)
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(s);
        List<GrantedAuthority> authorities;
        authorities=buildUserAuthority(user.getUserRole());
        return buildUserForAuthentication(user,authorities);
    }
    // Converts User user to
    // org.springframework.security.core.userdetails.User
    private org.springframework.security.core.userdetails.User buildUserForAuthentication(User user,
                                            List<GrantedAuthority> authorities) {

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                user.isEnabled(), true, true, true, authorities);
    }

    private List<GrantedAuthority> buildUserAuthority(Set<UserRole> userRoles) {

        Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();

        // Build user's authorities
        for (UserRole userRole : userRoles) {
            setAuths.add(new SimpleGrantedAuthority(userRole.getRole()));
        }

        List<GrantedAuthority> Result = new ArrayList<GrantedAuthority>(setAuths);

        return Result;
    }
}
