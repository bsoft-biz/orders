package biz.bsoft.util;

import biz.bsoft.orders.dao.OrderDaoImpl;
import biz.bsoft.orders.model.OrderGroupStatus;
import biz.bsoft.users.dao.UserService;
import biz.bsoft.users.model.UserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Created by vbabin on 16.09.2016.
 */
@Component
public class MailUtil {
    private static final Logger logger =
            LoggerFactory.getLogger(OrderDaoImpl.class);

    @Autowired
    UserService userService;

    @Autowired
    private Environment env;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MessageSource messages;

    public SimpleMailMessage constructEmail(String subject, String body, String mailTo) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(mailTo);
        email.setFrom(env.getProperty("support.email"));
        return email;
    }

    public void constructAndSendEmail(String subject, String body, String mailTo) {
        mailSender.send(constructEmail(subject, body, mailTo));
    }

    @Async
    public void sendNotificationEmailConfirm(OrderGroupStatus orderGroupStatus){
        Locale locale = LocaleContextHolder.getLocale();
        String title = messages.getMessage("email.newConfirmMessageTitle", new Object[] {orderGroupStatus.getOrder().getClientPOS().getPosName(), orderGroupStatus.getOrder().getOrderDate(), orderGroupStatus.getGroup().getGroupName()},locale);
        String body = messages.getMessage("email.newConfirmMessageBody", new Object[] {orderGroupStatus.getOrder().getClientPOS().getPosName(), orderGroupStatus.getOrder().getOrderDate(), orderGroupStatus.getGroup().getGroupName()},locale);
        constructAndSendEmail(title, body, env.getProperty("operator.email"));
    }

    @Async
    public void sendPasswordResetTokenEmail(HttpServletRequest request, String userEmail, String userName, String token){
        Locale locale = LocaleContextHolder.getLocale();
        String title;
        String body;
        String url = getAppUrl(request) + "/#/passwdreset?id=" + userName + "&token=" + token;
        title = "Password reset request";
        body=url;
//        title = messages.getMessage("email.newConfirmMessageTitle", new Object[] {orderGroupStatus.getOrder().getClientPOS().getPosName(), orderGroupStatus.getOrder().getOrderDate(), orderGroupStatus.getGroup().getGroupName()},locale);
//        body = messages.getMessage("email.newConfirmMessageBody", new Object[] {orderGroupStatus.getOrder().getClientPOS().getPosName(), orderGroupStatus.getOrder().getOrderDate(), orderGroupStatus.getGroup().getGroupName()},locale);
        constructAndSendEmail(title, body, userEmail);
    }

    private String getAppUrl(HttpServletRequest request) {
        String url;
        if (request.isSecure())
            url="https://";
        else
            url="http://";
        url+= request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        return url;
    }
}
