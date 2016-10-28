package biz.bsoft.users.service;

import biz.bsoft.orders.model.ClientPOS;
import biz.bsoft.users.model.User;
import biz.bsoft.users.model.UserSettings;

import java.util.Set;

/**
 * Created by vbabin on 27.03.2016.
 */
public interface UserService {
    User findByUsername(String username);
    User add(String username);
    UserSettings getCurrentUserSettings();
    void setUserPassword(String oldPassword, String newPassword);
    void saveUserPassword(String token, String password);

    User findUserByEmail(String email);

    void createPasswordResetTokenForUser(User user, String token);

    Set<ClientPOS> getUserPoses(String userName);

    void checkUserPos(String userName, Integer PosId);
}
