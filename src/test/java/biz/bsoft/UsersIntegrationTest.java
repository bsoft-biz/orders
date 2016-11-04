package biz.bsoft;

import biz.bsoft.orders.dao.ClientPosRepository;
import biz.bsoft.orders.model.ClientPOS;
import biz.bsoft.users.dao.UserPosRepository;
import biz.bsoft.users.dao.UserRepository;
import biz.bsoft.users.dao.UserSettingsRepository;
import biz.bsoft.users.model.User;
import biz.bsoft.users.model.UserPos;
import biz.bsoft.users.model.UserSettings;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * Created by vbabin on 04.11.2016.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UsersIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientPosRepository clientPosRepository;

    @Autowired
    private UserPosRepository userPosRepository;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @LocalServerPort
    int port;

    private String PREFIX_URL;

    private String email = "test@test.com";

    @Before
    public void init() {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setUsername("testUser");
            user.setPassword(passwordEncoder.encode("test"));
            user.setEmail(email);
            user.setEnabled(true);
        } else {
            user.setPassword(passwordEncoder.encode("test"));
        }
        userRepository.save(user);

        userPosRepository.deleteAll();
        userSettingsRepository.deleteAll();
        clientPosRepository.deleteAll();

        ClientPOS pos1 = new ClientPOS("Pos1");
        ClientPOS pos2 = new ClientPOS("Pos2");

        UserSettings userSettings =new UserSettings(user, email, pos1, "Mr. Brown");

        userSettingsRepository.save(userSettings);
        clientPosRepository.save(Arrays.asList(pos1,pos2));

        //UserPos userPos1 = new UserPos(user, pos1);
        UserPos userPos2 = new UserPos(user, pos2);
        //we can save only one userPos but controller should return both of them
        userPosRepository.save(Arrays.asList(userPos2));

        RestAssured.port = port;
        PREFIX_URL = "http://localhost:" + String.valueOf(port);
    }

    @Test
    public void givenNotAuthenticatedUser_whenLoggingIn_thenCorrect() {
        final RequestSpecification request = RestAssured.given().auth().basic("testUser", "test");
        request.when().get(PREFIX_URL + "/users/user").then().assertThat().statusCode(200);
    }

    @Test
    public void canFetchUserSettings() {
        final RequestSpecification request = RestAssured.given().auth().basic("testUser", "test");
        request.when().get(PREFIX_URL + "/users/userSettings").then().assertThat().statusCode(200).body("email", Matchers.equalTo(email));
    }

    @Test
    public void canFetchPoses() {
        final RequestSpecification request = RestAssured.given().auth().basic("testUser", "test");
        request.when().get(PREFIX_URL + "/users/userPoses").then().assertThat().statusCode(200).body("posName", Matchers.hasItems("Pos1","Pos2"));
    }
}