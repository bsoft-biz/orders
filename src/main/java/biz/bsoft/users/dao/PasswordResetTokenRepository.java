package biz.bsoft.users.dao;

import biz.bsoft.users.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;

/**
 * Created by vbabin on 16.08.2016.
 */
public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Integer> {
    PasswordResetToken findByToken(String token);

    @Modifying
    @Query("delete from PasswordResetToken t where t.expiryDate <= ?1")
    void deleteAllExpiredSince(Date now);
}
