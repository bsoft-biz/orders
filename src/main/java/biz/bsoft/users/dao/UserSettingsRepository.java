package biz.bsoft.users.dao;

import biz.bsoft.users.model.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by vbabin on 25.09.2016.
 */
public interface UserSettingsRepository extends CrudRepository<UserSettings, Integer> {
    UserSettings findByUser_Username(String  username);
}
