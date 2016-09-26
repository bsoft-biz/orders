package biz.bsoft.web.controller;

import biz.bsoft.orders.model.ClientPOS;
import biz.bsoft.security.SecurityUserService;
import biz.bsoft.users.service.UserService;
import biz.bsoft.users.dao.UserSettingsRepository;
import biz.bsoft.users.model.User;
import biz.bsoft.users.model.UserSettings;
import biz.bsoft.service.MailService;
import biz.bsoft.web.errors.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.UUID;

/**
 * Created by vbabin on 27.03.2016.
 */
@RestController
//@CrossOrigin(origins = "*", maxAge=3600, allowedHeaders={"Authorization", "x-requested-with"})
//@CrossOrigin(origins = "http://localhost:8000")//http://localhost:8000
@RequestMapping("/users")
public class UsersRestController {

    private static final Logger logger =
            LoggerFactory.getLogger(UsersRestController.class);

    @Autowired
    UserService userService;

    //@Autowired
    //private SessionFactory sessionFactory;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SecurityUserService securityUserService;

    @Autowired
    UserSettingsRepository userSettingsRepository;

    @Autowired
    MailService mailService;

//    @RequestMapping(value = "/list/", method = RequestMethod.GET)
//    @Transactional
//    public List<User> list() {
//        List<User> users= new ArrayList<>();
//        try {
//            sessionFactory.getCurrentSession().createQuery("from UserRole")
//                    .list();
//            //logger.info("users' size = "+users.size());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return users;
//    }

    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }

 /* Ger a single objct in Json form in Spring Rest Services */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User getEmployee(@PathVariable("id") String id) {
        //logger.info("REST ID=" + id);
        User user = null;
        try {
            user = userService.findByUsername(id);
            /*if (user!=null) {
                logger.info("REST user=" + user.toString());
                logger.info("REST user.roles=" + user.getUserRole().toString());
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    @RequestMapping(value = "/add/{name}", method = RequestMethod.GET)
    public User add(@PathVariable("name") String id) {
        logger.error("REST name=" + id);
        User user = null;
        try {
            user = userService.add(id);
            /*if (user!=null) {
                logger.info("REST user=" + user.toString());
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    @RequestMapping(value = "/userSettings", method = RequestMethod.GET)
    public UserSettings getUserSettings() {
        UserSettings userSettings = userService.getCurrentUserSettings();
        //logger.info("userSettings =" + userSettings);
        return userSettings;
    }

    @RequestMapping(value = "/userSettings", method = RequestMethod.POST)
    public String setUserSettings(@RequestBody UserSettings userSettings) {
        //
        try {
            //logger.info("setUserSettings userSettings =" + userSettings);
            UserSettings currentUserSettings = userService.getCurrentUserSettings();
            //logger.info("setUserSettings currentUserSettings =" + currentUserSettings);
            ClientPOS currentClientPOS = currentUserSettings.getClientPOS();
            currentClientPOS.setPosName(userSettings.getClientPOS().getPosName());
            currentClientPOS.setPosAddress(userSettings.getClientPOS().getPosAddress());
            currentClientPOS.setPosPhone(userSettings.getClientPOS().getPosPhone());
            currentClientPOS.setManagerName(userSettings.getClientPOS().getManagerName());
            currentClientPOS.setManagerPhone(userSettings.getClientPOS().getManagerPhone());
            userSettingsRepository.save(currentUserSettings);
            //userService.setCurrentUserSettings(currentUserSettings);

        } catch (Exception e) {
            e.printStackTrace();
            return(e.getMessage());
        }
        return"";
    }

    @RequestMapping(value = "/userPassword", method = RequestMethod.POST)
    public String setUserPassword(@RequestParam("old")String oldPassword, @RequestParam("new")String newPassword) {
//        logger.info("userPassword' old = " + oldPassword);
//        logger.info("userPassword' new = " + newPassword);
        userService.setUserPassword(oldPassword, newPassword);
        /*try {
            userService.setUserPassword(oldPassword, newPassword);

        } catch (Exception e) {
            e.printStackTrace();
            //Ошибка смены пароля
            return("{\"msg\": \""+e.getMessage()+"\"}");
        }*/
        return "";
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public void resetUserPassword(HttpServletRequest request, @RequestParam("email") String userEmail) {
        final User user = userService.findUserByEmail(userEmail);
        if (user == null) {
            throw new UserNotFoundException();
        }
        final String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);

        mailService.sendPasswordResetTokenEmail(request, userEmail, user.getUsername(), token);
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.GET)
    public void changeUserPassword(HttpServletRequest request, @RequestParam("id") String userName, @RequestParam("token") String token) {
        securityUserService.authByPasswordResetToken(userName, token);
    }


    @RequestMapping(value = "/savePassword", method = RequestMethod.POST)
    public String savePassword(@RequestParam("token")String token, @RequestParam("password")String password) {
        userService.saveUserPassword(token, password);
        return "";
    }
}
