package biz.bsoft.web.errors;

import biz.bsoft.web.errors.UserNotFoundException;
import biz.bsoft.web.errors.ValidateOrderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by vbabin on 16.08.2016.
 */
@ControllerAdvice
public class ControllerResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messages;


    @ExceptionHandler({ValidateOrderException.class})
    void handleValidateOrderException(ValidateOrderException e, HttpServletResponse response) throws IOException {
        Locale locale = LocaleContextHolder.getLocale();
        response.sendError(HttpStatus.BAD_REQUEST.value(),messages.getMessage("error.validateOrderMessage",new Object[] {e.getMessage()},locale));
    }

    @ExceptionHandler({ UserNotFoundException.class })
    void handleUserNotFound(UserNotFoundException e, HttpServletResponse response) throws IOException {
        Locale locale = LocaleContextHolder.getLocale();
        response.sendError(HttpStatus.NOT_FOUND.value(),messages.getMessage("error.userNotFound",null,locale));
    }

    @ExceptionHandler({ PosNotFoundException.class })
    void handlePosNotFound(PosNotFoundException e, HttpServletResponse response) throws IOException {
        Locale locale = LocaleContextHolder.getLocale();
        response.sendError(HttpStatus.BAD_REQUEST.value(),messages.getMessage("error.posNotFound",new Object[] {e.getUserPos()},locale));
    }
}
