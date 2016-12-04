package biz.bsoft.web.controller;

import biz.bsoft.orders.model.ClientPOS;
import biz.bsoft.web.View;
import biz.bsoft.security.SecurityUserService;
import biz.bsoft.service.MailService;
import biz.bsoft.users.dao.UserSettingsRepository;
import biz.bsoft.users.model.User;
import biz.bsoft.users.model.UserSettings;
import biz.bsoft.users.service.UserService;
import biz.bsoft.web.dto.UserDto;
import biz.bsoft.web.errors.UserNotFoundException;
import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UsersRestController {

    private static final Logger logger =
            LoggerFactory.getLogger(UsersRestController.class);

    @Autowired
    UserService userService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SecurityUserService securityUserService;

    @Autowired
    UserSettingsRepository userSettingsRepository;

    @Autowired
    MailService mailService;

    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }

    @RequestMapping(value = "/userSettings", method = RequestMethod.GET)
    public UserSettings getUserSettings() {
        UserSettings userSettings = userService.getCurrentUserSettings();
        //logger.info("userSettings =" + userSettings);
        return userSettings;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void register(HttpServletRequest request, @RequestBody UserDto userDto) {
        User user = userService.registerNewUser(userDto);
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);

        mailService.sendPasswordResetTokenEmail(request, user.getEmail(), user.getUsername(), token);
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

    @RequestMapping(value = "/userPoses", method = RequestMethod.GET)
    @JsonView(View.Summary.class)
    public Set<ClientPOS> getUserPoses(Principal user){
        Set<ClientPOS> clientPOSes = userService.getUserPoses(user.getName());
        return clientPOSes;
    }

    @RequestMapping(value = "/userPassword", method = RequestMethod.POST)
    public String setUserPassword(@RequestParam("old")String oldPassword, @RequestParam("new")String newPassword) {
        userService.setUserPassword(oldPassword, newPassword);
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
