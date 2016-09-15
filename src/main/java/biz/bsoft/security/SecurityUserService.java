package biz.bsoft.security;

import biz.bsoft.users.model.PasswordResetToken;

/**
 * Created by vbabin on 16.08.2016.
 */
public interface SecurityUserService {
    PasswordResetToken validatePasswordResetToken (String userName, String token);
    void authByPasswordResetToken (String userName, String token);
}
