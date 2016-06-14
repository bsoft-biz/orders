package biz.bsoft.users.dao;

import biz.bsoft.users.model.User;
import biz.bsoft.users.model.UserSettings;

/**
 * Created by vbabin on 27.03.2016.
 */
public interface UserDao {
    User findByUserName(String username);
    User add(String username);
    UserSettings getCurrentUserSettings();
    void setCurrentUserSettings(UserSettings userSettings);
    void setUserPassword(String oldPassword, String newPassword);
}
