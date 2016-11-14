package biz.bsoft.old;

import biz.bsoft.orders.dao.ItemRepository;
import biz.bsoft.orders.model.Item;
import biz.bsoft.users.dao.UserRepository;
import biz.bsoft.users.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
public class OrdersApplicationTests {
	@Autowired
	private TestRestTemplate restTemplate;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ItemRepository itemRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@LocalServerPort
	int port;
	private String URL;

	private static final Logger logger =
			LoggerFactory.getLogger(OrdersApplicationTests.class);
	private String email="test@gmail.com";
	private String userName="testUser";
	private String password="qwerty";
	@Before
	public void givenUserAndVerificationToken() {
		User user;
		//user = userRepository.findByUsername(userName);
		user = userRepository.findByEmail(email);
		if(user == null){
			user = new User();
			user.setUsername(userName);
			user.setEmail(email);
			user.setPassword(passwordEncoder.encode(password));
			user.setEnabled(true);
		} else {
			user.setPassword(passwordEncoder.encode(password));
		}
		userRepository.save(user);
		URL = "http://localhost:" + String.valueOf(port);
	}


	@Test
	public void testRestItemsGetCount() {
		//get test user from properties file or create one in test
		TestRestTemplate restTemplateWithAuth;
		restTemplateWithAuth = restTemplate.withBasicAuth(userName,password);
		HttpHeaders requestHeaders = new HttpHeaders();
		HttpEntity<?> requestEntity = new HttpEntity<>(requestHeaders);
		String url;
		url= "/users/user";
		ResponseEntity<String> userResponse =
				restTemplateWithAuth.exchange(url, HttpMethod.GET, requestEntity, String.class);
		String user = userResponse.getBody();
		logger.info(user);
		logger.info(userResponse.getHeaders().toString());
		assertThat("Status code must be ok",userResponse.getStatusCode(),equalTo(HttpStatus.OK));
		url= "/orders/items";
		ResponseEntity<List<Item>> itemResponse =
				restTemplateWithAuth.exchange(url, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<Item>>() {});
		List<Item> items = itemResponse.getBody();
		assertEquals(items.size(),itemRepository.count());
	}
	@Test
	public void testUserRepositoryCanFindUser(){
		User user=userRepository.findByEmail(email);
		assertNotNull(user);
		assertThat("Can decode password",passwordEncoder.matches(password,user.getPassword()));
		assertEquals(userName, user.getUsername());
		assertEquals(email, user.getEmail());
	}
}
