package biz.bsoft.security;

import biz.bsoft.users.dao.PasswordResetTokenRepository;
import biz.bsoft.users.model.PasswordResetToken;
import biz.bsoft.users.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Calendar;

/**
 * Created by vbabin on 16.08.2016.
 */
@Service
public class SecurityUserServiceImpl implements SecurityUserService {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public PasswordResetToken validatePasswordResetToken(String userName, String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if (passwordResetToken == null) {
            throw new RuntimeException("Token not found");
        }
        if ((passwordResetToken == null) || !(passwordResetToken.getUser().getUsername().equals(userName))) {
            throw new RuntimeException("Invalid token for user "+userName);
        }

        Calendar cal = Calendar.getInstance();
        if ((passwordResetToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            throw new RuntimeException("Token has expired");
        }
        return passwordResetToken;
    }

    @Override
    public void authByPasswordResetToken(String userName, String token) {
        PasswordResetToken passwordResetToken = validatePasswordResetToken(userName, token);

        User orderUser = passwordResetToken.getUser();
        UserDetails springUser = userDetailsService.loadUserByUsername(orderUser.getUsername());

        final Authentication auth = new UsernamePasswordAuthenticationToken(springUser, null, springUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
