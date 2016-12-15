package biz.bsoft.test;

import biz.bsoft.orders.dao.ClientPosRepository;
import biz.bsoft.orders.model.ClientPOS;
import biz.bsoft.users.dao.UserPosRepository;
import biz.bsoft.users.dao.UserRepository;
import biz.bsoft.users.model.User;
import biz.bsoft.users.model.UserPos;
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
    private PasswordEncoder passwordEncoder;

    @LocalServerPort
    int port;

    private String PREFIX_URL;

    private String email = "test@test.com";

    @Before
    public void init() {
        userPosRepository.deleteAll();
        userRepository.deleteAll();
        clientPosRepository.deleteAll();

        ClientPOS pos1 = new ClientPOS("Pos1");
        ClientPOS pos2 = new ClientPOS("Pos2");

        User user;
        user = new User();
        user.setUsername("testUser");
        user.setPassword(passwordEncoder.encode("test"));
        user.setEmail(email);
        user.setEnabled(true);
        user.setClientPOS(pos1);
        userRepository.save(user);

        clientPosRepository.save(Arrays.asList(pos1,pos2));

        //UserPos userPos1 = new UserPos(user, pos);
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
        request.when().get(PREFIX_URL + "/users/user").then().log().all().assertThat().statusCode(200).body("name", Matchers.equalTo("testUser"));
    }

    @Test
    public void canFetchPoses() {
        final RequestSpecification request = RestAssured.given().auth().basic("testUser", "test");
        request.when().get(PREFIX_URL + "/users/userPoses").then().assertThat().statusCode(200).body("posName", Matchers.hasItems("Pos1","Pos2"));
    }
}