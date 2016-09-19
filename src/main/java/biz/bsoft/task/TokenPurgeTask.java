package biz.bsoft.task;

import biz.bsoft.users.dao.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;

/**
 * Created by vbabin on 16.09.2016.
 */
@Service
@Transactional
public class TokenPurgeTask {
    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Scheduled(cron ="${purge.cron.expression}")
    public void purgeExpired() {
        Date now = Date.from(Instant.now());
        passwordResetTokenRepository.deleteAllExpiredSince(now);
    }
}
