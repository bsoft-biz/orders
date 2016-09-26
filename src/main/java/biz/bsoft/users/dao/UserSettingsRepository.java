package biz.bsoft.users.dao;

import biz.bsoft.users.model.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by vbabin on 25.09.2016.
 */
public interface UserSettingsRepository extends JpaRepository<UserSettings, Integer> {
    UserSettings findByUser_Username(String  username);
}
