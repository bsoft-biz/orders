package biz.bsoft.users.service;

import biz.bsoft.orders.dao.ClientPosRepository;
import biz.bsoft.orders.model.ClientPOS;
import biz.bsoft.security.SecurityUserService;
import biz.bsoft.users.dao.*;
import biz.bsoft.users.model.PasswordResetToken;
import biz.bsoft.users.model.User;
import biz.bsoft.users.model.UserSettings;
import biz.bsoft.users.model.VerificationToken;
import biz.bsoft.web.dto.UserDto;
import biz.bsoft.web.errors.PosNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by vbabin on 27.03.2016.
 */
@Repository
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger =
            LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private MessageSource messages;

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    UserPosRepository userPosRepository;

    @Autowired
    ClientPosRepository clientPosRepository;

    @Autowired
    private SecurityUserService securityUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public UserSettings getCurrentUserSettings() { //@AuthenticationPrincipal User user;
        org.springframework.security.core.userdetails.User user =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userSettingsRepository.findByUser_Username(user.getUsername());
    }

    @Override
    public void setUserPassword(String oldPassword, String newPassword) {
        Locale locale = LocaleContextHolder.getLocale();
        UserSettings userSettings = getCurrentUserSettings();
        if(passwordEncoder.matches(oldPassword,userSettings.getUser().getPassword())){
            getCurrentUserSettings().getUser().setPassword(passwordEncoder.encode(newPassword));
        }
        else
            throw new RuntimeException(messages.getMessage("error.userWrongOldPassword",null,locale));
    }

    @Override
    public void saveUserPassword(String token, String password) {
        final User user = /*repository.*/findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        securityUserService.validatePasswordResetToken(user.getUsername(), token);
        user.setPassword(passwordEncoder.encode(password));
        repository.save(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public void createVerificationTokenForUser(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(user, token);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, token);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public Set<ClientPOS> getUserPoses(String userName) {
        HashSet<ClientPOS> clientPOSes = userPosRepository.findByUser_Username(userName).stream().map(userPos -> userPos.getClientPOS()).collect(Collectors.toCollection(HashSet<ClientPOS>::new));
        ClientPOS defaultPos=getCurrentUserSettings().getClientPOS();
        if (!clientPOSes.contains(defaultPos))
            clientPOSes.add(defaultPos);
        return clientPOSes;
    }

    @Override
    public void checkUserPos(String userName, Integer PosId) {
        ClientPOS clientPOS = clientPosRepository.findOne(PosId);
        if(!getUserPoses(userName).contains(clientPOS))
            throw new PosNotFoundException(PosId);
    }

    @Override
    public User registerNewUser(UserDto userDto) {
        if (repository.findByEmail(userDto.getEmail()) != null)
            throw new RuntimeException("The email address you have entered is already registered.");
        if (repository.findByUsername(userDto.getUsername()) != null)
            throw new RuntimeException("The login you have entered is already registered.");
        User user = new User();
        user.setEnabled(false);
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        repository.save(user);
        return user;
    }
}
