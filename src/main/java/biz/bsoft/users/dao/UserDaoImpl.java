package biz.bsoft.users.dao;

import biz.bsoft.users.model.User;
import biz.bsoft.users.model.UserSettings;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vbabin on 27.03.2016.
 */
@Repository
@Transactional
public class UserDaoImpl implements UserDao {

    private static final Logger logger =
            LoggerFactory.getLogger(UserDaoImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public User findByUserName(String username) {
        List<User> users= new ArrayList<>();
        users = sessionFactory.getCurrentSession().createQuery("from User where username=:p_uname")
                .setParameter("p_uname",username)
                .list();
//        Session session = sessionFactory.getCurrentSession();
        //session.createQuery("from User").list();
//        Query query = session.getNamedQuery("allUsers");
        //logger.info("users' size = "+users.size());
        if(users.size()>0){
            return users.get(0);
        }
        else {
            return null;
        }

    }

    @Override
    public User add(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(username);
        user.setEnabled(true);
        //user.setUserRole();
        sessionFactory.getCurrentSession().save(user);
        return user;
    }

    @Override
    public UserSettings getCurrentUserSettings() { //@AuthenticationPrincipal User user;
        org.springframework.security.core.userdetails.User user =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Session session = sessionFactory.getCurrentSession();
        Query query = session.getNamedQuery(UserSettings.GET_USER_SETTINGS);
        query.setParameter("p_username", user.getUsername());
        UserSettings userSettings= (UserSettings) query.list().get(0);
        return userSettings;
    }

    @Override
    public void setCurrentUserSettings(UserSettings userSettings) {
        sessionFactory.getCurrentSession().update(userSettings);
    }
}
