package biz.bsoft.users.service;

import biz.bsoft.orders.dao.ClientPosRepository;
import biz.bsoft.orders.model.ClientPOS;
import biz.bsoft.security.SecurityUserService;
import biz.bsoft.users.dao.PasswordResetTokenRepository;
import biz.bsoft.users.dao.UserPosRepository;
import biz.bsoft.users.dao.UserRepository;
import biz.bsoft.users.dao.VerificationTokenRepository;
import biz.bsoft.users.model.PasswordResetToken;
import biz.bsoft.users.model.User;
import biz.bsoft.users.model.VerificationToken;
import biz.bsoft.web.dto.UserDto;
import biz.bsoft.web.errors.PosNotFoundException;
import biz.bsoft.web.errors.UserAlreadyExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

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
    public User getCurrentUser() {
        return ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

    @Override
    public void setUserPassword(String oldPassword, String newPassword) {
        Locale locale = LocaleContextHolder.getLocale();
        User user = getCurrentUser();
        if(passwordEncoder.matches(oldPassword,user.getPassword())){
            user.setPassword(passwordEncoder.encode(newPassword));
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
        ClientPOS defaultPos=getCurrentUser().getClientPOS();
        if (!clientPOSes.contains(defaultPos))
            clientPOSes.add(defaultPos);
        return clientPOSes;
    }

    @Override
    public void checkUserPos(String userName, Integer PosId) {
        Set<Integer> clientPOSes = getUserPoses(userName).stream().map(clientPOS -> clientPOS.getId()).collect(Collectors.toCollection(HashSet<Integer>::new));
        if(!clientPOSes.contains(PosId))
            throw new PosNotFoundException(PosId);
    }

    @Override
    public User registerNewUser(UserDto userDto) {
        Locale locale = LocaleContextHolder.getLocale();
        if (repository.findByEmail(userDto.getEmail()) != null)
            throw new UserAlreadyExistException(messages.getMessage("error.EmailAlreadyExist",null,locale));//""
        if (repository.findByUsername(userDto.getUsername()) != null)
            throw new UserAlreadyExistException(messages.getMessage("error.UsernameAlreadyExist",null,locale));//""
        User user = new User();
        user.setEnabled(false);
        user.setLocked(true);
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        repository.save(user);
        return user;
    }

    @Override
    public void validateVerificationToken(String token) {
        final VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            return;
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            verificationTokenRepository.delete(verificationToken);
            return;
        }

        user.setEnabled(true);
        repository.save(user);
    }

    @Override
    public void updatePos(ClientPOS clientPOS) {
        checkUserPos(getCurrentUser().getUsername(), clientPOS.getId());
        ClientPOS savedClientPOS = clientPosRepository.findOne(clientPOS.getId());
        savedClientPOS.setPosName(clientPOS.getPosName());
        savedClientPOS.setPosAddress(clientPOS.getPosAddress());
        savedClientPOS.setPosPhone(clientPOS.getPosPhone());
        clientPosRepository.save(savedClientPOS);
    }

    @Override
    public void updateUser(UserDto userDto) {
        User user = getCurrentUser();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        repository.save(user);
    }
}
