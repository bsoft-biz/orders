package biz.bsoft.web.errors;

/**
 * Created by vbabin on 28.10.2016.
 */
public class PosNotFoundException extends RuntimeException {
    private Integer userPos;

    public Integer getUserPos() {
        return userPos;
    }

    public PosNotFoundException(Integer userPos) {
        super();
        this.userPos = userPos;
    }
}
