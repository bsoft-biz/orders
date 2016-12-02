package biz.bsoft.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vbabin on 01.07.2016.
 */
public class OrderItemError {
    Integer id;
    String message;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("msg")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
