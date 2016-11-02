package biz.bsoft;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by vbabin on 02.11.2016.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class IntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @LocalServerPort
    int port;

    private String URL;

    @Before
    public void init() {
        User user = userRepository.findByEmail("test@test.com");
        if (user == null) {
            user = new User();
            user.setUsername("testUser");
            user.setPassword(passwordEncoder.encode("test"));
            user.setEmail("test@test.com");
            user.setEnabled(true);
            userRepository.save(user);
        } else {
            user.setPassword(passwordEncoder.encode("test"));
            userRepository.save(user);
        }

        RestAssured.port = port;

        final String URL_PREFIX = "http://localhost:" + String.valueOf(port);
        URL = URL_PREFIX + "/users/user";
    }

    @Test
    public void givenNotAuthenticatedUser_whenLoggingIn_thenCorrect() {
        final RequestSpecification request = RestAssured.given().auth().basic("testUser", "test");

        request.when().get(URL).then().assertThat().statusCode(200);
    }
}
