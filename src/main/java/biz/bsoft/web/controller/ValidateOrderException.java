package biz.bsoft.web.controller;

/**
 * Created by vbabin on 10.08.2016.
 */
public class ValidateOrderException extends RuntimeException{
    public ValidateOrderException(String message) {
        super(message);
    }
}
