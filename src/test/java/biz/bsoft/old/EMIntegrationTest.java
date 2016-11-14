package biz.bsoft.old;

import biz.bsoft.users.dao.UserRepository;
import biz.bsoft.users.model.User;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by vbabin on 03.11.2016.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
@Transactional
@Commit
public class EMIntegrationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @LocalServerPort
    int port;

    private String URL;

    @Before
    public void init() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword(passwordEncoder.encode("test"));
        user.setEmail("test@test.com");
        user.setEnabled(true);
        entityManager.persist(user);
        entityManager.flush();
        entityManager.clear();
        TestTransaction.end();

        RestAssured.port = port;

        URL = "http://localhost:" + String.valueOf(port) + "/users/user";
    }

    @Test
    public void givenNotAuthenticatedUser_whenLoggingIn_thenCorrect() {
        final RequestSpecification request = RestAssured.given().auth().basic("testUser", "test");

        request.when().get(URL).then().assertThat().statusCode(200);
    }
}