package biz.bsoft.test;

import biz.bsoft.users.dao.PasswordResetTokenRepository;
import biz.bsoft.users.dao.UserRepository;
import biz.bsoft.users.model.PasswordResetToken;
import biz.bsoft.users.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by vbabin on 15.10.2016.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@ActiveProfiles("test")
public class TokensTests {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    UserRepository userRepository;

    private Integer tokenId;
    private String userName;
    private String token;

    //

    @Before
    public void givenUserAndVerificationToken() {
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("SecretPassword");
        entityManager.persist(user);

        token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(user,token);
        passwordResetToken.setExpiryDate(Date.from(Instant.now()));
        entityManager.persist(passwordResetToken);

        entityManager.flush();
        entityManager.clear();

        tokenId = passwordResetToken.getId();
        userName = user.getUsername();
    }

    @After
    public void flushAfter() {
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    public void shouldBeInRepository(){
        assertThat("There should be at least one user",userRepository.count(), greaterThan(new Long(0)));
        assertThat("There should be at least one token",passwordResetTokenRepository.count(), greaterThan(new Long(0)));
    }
    @Test
    public void shouldFindByToken(){
        PasswordResetToken passwordResetToken=passwordResetTokenRepository.findByToken(token);
        assertThat("Should find token by its value",passwordResetToken.getUser().getUsername(),equalTo(userName));
    }
    @Test
    public void shouldDeleteToken(){
        passwordResetTokenRepository.deleteAllExpiredSince(Date.from(Instant.now()));
        PasswordResetToken token=passwordResetTokenRepository.findOne(tokenId);
        assertThat("Should delete token by expire date",token,nullValue());
    }
}
