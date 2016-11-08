package biz.bsoft.test;

import biz.bsoft.orders.dao.*;
import biz.bsoft.orders.model.ClientPOS;
import biz.bsoft.orders.model.Item;
import biz.bsoft.orders.model.ItemGroup;
import biz.bsoft.orders.model.OrderItem;
import biz.bsoft.users.dao.UserRepository;
import biz.bsoft.users.dao.UserSettingsRepository;
import biz.bsoft.users.model.User;
import biz.bsoft.users.model.UserSettings;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.hasItems;

/**
 * Created by vbabin on 02.11.2016.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OrdersIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemGroupRepository itemGroupRepository;

    @Autowired
    private ClientPosRepository clientPosRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;


    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @LocalServerPort
    int port;

    private String PREFIX_URL;

    private String userName = "testUser";
    private String userPassword = "test";
    private String email = "test@test.com";

    private ClientPOS pos;
    private ItemGroup itemGroup;
    private List<OrderItem> orderItems;

    @Before
    public void init() {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setUsername(userName);
            user.setPassword(passwordEncoder.encode(userPassword));
            user.setEmail(email);
            user.setEnabled(true);
        } else {
            user.setPassword(passwordEncoder.encode(userPassword));
        }
        userRepository.save(user);

        userSettingsRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        clientPosRepository.deleteAll();

        pos = new ClientPOS("Pos1");
        ClientPOS pos2 = new ClientPOS("Pos2");

        UserSettings userSettings =new UserSettings(user, email, pos, "Mr. Brown");

        userSettingsRepository.save(userSettings);
        clientPosRepository.save(Arrays.asList(pos,pos2));

        
        itemRepository.deleteAll();
        itemGroupRepository.deleteAll();

        itemGroup = new ItemGroup("Tasty");
        ItemGroup bunGroup=new ItemGroup("Buns");
        itemGroupRepository.save(Arrays.asList(itemGroup,bunGroup));

        Item cake, tart, bread;
        cake=new Item("Cake",itemGroup);
        tart=new Item("Tart",itemGroup);
        bread=new Item("Bread",itemGroup);
        itemRepository.save(Arrays.asList(cake,tart,bread));

        OrderItem orderItemCake, orderItemTart, orderItemBread;
        orderItemCake = new OrderItem(cake,1,2);
        orderItemTart = new OrderItem(tart,0,22);
        orderItemBread = new OrderItem(bread,10,0);
        orderItems= Arrays.asList(orderItemBread,orderItemCake,orderItemTart);

        RestAssured.port = port;
        PREFIX_URL = "http://localhost:" + String.valueOf(port);
    }

    @Test
    public void canFetchItems() {
//        final RequestSpecification request = RestAssured.given().auth().basic(userName, userPassword);
//        request.when().get(PREFIX_URL + "/users/user").then().assertThat().statusCode(200);
        given().auth().basic(userName, userPassword).
                when().get(PREFIX_URL + "/orders/items").
                then().assertThat().statusCode(200).body("itemName", hasItems("Cake","Tart","Bread"));
    }

    @Test
    public void canFetchGroups() {
        final RequestSpecification request = given().auth().basic(userName, userPassword);
        request.when().get(PREFIX_URL + "/orders/item_groups").then().assertThat().statusCode(200).body("groupName", hasItems("Tasty","Buns"));
    }

    @Test
    public void canPostOrder(){
        String date = LocalDateTime.now().toLocalDate().plusDays(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        //1) get sessionId
        Response response =
                given().auth().preemptive().basic(userName, userPassword).contentType(JSON).
                when().get(PREFIX_URL + "/users/user").
                then().log().all().extract().response();
        String jsessionidId =  response.getSessionId();//response.cookie("JSESSIONID");

        //2) get XSRF-TOKEN using new/real sessionId
        response =
                given().
                sessionId(jsessionidId).//cookie("JSESSIONID", jsessionidId).
                contentType(JSON).
                when().get(PREFIX_URL + "/users/user").
                then().log().all().extract().response();

        //3) post data using XSRF-TOKEN
        given().log().all().
                sessionId(jsessionidId).//cookie("JSESSIONID", jsessionidId).
                header("X-XSRF-TOKEN", response.cookie("XSRF-TOKEN")).
                queryParam("pos",pos.getId()).
                queryParam("date",date).
                queryParam("group_id",itemGroup.getId()).
                body(orderItems).
                contentType(JSON).
        when().
                post(PREFIX_URL + "/orders/orderitems").
        then().
            log().all().assertThat().statusCode(200);

        // get order
        given().
                auth().preemptive().basic(userName, userPassword).
                queryParam("pos",pos.getId()).
                queryParam("date",date).
                queryParam("group_id",itemGroup.getId()).
                contentType(JSON).
        when().
                get(PREFIX_URL + "/orders/orderitems").
        then().
                log().all().assertThat().statusCode(200).
                body("item.id", Matchers.containsInAnyOrder(orderItems.stream().map(oi->oi.getItem().getId()).toArray()));
    }
}
