package biz.bsoft.util;

import biz.bsoft.orders.dao.OrderDaoImpl;
import biz.bsoft.orders.dao.OrderItemRepository;
import biz.bsoft.orders.model.OrderGroupStatus;
import biz.bsoft.orders.model.OrderItem;
import biz.bsoft.users.model.UserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

/**
 * Created by vbabin on 16.09.2016.
 */
@Component
public class MailUtil {
    private static final Logger logger =
            LoggerFactory.getLogger(OrderDaoImpl.class);

    @Autowired
    private Environment env;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MessageSource messages;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public SimpleMailMessage constructEmail(String subject, String body, String mailTo) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(mailTo);
        email.setFrom(env.getProperty("support.email"));
        return email;
    }

    public MimeMessage constructMessage(String subject, String body, String mailTo) throws MessagingException {
        final MimeMessage msg = mailSender.createMimeMessage();

        msg.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(mailTo));
        msg.addFrom(new InternetAddress[] { new InternetAddress(env.getProperty("support.email")) });

        msg.setSubject(subject, "UTF-8");
        msg.setText(body, "UTF-8");
        return msg;
    }

    public void constructAndSendEmail(String subject, String body, String mailTo) {
        try {
            final MimeMessage msg = constructMessage(subject, body, mailTo);
            mailSender.send(msg);
        }
        catch (MessagingException E){
            throw new RuntimeException(E.getMessage(),E.getCause());
        }
    }

    /**
     * THYMELEAF: Template Resolver for email templates.
     */
    private TemplateResolver emailTemplateResolver() {
        TemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/mail/");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setOrder(1);
        return templateResolver;
    }

    public void constructAndSendEmail(String mailTo, String subject, Context ctx, String template) {
        try {
            final MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage,true,"UTF-8");
            message.setTo(mailTo);
            message.setFrom(env.getProperty("support.email"));
            message.setSubject(subject);

            SpringTemplateEngine templateEngine = new SpringTemplateEngine();
            templateEngine.setMessageSource(messages);
            templateEngine.addTemplateResolver(emailTemplateResolver());
            // Prepare the evaluation context
            final String htmlContent = templateEngine.process(template, ctx);
            message.setText(htmlContent, true); // true = isHtml
            message.addInline("logo.png", new ClassPathResource("images/logo.png"), "image/png");
            message.addInline("bsoft.biz.png", new ClassPathResource("images/poweredbybs.png"), "image/png");
            mailSender.send(mimeMessage);
        }
        catch (MessagingException E){
            throw new RuntimeException(E.getMessage(),E.getCause());
        }
    }

    @Async
    public void sendNotificationEmailConfirmOperator(OrderGroupStatus orderGroupStatus){
        Locale locale = new Locale(env.getProperty("email.notification.locale"));
        String subject = messages.getMessage("email.notificationConfirmOperatorSubject", new Object[] {orderGroupStatus.getOrder().getClientPOS().getPosName(), orderGroupStatus.getOrder().getOrderDate(), orderGroupStatus.getGroup().getGroupName()},locale);
        //String body = messages.getMessage("email.newConfirmMessageBody", new Object[] {},locale);

        final Context ctx = new Context(locale);
        ctx.setVariable("posName", orderGroupStatus.getOrder().getClientPOS().getPosName());
        ctx.setVariable("orderDate", orderGroupStatus.getOrder().getOrderDate());
        ctx.setVariable("groupName", orderGroupStatus.getGroup().getGroupName());
        constructAndSendEmail(env.getProperty("operator.email"), subject, ctx, "confirmationOperator.html");
    }

    @Async
    public void sendNotificationEmailConfirmClient(OrderGroupStatus orderGroupStatus, UserSettings currentUserSettings ){
        Locale locale = new Locale(env.getProperty("email.notification.locale"));
        String subject = messages.getMessage("email.notificationConfirmClientSubject", new Object[] {orderGroupStatus.getOrder().getOrderDate(), orderGroupStatus.getGroup().getGroupName()},locale);
        //String body = messages.getMessage("email.newConfirmMessageBody", new Object[] {},locale);

        List<OrderItem> orderItems = orderItemRepository.findByOrderAndItem_ItemGroup(orderGroupStatus.getOrder(),orderGroupStatus.getGroup());
        final Context ctx = new Context(locale);
        ctx.setVariable("userName", currentUserSettings.getUserGreeting());
        ctx.setVariable("orderDate", orderGroupStatus.getOrder().getOrderDate());
        ctx.setVariable("groupName", orderGroupStatus.getGroup().getGroupName());
        ctx.setVariable("orderItems", orderItems);
        constructAndSendEmail(currentUserSettings.getEmail(), subject, ctx, "confirmationClient.html");
    }

    @Async
    public void sendPasswordResetTokenEmail(HttpServletRequest request, String userEmail, String userName, String token){
        //Locale locale = LocaleContextHolder.getLocale();
        Locale locale = new Locale(env.getProperty("email.notification.locale"));
        String subject = messages.getMessage("email.ResetPasswordTitle", null, locale);
        String url = getAppUrl(request) + "/#/passwdreset?id=" + userName + "&token=" + token;
        final Context ctx = new Context(locale);
        ctx.setVariable("url", url);
        constructAndSendEmail(userEmail, subject, ctx, "passwordReset.html");
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
