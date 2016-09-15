package biz.bsoft.users.dao;

import biz.bsoft.users.model.User;
import biz.bsoft.users.model.UserSettings;

/**
 * Created by vbabin on 27.03.2016.
 */
public interface UserService {
    User findByUsername(String username);
    User add(String username);
    UserSettings getCurrentUserSettings();
    void setCurrentUserSettings(UserSettings userSettings);
    void setUserPassword(String oldPassword, String newPassword);
    void saveUserPassword(String token, String password);

    User findUserByEmail(String email);

    void createPasswordResetTokenForUser(User user, String token);
}
