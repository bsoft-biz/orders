package biz.bsoft.users.service;

import biz.bsoft.security.SecurityUserService;
import biz.bsoft.users.dao.PasswordResetTokenRepository;
import biz.bsoft.users.dao.UserRepository;
import biz.bsoft.users.dao.UserSettingsRepository;
import biz.bsoft.users.model.PasswordResetToken;
import biz.bsoft.users.model.User;
import biz.bsoft.users.model.UserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Locale;

/**
 * Created by vbabin on 27.03.2016.
 */
@Repository
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger =
            LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private MessageSource messages;

//    @Autowired
//    private SessionFactory sessionFactory;

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private SecurityUserService securityUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String username) {
        return repository.findByUsername(username);
//        List<User> users= new ArrayList<>();
//        users = sessionFactory.getCurrentSession().createQuery("from User where username=:p_uname")
//                .setParameter("p_uname",username)
//                .list();
////        Session session = sessionFactory.getCurrentSession();
//        //session.createQuery("from User").list();
////        Query query = session.getNamedQuery("allUsers");
//        //logger.info("users' size = "+users.size());
//        if(users.size()>0){
//            return users.get(0);
//        }
//        else {
//            return null;
//        }

    }

    @Override
    public User add(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(username);
        user.setEnabled(true);
        //user.setUserRole();
        //sessionFactory.getCurrentSession().save(user);
        repository.save(user);
        return user;
    }

    @Override
    public UserSettings getCurrentUserSettings() { //@AuthenticationPrincipal User user;
        org.springframework.security.core.userdetails.User user =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userSettingsRepository.findByUser_Username(user.getUsername());

//        Session session = sessionFactory.getCurrentSession();
//        Query query = session.getNamedQuery(UserSettings.GET_USER_SETTINGS);
//        query.setParameter("p_username", user.getUsername());
//        UserSettings userSettings = (UserSettings) query.list().get(0);
//        return userSettings;
    }

//    @Override
//    public void setCurrentUserSettings(UserSettings userSettings) {
//        sessionFactory.getCurrentSession().update(userSettings);
//    }

    @Override
    public void setUserPassword(String oldPassword, String newPassword) {
        Locale locale = LocaleContextHolder.getLocale();
        UserSettings userSettings = getCurrentUserSettings();
        //PasswordEncoder encoder = new BCryptPasswordEncoder();
        if(passwordEncoder.matches(oldPassword,userSettings.getUser().getPassword())){
            getCurrentUserSettings().getUser().setPassword(passwordEncoder.encode(newPassword));
        }
        else
            throw new RuntimeException(messages.getMessage("error.userWrongOldPassword",null,locale));
    }

    @Override
    public void saveUserPassword(String token, String password) {
        final User user = /*repository.*/findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        securityUserService.validatePasswordResetToken(user.getUsername(), token);
        user.setPassword(passwordEncoder.encode(password));
        //sessionFactory.getCurrentSession().update(user);
        repository.save(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, token);
        //sessionFactory.getCurrentSession().save(passwordResetToken);
        passwordResetTokenRepository.save(passwordResetToken);
    }
}
