package biz.bsoft;

import biz.bsoft.orders.dao.ItemGroupRepository;
import biz.bsoft.orders.dao.ItemRepository;
import biz.bsoft.orders.model.Item;
import biz.bsoft.orders.model.ItemGroup;
import biz.bsoft.users.dao.UserRepository;
import biz.bsoft.users.model.User;
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
 * Created by vbabin on 02.11.2016.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class IntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemGroupRepository itemGroupRepository;

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

        URL = "http://localhost:" + String.valueOf(port);
    }

    @Test
    public void givenNotAuthenticatedUser_whenLoggingIn_thenCorrect() {
        final RequestSpecification request = RestAssured.given().auth().basic("testUser", "test");
        request.when().get(URL + "/users/user").then().assertThat().statusCode(200);
    }

    @Test
    public void canFetchItems() {
        ItemGroup tastyGroup=new ItemGroup("Tasty");
        Item cake, tart, bread;
        cake=new Item("Cake",tastyGroup);
        tart=new Item("Tart",tastyGroup);
        bread=new Item("Bread",tastyGroup);
        itemGroupRepository.save(tastyGroup);
        itemRepository.save(Arrays.asList(cake,tart,bread));
        final RequestSpecification request = RestAssured.given().auth().basic("testUser", "test");
        request.when().get(URL + "/orders/items").then().assertThat().statusCode(200).body("itemName", Matchers.hasItems("Cake","Tart","Bread"));
    }
}
